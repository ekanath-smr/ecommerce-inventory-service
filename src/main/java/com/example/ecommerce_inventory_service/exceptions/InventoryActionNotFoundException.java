package com.example.ecommerce_inventory_service.exceptions;

import lombok.Getter;

@Getter
public class InventoryActionNotFoundException extends RuntimeException {
    private final String message;
    private final String actionName;
    public InventoryActionNotFoundException(String message, String actionName) {
        super("Inventory action not found: " + actionName);
        this.message = message;
        this.actionName = actionName;
    }
}
