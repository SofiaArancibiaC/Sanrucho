package cl.sanrucho.pedidos.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.common.dto.PageResponse;
import cl.sanrucho.common.exception.BusinessRuleException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.pedidos.client.CatalogoClient;
import cl.sanrucho.pedidos.client.UsuarioClient;
import cl.sanrucho.pedidos.client.dto.ProductoCatalogoResponse;
import cl.sanrucho.pedidos.client.dto.StockUpdateRequest;
import cl.sanrucho.pedidos.dto.DetallePedidoResponse;
import cl.sanrucho.pedidos.dto.ItemPedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoRequest;
import cl.sanrucho.pedidos.dto.PedidoResumenResponse;
import cl.sanrucho.pedidos.dto.PedidoResponse;
import cl.sanrucho.pedidos.mapper.DetallePedidoMapper;
import cl.sanrucho.pedidos.mapper.PedidoMapper;
import cl.sanrucho.pedidos.model.entity.detallepedidos.DetallePedido;
import cl.sanrucho.pedidos.model.entity.pedidos.Pedido;
import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import cl.sanrucho.pedidos.repository.pedidos.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Reglas de negocio de pedidos.
 *
 * - El usuario debe existir (se valida via Feign contra ms-usuarios).
 * - Cada producto debe existir, estar activo y tener stock suficiente
 *   (se valida via Feign contra ms-catalogo).
 * - El total se calcula con el precio final vigente de cada producto.
 * - Al crear el pedido se descuenta stock; si algo falla, se compensa.
 * - Al cancelar o eliminar un pedido se repone el stock.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final Map<EstadoPedido, Set<EstadoPedido>> TRANSICIONES = Map.of(
            EstadoPedido.Pendiente, Set.of(EstadoPedido.Pagado, EstadoPedido.Cancelado),
            EstadoPedido.Pagado, Set.of(EstadoPedido.Enviado, EstadoPedido.Cancelado),
            EstadoPedido.Enviado, Set.of(EstadoPedido.Entregado),
            EstadoPedido.Entregado, Set.of(),
            EstadoPedido.Cancelado, Set.of());

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoService detallePedidoService;
    private final CatalogoClient catalogoClient;
    private final UsuarioClient usuarioClient;
    private final PedidoMapper pedidoMapper;
    private final DetallePedidoMapper detallePedidoMapper;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public PedidoResponse obtener(Integer id) {
        return construirRespuesta(buscar(id));
    }

    @Transactional
    public PedidoResponse crear(PedidoRequest request) {
        validarItems(request.getItems());

        // 1. Validar que el usuario exista en ms-usuarios.
        usuarioClient.obtenerUsuario(request.getIdUsuario());

        // 2. Consolidar cantidades por producto y validar contra ms-catalogo.
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (ItemPedidoRequest item : request.getItems()) {
            cantidades.merge(item.getIdProducto(), item.getCantidad(), Integer::sum);
        }

        Map<Integer, ProductoCatalogoResponse> productos = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> entry : cantidades.entrySet()) {
            ProductoCatalogoResponse producto = catalogoClient.obtenerProducto(entry.getKey());
            validarProducto(producto, entry.getValue());

            productos.put(entry.getKey(), producto);
            total = total.add(precioUnitario(producto)
                    .multiply(BigDecimal.valueOf(entry.getValue())));
        }

        // 3. Crear el pedido en estado Pendiente.
        Pedido pedido = pedidoMapper.toEntity(request);
        pedido.setFechaCompra(LocalDateTime.now());
        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.Pendiente);
        pedido = pedidoRepository.save(pedido);

        // 4. Descontar stock. Si una linea falla, se repone lo ya descontado.
        List<DetallePedido> detalles = new ArrayList<>();
        try {
            for (Map.Entry<Integer, Integer> entry : cantidades.entrySet()) {
                catalogoClient.ajustarStock(
                        entry.getKey(),
                        StockUpdateRequest.builder().cantidad(-entry.getValue()).build());

                detalles.add(DetallePedido.builder()
                        .idPedido(pedido.getId())
                        .idProducto(entry.getKey())
                        .cantidad(entry.getValue())
                        .precioUnitario(precioUnitario(productos.get(entry.getKey())))
                        .build());
            }

            detallePedidoService.guardarTodos(detalles);
        } catch (RuntimeException ex) {
            reponerStock(detalles);
            pedidoRepository.delete(pedido);
            throw ex;
        }

        Map<Integer, String> nombres = new LinkedHashMap<>();
        productos.forEach((idProducto, producto) -> nombres.put(idProducto, producto.getNombre()));

        return construirRespuesta(pedido, detalles, nombres);
    }

    @Transactional
    public PedidoResponse actualizarEstado(Integer id, EstadoPedido nuevoEstado) {
        Pedido pedido = buscar(id);
        EstadoPedido actual = pedido.getEstado();

        if (actual == null) {
            throw new BusinessRuleException("El pedido no tiene un estado valido.");
        }

        if (!TRANSICIONES.getOrDefault(actual, Set.of()).contains(nuevoEstado)) {
            throw new BusinessRuleException(String.format(
                    "Transicion de estado no permitida: %s -> %s.", actual, nuevoEstado));
        }

        List<DetallePedido> detalles = detallePedidoService.listarPorPedido(id);

        if (nuevoEstado == EstadoPedido.Cancelado) {
            reponerStock(detalles);
        }

        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);

        return construirRespuesta(pedido, detalles);
    }

    @Transactional
    public void eliminar(Integer id) {
        Pedido pedido = buscar(id);

        if (pedido.getEstado() == EstadoPedido.Enviado || pedido.getEstado() == EstadoPedido.Entregado) {
            throw new BusinessRuleException(
                    "No se puede eliminar un pedido enviado o entregado.");
        }

        List<DetallePedido> detalles = detallePedidoService.listarPorPedido(id);

        if (pedido.getEstado() != EstadoPedido.Cancelado) {
            reponerStock(detalles);
        }

        detallePedidoService.eliminarPorPedido(id);
        pedidoRepository.delete(pedido);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Pedido buscar(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", "ID", id));
    }

    private void validarItems(List<ItemPedidoRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("El pedido debe tener al menos un producto.");
        }
    }

    private void validarProducto(ProductoCatalogoResponse producto, Integer cantidad) {
        if (producto == null) {
            throw new BusinessRuleException("El producto solicitado no existe.");
        }

        if (!"Activo".equalsIgnoreCase(producto.getEstado())) {
            throw new BusinessRuleException(String.format(
                    "El producto '%s' no esta disponible para la venta.", producto.getNombre()));
        }

        int stock = producto.getStock() != null ? producto.getStock() : 0;
        if (stock < cantidad) {
            throw new BusinessRuleException(String.format(
                    "Stock insuficiente para el producto '%s'. Disponible: %d, solicitado: %d.",
                    producto.getNombre(), stock, cantidad));
        }
    }

    private BigDecimal precioUnitario(ProductoCatalogoResponse producto) {
        if (producto.getPrecioFinal() != null) {
            return producto.getPrecioFinal();
        }

        if (Boolean.TRUE.equals(producto.getOferta()) && producto.getPrecioOferta() != null) {
            return producto.getPrecioOferta();
        }

        return producto.getPrecio();
    }

    private void reponerStock(List<DetallePedido> detalles) {
        for (DetallePedido detalle : detalles) {
            try {
                catalogoClient.ajustarStock(
                        detalle.getIdProducto(),
                        StockUpdateRequest.builder().cantidad(detalle.getCantidad()).build());
            } catch (RuntimeException ex) {
                log.error("No se pudo reponer stock del producto {}: {}",
                        detalle.getIdProducto(), ex.getMessage());
            }
        }
    }

    private PedidoResponse construirRespuesta(Pedido pedido) {
        List<DetallePedido> detalles = detallePedidoService.listarPorPedido(pedido.getId());
        return construirRespuesta(pedido, detalles);
    }

    private PedidoResponse construirRespuesta(Pedido pedido, List<DetallePedido> detalles) {
        Map<Integer, String> nombres = new LinkedHashMap<>();
        for (DetallePedido detalle : detalles) {
            nombres.computeIfAbsent(detalle.getIdProducto(), this::consultarNombreProducto);
        }
        return construirRespuesta(pedido, detalles, nombres);
    }

    private PedidoResponse construirRespuesta(
            Pedido pedido, List<DetallePedido> detalles, Map<Integer, String> nombres) {

        PedidoResponse response = pedidoMapper.toResponse(pedido);
        List<DetallePedidoResponse> detallesResponse = detallePedidoMapper.toResponseList(detalles);

        for (DetallePedidoResponse detalle : detallesResponse) {
            detalle.setNombreProducto(nombres.get(detalle.getIdProducto()));
        }

        response.setDetalles(detallesResponse);
        return response;
    }

    private String consultarNombreProducto(Integer idProducto) {
        try {
            ProductoCatalogoResponse producto = catalogoClient.obtenerProducto(idProducto);
            return producto != null ? producto.getNombre() : null;
        } catch (RuntimeException ex) {
            log.warn("No se pudo obtener el producto {} desde ms-catalogo: {}",
                    idProducto, ex.getMessage());
            return null;
        }
    }
}
