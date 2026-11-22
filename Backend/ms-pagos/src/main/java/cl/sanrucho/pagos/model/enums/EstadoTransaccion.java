package cl.sanrucho.pagos.model.enums;

/**
 * Estados de una transaccion de pago:
 *
 * Pendiente -> Aprobado | Rechazado
 * Aprobado  -> Anulado
 * Rechazado -> (final)
 * Anulado   -> (final)
 */
public enum EstadoTransaccion {
    Pendiente,
    Aprobado,
    Rechazado,
    Anulado
}
