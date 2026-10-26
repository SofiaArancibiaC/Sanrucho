package cl.sanrucho.pedidos.dto;

import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstadoUpdateRequest {

     @NotNull
    private EstadoPedido estado;

    private String notas;

    @NotNull
    private String updatedBy;
}
