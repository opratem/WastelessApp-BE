package com.wasteless.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        // Determine status code based on error message
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex.getMessage().contains("already saved") ||
                ex.getMessage().contains("already exists")) {
            status = HttpStatus.CONFLICT; // 409
        } else if (ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND; // 404
        } else if (ex.getMessage().contains("does not belong")) {
            status = HttpStatus.FORBIDDEN; // 403
        }

        body.put("status", status.value());
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }
}
