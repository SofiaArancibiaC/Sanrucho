package cl.sanrucho.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private Integer id;
    private Integer idUsuario;
    private LocalDateTime fechaCompra;
    private BigDecimal total;
    private EstadoPedido estado;
    private List<DetallePedidoResponse> detalles;
}
