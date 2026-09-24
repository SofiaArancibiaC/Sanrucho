package cl.sanrucho.usuarios.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import cl.sanrucho.usuarios.dto.UsuarioRequest;
import cl.sanrucho.usuarios.dto.UsuarioResponse;
import cl.sanrucho.usuarios.model.Usuario;

/**
 * Mapper MapStruct entre UsuarioRequest/Response y la entidad Usuario.
 *
 * - toModel: convierte el DTO de entrada a entidad para guardarla.
 * - toResponse: convierte la entidad a DTO de salida (el estado, al ser un
 *   enum en la entidad y un String en el Response, se traduce automaticamente).
 * - updateEntity: vuelca los datos del DTO sobre la entidad que YA existe en
 *   la base de datos (@MappingTarget), de modo que JPA detecta el cambio al
 *   terminar el metodo transaccionado y genera el UPDATE (Dirty Checking).
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    // El id lo genera la base de datos (@GeneratedValue), los campos de auditoria
    // (fechaRegistro/createdAt) y el nombre completo se gestionan en el service
    // o en la propia BD, y las relaciones no vienen en el request.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombreCompleto", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "direcciones", ignore = true)
    @Mapping(target = "eventos", ignore = true)
    Usuario toModel(UsuarioRequest request);

    // nombre_completo es una columna calculada por la BD; si aun no existe
    // (objeto recien creado), se arma con nombre + apellido.
    @Mapping(target = "nombreCompleto",
             expression = "java(usuario.getNombreCompleto() != null ? usuario.getNombreCompleto() : usuario.getNombre() + \" \" + usuario.getApellido())")
    UsuarioResponse toResponse(Usuario usuario);

    List<UsuarioResponse> toResponseList(List<Usuario> usuarios);

    // Actualizacion parcial sobre la entidad persistida: conserva id, fecha de
    // registro, createdAt, version, createdBy (auditoria) y las relaciones.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombreCompleto", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "direcciones", ignore = true)
    @Mapping(target = "eventos", ignore = true)
    void updateEntity(UsuarioRequest request, @MappingTarget Usuario usuario);
}
