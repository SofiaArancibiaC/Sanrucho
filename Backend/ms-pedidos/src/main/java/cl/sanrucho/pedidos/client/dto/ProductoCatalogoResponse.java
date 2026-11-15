package cl.sanrucho.pedidos.client.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copia local (contrato) de la respuesta de ms-catalogo.
 * Evita acoplar ms-pedidos a las clases internas del otro microservicio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCatalogoResponse {

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
    private String imagenUrl;
    private String estado;
}
