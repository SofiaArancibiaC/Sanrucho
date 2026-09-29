package cl.sanrucho.reviews.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "respuestas_reviews",
    indexes = {
        @Index(name = "idx_respuestas_review", columnList = "review_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "nombre_usuario", length = 200)
    private String nombreUsuario;

    @Column(name = "es_vendedor")
    private Boolean esVendedor;

    @Column(name = "comentario", nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_respuesta", nullable = false, updatable = false)
    private LocalDateTime fechaRespuesta;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", insertable = false, updatable = false)
    private Review review;

    @PrePersist
    protected void onCreate() {
        this.fechaRespuesta = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        if (this.esVendedor == null) this.esVendedor = false;
    }
}
