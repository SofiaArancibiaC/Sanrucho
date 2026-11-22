package cl.sanrucho.pedidos.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @NotNull(message = "El usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser positivo")
    private Integer idUsuario;

    @NotEmpty(message = "El pedido debe tener al menos un producto")
    @Valid
    private List<ItemPedidoRequest> items;
}
