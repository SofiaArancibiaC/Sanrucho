package cl.sanrucho.catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "CategoriaResponse", description = "Categoria devuelta por la API")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Peluches")
    private String nombre;

    @Schema(example = "Los peluches mas tiernos del universo.")
    private String descripcion;

    @Schema(description = "Cantidad de productos asociados (referencia logica)", example = "5")
    private Long cantidadProductos;
}
