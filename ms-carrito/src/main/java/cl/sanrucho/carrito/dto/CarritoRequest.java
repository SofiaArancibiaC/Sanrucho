package cl.sanrucho.carrito.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CarritoRequest {

    @NotNull(message = "El usuarioId es obligatorio.")
    @Positive(message = "El usuarioId no puede ser negativo.")
    private Integer usuarioId;

}
