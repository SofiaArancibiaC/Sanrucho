package cl.sanrucho.pedidos.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copia local minima del UsuarioResponse de ms-usuarios.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Integer id;
    private String nombre;
    private String apellidos;
    private String email;
    private String rol;
}
