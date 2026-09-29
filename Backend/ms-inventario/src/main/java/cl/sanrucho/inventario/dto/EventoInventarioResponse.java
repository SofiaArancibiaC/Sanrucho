package cl.sanrucho.inventario.dto;

import java.time.LocalDateTime;

import cl.sanrucho.inventario.model.EventoInventario.TipoEvento;
import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta de un evento de inventario.
 *
 * publicado=false significa que el evento quedo registrado pero todavia no se
 * difundo a los demas microservicios. Ese es el mecanismo de outbox: el evento
 * se persiste primero en la misma transaccion que el movimiento de stock, y
 * se publica despues.
 */
@Data
@Builder
public class EventoInventarioResponse {

    private Integer id;

    private String eventId;

    private Integer productoId;

    private String sku;

    private TipoEvento tipoEvento;

    private String payload;

    private Boolean publicado;

    private LocalDateTime fechaEvento;

    private LocalDateTime fechaPublicacion;

}
