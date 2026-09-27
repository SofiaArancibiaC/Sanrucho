package cl.sanrucho.notificaciones.mapper;

import java.util.List;
 
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
 
import cl.sanrucho.notificaciones.dto.ConfiguracionNotificacionesRequest;
import cl.sanrucho.notificaciones.dto.ConfiguracionNotificacionesResponse;
import cl.sanrucho.notificaciones.model.entity.ConfiguracionNotificaciones;
 
@Mapper(componentModel = "spring")
public interface ConfiguracionNotificacionesMapper {
 
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    ConfiguracionNotificaciones toEntity(ConfiguracionNotificacionesRequest dto);
 
    ConfiguracionNotificacionesResponse toResponse(ConfiguracionNotificaciones entity);
 
    List<ConfiguracionNotificacionesResponse> toResponseList(List<ConfiguracionNotificaciones> entities);
}