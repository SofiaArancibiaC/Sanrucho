package cl.sanrucho.catalogo.dto;

import java.math.BigDecimal;

import cl.sanrucho.catalogo.model.enums.EstadoProducto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "ProductoResponse", description = "Producto devuelto por la API, enriquecido con su categoria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "HK-PLUSH-001")
    private String codigo;

    @Schema(example = "2")
    private Integer idCategoria;

    @Schema(example = "Peluches")
    private String nombreCategoria;

    @Schema(example = "Peluche Hello Kitty Clasico 30cm")
    private String nombre;

    private String descripcion;

    @Schema(example = "24990")
    private BigDecimal precio;

    @Schema(example = "true")
    private Boolean oferta;

    @Schema(example = "19990")
    private BigDecimal precioOferta;

    @Schema(example = "20")
    private Integer descuento;

    @Schema(description = "Precio efectivo: precioOferta si hay oferta, si no precio", example = "19990")
    private BigDecimal precioFinal;

    @Schema(example = "15")
    private Integer stock;

    @Schema(example = "3")
    private Integer stockCritico;

    @Schema(description = "true si stock <= stockCritico", example = "false")
    private Boolean stockBajo;

    @Schema(example = "/static/img/productos/hello-kitty1.webp")
    private String imagenUrl;

    @Schema(example = "Activo")
    private EstadoProducto estado;

    @Schema(description = "Categoria asociada (resuelta desde la BD categorias)")
    private CategoriaResponse categoria;
}
