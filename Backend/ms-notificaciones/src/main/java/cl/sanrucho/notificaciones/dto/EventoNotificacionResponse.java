package cl.sanrucho.notificaciones.dto;

import java.time.LocalDateTime;
import java.util.Map;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoNotificacionResponse {
    
    private Integer id;
    private String eventId;
    private Integer notificacionId;
    private String tipoEvento;
    private Map<String, Object> payload;
    private Boolean publicado;
    private LocalDateTime fechaEvento;
    private LocalDateTime fechaPublicacion;
}
