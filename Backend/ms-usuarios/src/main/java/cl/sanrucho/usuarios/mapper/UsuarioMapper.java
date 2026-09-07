package cl.sanrucho.usuarios.mapper;

import cl.sanrucho.usuarios.dto.UsuarioResponse;
import cl.sanrucho.usuarios.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

UsuarioResponse toUsuarioResponse(Usuario usuario);

}

