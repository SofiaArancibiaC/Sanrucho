package cl.sanrucho.pagos.dto;

import cl.sanrucho.pagos.model.entity.MetodoPago.NombreMetodoPago;
import lombok.Data;

@Data
public class MetodoPagoResponse {

    private Integer id;
    private NombreMetodoPago nombreMetodoPago;
}
