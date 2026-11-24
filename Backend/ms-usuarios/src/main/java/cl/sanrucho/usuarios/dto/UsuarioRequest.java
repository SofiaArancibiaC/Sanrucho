package cl.sanrucho.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    // Vacía = no cambiar la contraseña (útil al editar un usuario).
    @Pattern(regexp = "^$|^.{4,10}$", message = "La contraseña debe tener entre 4 y 10 caracteres, o dejarse vacía para no cambiarla")
    private String password;

    private java.time.LocalDate fechaNacimiento;

    private Integer regionId;

    private Integer comunaId;

    private String direccion;

    private String telefono;

    private String estado;

    private Rol rol;
}
