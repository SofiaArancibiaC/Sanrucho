package cl.sanrucho.common.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Envelope unico de error para todas las APIs REST.
 * Todas las respuestas de error comparten esta estructura, lo que facilita
 * el consumo desde el frontend y desde otros microservicios.
 */
@Data
@Builder
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    /** Errores de campo para respuestas de validacion (HTTP 400). */
    private List<String> errors;

    /** Ruta URI que origino la peticion con error. */
    private String path;
}
