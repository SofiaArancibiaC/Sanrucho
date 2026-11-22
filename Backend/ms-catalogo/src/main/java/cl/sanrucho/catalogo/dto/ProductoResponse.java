package cl.sanrucho.catalogo.dto;

import java.math.BigDecimal;

import cl.sanrucho.catalogo.model.enums.EstadoProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    private Integer id;
    private String codigo;
    private Integer idCategoria;
    private String nombreCategoria;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Boolean oferta;
    private BigDecimal precioOferta;
    private Integer descuento;
    private BigDecimal precioFinal;
    private Integer stock;
    private Integer stockCritico;
    private Boolean stockBajo;
    private String imagenUrl;
    private EstadoProducto estado;
    private CategoriaResponse categoria;
}
