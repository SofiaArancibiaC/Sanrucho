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
public class NotificacionResponse {

    private Integer id;
    private Integer usuarioId;
    private String nombreUsuario;
    private String emailUsuario;
    private String tipo;
    private String titulo;
    private String mensaje;
    private String canal;
    private String prioridad;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaLectura;
    private Map<String, Object> metadata;
}
