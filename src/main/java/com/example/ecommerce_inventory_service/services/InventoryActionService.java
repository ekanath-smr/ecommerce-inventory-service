package com.example.ecommerce_inventory_service.services;
import com.example.ecommerce_inventory_service.models.InventoryAction;

public interface InventoryActionService {
    InventoryAction getByName(String name);
}
