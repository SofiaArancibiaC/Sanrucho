package cl.sanrucho.pedidos.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cl.sanrucho.common.dto.PageResponse;
import cl.sanrucho.common.exception.BusinessRuleException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.pedidos.client.CatalogoClient;
import cl.sanrucho.pedidos.client.dto.ProductoCatalogoResponse;
import cl.sanrucho.pedidos.client.dto.StockUpdateRequest;
import cl.sanrucho.pedidos.dto.DetallePedidoResponse;
import cl.sanrucho.pedidos.dto.ItemPedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoResponse;
import cl.sanrucho.pedidos.dto.PedidoResumenResponse;
import cl.sanrucho.pedidos.mapper.DetallePedidoMapper;
import cl.sanrucho.pedidos.mapper.PedidoMapper;
import cl.sanrucho.pedidos.model.entity.detallepedidos.DetallePedido;
import cl.sanrucho.pedidos.model.entity.pedidos.Pedido;
import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import cl.sanrucho.pedidos.repository.pedidos.PedidoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Reglas de negocio de pedidos.
 *
 * Flujo de creacion:
 *   1. Valida items (no vacios, sin productos repetidos).
 *   2. Consulta cada producto en ms-catalogo (Feign) y verifica estado y stock.
 *   3. Calcula precio unitario (oferta o normal) y total.
 *   4. Guarda la cabecera en la BD "pedidos".
 *   5. Guarda el detalle en la BD "detalle_pedidos".
 *   6. Descuenta stock en ms-catalogo.
 *
 * Como pedidos, detalle_pedidos y productos viven en bases de datos distintas,
 * no existe una transaccion ACID global. Si un paso falla se aplican
 * COMPENSACIONES: se repone el stock ya descontado y se eliminan las filas
 * creadas, para no dejar datos huerfanos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final Map<EstadoPedido, Set<EstadoPedido>> TRANSICIONES = Map.of(
            EstadoPedido.Pendiente, EnumSet.of(EstadoPedido.Pagado, EstadoPedido.Cancelado),
            EstadoPedido.Pagado, EnumSet.of(EstadoPedido.Enviado, EstadoPedido.Cancelado),
            EstadoPedido.Enviado, EnumSet.of(EstadoPedido.Entregado),
            EstadoPedido.Entregado, EnumSet.noneOf(EstadoPedido.class),
            EstadoPedido.Cancelado, EnumSet.noneOf(EstadoPedido.class)
    );

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoService detallePedidoService;
    private final CatalogoClient catalogoClient;
    private final PedidoMapper pedidoMapper;
    private final DetallePedidoMapper detallePedidoMapper;

    public PageResponse<PedidoResumenResponse> listar(
            Integer idUsuario, EstadoPedido estado, Pageable pageable) {

        Page<Pedido> page;

        if (idUsuario != null && estado != null) {
            page = pedidoRepository.findByIdUsuarioAndEstado(idUsuario, estado, pageable);
        } else if (idUsuario != null) {
            page = pedidoRepository.findByIdUsuario(idUsuario, pageable);
        } else if (estado != null) {
            page = pedidoRepository.findByEstado(estado, pageable);
        } else {
            page = pedidoRepository.findAll(pageable);
        }

        return PageResponse.from(page.map(pedidoMapper::toResumen));
    }

    public PedidoResponse obtener(Integer id) {
        Pedido pedido = buscar(id);
        List<DetallePedido> detalles = detallePedidoService.listarPorPedido(id);
        return construirRespuesta(pedido, detalles);
    }

    public PedidoResponse crear(PedidoRequest request) {
        List<ItemPedidoRequest> items = request.getItems();
        validarItems(items);

        List<Linea> lineas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequest item : items) {
            ProductoCatalogoResponse producto = catalogoClient.obtenerProducto(item.getIdProducto());
            validarProducto(producto, item.getCantidad());

            BigDecimal precioUnitario = calcularPrecioUnitario(producto);
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));

            lineas.add(new Linea(producto.getId(), producto.getNombre(), item.getCantidad(),
                    precioUnitario, subtotal));
            total = total.add(subtotal);
        }

        Pedido pedido = Pedido.builder()
                .idUsuario(request.getIdUsuario())
                .fechaCompra(LocalDateTime.now())
                .total(total)
                .estado(EstadoPedido.Pendiente)
                .build();

        Pedido guardado = pedidoRepository.save(pedido);

        List<DetallePedido> detalles = lineas.stream()
                .map(linea -> DetallePedido.builder()
                        .idPedido(guardado.getId())
                        .idProducto(linea.idProducto())
                        .cantidad(linea.cantidad())
                        .precioUnitario(linea.precioUnitario())
                        .build())
                .collect(Collectors.toList());

        try {
            detalles = detallePedidoService.guardarTodos(detalles);
        } catch (RuntimeException e) {
            log.error("Fallo al guardar el detalle del pedido {}. Se elimina la cabecera.", guardado.getId(), e);
            limpiarPedido(guardado.getId());
            throw e;
        }

        List<Linea> descontadas = new ArrayList<>();
        try {
            for (Linea linea : lineas) {
                catalogoClient.ajustarStock(linea.idProducto(),
                        new StockUpdateRequest(-linea.cantidad()));
                descontadas.add(linea);
            }
        } catch (RuntimeException e) {
            log.error("Fallo al descontar stock del pedido {}. Se aplican compensaciones.",
                    guardado.getId(), e);
            compensarStock(descontadas);
            limpiarPedido(guardado.getId());
            throw e;
        }

        Map<Integer, String> nombres = lineas.stream()
                .collect(Collectors.toMap(Linea::idProducto, Linea::nombreProducto, (a, b) -> a));

        return construirRespuesta(guardado, detalles, nombres);
    }

    public PedidoResponse actualizarEstado(Integer id, EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new BusinessRuleException("El estado es obligatorio.");
        }

        Pedido pedido = buscar(id);
        EstadoPedido actual = pedido.getEstado();

        if (actual == null) {
            throw new BusinessRuleException("El pedido no tiene un estado definido.");
        }

        if (actual == nuevoEstado) {
            throw new BusinessRuleException("El pedido ya se encuentra en estado " + nuevoEstado + ".");
        }

        Set<EstadoPedido> permitidos = TRANSICIONES.getOrDefault(actual, Set.of());
        if (!permitidos.contains(nuevoEstado)) {
            throw new BusinessRuleException(String.format(
                    "Transicion no permitida: %s -> %s. Estados permitidos: %s.",
                    actual, nuevoEstado, permitidos));
        }

        List<DetallePedido> detalles = detallePedidoService.listarPorPedido(id);

        // Al cancelar se devuelve el stock reservado/descontado.
        if (nuevoEstado == EstadoPedido.Cancelado) {
            reponerStock(detalles);
        }

        pedido.setEstado(nuevoEstado);
        return construirRespuesta(pedidoRepository.save(pedido), detalles);
    }

    public void eliminar(Integer id) {
        Pedido pedido = buscar(id);

        if (pedido.getEstado() != EstadoPedido.Pendiente
                && pedido.getEstado() != EstadoPedido.Cancelado) {
            throw new BusinessRuleException(
                    "Solo se pueden eliminar pedidos en estado Pendiente o Cancelado. Estado actual: "
                            + pedido.getEstado() + ".");
        }

        List<DetallePedido> detalles = detallePedidoService.listarPorPedido(id);

        // Un pedido Pendiente ya desconto stock al crearse: hay que devolverlo.
        if (pedido.getEstado() == EstadoPedido.Pendiente) {
            reponerStock(detalles);
        }

        detallePedidoService.eliminarPorPedido(id);
        pedidoRepository.delete(pedido);
    }

    // ─── Helpers de negocio ───────────────────────────────────────────────────

    private Pedido buscar(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", "ID", id));
    }

    private void validarItems(List<ItemPedidoRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("El pedido debe incluir al menos un producto.");
        }

        Set<Integer> vistos = new HashSet<>();
        for (ItemPedidoRequest item : items) {
            if (!vistos.add(item.getIdProducto())) {
                throw new BusinessRuleException(
                        "El producto con ID " + item.getIdProducto() + " esta repetido en el pedido.");
            }
        }
    }

    private void validarProducto(ProductoCatalogoResponse producto, Integer cantidad) {
        if (producto == null) {
            throw new BusinessRuleException("El producto solicitado no existe en el catalogo.");
        }

        if (producto.getEstado() != null && !"Activo".equalsIgnoreCase(producto.getEstado())) {
            throw new BusinessRuleException(String.format(
                    "El producto '%s' no esta disponible (estado %s).",
                    producto.getNombre(), producto.getEstado()));
        }

        int stock = producto.getStock() != null ? producto.getStock() : 0;
        if (stock < cantidad) {
            throw new BusinessRuleException(String.format(
                    "Stock insuficiente para '%s'. Disponible: %d, solicitado: %d.",
                    producto.getNombre(), stock, cantidad));
        }
    }

    private BigDecimal calcularPrecioUnitario(ProductoCatalogoResponse producto) {
        if (producto.getPrecioFinal() != null) {
            return producto.getPrecioFinal();
        }
        if (Boolean.TRUE.equals(producto.getOferta()) && producto.getPrecioOferta() != null) {
            return producto.getPrecioOferta();
        }
        if (producto.getPrecio() != null) {
            return producto.getPrecio();
        }
        throw new BusinessRuleException(
                "El producto '" + producto.getNombre() + "' no tiene precio configurado.");
    }

    /** Repone stock; si falla a mitad de camino, revierte lo ya repuesto. */
    private void reponerStock(List<DetallePedido> detalles) {
        List<DetallePedido> repuestos = new ArrayList<>();

        try {
            for (DetallePedido detalle : detalles) {
                catalogoClient.ajustarStock(detalle.getIdProducto(),
                        new StockUpdateRequest(detalle.getCantidad()));
                repuestos.add(detalle);
            }
        } catch (RuntimeException e) {
            for (DetallePedido detalle : repuestos) {
                try {
                    catalogoClient.ajustarStock(detalle.getIdProducto(),
                            new StockUpdateRequest(-detalle.getCantidad()));
                } catch (RuntimeException ex) {
                    log.error("No se pudo revertir la reposicion de stock del producto {}",
                            detalle.getIdProducto(), ex);
                }
            }
            throw e;
        }
    }

    /** Devuelve el stock de las lineas ya descontadas (compensacion de crear()). */
    private void compensarStock(List<Linea> descontadas) {
        for (Linea linea : descontadas) {
            try {
                catalogoClient.ajustarStock(linea.idProducto(),
                        new StockUpdateRequest(linea.cantidad()));
            } catch (RuntimeException e) {
                log.error("No se pudo reponer el stock del producto {} tras el fallo del pedido",
                        linea.idProducto(), e);
            }
        }
    }

    /** Elimina cabecera y detalle sin enmascarar la excepcion original. */
    private void limpiarPedido(Integer id) {
        try {
            detallePedidoService.eliminarPorPedido(id);
        } catch (Exception e) {
            log.error("No se pudo limpiar el detalle del pedido {}", id, e);
        }
        try {
            pedidoRepository.deleteById(id);
        } catch (Exception e) {
            log.error("No se pudo limpiar la cabecera del pedido {}", id, e);
        }
    }

    // ─── Construccion de respuestas ───────────────────────────────────────────

    private PedidoResponse construirRespuesta(Pedido pedido, List<DetallePedido> detalles) {
        Map<Integer, String> nombres = new HashMap<>();
        for (DetallePedido detalle : detalles) {
            nombres.computeIfAbsent(detalle.getIdProducto(), this::consultarNombreProducto);
        }
        return construirRespuesta(pedido, detalles, nombres);
    }

    private PedidoResponse construirRespuesta(
            Pedido pedido, List<DetallePedido> detalles, Map<Integer, String> nombres) {

        PedidoResponse response = pedidoMapper.toResponse(pedido);
        List<DetallePedidoResponse> detalleResponses = detallePedidoMapper.toResponseList(detalles);

        for (DetallePedidoResponse detalle : detalleResponses) {
            detalle.setNombreProducto(nombres.getOrDefault(detalle.getIdProducto(),
                    "Producto no disponible (ID " + detalle.getIdProducto() + ")"));
        }

        response.setDetalles(detalleResponses);
        return response;
    }

    private String consultarNombreProducto(Integer idProducto) {
        try {
            ProductoCatalogoResponse producto = catalogoClient.obtenerProducto(idProducto);
            return producto.getNombre();
        } catch (FeignException e) {
            log.warn("No se pudo obtener el nombre del producto {}: {}", idProducto, e.getMessage());
            return "Producto no disponible (ID " + idProducto + ")";
        }
    }

    /** Linea calculada en memoria antes de persistir. */
    private record Linea(
            Integer idProducto,
            String nombreProducto,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal) {
    }
}
