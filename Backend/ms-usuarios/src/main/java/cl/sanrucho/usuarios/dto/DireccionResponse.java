package cl.sanrucho.usuarios.dto;

import cl.sanrucho.usuarios.model.Direccion.TipoDireccion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DireccionResponse {

private Integer id;

private TipoDireccion tipo;

private String direccion;

private String ciudad;

private String region;

private String codigoPostal;

private String pais;

private Boolean esPrincipal;

private Integer usuarioId;

}

