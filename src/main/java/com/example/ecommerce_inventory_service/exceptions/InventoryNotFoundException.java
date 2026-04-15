package com.example.ecommerce_inventory_service.exceptions;

import lombok.Getter;

@Getter
public class InventoryNotFoundException extends RuntimeException {
    private final String message;
    private final Long productId;
    public InventoryNotFoundException(String message, Long productId) {
        super("Inventory not found for product id: " + productId);
        this.message = message;
        this.productId = productId;
    }
}
