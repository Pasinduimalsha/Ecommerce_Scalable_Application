package com.sysco.order_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String STATUS_KEY = "status";
    private static final String MESSAGE_KEY = "message";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String PATH_KEY = "path";

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCartNotFoundException(CartNotFoundException ex) {
        log.error("Cart not found: {}", ex.getMessage());
        Map<String, Object> errorResponse = Map.of(
                STATUS_KEY, HttpStatus.NOT_FOUND.value(),
                MESSAGE_KEY, ex.getMessage(),
                TIMESTAMP_KEY, LocalDateTime.now().toString(),
                PATH_KEY, "/api/v1/order"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleItemNotFoundException(ItemNotFoundException ex) {
        log.error("Item not found: {}", ex.getMessage());
        Map<String, Object> errorResponse = Map.of(
                STATUS_KEY, HttpStatus.NOT_FOUND.value(),
                MESSAGE_KEY, ex.getMessage(),
                TIMESTAMP_KEY, LocalDateTime.now().toString(),
                PATH_KEY, "/api/v1/order"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CartAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCartAlreadyExistsException(CartAlreadyExistsException ex) {
        log.error("Cart already exists: {}", ex.getMessage());
        Map<String, Object> errorResponse = Map.of(
                STATUS_KEY, HttpStatus.CONFLICT.value(),
                MESSAGE_KEY, ex.getMessage(),
                TIMESTAMP_KEY, LocalDateTime.now().toString(),
                PATH_KEY, "/api/v1/order"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorResponse = Map.of(
                STATUS_KEY, HttpStatus.BAD_REQUEST.value(),
                MESSAGE_KEY, "Validation failed",
                "validationErrors", validationErrors,
                TIMESTAMP_KEY, LocalDateTime.now().toString(),
                PATH_KEY, "/api/v1/order"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = Map.of(
                STATUS_KEY, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                MESSAGE_KEY, "An unexpected error occurred",
                TIMESTAMP_KEY, LocalDateTime.now().toString(),
                PATH_KEY, "/api/v1/order"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}