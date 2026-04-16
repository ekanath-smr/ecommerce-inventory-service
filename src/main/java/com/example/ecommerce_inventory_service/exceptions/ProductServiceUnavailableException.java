package com.example.ecommerce_inventory_service.exceptions;

public class ProductServiceUnavailableException extends RuntimeException {

    public ProductServiceUnavailableException(String message) {
        super(message);
    }

    public ProductServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
