package cl.sanrucho.usuarios.dto;


import cl.sanrucho.usuarios.model.Direccion.TipoDireccion;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DireccionRequest {

@NotNull(message = "El usuarioId es obligatorio")
private Integer usuarioId;

@NotNull(message = "El tipo de dirección es obligatorio")
private TipoDireccion tipo;

@NotBlank(message = "La dirección es obligatoria")
private String direccion;

@NotBlank(message = "La ciudad es obligatoria")
@Size(max = 100)
private String ciudad;

@NotBlank(message = "La región es obligatoria")
@Size(max = 100)
private String region;

@Size(max = 20)
private String codigoPostal;

@Size(max = 100)
private String pais;

@NotNull(message = "Debe indicar si es dirección principal")
private Boolean esPrincipal;

}
