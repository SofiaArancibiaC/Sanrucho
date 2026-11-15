package cl.sanrucho.pedidos.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Contrato del ajuste de stock expuesto por ms-catalogo. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "StockUpdateRequest", description = "Ajuste de stock enviado a ms-catalogo")
public class StockUpdateRequest {

    @Schema(description = "Unidades a sumar (positivo) o restar (negativo)", example = "-2")
    @NotNull
    private Integer cantidad;
}
