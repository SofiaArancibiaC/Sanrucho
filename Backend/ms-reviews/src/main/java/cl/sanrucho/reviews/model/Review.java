package cl.sanrucho.reviews.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "reviews",
    indexes = {
        @Index(name = "idx_reviews_producto", columnList = "producto_id"),
        @Index(name = "idx_reviews_usuario", columnList = "usuario_id"),
        @Index(name = "idx_reviews_pedido", columnList = "pedido_id"),
        @Index(name = "idx_reviews_estado", columnList = "estado"),
        @Index(name = "idx_reviews_calificacion", columnList = "calificacion"),
        @Index(name = "idx_reviews_verificado", columnList = "verificado")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Column(name = "nombre_producto", nullable = false, length = 200)
    private String nombreProducto;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "nombre_usuario", nullable = false, length = 200)
    private String nombreUsuario;

    @Column(name = "pedido_id", nullable = false)
    private Integer pedidoId;

    @Column(name = "numero_pedido", nullable = false, length = 50)
    private String numeroPedido;

    @Column(name = "calificacion", nullable = false)
    private Integer calificacion;

    @Column(name = "titulo", length = 200)
    private String titulo;

    @Lob
    @Column(name = "comentario")
    private String comentario;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "verificado")
    private Boolean verificado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoReview estado;

    @Column(name = "votos_utiles")
    private Integer votosUtiles;

    @Column(name = "votos_no_utiles")
    private Integer votosNoUtiles;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "version")
    private Integer version;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RespuestaReview> respuestas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        if (this.verificado == null) this.verificado = false;
        if (this.estado == null) this.estado = EstadoReview.PUBLICADO;
        if (this.votosUtiles == null) this.votosUtiles = 0;
        if (this.votosNoUtiles == null) this.votosNoUtiles = 0;
        if (this.version == null) this.version = 1;
    }

    public enum EstadoReview {
        PUBLICADO,
        PENDIENTE,
        RECHAZADO,
        REPORTADO
    }
}
