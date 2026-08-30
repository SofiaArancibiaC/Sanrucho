package cl.sanrucho.pedidos.dto;

import java.time.LocalDateTime;

import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import lombok.Data;

@Data
public class ResumenPedidoResponse {

    private Integer id;
    private String numeroPedido;
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;
    private Integer total;
    private Integer cantidadItems;
}
