package cl.sanrucho.pagos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {

    @NotNull(message = "El pedido es obligatorio")
    @Positive(message = "El ID del pedido debe ser positivo")
    private Integer idPedido;

    @NotBlank(message = "La pasarela es obligatoria")
    @Size(max = 50, message = "La pasarela no puede superar los 50 caracteres")
    private String pasarela;
}
