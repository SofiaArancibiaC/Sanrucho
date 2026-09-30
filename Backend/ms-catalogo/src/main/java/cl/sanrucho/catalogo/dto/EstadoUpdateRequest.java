package cl.sanrucho.catalogo.dto;

import cl.sanrucho.catalogo.model.enums.EstadoProducto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoUpdateRequest {
    @NotNull(message = "El estado es obligatorio")
    private EstadoProducto estado;
}
