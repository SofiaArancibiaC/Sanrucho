package cl.sanrucho.inventario.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta del inventario de un producto.
 *
 * stockDisponible y alertaStockBajo los calcula el service, nunca el cliente:
 * stockDisponible = stockActual - stockReservado, y la alerta se enciende
 * cuando el disponible queda en o por debajo del stock minimo.
 */
@Data
@Builder
public class InventarioResponse {

    private Integer id;

    private Integer productoId;

    private String sku;

    private String nombreProducto;

    private Integer stockActual;

    private Integer stockReservado;

    private Integer stockDisponible;

    private Integer stockMinimo;

    private Integer stockMaximo;

    private String ubicacionBodega;

    private Boolean alertaStockBajo;

    private LocalDateTime createdAt;

    private Integer version;

}
