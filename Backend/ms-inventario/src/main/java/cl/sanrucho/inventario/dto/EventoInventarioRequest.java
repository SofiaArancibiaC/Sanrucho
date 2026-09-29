package cl.sanrucho.inventario.dto;

import cl.sanrucho.inventario.model.EventoInventario.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para registrar un evento de inventario.
 *
 * El eventId es la clave de idempotencia del evento: si se reintenta el mismo
 * evento con el mismo eventId, el service lo rechaza en vez de duplicarlo.
 */
@Data
public class EventoInventarioRequest {

    @NotBlank(message = "El eventId es obligatorio")
    @Size(max = 100, message = "El eventId no puede superar los 100 caracteres")
    private String eventId;

    @NotNull(message = "El productoId es obligatorio")
    private Integer productoId;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede superar los 50 caracteres")
    private String sku;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipoEvento;

    @NotBlank(message = "El payload es obligatorio")
    private String payload;

    private Boolean publicado;

}
