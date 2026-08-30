package cl.sanrucho.pagos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.pagos.dto.MetodoPagoRequest;
import cl.sanrucho.pagos.dto.MetodoPagoResponse;
import cl.sanrucho.pagos.model.entity.MetodoPago;

@Mapper(componentModel = "spring")
public interface MetodoPagoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", ignore = true)
    MetodoPago toEntity(MetodoPagoRequest request);

    @Mapping(target = "nombreMetodoPago", ignore = true)
    MetodoPagoResponse toResponse(MetodoPago metodo);
}