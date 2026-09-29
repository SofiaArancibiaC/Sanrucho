package cl.sanrucho.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para dar de alta o actualizar el inventario de un producto.
 *
 * El stock actual nunca se envia desde aqui: solo cambia a traves de los
 * movimientos (entrada, salida, ajuste, reserva y liberacion). El campo
 * stockInicial solo aplica al alta, donde el service lo registra como un
 * movimiento de tipo entrada para que quede trazabilidad desde el primer dia.
 */
@Data
public class InventarioRequest {

    @NotNull(message = "El productoId es obligatorio")
    private Integer productoId;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede superar los 50 caracteres")
    private String sku;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200, message = "El nombre del producto no puede superar los 200 caracteres")
    private String nombreProducto;

    @Min(value = 0, message = "El stock inicial no puede ser negativo")
    private Integer stockInicial;

    @NotNull(message = "El stock minimo es obligatorio")
    @Min(value = 0, message = "El stock minimo no puede ser negativo")
    private Integer stockMinimo;

    @NotNull(message = "El stock maximo es obligatorio")
    @Min(value = 0, message = "El stock maximo no puede ser negativo")
    private Integer stockMaximo;

    @Size(max = 50, message = "La ubicacion de bodega no puede superar los 50 caracteres")
    private String ubicacionBodega;

}
