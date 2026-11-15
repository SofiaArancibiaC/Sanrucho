package cl.sanrucho.pedidos.dto;

import java.math.BigDecimal;

import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "EstadoUpdateRequest", description = "Cambio de estado de un pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoUpdateRequest {

    @Schema(description = "Nuevo estado. Transiciones permitidas: Pendiente->Pagado/Cancelado, "
            + "Pagado->Enviado/Cancelado, Enviado->Entregado",
            example = "Pagado", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El estado es obligatorio")
    private EstadoPedido estado;
}
