package cl.sanrucho.pagos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransaccionRequest {

    @NotNull(message = "El id del pedido es obligatorio.")
    private Integer pedidoId;

    @NotNull(message = "El id del método de pago es obligatorio.")
    private Integer metodoId;

    @NotNull(message = "El monto es obligatorio.")
    @Positive(message = "El monto no puede ser negativo.")
    private Long monto;

}
