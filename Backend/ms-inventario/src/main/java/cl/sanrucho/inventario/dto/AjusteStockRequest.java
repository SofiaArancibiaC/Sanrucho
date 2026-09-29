package cl.sanrucho.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para el ajuste de inventario.
 *
 * A diferencia de los demas movimientos, el ajuste no suma ni resta: fija el
 * stock actual a un valor absoluto. Sirve para corregir conteos fisicos, por
 * ejemplo cuando un inventario cyclicico detecta que en bodega hay 8 unidades
 * pero el sistema marcaba 10.
 */
@Data
public class AjusteStockRequest {

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    private Integer stockActual;

    @Size(max = 100, message = "La referencia no puede superar los 100 caracteres")
    private String referencia;

    private String motivo;

    @Size(max = 100, message = "El campo realizadoPor no puede superar los 100 caracteres")
    private String realizadoPor;

}
