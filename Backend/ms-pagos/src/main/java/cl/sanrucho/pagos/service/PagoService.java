package cl.sanrucho.pagos.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.pagos.dto.ReembolsoRequest;
import cl.sanrucho.pagos.dto.ReembolsoResponse;
import cl.sanrucho.pagos.dto.TransaccionRequest;
import cl.sanrucho.pagos.dto.TransaccionResponse;
import cl.sanrucho.pagos.mapper.ReembolsoMapper;
import cl.sanrucho.pagos.mapper.TransaccionMapper;
import cl.sanrucho.pagos.model.entity.MetodoPago;
import cl.sanrucho.pagos.model.entity.Reembolso;
import cl.sanrucho.pagos.model.entity.Transaccion;
import cl.sanrucho.pagos.model.entity.Transaccion.EstadoTransaccion;
import cl.sanrucho.pagos.repository.MetodoPagoRepository;
import cl.sanrucho.pagos.repository.ReembolsoRepository;
import cl.sanrucho.pagos.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final TransaccionRepository transaccionRepository;
    private final ReembolsoRepository reembolsoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final TransaccionMapper transaccionMapper;
    private final ReembolsoMapper reembolsoMapper;

    //Transacciones 

    public TransaccionResponse crearTransaccion(TransaccionRequest request) {
        MetodoPago metodo = metodoPagoRepository.findById(Objects.requireNonNull(request.getMetodoId()))
                .orElseThrow(() -> new EntityNotFoundException("MetodoPago", "ID", request.getMetodoId()));

        Transaccion transaccion = transaccionMapper.toEntity(request);
        transaccion.setMetodo(metodo);
        transaccion.setEstado(EstadoTransaccion.PENDIENTE);

        transaccionRepository.save(transaccion);
        return transaccionMapper.toResponse(transaccion);
    }

    public TransaccionResponse obtenerTransaccionPorId(Integer id) {
        Transaccion transaccion = transaccionRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException("Transaccion", "ID", id));
        return transaccionMapper.toResponse(transaccion);
    }

    public List<TransaccionResponse> listarTransaccionesPorPedido(Integer pedidoId) {
        return transaccionRepository.findByPedidoId(pedidoId)
                .stream()
                .map(transaccionMapper::toResponse)
                .toList();
    }

    public TransaccionResponse actualizarEstado(Integer id, EstadoTransaccion nuevoEstado) {
        Transaccion transaccion = transaccionRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException("Transaccion", "ID", id));

        transaccion.setEstado(nuevoEstado);
        transaccionRepository.save(transaccion);
        return transaccionMapper.toResponse(transaccion);
    }

    //Reembolsos

    public ReembolsoResponse crearReembolso(ReembolsoRequest request) {
        Transaccion transaccion = transaccionRepository.findById(Objects.requireNonNull(request.getTransaccionId()))
                .orElseThrow(() -> new EntityNotFoundException("Transaccion", "ID", request.getTransaccionId()));

        Reembolso reembolso = reembolsoMapper.toEntity(request);
        reembolso.setTransaccion(transaccion);
        reembolsoRepository.save(reembolso);

        transaccion.setEstado(EstadoTransaccion.REEMBOLSADO);
        transaccionRepository.save(transaccion);

        return reembolsoMapper.toResponse(reembolso);
    }

    public List<ReembolsoResponse> listarReembolsosPorTransaccion(Integer transaccionId) {
        return reembolsoRepository.findByTransaccionId(transaccionId)
                .stream()
                .map(reembolsoMapper::toResponse)
                .toList();
    }

    public ReembolsoResponse obtenerReembolsoPorId(Integer id) {
        Reembolso reembolso = reembolsoRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException("Reembolso", "ID", id));
        return reembolsoMapper.toResponse(reembolso);
    }
}
