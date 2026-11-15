package cl.sanrucho.common.exception;

/**
 * Se lanza cuando se intenta crear o actualizar un recurso con un valor
 * que debe ser unico y ya existe.
 * El GlobalExceptionHandler la traduce a HTTP 409 CONFLICT.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String entity, String field, Object value, String description) {
        super(String.format("%s con %s igual a '%s' ya existe en el sistema, descrito por '%s'.",
                entity, field, (value != null ? value.toString() : "N/A"), description));
    }
}
