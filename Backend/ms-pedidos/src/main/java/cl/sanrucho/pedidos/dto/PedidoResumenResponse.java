package cl.sanrucho.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cl.sanrucho.pedidos.model.enums.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "PedidoResumenResponse", description = "Pedido sin detalle, para listados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResumenResponse {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "3")
    private Integer idUsuario;

    @Schema(example = "2026-11-14T10:30:00")
    private LocalDateTime fechaCompra;

    @Schema(example = "39980")
    private BigDecimal total;

    @Schema(example = "Pendiente")
    private EstadoPedido estado;
}
