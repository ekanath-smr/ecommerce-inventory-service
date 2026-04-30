package com.example.ecommerce_inventory_service.services;

import com.example.ecommerce_inventory_service.exceptions.InventoryActionNotFoundException;
import com.example.ecommerce_inventory_service.models.InventoryAction;
import com.example.ecommerce_inventory_service.repositories.InventoryActionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryActionServiceImpl implements InventoryActionService {

    private final InventoryActionRepository inventoryActionRepository;

    public InventoryActionServiceImpl(InventoryActionRepository inventoryActionRepository) {
        this.inventoryActionRepository = inventoryActionRepository;
    }

//    @Override
//    public InventoryAction getByName(String name) {
//        log.debug("Fetching InventoryAction with name: {}", name);
//
//        return inventoryActionRepository.findByName(name)
//                .map(action -> {
//                    log.debug("InventoryAction found: {}", action.getName());
//                    return action;
//                })
//                .orElseThrow(() -> {
//                    log.warn("InventoryAction not found for name: {}", name);
//                    return new InventoryActionNotFoundException("Inventory action not found", name);
//                });
//    }

    @Override
    public InventoryAction getByName(String name) {
        return inventoryActionRepository.findByName(name)
                .orElseThrow(() ->
                        new InventoryActionNotFoundException(
                                "Inventory action not found", name)
                );
    }
}