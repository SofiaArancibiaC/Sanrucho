package cl.sanrucho.usuarios.dto;

import cl.sanrucho.usuarios.model.EventoUsuario.TipoEvento;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventoUsuarioResponse {

private Integer id;

private String eventId;

private Integer usuarioId;

private TipoEvento tipoEvento;

private String payload;

private Boolean publicado;

private LocalDateTime fechaEvento;

private LocalDateTime fechaPublicacion;

}

