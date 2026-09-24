package cl.sanrucho.usuarios.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cl.sanrucho.usuarios.dto.EventoUsuarioRequest;
import cl.sanrucho.usuarios.dto.EventoUsuarioResponse;
import cl.sanrucho.usuarios.model.EventoUsuario;

/**
 * Mapper MapStruct entre EventoUsuarioRequest/Response y la entidad EventoUsuario.
 *
 * El usuario se resuelve en el service a partir de usuarioId; las fechas
 * (fechaEvento/fechaPublicacion) se generan en el service, y el eventId es
 * la identidad del evento por lo que no se modifica en una actualizacion.
 */
@Mapper(componentModel = "spring")
public interface EventoUsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaEvento", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    EventoUsuario toModel(EventoUsuarioRequest request);

    @Mapping(target = "usuarioId", source = "usuario.id")
    EventoUsuarioResponse toResponse(EventoUsuario evento);

    List<EventoUsuarioResponse> toResponseList(List<EventoUsuario> eventos);

    // Actualizacion sobre la entidad persistida (Dirty Checking).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "fechaEvento", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    void updateEntity(EventoUsuarioRequest request, @MappingTarget EventoUsuario evento);
}
