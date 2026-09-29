package cl.sanrucho.carrito.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AgregarItemRequest {

    @NotNull(message = "El id es obligatorio.")
    @Positive(message = "El id no puede ser negativo.")
    private Integer idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad no puede ser negativa.")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio.")
    @Positive(message = "El precio unitario no puede ser negativo.")
    private Integer precioUnit;
    
}
