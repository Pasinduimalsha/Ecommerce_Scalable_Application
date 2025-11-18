package com.sysco.order_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Map;

@SuppressWarnings("ALL")
@Slf4j
@Controller
public abstract class OrderAbstractController {
    
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
            SERVICE_KEY, "Order Service",
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

    protected ResponseEntity<Map<String, Object>> sendAcceptedResponse(Object response, String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.ACCEPTED.value(),
            MESSAGE_KEY, message,
            DATA_KEY, response
        );
        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    protected ResponseEntity<Map<String, Object>> sendNoContentResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.NO_CONTENT.value(),
            MESSAGE_KEY, message
        );
        return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    }

    protected ResponseEntity<Map<String, Object>> sendNotFoundResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.NOT_FOUND.value(),
            MESSAGE_KEY, message
        );
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    protected ResponseEntity<Map<String, Object>> sendBadRequestResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.BAD_REQUEST.value(),
            MESSAGE_KEY, message
        );
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    protected ResponseEntity<Map<String, Object>> sendConflictResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.CONFLICT.value(),
            MESSAGE_KEY, message
        );
        return new ResponseEntity<>(result, HttpStatus.CONFLICT);
    }

    protected ResponseEntity<Map<String, Object>> sendInternalServerErrorResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.INTERNAL_SERVER_ERROR.value(),
            MESSAGE_KEY, message
        );
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<Map<String, Object>> sendForbiddenResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.FORBIDDEN.value(),
            MESSAGE_KEY, message
        );
        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }

    protected ResponseEntity<Map<String, Object>> sendUnauthorizedResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.UNAUTHORIZED.value(),
            MESSAGE_KEY, message
        );
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    // Helper method for input validation
    protected boolean isValidCustomerId(String customerId) {
        return customerId != null && 
               !customerId.trim().isEmpty() && 
               customerId.trim().length() >= 1 && 
               customerId.trim().length() <= 50;
    }

    protected boolean isValidSkuCode(String skuCode) {
        return skuCode != null && 
               !skuCode.trim().isEmpty() && 
               skuCode.trim().length() >= 2 && 
               skuCode.trim().length() <= 50;
    }

    protected boolean isValidCartId(Long cartId) {
        return cartId != null && cartId > 0;
    }

    // Create success response with data - convenience method
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

    // Create validation error response for cart operations
    protected ResponseEntity<Map<String, Object>> createCartValidationErrorResponse(String field, String requirement) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.BAD_REQUEST.value(),
            MESSAGE_KEY, field + " " + requirement
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

    // Create response for empty results
    protected ResponseEntity<Map<String, Object>> createEmptyResponse(String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.OK.value(),
            MESSAGE_KEY, message,
            DATA_KEY, Map.of()
        );
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}