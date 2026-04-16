package com.example.ecommerce_inventory_service.exceptions;

import lombok.Getter;

@Getter
public class InvalidInventoryOperationException extends RuntimeException {
    private final Long productId;
    public InvalidInventoryOperationException(String message, Long productId) {
        super(message);
        this.productId = productId;
    }
}
