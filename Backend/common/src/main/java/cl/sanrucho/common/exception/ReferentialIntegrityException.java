package cl.sanrucho.common.exception;

/**
 * Se lanza cuando una operacion de borrado dejaria registros huerfanos.
 * El GlobalExceptionHandler la traduce a HTTP 409 CONFLICT.
 */
public class ReferentialIntegrityException extends RuntimeException {

    public ReferentialIntegrityException(String entity, Object id, String dependencies) {
        super(String.format("No se puede eliminar el registro de %s con ID '%s', "
                        + "porque tiene registros asociados en: %s. Para eliminarlo, primero "
                        + "debe eliminar o desvincular esos registros relacionados.",
                entity, id.toString(), dependencies));
    }
}
