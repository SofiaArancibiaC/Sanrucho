package cl.sanrucho.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "ItemPedidoRequest", description = "Producto y cantidad solicitada dentro de un pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoRequest {

    @Schema(description = "ID del producto en ms-catalogo", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del producto es obligatorio")
    @Positive(message = "El ID del producto debe ser positivo")
    private Integer idProducto;

    @Schema(description = "Cantidad solicitada", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
