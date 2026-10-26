package cl.sanrucho.pedidos.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PedidoRequest {

    @NotNull
    private Integer usuarioId;

    @NotBlank
    @Size(max = 200)
    private String nombreUsuario;

    @NotBlank
    @Email
    private String emailUsuario;

    private Integer descuento = 0;

    @NotNull
    private Integer costoEnvio;

    @NotNull
    private Integer direccionEnvioId;

    @NotBlank
    private String direccionEnvioSnapshot;

    @NotBlank
    @Size(max = 100)
    private String ciudadEnvio;

    @NotBlank
    @Size(max = 100)
    private String regionEnvio;

    @NotBlank
    @Size(max = 50)
    private String metodoPago;

    private String notas;

    @NotEmpty
    @Valid
    private List<ItemPedidoRequest> items;
}
