package cl.sanrucho.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import cl.sanrucho.usuarios.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    private String run;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres")
    private String password;

    private java.time.LocalDate fechaNacimiento;

    private Integer regionId;

    private Integer comunaId;

    private String direccion;

    private String telefono;

    private String estado;

    private Rol rol;
}
