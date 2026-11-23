package cl.sanrucho.pedidos.dto;

import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoUpdateRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoPedido estado;
}
