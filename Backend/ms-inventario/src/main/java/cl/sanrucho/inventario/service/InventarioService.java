package cl.sanrucho.inventario.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.sanrucho.common.exception.DuplicateResourceException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.common.exception.ReferentialIntegrityException;
import cl.sanrucho.inventario.dto.AjusteStockRequest;
import cl.sanrucho.inventario.dto.InventarioRequest;
import cl.sanrucho.inventario.dto.InventarioResponse;
import cl.sanrucho.inventario.dto.OperacionStockRequest;
import cl.sanrucho.inventario.exception.RangoStockInvalidoException;
import cl.sanrucho.inventario.exception.StockInsuficienteException;
import cl.sanrucho.inventario.mapper.InventarioMapper;
import cl.sanrucho.inventario.model.EventoInventario;
import cl.sanrucho.inventario.model.EventoInventario.TipoEvento;
import cl.sanrucho.inventario.model.Inventario;
import cl.sanrucho.inventario.model.MovimientoInventario;
import cl.sanrucho.inventario.model.MovimientoInventario.TipoMovimiento;
import cl.sanrucho.inventario.repository.EventoInventarioRepository;
import cl.sanrucho.inventario.repository.InventarioRepository;
import cl.sanrucho.inventario.repository.MovimientoInventarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de la logica de negocio del inventario.
 *
 * Modelo de stock:
 * - stockActual    es el stock fisico en bodega.
 * - stockReservado son las unidades comprometidas por pedidos aun no despachados.
 * - stockDisponible es lo que realmente se puede vender: actual - reservado.
 * - stockMinimo y stockMaximo delimitan el rango sano del stock.
 *
 * Reglas:
 * - Ninguna operacion deja el stock en negativo ni el disponible en negativo.
 * - Todo cambio de stock deja un movimiento con la cantidad anterior y la nueva,
 *   de modo que el stock siempre se puede reconstruir desde el historial.
 * - Todo cambio de stock genera ademas un evento de outbox (publicado=false)
 *   que describe lo que ocurrio y, si el stock quedo critico, otro que avisa.
 *
 * Sobre el bloqueo optimista: Inventario esta anotado con @Version, asi que si
 * dos operaciones sobre el mismo producto se ejecutan a la vez, la segunda
 * falla con ObjectOptimisticLockingFailureException en vez de pisar el stock
 * de la primera. La operacion que gane es la que se guarda.
 *
 * El campo realizadoPor viaja en el request. Cuando se conecte Spring Security
 * deberia pasar a leerse del principal del JWT en vez de aceptarlo del cliente.
 */
@Service
@RequiredArgsConstructor
public class InventarioService {

    private static final String ENTIDAD = "Inventario";
    private static final String MOTIVO_CARGA_INICIAL = "Carga inicial de inventario";

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final EventoInventarioRepository eventoInventarioRepository;
    private final InventarioMapper inventarioMapper;
    private final ObjectMapper objectMapper;

    // ─── Consultas ───────────────────────────────────────────────────────────

    public List<InventarioResponse> findAll() {
        return inventarioMapper.toResponseList(inventarioRepository.findAll());
    }

    public InventarioResponse findById(int id) {
        return inventarioMapper.toResponse(getInventarioById(id));
    }

    public InventarioResponse findByProductoId(int productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .map(inventarioMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(ENTIDAD, "productoId", productoId));
    }

    public InventarioResponse findBySku(String sku) {
        return inventarioRepository.findBySku(sku.trim())
                .map(inventarioMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(ENTIDAD, "SKU", sku));
    }

    public List<InventarioResponse> findConAlertaStockBajo() {
        return inventarioMapper.toResponseList(inventarioRepository.findByAlertaStockBajoTrue());
    }

    public List<InventarioResponse> findByNombreProducto(String nombre) {
        return inventarioMapper.toResponseList(
                inventarioRepository.findByNombreProductoContainingIgnoreCase(nombre.trim()));
    }

    // ─── Alta y actualizacion ─────────────────────────────────────────────────

    @Transactional
    public InventarioResponse create(InventarioRequest request) {

        validateProductoIdUnico(request.getProductoId(), null);
        validateSkuUnico(request.getSku(), null);
        validateRangoStock(request.getStockMinimo(), request.getStockMaximo());

        int stockInicial = request.getStockInicial() == null ? 0 : request.getStockInicial();

        Inventario inventario = inventarioMapper.toModel(request);

        inventario.setSku(request.getSku().trim());
        inventario.setStockActual(stockInicial);
        inventario.setStockReservado(0);
        inventario.setCreatedAt(LocalDateTime.now());
        normalizarUbicacion(inventario, request.getUbicacionBodega());
        recalcularStock(inventario);

        Inventario guardado = inventarioRepository.save(inventario);

        // La carga inicial tambien queda registrada como movimiento, para que
        // el historial del producto nazca completo y no con un salto sin origen.
        if (stockInicial > 0) {
            registrarMovimiento(guardado, TipoMovimiento.entrada, 0, stockInicial,
                    null, MOTIVO_CARGA_INICIAL, null);
        }

        registrarEvento(guardado, TipoEvento.stock_actualizado);
        registrarAlerta(guardado);

        return inventarioMapper.toResponse(guardado);
    }

