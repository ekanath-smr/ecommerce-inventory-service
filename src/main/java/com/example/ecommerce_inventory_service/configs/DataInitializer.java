package com.example.ecommerce_inventory_service.configs;

import com.example.ecommerce_inventory_service.models.InventoryAction;
import com.example.ecommerce_inventory_service.repositories.InventoryActionRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final InventoryActionRepository inventoryActionRepository;

    @Override
    public void run(String @NonNull ... args) {
        seedInventoryActions();
    }

    private void seedInventoryActions() {
        seedIfMissing("STOCK_ADDED");
        seedIfMissing("STOCK_RESERVED");
        seedIfMissing("STOCK_RELEASED");
        seedIfMissing("SALE_CONFIRMED");
        seedIfMissing("UNDO_SALE");
        seedIfMissing("DAMAGED");
    }

    private void seedIfMissing(String actionName) {
        if (!inventoryActionRepository.existsByName(actionName)) {
            InventoryAction action = new InventoryAction();
            action.setName(actionName);
            inventoryActionRepository.save(action);
        }
    }
}
