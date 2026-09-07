package cl.sanrucho.usuarios.mapper;
import cl.sanrucho.usuarios.dto.DireccionResponse;
import cl.sanrucho.usuarios.model.Direccion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DireccionMapper {

@Mapping(source = "usuario.id", target = "usuarioId")
DireccionResponse toDireccionResponse(Direccion direccion);

}