    @Transactional
    public InventarioResponse update(int id, InventarioRequest request) {

        Inventario inventario = getInventarioById(id);

        // Solo se valida la unicidad si el valor realmente cambio: si el
        // producto o el SKU se mantienen, la validacion siempre pasaria.
        if (!inventario.getProductoId().equals(request.getProductoId())) {
            validateProductoIdUnico(request.getProductoId(), id);
        }
        if (!inventario.getSku().equalsIgnoreCase(request.getSku().trim())) {
            validateSkuUnico(request.getSku(), id);
        }
        validateRangoStock(request.getStockMinimo(), request.getStockMaximo());

        // Dirty Checking: JPA detecta los cambios y genera el UPDATE solo
        inventarioMapper.updateEntity(request, inventario);

        inventario.setSku(request.getSku().trim());
        normalizarUbicacion(inventario, request.getUbicacionBodega());
        recalcularStock(inventario);

        Inventario guardado = inventarioRepository.save(inventario);

        registrarEvento(guardado, TipoEvento.stock_actualizado);
        registrarAlerta(guardado);

        return inventarioMapper.toResponse(guardado);
    }

    @Transactional
    public void deleteById(int id) {

        Inventario inventario = getInventarioById(id);

        if (movimientoInventarioRepository.existsByInventarioId(id)) {
            throw new ReferentialIntegrityException(ENTIDAD, id, "Movimientos de inventario");
        }

        inventarioRepository.delete(inventario);
    }

    // ─── Operaciones de stock ─────────────────────────────────────────────────

    @Transactional
    public InventarioResponse registrarEntrada(int id, OperacionStockRequest request) {
        Inventario inventario = getInventarioById(id);
        int anterior = inventario.getStockActual();
        int nuevo = anterior + request.getCantidad();

        return aplicarOperacion(inventario, TipoMovimiento.entrada, anterior, nuevo,
                request.getReferencia(), request.getMotivo(), request.getRealizadoPor(),
                TipoEvento.stock_actualizado);
    }

    @Transactional
    public InventarioResponse registrarSalida(int id, OperacionStockRequest request) {
        Inventario inventario = getInventarioById(id);
        int anterior = inventario.getStockActual();
        int nuevo = anterior - request.getCantidad();

        if (nuevo < 0) {
            throw new StockInsuficienteException(inventario.getSku(), request.getCantidad(), anterior);
        }

        return aplicarOperacion(inventario, TipoMovimiento.salida, anterior, nuevo,
                request.getReferencia(), request.getMotivo(), request.getRealizadoPor(),
                TipoEvento.stock_actualizado);
    }

    @Transactional
    public InventarioResponse registrarReserva(int id, OperacionStockRequest request) {
        Inventario inventario = getInventarioById(id);
        int anterior = inventario.getStockReservado();
        int nuevo = anterior + request.getCantidad();

        // La reserva no puede superar lo disponible, porque lo reservado sigue
        // siendo stock fisico: se puede comprometer, no duplicar.
        int disponible = inventario.getStockActual() - anterior;
        if (request.getCantidad() > disponible) {
            throw new StockInsuficienteException(
                    inventario.getSku(), request.getCantidad(), disponible);
        }

        return aplicarOperacion(inventario, TipoMovimiento.reserva, anterior, nuevo,
                request.getReferencia(), request.getMotivo(), request.getRealizadoPor(),
                TipoEvento.stock_reservado);
    }

    @Transactional
    public InventarioResponse registrarLiberacion(int id, OperacionStockRequest request) {
        Inventario inventario = getInventarioById(id);
        int anterior = inventario.getStockReservado();

        if (request.getCantidad() > anterior) {
            throw new StockInsuficienteException(
                    inventario.getSku(), request.getCantidad(), anterior);
        }

        int nuevo = anterior - request.getCantidad();

        return aplicarOperacion(inventario, TipoMovimiento.liberacion, anterior, nuevo,
                request.getReferencia(), request.getMotivo(), request.getRealizadoPor(),
                TipoEvento.stock_liberado);
    }

    @Transactional
    public InventarioResponse registrarAjuste(int id, AjusteStockRequest request) {
        Inventario inventario = getInventarioById(id);
        int anterior = inventario.getStockActual();

        // El ajuste no puede dejar el reservado por sobre el fisico: si el
        // conteo fisico bajo, hay que liberar antes lo que quedo comprometido.
        if (request.getStockActual() < inventario.getStockReservado()) {
            throw new StockInsuficienteException(
                    inventario.getSku(),
                    inventario.getStockReservado(),
                    request.getStockActual());
        }

        return aplicarOperacion(inventario, TipoMovimiento.ajuste,
                anterior, request.getStockActual(),
                request.getReferencia(), request.getMotivo(), request.getRealizadoPor(),
                TipoEvento.stock_actualizado);
    }

    // ─── Metodos privados auxiliares ─────────────────────────────────────────

