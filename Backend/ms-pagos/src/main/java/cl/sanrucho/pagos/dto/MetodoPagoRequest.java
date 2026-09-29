package cl.sanrucho.pagos.dto;

import cl.sanrucho.pagos.model.entity.MetodoPago.NombreMetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MetodoPagoRequest {

    @NotNull(message = "El nombre del método de pago es obligatorio.")
    private NombreMetodoPago nombreMetodoPago;
}
