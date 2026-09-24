package cl.sanrucho.usuarios.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cl.sanrucho.usuarios.dto.DireccionRequest;
import cl.sanrucho.usuarios.dto.DireccionResponse;
import cl.sanrucho.usuarios.model.Direccion;

/**
 * Mapper MapStruct entre DireccionRequest/Response y la entidad Direccion.
 *
 * La relacion con Usuario (ManyToOne) no se copia desde el DTO: el service
 * busca el usuario por el usuarioId y se lo asigna a la entidad.
 * En la respuesta, usuarioId se obtiene de la entidad anidada usuario.id.
 */
@Mapper(componentModel = "spring")
public interface DireccionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Direccion toModel(DireccionRequest request);

    @Mapping(target = "usuarioId", source = "usuario.id")
    DireccionResponse toResponse(Direccion direccion);

    List<DireccionResponse> toResponseList(List<Direccion> direcciones);

    // Actualizacion sobre la entidad persistida (Dirty Checking): cambian los
    // datos de la direccion, nunca el id, el usuario ni la auditoria.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(DireccionRequest request, @MappingTarget Direccion direccion);
}
