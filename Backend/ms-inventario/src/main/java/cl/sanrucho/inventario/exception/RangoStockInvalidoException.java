package cl.sanrucho.inventario.exception;

/**
 * Se lanza cuando el inventario se configura con parametros incoherentes,
 * por ejemplo cuando stockMinimo es mayor que stockMaximo.
 */
public class RangoStockInvalidoException extends RuntimeException {

    public RangoStockInvalidoException(int stockMinimo, int stockMaximo) {
        super(String.format("El rango de stock es invalido: el stock minimo (%d) "
            + "no puede ser mayor que el stock maximo (%d).",
            stockMinimo, stockMaximo));
    }
}
