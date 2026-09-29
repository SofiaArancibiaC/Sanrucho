package cl.sanrucho.reviews.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class RespuestaReviewResponse extends RepresentationModel<RespuestaReviewResponse> {

    private Long id;
    private Integer reviewId;
    private Integer usuarioId;
    private String nombreUsuario;
    private Boolean esVendedor;
    private String comentario;
    private LocalDateTime fechaRespuesta;
    private LocalDateTime createdAt;
}
