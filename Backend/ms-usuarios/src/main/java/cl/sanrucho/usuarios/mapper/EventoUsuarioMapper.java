package cl.sanrucho.usuarios.mapper;

import cl.sanrucho.usuarios.dto.EventoUsuarioResponse;
import cl.sanrucho.usuarios.model.EventoUsuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventoUsuarioMapper {
@Mapping(source = "usuario.id", target = "usuarioId")

EventoUsuarioResponse toEventoUsuarioResponse(EventoUsuario eventoUsuario);

}
