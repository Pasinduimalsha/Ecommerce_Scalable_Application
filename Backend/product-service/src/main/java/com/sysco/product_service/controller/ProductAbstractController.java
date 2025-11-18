package com.sysco.product_service.controller;

import com.sysco.product_service.dto.ProductDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Map;

@SuppressWarnings("ALL")
@Log4j2
@Controller
public abstract class ProductAbstractController {
    
    // Constants to avoid code duplication
    protected static final String STATUS_KEY = "status";
    protected static final String MESSAGE_KEY = "message";
    protected static final String DATA_KEY = "data";
    protected static final String TIMESTAMP_KEY = "timestamp";
    protected static final String SERVICE_KEY = "service";
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            STATUS_KEY, "UP",
            SERVICE_KEY, "Product Service",
            TIMESTAMP_KEY, LocalDateTime.now().toString()
        ));
    }

    protected <T> ResponseEntity<Map<String, Object>> sendSuccessResponse(T response, String message) {
        // For success responses, we'll wrap the data with status and message
        // Handle null response properly
        Map<String, Object> result;
        if (response != null) {
            result = Map.of(
                STATUS_KEY, HttpStatus.OK.value(),
                MESSAGE_KEY, message,
                DATA_KEY, response
            );
        } else {
            result = Map.of(
                STATUS_KEY, HttpStatus.OK.value(),
                MESSAGE_KEY, message
            );
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    protected <T> ResponseEntity<Map<String, Object>> sendCreatedResponse(T response, String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.CREATED.value(),
            MESSAGE_KEY, message,
            DATA_KEY, response
        );
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    protected <T> ResponseEntity<T> sendAcceptedResponse(T response, String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.ACCEPTED.value(),
            MESSAGE_KEY, message,
            DATA_KEY, response
        );
        return (ResponseEntity<T>) new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    protected <T> ResponseEntity<T> sendNoContentResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.NO_CONTENT.value(),
            MESSAGE_KEY, message
        );
        return (ResponseEntity<T>) new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    }

    protected <T> ResponseEntity<T> sendNotFoundResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.NOT_FOUND.value(),
            MESSAGE_KEY, message
        );
        return (ResponseEntity<T>) new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    protected <T> ResponseEntity<T> sendBadRequestResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.BAD_REQUEST.value(),
            MESSAGE_KEY, message
        );
        return (ResponseEntity<T>) new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    protected <T> ResponseEntity<T> sendInternalServerErrorResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.INTERNAL_SERVER_ERROR.value(),
            MESSAGE_KEY, message
        );
        return (ResponseEntity<T>) new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected <T> ResponseEntity<T> sendForbiddenResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.FORBIDDEN.value(),
            MESSAGE_KEY, message
        );
        return (ResponseEntity<T>) new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }

    protected <T> ResponseEntity<T> sendUnauthorizedResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.UNAUTHORIZED.value(),
            MESSAGE_KEY, message
        );
        return (ResponseEntity<T>) new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    // Helper method for input validation
    protected boolean isValidSearchValue(String searchValue) {
        return searchValue != null && 
               !searchValue.trim().isEmpty() && 
               searchValue.trim().length() >= 2 && 
               searchValue.trim().length() <= 100;
    }

    // Create success response with data
    protected <T> ResponseEntity<Map<String, Object>> createSuccessResponse(String message, T data) {
        Map<String, Object> result;
        if (data != null) {
            result = Map.of(
                STATUS_KEY, HttpStatus.OK.value(),
                MESSAGE_KEY, message,
                DATA_KEY, data
            );
        } else {
            result = Map.of(
                STATUS_KEY, HttpStatus.OK.value(),
                MESSAGE_KEY, message
            );
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Create validation error response for search
    protected ResponseEntity<Map<String, Object>> createSearchValidationErrorResponse() {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.BAD_REQUEST.value(),
            MESSAGE_KEY, "Search value must be between 2 and 100 characters long"
        );
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    // Create error response with specific status
    protected ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, status.value(),
            MESSAGE_KEY, message
        );
        return new ResponseEntity<>(result, status);
    }
}