package cl.sanrucho.pagos.dto;

import java.time.LocalDateTime;

import cl.sanrucho.pagos.model.entity.Transaccion;
import lombok.Data;

@Data
public class ReembolsoResponse {

    private Integer id;
    private Transaccion transaccionId;
    private Long montoReembolso;
    private String motivo;
    private LocalDateTime procesadoEn;
}
