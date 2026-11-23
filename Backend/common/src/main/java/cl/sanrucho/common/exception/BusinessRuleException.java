package cl.sanrucho.common.exception;

/**
 * Se lanza cuando una operacion viola una regla de negocio
 * (por ejemplo: stock insuficiente, precio invalido, transicion de estado
 * no permitida). El GlobalExceptionHandler la traduce a HTTP 400 BAD REQUEST.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
