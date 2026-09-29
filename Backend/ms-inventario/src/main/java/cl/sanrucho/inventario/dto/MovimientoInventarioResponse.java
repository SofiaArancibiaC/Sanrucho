package cl.sanrucho.inventario.dto;

import java.time.LocalDateTime;

import cl.sanrucho.inventario.model.MovimientoInventario.TipoMovimiento;
import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta de un movimiento de inventario.
 *
 * inventarioId se aplana desde la relacion con Inventario para que el cliente
 * no tenga que navegar el objeto anidado.
 */
@Data
@Builder
public class MovimientoInventarioResponse {

    private Integer id;

    private Integer inventarioId;

    private TipoMovimiento tipoMovimiento;

    private Integer cantidad;

    private Integer cantidadAnterior;

    private Integer cantidadNueva;

    private String referencia;

    private String motivo;

    private String realizadoPor;

    private LocalDateTime fechaMovimiento;

    private LocalDateTime createdAt;

}
