package cl.sanrucho.pagos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.sanrucho.common.dto.PageResponse;
import cl.sanrucho.common.exception.BusinessRuleException;
import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.pagos.client.PedidoClient;
import cl.sanrucho.pagos.client.dto.EstadoUpdateRequest;
import cl.sanrucho.pagos.client.dto.PedidoResponse;
import cl.sanrucho.pagos.dto.PagoRequest;
import cl.sanrucho.pagos.dto.PagoResponse;
import cl.sanrucho.pagos.mapper.TransaccionMapper;
import cl.sanrucho.pagos.model.entity.Transaccion;
import cl.sanrucho.pagos.model.enums.EstadoTransaccion;
import cl.sanrucho.pagos.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;

/**
 * Reglas de negocio de pagos.
 *
 * - Solo se puede iniciar el pago de un pedido en estado Pendiente.
 * - El monto se toma desde ms-pedidos (fuente de verdad del total).
 * - Al aprobar un pago se marca el pedido como Pagado via Feign.
 * - Al anular un pago aprobado se marca el pedido como Cancelado via Feign.
 */
@Service
@RequiredArgsConstructor
public class PagoService {

    private static final Map<EstadoTransaccion, Set<EstadoTransaccion>> TRANSICIONES = Map.of(
            EstadoTransaccion.Pendiente, Set.of(EstadoTransaccion.Aprobado, EstadoTransaccion.Rechazado),
            EstadoTransaccion.Aprobado, Set.of(EstadoTransaccion.Anulado),
            EstadoTransaccion.Rechazado, Set.of(),
            EstadoTransaccion.Anulado, Set.of());

    private final TransaccionRepository transaccionRepository;
    private final TransaccionMapper transaccionMapper;
    private final PedidoClient pedidoClient;

    @Transactional(readOnly = true)
    public PageResponse<PagoResponse> listar(
            Integer idPedido, EstadoTransaccion estado, Pageable pageable) {

        Page<Transaccion> page;

        if (idPedido != null && estado != null) {
            page = transaccionRepository.findByIdPedidoAndEstado(idPedido, estado, pageable);
        } else if (idPedido != null) {
            page = transaccionRepository.findByIdPedido(idPedido, pageable);
        } else if (estado != null) {
            page = transaccionRepository.findByEstado(estado, pageable);
        } else {
            page = transaccionRepository.findAll(pageable);
        }

        return PageResponse.from(page.map(transaccionMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PagoResponse obtener(Integer id) {
        return transaccionMapper.toResponse(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorPedido(Integer idPedido) {
        return transaccionMapper.toResponseList(
                transaccionRepository.findByIdPedidoOrderByFechaTransaccionDesc(idPedido));
    }

    @Transactional
    public PagoResponse crear(PagoRequest request) {
        // 1. El pedido debe existir y estar pendiente de pago.
        PedidoResponse pedido = pedidoClient.obtenerPedido(request.getIdPedido());

        if (pedido.getEstado() == null || !"Pendiente".equalsIgnoreCase(pedido.getEstado())) {
            throw new BusinessRuleException(String.format(
                    "El pedido %d no esta pendiente de pago (estado actual: %s).",
                    request.getIdPedido(), pedido.getEstado()));
        }

        if (transaccionRepository.existsByIdPedidoAndEstado(
                request.getIdPedido(), EstadoTransaccion.Aprobado)) {
            throw new BusinessRuleException(
                    "El pedido ya tiene una transaccion aprobada.");
        }

        // 2. Crear la transaccion en estado Pendiente.
        Transaccion transaccion = transaccionMapper.toEntity(request);
        transaccion.setMonto(pedido.getTotal());
        transaccion.setEstado(EstadoTransaccion.Pendiente);
        transaccion.setFechaTransaccion(LocalDateTime.now());

        return transaccionMapper.toResponse(transaccionRepository.save(transaccion));
    }

    @Transactional
    public PagoResponse actualizarEstado(Integer id, EstadoTransaccion nuevoEstado) {
        Transaccion transaccion = buscar(id);
        EstadoTransaccion actual = transaccion.getEstado();

        if (actual == null) {
            throw new BusinessRuleException("La transaccion no tiene un estado valido.");
        }

        if (!TRANSICIONES.getOrDefault(actual, Set.of()).contains(nuevoEstado)) {
            throw new BusinessRuleException(String.format(
                    "Transicion de estado no permitida: %s -> %s.", actual, nuevoEstado));
        }

        if (nuevoEstado == EstadoTransaccion.Aprobado) {
            transaccion.setEstado(EstadoTransaccion.Aprobado);
            transaccion.setCodigoAutorizacion(generarCodigoAutorizacion());

            // Si ms-pedidos rechaza el cambio, la transaccion local hace rollback.
            pedidoClient.actualizarEstado(
                    transaccion.getIdPedido(),
                    EstadoUpdateRequest.builder().estado("Pagado").build());

        } else if (nuevoEstado == EstadoTransaccion.Anulado) {
            transaccion.setEstado(EstadoTransaccion.Anulado);

            pedidoClient.actualizarEstado(
                    transaccion.getIdPedido(),
                    EstadoUpdateRequest.builder().estado("Cancelado").build());

        } else {
            transaccion.setEstado(nuevoEstado);
        }

        return transaccionMapper.toResponse(transaccionRepository.save(transaccion));
    }

    @Transactional
    public void eliminar(Integer id) {
        Transaccion transaccion = buscar(id);

        if (transaccion.getEstado() == EstadoTransaccion.Aprobado
                || transaccion.getEstado() == EstadoTransaccion.Anulado) {
            throw new BusinessRuleException(
                    "No se puede eliminar una transaccion aprobada o anulada.");
        }

        transaccionRepository.delete(transaccion);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Transaccion buscar(Integer id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaccion", "ID", id));
    }

    private String generarCodigoAutorizacion() {
        return "AUTH-" + UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }
}
