package cl.sanrucho.pedidos.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ItemPedidoResponse {

     private Integer id;
    private Integer productoId;
    private String sku;
    private String nombreProducto;
    private String personaje;
    private Integer precioUnitario;
    private Integer cantidad;
    private Integer subtotal;
    private LocalDateTime createdAt;

}
