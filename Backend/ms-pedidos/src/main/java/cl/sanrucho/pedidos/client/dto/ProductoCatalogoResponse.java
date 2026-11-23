package cl.sanrucho.pedidos.client.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copia local minima del ProductoResponse de ms-catalogo.
 * Solo se declaran los campos que necesita ms-pedidos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCatalogoResponse {

    private Integer id;
    private String codigo;
    private Integer idCategoria;
    private String nombre;
    private BigDecimal precio;
    private Boolean oferta;
    private BigDecimal precioOferta;
    private BigDecimal precioFinal;
    private Integer stock;
    private Integer stockCritico;
    private String estado;
}
