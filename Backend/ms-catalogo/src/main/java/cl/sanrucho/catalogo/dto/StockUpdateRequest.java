package cl.sanrucho.catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "StockUpdateRequest",
        description = "Ajuste de stock. Cantidad positiva suma unidades; cantidad negativa descuenta unidades.")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {

    @Schema(description = "Unidades a sumar (positivo) o restar (negativo)", example = "-2",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
}
