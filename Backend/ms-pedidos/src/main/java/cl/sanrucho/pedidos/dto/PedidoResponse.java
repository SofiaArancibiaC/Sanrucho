package cl.sanrucho.pedidos.dto;

import java.time.LocalDateTime;
import java.util.List;

import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import lombok.Data;

@Data
public class PedidoResponse {

    private Integer id;
    private String numeroPedido;
    private Integer usuarioId;
    private String nombreUsuario;
    private String emailUsuario;
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;
    private Integer subtotal;
    private Integer descuento;
    private Integer costoEnvio;
    private Integer total;
    private Integer direccionEnvioId;
    private String direccionEnvioSnapshot;
    private String ciudadEnvio;
    private String regionEnvio;
    private String metodoPago;
    private String notas;
    private LocalDateTime createdAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
    private List<ItemPedidoResponse> items;

}
