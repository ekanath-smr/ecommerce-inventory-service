package com.example.ecommerce_inventory_service.exceptions;

import lombok.Getter;

@Getter
public class InsufficientStockException extends RuntimeException {
    private final String message;
    private final Long productId;
    public InsufficientStockException(String message, Long productId) {
        super("Insufficient stock for product id: " + productId);
        this.message = message;
        this.productId = productId;
    }
}
