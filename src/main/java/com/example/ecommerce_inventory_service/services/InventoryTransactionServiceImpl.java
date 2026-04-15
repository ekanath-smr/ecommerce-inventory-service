package com.example.ecommerce_inventory_service.services;

import com.example.ecommerce_inventory_service.dtos.InventoryTransactionDto;

import java.util.List;

public class InventoryTransactionServiceImpl implements InventoryTransactionService {
    @Override
    public void recordTransaction(Long productId, String actionName, Integer quantity, String referenceId) {

    }

    @Override
    public List<InventoryTransactionDto> getTransactionsByProductId(Long productId) {
        return List.of();
    }
}
