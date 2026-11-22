package cl.sanrucho.catalogo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ajuste de stock. Cantidad positiva suma unidades; cantidad negativa
 * descuenta unidades (checkout de ms-pedidos).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateRequest {

    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
}
