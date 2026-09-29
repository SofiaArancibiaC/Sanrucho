package cl.sanrucho.reviews.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RespuestaReviewRequest {

    @NotNull(message = "El ID de la review es obligatorio")
    private Integer reviewId;

    private Integer usuarioId;

    @Size(max = 200, message = "El nombre del usuario no puede superar los 200 caracteres")
    private String nombreUsuario;

    private Boolean esVendedor;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
    private String comentario;
}
