package com.sysco.product_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFoundException(ProductNotFoundException ex) {
        log.error("Product not found: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
            "status", HttpStatus.NOT_FOUND.value(),
            "message", ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductCreationException.class)
    public ResponseEntity<Map<String, Object>> handleProductCreationException(ProductCreationException ex) {
        log.error("Product creation failed: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "message", ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateSkuException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateSkuException(DuplicateSkuException ex) {
        Map<String, Object> response = Map.of(
                "status", HttpStatus.CONFLICT.value(),
                "message", ex.getMessage(),
                "error", "DUPLICATE_SKU"
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            org.springframework.dao.DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());

        String message = "Data integrity violation";
        if (ex.getMessage() != null && ex.getMessage().contains("sku")) {
            message = "SKU must be unique. This SKU already exists.";
        }

        Map<String, Object> response = Map.of(
                "status", HttpStatus.CONFLICT.value(),
                "message", message,
                "error", "DATA_INTEGRITY_VIOLATION"
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductNotApprovedException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotApprovedException(ProductNotApprovedException ex) {
        log.error("Product not approved: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
            "status", HttpStatus.FORBIDDEN.value(),
            "message", ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatusException(InvalidStatusException ex) {
        log.error("Invalid status: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "message", ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());
        
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMessage.append(fieldName).append(" - ").append(message).append("; ");
        });
        
        Map<String, Object> response = Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "message", errorMessage.toString()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidInputException(InvalidInputException ex) {
        log.error("Invalid input error: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "message", ex.getMessage(),
                "error", "INVALID_INPUT"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "message", ex.getMessage(),
                "error", "RESOURCE_NOT_FOUND"
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        log.error("Duplicate resource error: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
                "status", HttpStatus.CONFLICT.value(),
                "message", ex.getMessage(),
                "error", "DUPLICATE_RESOURCE"
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        log.error("Business rule violation: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "message", ex.getMessage(),
                "error", "BUSINESS_RULE_VIOLATION"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<Map<String, Object>> handleInternalServerException(InternalServerException ex) {
        log.error("Internal server error: {}", ex.getMessage());
        Map<String, Object> response = Map.of(
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "message", ex.getMessage(),
                "error", "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        Map<String, Object> response = Map.of(
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "message", "An unexpected error occurred"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}