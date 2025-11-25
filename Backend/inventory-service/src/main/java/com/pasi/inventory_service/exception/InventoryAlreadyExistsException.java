package com.pasi.inventory_service.exception;

public class InventoryAlreadyExistsException extends RuntimeException {
    public InventoryAlreadyExistsException(String message) {
        super(message);
    }
    
    public InventoryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}