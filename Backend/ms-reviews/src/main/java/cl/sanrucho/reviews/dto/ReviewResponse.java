package cl.sanrucho.reviews.dto;

import cl.sanrucho.reviews.model.Review.EstadoReview;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReviewResponse extends RepresentationModel<ReviewResponse> {

    private Long id;
    private Integer productoId;
    private String sku;
    private String nombreProducto;
    private Integer usuarioId;
    private String nombreUsuario;
    private Integer pedidoId;
    private String numeroPedido;
    private Integer calificacion;
    private String titulo;
    private String comentario;
    private LocalDateTime fechaCreacion;
    private Boolean verificado;
    private EstadoReview estado;
    private Integer votosUtiles;
    private Integer votosNoUtiles;
    private LocalDateTime createdAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;
}
