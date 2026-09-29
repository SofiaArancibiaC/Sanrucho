package cl.sanrucho.notificaciones.mapper;

import java.util.List;
 
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
 
import cl.sanrucho.notificaciones.dto.NotificacionRequest;
import cl.sanrucho.notificaciones.dto.NotificacionResponse;
import cl.sanrucho.notificaciones.model.entity.Notificacion;
 
@Mapper(componentModel = "spring")
public interface NotificacionMapper {
 
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaEnvio", ignore = true)
    @Mapping(target = "fechaLectura", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Notificacion toEntity(NotificacionRequest dto);
 
    NotificacionResponse toResponse(Notificacion entity);
 
    List<NotificacionResponse> toResponseList(List<Notificacion> entities);
}