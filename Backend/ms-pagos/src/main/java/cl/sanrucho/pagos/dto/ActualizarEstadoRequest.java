package cl.sanrucho.pagos.dto;

import cl.sanrucho.pagos.model.entity.Transaccion.EstadoTransaccion;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoRequest {
    
    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoTransaccion nuevoEstado;
}
