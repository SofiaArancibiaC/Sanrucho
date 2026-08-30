package cl.sanrucho.pagos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.pagos.dto.ReembolsoRequest;
import cl.sanrucho.pagos.dto.ReembolsoResponse;
import cl.sanrucho.pagos.model.entity.Reembolso;

@Mapper(componentModel = "spring")
public interface ReembolsoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transaccion", ignore = true)
    @Mapping(target = "procesadoEn", ignore = true)
    Reembolso toEntity(ReembolsoRequest request);

    @Mapping(target = "transaccionId", ignore = true)
    ReembolsoResponse toResponse(Reembolso reembolso);
}
