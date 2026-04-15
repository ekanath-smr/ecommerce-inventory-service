package com.example.ecommerce_inventory_service.exceptions;

import lombok.Getter;

@Getter
public class InvalidInventoryOperationException extends RuntimeException {
    private final String message;
    public InvalidInventoryOperationException(String message) {
        super(message);
        this.message = message;
    }
}
