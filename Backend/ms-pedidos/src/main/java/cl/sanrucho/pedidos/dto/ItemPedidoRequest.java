package cl.sanrucho.pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemPedidoRequest {

   @NotNull
    private Integer productoId;

    @NotBlank
    @Size(max = 50)
    private String sku;

    @NotBlank
    @Size(max = 200)
    private String nombreProducto;

    private String personaje;

    @NotNull
    @Positive
    private Integer precioUnitario;

    @NotNull
    @Positive
    private Integer cantidad;
}
