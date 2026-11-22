package cl.sanrucho.pagos.dto;

import cl.sanrucho.pagos.model.enums.EstadoTransaccion;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPagoRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoTransaccion estado;
}
