package cl.sanrucho.usuarios.dto;


import cl.sanrucho.usuarios.model.Usuario.EstadoUsuario;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequest {

@NotBlank(message = "El email es obligatorio")
@Email(message = "El email debe tener un formato válido")
@Size(max = 255)
private String email;

@NotBlank(message = "El nombre es obligatorio")
@Size(max = 100)
private String nombre;

@NotBlank(message = "El apellido es obligatorio")
@Size(max = 100)
private String apellido;

@Size(max = 20)
private String telefono;

private EstadoUsuario estado;

@Size(max = 50)
private String personajeFavorito;

private Boolean notificacionesEmail;

private Boolean notificacionesPush;

@Size(max = 100)
private String createdBy;

@Size(max = 100)
private String updatedBy;

}

