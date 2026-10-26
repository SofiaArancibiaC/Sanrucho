package cl.sanrucho.pedidos.dto;

import java.time.LocalDateTime;

import cl.sanrucho.pedidos.model.enums.TipoEvento;
import lombok.Data;

@Data
public class EventoPedidoResponse {

    private Integer id;
    private String eventId;
    private Integer pedidoId;
    private String numeroPedido;
    private TipoEvento tipoEvento;
    private Boolean publicado;
    private LocalDateTime fechaEvento;
    private LocalDateTime fechaPublicacion;
}
