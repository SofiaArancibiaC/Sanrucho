package cl.sanrucho.reviews.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

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

    @Column(name = "fecha_creacion")
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

    @Column(name = "created_at")
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

    public enum EstadoReview {
        PUBLICADO,
        PENDIENTE,
        RECHAZADO,
        REPORTADO
    }
}
