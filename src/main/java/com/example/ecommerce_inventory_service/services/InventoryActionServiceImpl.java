package com.example.ecommerce_inventory_service.services;

import com.example.ecommerce_inventory_service.exceptions.InventoryActionNotFoundException;
import com.example.ecommerce_inventory_service.models.InventoryAction;
import com.example.ecommerce_inventory_service.repositories.InventoryActionRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryActionServiceImpl implements InventoryActionService {

    private final InventoryActionRepository inventoryActionRepository;

    public InventoryActionServiceImpl(InventoryActionRepository inventoryActionRepository) {
        this.inventoryActionRepository = inventoryActionRepository;
    }

    @Override
    public InventoryAction getByName(String name) {
        return inventoryActionRepository.findByName(name)
                .orElseThrow(() -> new InventoryActionNotFoundException("Inventory action not found", name));
    }
}