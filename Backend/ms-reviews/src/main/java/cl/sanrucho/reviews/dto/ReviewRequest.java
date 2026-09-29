package cl.sanrucho.reviews.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private Integer productoId;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede superar los 50 caracteres")
    private String sku;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200, message = "El nombre del producto no puede superar los 200 caracteres")
    private String nombreProducto;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer usuarioId;

    @NotBlank(message = "El nombre del usuario es obligatorio")
    @Size(max = 200, message = "El nombre del usuario no puede superar los 200 caracteres")
    private String nombreUsuario;

    @NotNull(message = "El ID del pedido es obligatorio")
    private Integer pedidoId;

    @NotBlank(message = "El número de pedido es obligatorio")
    @Size(max = 50, message = "El número de pedido no puede superar los 50 caracteres")
    private String numeroPedido;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación debe ser al menos 1")
    @Max(value = 5, message = "La calificación no puede superar 5")
    private Integer calificacion;

    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    private String titulo;

    @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
    private String comentario;
}
