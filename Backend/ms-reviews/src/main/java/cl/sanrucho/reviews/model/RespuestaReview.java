package cl.sanrucho.reviews.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "respuestas_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "nombre_usuario", length = 200)
    private String nombreUsuario;

    @Column(name = "es_vendedor")
    private Boolean esVendedor;

    @Lob
    @Column(name = "comentario", nullable = false)
    private String comentario;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
