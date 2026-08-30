package cl.sanrucho.pagos.dto;

import java.time.LocalDateTime;

import cl.sanrucho.pagos.model.entity.MetodoPago.NombreMetodoPago;
import cl.sanrucho.pagos.model.entity.Transaccion.EstadoTransaccion;
import lombok.Data;

@Data
public class TransaccionResponse {

    private Integer id;
    private Integer pedidoId;
    private Long monto;
    private EstadoTransaccion estado;
    private LocalDateTime creadoEn;
    private NombreMetodoPago nombreMetodoPago;
}
