package edu.acc.neonark.backend.error;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fields
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(LocalDateTime.now().toString(), status, error, message, null);
    }

    public static ErrorResponse of(int status, String error, String message, Map<String, String> fields) {
        return new ErrorResponse(LocalDateTime.now().toString(), status, error, message, fields);
    }
}

