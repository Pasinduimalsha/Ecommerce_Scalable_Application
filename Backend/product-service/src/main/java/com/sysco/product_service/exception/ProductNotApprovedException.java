package com.sysco.product_service.exception;

public class ProductNotApprovedException extends RuntimeException {
    public ProductNotApprovedException(String message) {
        super(message);
    }
    
    public ProductNotApprovedException(String message, Throwable cause) {
        super(message, cause);
    }
}