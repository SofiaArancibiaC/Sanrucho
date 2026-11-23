package cl.sanrucho.pagos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cl.sanrucho.pagos.model.enums.EstadoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {

    private Integer id;
    private Integer idPedido;
    private BigDecimal monto;
    private String pasarela;
    private EstadoTransaccion estado;
    private String codigoAutorizacion;
    private LocalDateTime fechaTransaccion;
}
