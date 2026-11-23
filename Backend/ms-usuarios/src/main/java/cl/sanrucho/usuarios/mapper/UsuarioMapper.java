package cl.sanrucho.usuarios.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cl.sanrucho.usuarios.dto.UsuarioRequest;
import cl.sanrucho.usuarios.dto.UsuarioResponse;
import cl.sanrucho.usuarios.model.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    Usuario toEntity(UsuarioRequest request);

    UsuarioResponse toResponse(Usuario usuario);
}
