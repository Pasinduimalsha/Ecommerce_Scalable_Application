package com.sysco.inventory_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String STATUS_KEY = "status";
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @ExceptionHandler(InventoryAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleInventoryAlreadyExistsException(InventoryAlreadyExistsException ex) {
        log.error("Inventory already exists: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
            TIMESTAMP_KEY, LocalDateTime.now(),
            STATUS_KEY, HttpStatus.CONFLICT.value(),
            ERROR_KEY, "Inventory Already Exists",
            MESSAGE_KEY, ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleInventoryNotFoundException(InventoryNotFoundException ex) {
        log.error("Inventory not found: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
            TIMESTAMP_KEY, LocalDateTime.now(),
            STATUS_KEY, HttpStatus.NOT_FOUND.value(),
            ERROR_KEY, "Inventory Not Found",
            MESSAGE_KEY, ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatusException(InvalidStatusException ex) {
        log.error("Invalid status: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
            TIMESTAMP_KEY, LocalDateTime.now(),
            STATUS_KEY, HttpStatus.BAD_REQUEST.value(),
            ERROR_KEY, "Invalid Status",
            MESSAGE_KEY, ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
            TIMESTAMP_KEY, LocalDateTime.now(),
            STATUS_KEY, HttpStatus.BAD_REQUEST.value(),
            ERROR_KEY, "Bad Request",
            MESSAGE_KEY, ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());
        
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMessage.append(fieldName).append(" - ").append(message).append("; ");
        });
        
        Map<String, Object> response = Map.of(
            TIMESTAMP_KEY, LocalDateTime.now(),
            STATUS_KEY, HttpStatus.BAD_REQUEST.value(),
            ERROR_KEY, "Validation Failed",
            MESSAGE_KEY, errorMessage.toString()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        Map<String, Object> response = Map.of(
            TIMESTAMP_KEY, LocalDateTime.now(),
            STATUS_KEY, HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ERROR_KEY, "Internal Server Error",
            MESSAGE_KEY, "An unexpected error occurred"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}