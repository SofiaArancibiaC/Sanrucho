package cl.sanrucho.reviews.dto;

import cl.sanrucho.reviews.model.EventoReview.TipoEvento;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EventoReviewRequest {

    @NotBlank(message = "El event_id es obligatorio")
    @Size(max = 100, message = "El event_id no puede superar los 100 caracteres")
    private String eventId;

    @NotNull(message = "El ID de la review es obligatorio")
    private Integer reviewId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Integer productoId;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipoEvento;

    @NotBlank(message = "El payload es obligatorio")
    private String payload;

    private Boolean publicado;
}
