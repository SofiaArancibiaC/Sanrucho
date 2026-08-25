package cl.sanrucho.carrito.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.carrito.dto.CarritoRequest;
import cl.sanrucho.carrito.dto.CarritoResponse;
import cl.sanrucho.carrito.model.entity.Carrito;

@Mapper(componentModel = "spring", uses = {ItemCarritoMapper.class} )
public interface CarritoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sesionId", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaUltimaModificacion", ignore = true)
    @Mapping(target = "fechaCierre", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "items", ignore = true)
    Carrito toEntity(CarritoRequest request);

    CarritoResponse toResponse(Carrito carrito);
}
