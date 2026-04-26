package edu.acc.neonark.backend.error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        HttpStatus status = ex.getStatus();
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), status.getReasonPhrase(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fields.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "Bad Request", "Request validation failed", fields));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableJson() {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "Bad Request", "Request body is missing or invalid JSON"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put(ex.getName(), "Invalid value. Expected " + expectedType(ex) + ".");
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "Bad Request", "Request parameter validation failed", fields));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(409, "Conflict", "Request conflicts with a database constraint"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "Internal Server Error", "An unexpected error occurred"));
    }

    private String expectedType(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        if (requiredType == null) {
            return "a valid value";
        }
        if (requiredType == Long.class) {
            return "a whole-number ID";
        }
        if (requiredType == Boolean.class || requiredType == boolean.class) {
            return "true or false";
        }
        return requiredType.getSimpleName();
    }
}
