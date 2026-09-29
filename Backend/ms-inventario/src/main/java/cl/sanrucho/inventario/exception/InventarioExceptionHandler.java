package cl.sanrucho.inventario.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.sanrucho.common.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Maneja las excepciones propias del dominio de inventario.
 *
 * La libreria common ya cubre not found, duplicados e integridad referencial.
 * Aqui solo se agrega lo que inventario necesita y common no tiene: falta de
 * stock y rangos incoherentes, que son reglas de negocio y no errores de
 * infraestructura, asi que deben traducirse a un codigo HTTP util en vez de
 * terminar como un 500.
 *
 * Al ser mas especificas que el handler generico de common, Spring les da
 * prioridad de forma automatica.
 */
@RestControllerAdvice
public class InventarioExceptionHandler {

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ApiError> handleStockInsuficiente(
            StockInsuficienteException ex,
            HttpServletRequest request) {

        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(RangoStockInvalidoException.class)
    public ResponseEntity<ApiError> handleRangoStockInvalido(
            RangoStockInvalidoException ex,
            HttpServletRequest request) {

        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    private ResponseEntity<ApiError> construirRespuesta(
            HttpStatus status,
            String mensaje,
            HttpServletRequest request) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(mensaje)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}
