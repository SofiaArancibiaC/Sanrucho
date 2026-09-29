package cl.sanrucho.pagos.dto;

import cl.sanrucho.pagos.model.entity.Transaccion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReembolsoRequest {

    @NotNull(message = "El id de transaccion es obligatorio.")
    private Integer transaccionId;

    @NotNull(message = "El monto de reembolso es obligatorio.")
    @Positive(message = "El monto de reembolso no puede ser un número negativo.")
    private Long montoReembolso;

    @NotBlank(message = "El motivo es obligatorio.")
    private String motivo;
}
