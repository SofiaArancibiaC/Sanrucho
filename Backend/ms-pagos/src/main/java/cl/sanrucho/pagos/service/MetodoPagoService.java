package cl.sanrucho.pagos.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import cl.sanrucho.common.exception.EntityNotFoundException;
import cl.sanrucho.pagos.dto.MetodoPagoResponse;
import cl.sanrucho.pagos.mapper.MetodoPagoMapper;
import cl.sanrucho.pagos.model.entity.MetodoPago;
import cl.sanrucho.pagos.repository.MetodoPagoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetodoPagoService {
    private final MetodoPagoRepository metodoPagoRepository;
    private final MetodoPagoMapper metodoPagoMapper;


    public List<MetodoPagoResponse> listarMetodosPago() {
        return metodoPagoRepository.findAll()
                .stream()
                .map(metodoPagoMapper::toResponse)
                .toList();
    }

    public MetodoPagoResponse obtenerMetodoPagoPorId(Integer id) {
        MetodoPago metodo = metodoPagoRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException("MetodoPago", "ID", id));
        return metodoPagoMapper.toResponse(metodo);
    }

    public void eliminarMetodoPago(Integer id) {
        MetodoPago metodo = metodoPagoRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException("MetodoPago", "ID", id));
        metodoPagoRepository.delete(Objects.requireNonNull(metodo));
    }
}