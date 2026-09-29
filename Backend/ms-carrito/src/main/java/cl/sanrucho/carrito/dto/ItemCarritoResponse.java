package cl.sanrucho.carrito.dto;

import lombok.Data;

@Data
public class ItemCarritoResponse {
    private Integer id;
    private Integer productoId;
    private Integer cantidad;
    private Integer precioUnit;
}
