package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles @Valid annotation failures — returns field-level errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // Handles business logic errors from services
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "An error occurred";

        // Auth errors → 401
        if (msg.contains("Invalid credentials") || msg.contains("expired") || msg.contains("Invalid refresh")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", msg));
        }
        // Not found errors → 404
        if (msg.contains("not found") || msg.contains("Not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", msg));
        }
        // Everything else → 400
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }

    // Handles invalid enum values (e.g. unknown category string)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegal(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid value: " + ex.getMessage()));
    }
}
