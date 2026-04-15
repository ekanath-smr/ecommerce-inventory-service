package com.example.ecommerce_inventory_service.services;
import com.example.ecommerce_inventory_service.dtos.InventoryTransactionDto;
import java.util.List;

public interface InventoryTransactionService {
    void recordTransaction(Long productId, String actionName, Integer quantity, String referenceId);
    List<InventoryTransactionDto> getTransactionsByProductId(Long productId);
}