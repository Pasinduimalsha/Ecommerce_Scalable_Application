package com.pasi.inventory_service.controller;

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
public abstract class InventoryAbstractController {
    
    private static final String STATUS_KEY = "status";
    private static final String MESSAGE_KEY = "message";
    private static final String DATA_KEY = "data";
    private static final String TIMESTAMP_KEY = "timestamp";
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            STATUS_KEY, "UP",
            "service", "Inventory Service",
            TIMESTAMP_KEY, LocalDateTime.now().toString()
        ));
    }

    protected <T> ResponseEntity<Map<String, Object>> sendSuccessResponse(T response, String message) {
        Map<String, Object> result = Map.of(
            STATUS_KEY, HttpStatus.OK.value(),
            MESSAGE_KEY, message,
            DATA_KEY, response
        );
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
}
