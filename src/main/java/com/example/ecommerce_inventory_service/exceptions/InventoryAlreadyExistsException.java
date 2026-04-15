package com.example.ecommerce_inventory_service.exceptions;

import lombok.Getter;

@Getter
public class InventoryAlreadyExistsException extends RuntimeException {
    private final String message;
    private final Long productId;
    public InventoryAlreadyExistsException(String message, Long productId) {
        super("Inventory already exists for product id: " + productId);
        this.message = message;
        this.productId = productId;
    }
}
