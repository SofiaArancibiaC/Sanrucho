package cl.sanrucho.usuarios.dto;

import java.time.LocalDateTime;

import cl.sanrucho.usuarios.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Integer idUsuario;

    private String run;

    private String nombre;

    private String apellidos;

    private String email;

    private LocalDateTime fechaRegistro;

    private java.time.LocalDate fechaNacimiento;

    private Integer regionId;

    private Integer comunaId;

    private String direccion;

    private String telefono;

    private String estado;

    private Rol rol;
}
