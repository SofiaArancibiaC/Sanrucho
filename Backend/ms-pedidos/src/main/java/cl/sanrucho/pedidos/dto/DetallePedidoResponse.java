package cl.sanrucho.pedidos.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "DetallePedidoResponse", description = "Linea de detalle de un pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoResponse {

    @Schema(example = "1")
    private Integer idDetalle;

    @Schema(example = "1")
    private Integer idProducto;

    @Schema(example = "Peluche Hello Kitty Clasico 30cm")
    private String nombreProducto;

    @Schema(example = "2")
    private Integer cantidad;

    @Schema(description = "Precio unitario congelado al momento de la compra", example = "19990")
    private BigDecimal precioUnitario;

    @Schema(description = "cantidad * precioUnitario", example = "39980")
    private BigDecimal subtotal;
}
