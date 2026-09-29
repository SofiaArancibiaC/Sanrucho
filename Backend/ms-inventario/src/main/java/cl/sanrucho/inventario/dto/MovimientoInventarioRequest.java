package cl.sanrucho.inventario.dto;

import cl.sanrucho.inventario.model.MovimientoInventario.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para registrar manualmente un movimiento de inventario.
 *
 * El service calcula cantidadAnterior y cantidadNueva a partir del stock real
 * del inventario, por lo que no se aceptan desde el request. Tampoco se acepta
 * fechaMovimiento: la genera el service.
 */
@Data
public class MovimientoInventarioRequest {

    @NotNull(message = "El inventarioId es obligatorio")
    private Integer inventarioId;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor que 0")
    private Integer cantidad;

    @Size(max = 100, message = "La referencia no puede superar los 100 caracteres")
    private String referencia;

    private String motivo;

    @Size(max = 100, message = "El campo realizadoPor no puede superar los 100 caracteres")
    private String realizadoPor;

}
