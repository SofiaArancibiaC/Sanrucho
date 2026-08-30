package cl.sanrucho.carrito.dto;

import lombok.Data;

@Data
public class ProductoResponse {

    private Integer idProducto;
    private String nombreProducto;
    private Integer precio;
}
