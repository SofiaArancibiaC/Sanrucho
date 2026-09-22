package cl.sanrucho.reviews.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_review",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_eventos_review_event_id", columnNames = "event_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "event_id", nullable = false, length = 100, unique = true)
    private String eventId;

    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private TipoEvento tipoEvento;

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "publicado")
    private Boolean publicado;

    @Column(name = "fecha_evento")
    private LocalDateTime fechaEvento;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    public enum TipoEvento {
        REVIEW_CREADA,
        REVIEW_ACTUALIZADA,
        REVIEW_RESPONDIDA,
        REVIEW_REPORTADA,
        REVIEW_ELIMINADA
    }
}
