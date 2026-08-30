package cl.sanrucho.usuarios.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponse {

private Integer id;

private String email;

private String nombre;

private String apellido;

private String nombreCompleto;

private String telefono;

private LocalDateTime fechaRegistro;

private String estado;

private String personajeFavorito;

private Boolean notificacionesEmail;

private Boolean notificacionesPush;

private LocalDateTime createdAt;

private String createdBy;

private String updatedBy;

private Integer version;

}
