package cl.sanrucho.usuarios.dto;


import cl.sanrucho.usuarios.model.EventoUsuario.TipoEvento;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EventoUsuarioRequest {

@NotBlank(message = "El eventId es obligatorio")
@Size(max = 100)
private String eventId;

@NotNull(message = "El usuarioId es obligatorio")
private Integer usuarioId;

@NotNull(message = "El tipo de evento es obligatorio")
private TipoEvento tipoEvento;

@NotBlank(message = "El payload es obligatorio")
private String payload;

private Boolean publicado;

}
