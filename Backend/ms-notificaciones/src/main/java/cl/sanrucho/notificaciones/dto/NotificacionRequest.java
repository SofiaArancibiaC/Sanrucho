package cl.sanrucho.notificaciones.dto;

import java.util.Map;
 
import jakarta.validation.constraints.Email;
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
public class NotificacionRequest {

    @NotNull(message = "El usuarioId es obligatorio")
    private Integer usuarioId;
 
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 200)
    private String nombreUsuario;
 
    @NotBlank(message = "El email de usuario es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 255)
    private String emailUsuario;
 
    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
        regexp = "pedido_confirmado|pedido_enviado|pedido_entregado|cambio_precio|stock_disponible|promocion|review_respuesta|newsletter",
        message = "Tipo de notificación no válido"
    )
    private String tipo;
 
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200)
    private String titulo;
 
    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;
 
    @NotBlank(message = "El canal es obligatorio")
    @Pattern(regexp = "email|push|sms|in_app", message = "Canal no válido")
    private String canal;
 
    @Pattern(regexp = "baja|normal|alta|urgente", message = "Prioridad no válida")
    private String prioridad;
 
    private Map<String, Object> metadata;
}