    private InventarioResponse aplicarOperacion(
            Inventario inventario,
            TipoMovimiento tipoMovimiento,
            int cantidadAnterior,
            int cantidadNueva,
            String referencia,
            String motivo,
            String realizadoPor,
            TipoEvento tipoEvento) {

        // El service es el unico que escribe el stock actual o el reservado,
        // segun el tipo de operacion.
        if (esMovimientoSobreStockReservado(tipoMovimiento)) {
            inventario.setStockReservado(cantidadNueva);
        } else {
            inventario.setStockActual(cantidadNueva);
        }

        recalcularStock(inventario);

        Inventario guardado = inventarioRepository.save(inventario);

        registrarMovimiento(guardado, tipoMovimiento, cantidadAnterior, cantidadNueva,
                referencia, motivo, realizadoPor);
        registrarEvento(guardado, tipoEvento);
        registrarAlerta(guardado);

        return inventarioMapper.toResponse(guardado);
    }

    private boolean esMovimientoSobreStockReservado(TipoMovimiento tipoMovimiento) {
        return tipoMovimiento == TipoMovimiento.reserva
                || tipoMovimiento == TipoMovimiento.liberacion;
    }

    private void registrarMovimiento(
            Inventario inventario,
            TipoMovimiento tipoMovimiento,
            int cantidadAnterior,
            int cantidadNueva,
            String referencia,
            String motivo,
            String realizadoPor) {

        // cantidadAnterior y cantidadNueva trackean la magnitud sobre la que
        // actua el movimiento: el stock actual para entrada, salida y ajuste,
        // y el stock reservado para reserva y liberacion.
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .inventario(inventario)
                .tipoMovimiento(tipoMovimiento)
                .cantidad(Math.abs(cantidadNueva - cantidadAnterior))
                .cantidadAnterior(cantidadAnterior)
                .cantidadNueva(cantidadNueva)
                .referencia(referencia)
                .motivo(motivo)
                .realizadoPor(realizadoPor)
                .fechaMovimiento(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        movimientoInventarioRepository.save(movimiento);
    }

    private void registrarEvento(Inventario inventario, TipoEvento tipoEvento) {
        registrarEvento(inventario, tipoEvento, construirPayload(inventario, tipoEvento));
    }

    private void registrarEvento(Inventario inventario, TipoEvento tipoEvento, String payload) {
        EventoInventario evento = EventoInventario.builder()
                .eventId(UUID.randomUUID().toString())
                .productoId(inventario.getProductoId())
                .sku(inventario.getSku())
                .tipoEvento(tipoEvento)
                .payload(payload)
                .publicado(false)
                .fechaEvento(LocalDateTime.now())
                .build();

        eventoInventarioRepository.save(evento);
    }

    private void registrarAlerta(Inventario inventario) {
        if (inventario.getStockDisponible() == 0) {
            registrarEvento(inventario, TipoEvento.sin_stock);
        } else if (Boolean.TRUE.equals(inventario.getAlertaStockBajo())) {
            registrarEvento(inventario, TipoEvento.stock_bajo);
        }
    }

    private String construirPayload(Inventario inventario, TipoEvento tipoEvento) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("evento", tipoEvento.name());
        payload.put("inventarioId", inventario.getId());
        payload.put("productoId", inventario.getProductoId());
        payload.put("sku", inventario.getSku());
        payload.put("nombreProducto", inventario.getNombreProducto());
        payload.put("stockActual", inventario.getStockActual());
        payload.put("stockReservado", inventario.getStockReservado());
        payload.put("stockDisponible", inventario.getStockDisponible());
        payload.put("stockMinimo", inventario.getStockMinimo());

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "No se pudo construir el payload del evento de inventario", e);
        }
    }

    private void recalcularStock(Inventario inventario) {
        int disponible = inventario.getStockActual() - inventario.getStockReservado();
        inventario.setStockDisponible(disponible);
        inventario.setAlertaStockBajo(disponible <= inventario.getStockMinimo());
    }

    private void normalizarUbicacion(Inventario inventario, String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            return;
        }
        inventario.setUbicacionBodega(ubicacion.trim());
    }

    private void validateProductoIdUnico(Integer productoId, Integer inventarioIdActual) {
        inventarioRepository.findByProductoId(productoId)
                .filter(existente -> !existente.getId().equals(inventarioIdActual))
                .ifPresent(existente -> {
                    throw new DuplicateResourceException(ENTIDAD, "productoId", productoId,
                            existente.getNombreProducto());
                });
    }

    private void validateSkuUnico(String sku, Integer inventarioIdActual) {
        inventarioRepository.findBySku(sku.trim())
                .filter(existente -> !existente.getId().equals(inventarioIdActual))
                .ifPresent(existente -> {
                    throw new DuplicateResourceException(ENTIDAD, "SKU", sku.trim(),
                            existente.getNombreProducto());
                });
    }

    private void validateRangoStock(Integer stockMinimo, Integer stockMaximo) {
        if (stockMinimo > stockMaximo) {
            throw new RangoStockInvalidoException(stockMinimo, stockMaximo);
        }
    }

    private Inventario getInventarioById(int id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTIDAD, "ID", id));
    }
}
