package cl.sanrucho.pedidos.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "PedidoRequest", description = "Datos para crear un pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @Schema(description = "ID del usuario que compra (referencia logica a la BD usuarios)",
            example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser positivo")
    private Integer idUsuario;

    @Schema(description = "Productos del pedido. No se permiten productos repetidos.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "El pedido debe incluir al menos un producto")
    @Valid
    private List<ItemPedidoRequest> items;
}
