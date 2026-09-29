package cl.sanrucho.inventario.exception;

/**
 * Se lanza cuando una operacion de stock no se puede ejecutar porque no hay
 * unidades suficientes, ya sea fisicas o disponibles.
 *
 * Ejemplos: una salida de 5 unidades con stockActual 3, o una reserva de 10
 * unidades cuando el stock disponible es de 4.
 */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String producto, int solicitado, int disponible) {
        super(String.format("No se puede completar la operacion sobre '%s': "
            + "se solicitaron %d unidades pero solo hay %d disponibles.",
            producto, solicitado, disponible));
    }
}
