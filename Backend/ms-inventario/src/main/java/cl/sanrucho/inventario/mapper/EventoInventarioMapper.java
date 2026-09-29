package cl.sanrucho.inventario.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cl.sanrucho.inventario.dto.EventoInventarioRequest;
import cl.sanrucho.inventario.dto.EventoInventarioResponse;
import cl.sanrucho.inventario.model.EventoInventario;

/**
 * Mapper MapStruct entre EventoInventarioRequest/Response y la entidad
 * EventoInventario.
 *
 * Las fechas las administra el service: fechaEvento marca cuando ocurrio el
 * hecho y fechaPublicacion cuando se difundo el evento.
 */
@Mapper(componentModel = "spring")
public interface EventoInventarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaEvento", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    EventoInventario toModel(EventoInventarioRequest request);

    EventoInventarioResponse toResponse(EventoInventario evento);

    List<EventoInventarioResponse> toResponseList(List<EventoInventario> eventos);

    // El eventId es la clave de idempotencia, por lo que no se modifica al actualizar.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "fechaEvento", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    void updateEntity(EventoInventarioRequest request, @MappingTarget EventoInventario evento);
}
