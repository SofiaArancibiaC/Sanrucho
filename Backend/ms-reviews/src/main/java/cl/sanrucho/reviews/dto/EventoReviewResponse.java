package cl.sanrucho.reviews.dto;

import cl.sanrucho.reviews.model.EventoReview.TipoEvento;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class EventoReviewResponse extends RepresentationModel<EventoReviewResponse> {

    private Long id;
    private String eventId;
    private Integer reviewId;
    private Integer productoId;
    private TipoEvento tipoEvento;
    private String payload;
    private Boolean publicado;
    private LocalDateTime fechaEvento;
    private LocalDateTime fechaPublicacion;
}
