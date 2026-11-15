package cl.sanrucho.common.exception;

/**
 * Se lanza cuando se busca una entidad que no existe.
 * El GlobalExceptionHandler la traduce a HTTP 404 NOT FOUND.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entity, String key, Object value) {
        super(String.format("No se encontraron registros en %s con el %s '%s'",
                entity, key, (value != null ? value.toString() : "N/A")));
    }
}
