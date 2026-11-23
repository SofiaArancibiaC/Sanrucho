package cl.sanrucho.pagos.client.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copia local minima del PedidoResponse de ms-pedidos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private Integer id;
    private Integer idUsuario;
    private BigDecimal total;
    private String estado;
}
