package cl.sanrucho.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para las operaciones de stock que mueven una cantidad:
 * entrada, salida, reserva y liberacion.
 *
 * El ajuste no usa este DTO porque fija un valor absoluto en vez de sumar o
 * restar: ese caso lo cubre AjusteStockRequest.
 */
@Data
public class OperacionStockRequest {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor que 0")
    private Integer cantidad;

    @Size(max = 100, message = "La referencia no puede superar los 100 caracteres")
    private String referencia;

    private String motivo;

    @Size(max = 100, message = "El campo realizadoPor no puede superar los 100 caracteres")
    private String realizadoPor;

}
