package cl.sanrucho.common.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Manejador global de excepciones para toda la API REST.
 *
 * Convierte las excepciones no controladas en respuestas HTTP coherentes
 * con el envelope {@link ApiError}. Centraliza el manejo de errores y
 * desacopla los controladores de esa responsabilidad.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── 401 UNAUTHORIZED ─────────────────────────────────────────────────────

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        return build(HttpStatus.UNAUTHORIZED,
                "No autenticado. Debe proporcionar un token JWT valido en el header Authorization.",
                null, request);
    }

    // ─── 403 FORBIDDEN ────────────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return build(HttpStatus.FORBIDDEN,
                "Acceso denegado. Su rol no tiene permisos para acceder a este recurso.",
                null, request);
    }

    // ─── 404 NOT FOUND ────────────────────────────────────────────────────────

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null, request);
    }

    @ExceptionHandler({
            org.springframework.web.servlet.NoHandlerFoundException.class,
            org.springframework.web.servlet.resource.NoResourceFoundException.class
    })
    public ResponseEntity<ApiError> handleEndpointNotFound(
            Exception ex,
            HttpServletRequest request) {

        return build(HttpStatus.NOT_FOUND, "El endpoint solicitado no existe.", null, request);
    }

    // ─── 409 CONFLICT ─────────────────────────────────────────────────────────

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        return build(HttpStatus.CONFLICT, ex.getMessage(), null, request);
    }

    @ExceptionHandler(ReferentialIntegrityException.class)
    public ResponseEntity<ApiError> handleReferentialIntegrity(
            ReferentialIntegrityException ex,
            HttpServletRequest request) {

        return build(HttpStatus.CONFLICT, ex.getMessage(), null, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.error("Violacion de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT,
                "La operacion viola una restriccion de integridad de la base de datos.",
                null, request);
    }

    // ─── 400 BAD REQUEST ──────────────────────────────────────────────────────

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(
            BusinessRuleException ex,
            HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> "'" + fe.getField() + "': " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        return build(HttpStatus.BAD_REQUEST,
                "La solicitud contiene datos invalidos. Revise el campo 'errors'.",
                fieldErrors, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> "'" + v.getPropertyPath() + "': " + v.getMessage())
                .collect(Collectors.toList());

        return build(HttpStatus.BAD_REQUEST,
                "La solicitud contiene parametros invalidos. Revise el campo 'errors'.",
                errors, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST,
                String.format("El parametro '%s' tiene un formato invalido.", ex.getName()),
                null, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonMalformed(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud (JSON) no es legible o tiene un formato invalido.",
                null, request);
    }

    // ─── ERRORES DE COMUNICACION ENTRE MICROSERVICIOS ─────────────────────────

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<ApiError> handleFeignException(
            feign.FeignException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String message;
        if (ex.status() == 404) {
            message = "El recurso solicitado no existe en el microservicio externo.";
        } else if (ex.status() == 400) {
            message = "Solicitud invalida enviada al microservicio externo. " + extraerDetalle(ex);
        } else if (ex.status() == 401 || ex.status() == 403) {
            message = "No fue posible autenticarse contra el microservicio externo. " + extraerDetalle(ex);
        } else {
            message = "Error en la comunicacion con un microservicio externo. " + extraerDetalle(ex);
        }

        log.error("Error Feign (status {}): {}", ex.status(), ex.getMessage());
        return build(status, message.trim(), null, request);
    }

    private String extraerDetalle(feign.FeignException ex) {
        String body = ex.contentUTF8();
        if (body == null || body.isBlank()) {
            return "";
        }
        return body.length() > 300 ? body.substring(0, 300) + "..." : body;
    }

    // ─── 500 INTERNAL SERVER ERROR ────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        if (ex instanceof org.springframework.web.servlet.NoHandlerFoundException
                || ex instanceof org.springframework.web.servlet.resource.NoResourceFoundException) {
            return build(HttpStatus.NOT_FOUND, "El endpoint solicitado no existe.", null, request);
        }

        log.error("Error interno no controlado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrio un error interno inesperado. Por favor, intente mas tarde.",
                null, request);
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            List<String> errors,
            HttpServletRequest request) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .errors(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}
