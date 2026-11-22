package cl.sanrucho.pagos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.pagos.dto.PagoRequest;
import cl.sanrucho.pagos.dto.PagoResponse;
import cl.sanrucho.pagos.model.entity.Transaccion;

@Mapper(componentModel = "spring")
public interface TransaccionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "monto", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "codigoAutorizacion", ignore = true)
    @Mapping(target = "fechaTransaccion", ignore = true)
    Transaccion toEntity(PagoRequest request);

    PagoResponse toResponse(Transaccion transaccion);

    List<PagoResponse> toResponseList(List<Transaccion> transacciones);
}
