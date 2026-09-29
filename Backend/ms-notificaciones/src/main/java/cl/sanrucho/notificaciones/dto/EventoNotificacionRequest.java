package cl.sanrucho.notificaciones.dto;

import java.util.Map;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class EventoNotificacionRequest {

    @NotBlank(message = "El eventId es obligatorio")
    @Size(max = 100)
    private String eventId;
 
    @NotNull(message = "El notificacionId es obligatorio")
    private Integer notificacionId;
 
    @NotBlank(message = "El tipo de evento es obligatorio")
    @Pattern(
        regexp = "notificacion_creada|notificacion_enviada|notificacion_fallida|notificacion_leida",
        message = "Tipo de evento no válido"
    )
    private String tipoEvento;
 
    @NotNull(message = "El payload es obligatorio")
    private Map<String, Object> payload;
    
}
