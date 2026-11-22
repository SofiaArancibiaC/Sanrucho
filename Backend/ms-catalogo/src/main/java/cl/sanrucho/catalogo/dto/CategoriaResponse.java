package cl.sanrucho.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponse {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Long cantidadProductos;
}
