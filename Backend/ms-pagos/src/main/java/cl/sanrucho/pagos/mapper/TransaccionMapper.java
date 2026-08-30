package cl.sanrucho.pagos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.pagos.dto.TransaccionRequest;
import cl.sanrucho.pagos.dto.TransaccionResponse;
import cl.sanrucho.pagos.model.entity.Transaccion;

@Mapper (componentModel = "spring")
public interface TransaccionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "metodo", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    Transaccion toEntity(TransaccionRequest request);

    @Mapping(source = "metodo.nombre", target = "nombreMetodoPago")
    TransaccionResponse toResponse(Transaccion transaccion);
}
