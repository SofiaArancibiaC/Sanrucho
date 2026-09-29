package cl.sanrucho.notificaciones.mapper;

import java.util.List;
 
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
 
import cl.sanrucho.notificaciones.dto.EventoNotificacionRequest;
import cl.sanrucho.notificaciones.dto.EventoNotificacionResponse;
import cl.sanrucho.notificaciones.model.entity.EventoNotificacion;
 
@Mapper(componentModel = "spring")
public interface EventoNotificacionMapper {
 
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicado", ignore = true)
    @Mapping(target = "fechaEvento", ignore = true)
    @Mapping(target = "fechaPublicacion", ignore = true)
    EventoNotificacion toEntity(EventoNotificacionRequest dto);
 
    EventoNotificacionResponse toResponseDTO(EventoNotificacion entity);
 
    List<EventoNotificacionResponse> toResponseDTOList(List<EventoNotificacion> entities);
}
 