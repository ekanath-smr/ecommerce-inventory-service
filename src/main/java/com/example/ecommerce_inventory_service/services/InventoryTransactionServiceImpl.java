package com.example.ecommerce_inventory_service.services;

import com.example.ecommerce_inventory_service.dtos.InventoryTransactionDto;
import com.example.ecommerce_inventory_service.mappers.InventoryTransactionMapper;
import com.example.ecommerce_inventory_service.models.InventoryAction;
import com.example.ecommerce_inventory_service.models.InventoryTransaction;
import com.example.ecommerce_inventory_service.repositories.InventoryTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryActionService inventoryActionService;

    public InventoryTransactionServiceImpl(InventoryTransactionRepository inventoryTransactionRepository, InventoryActionService inventoryActionService) {
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryActionService = inventoryActionService;
    }

    @Override
    public void recordTransaction(Long productId, String actionName, Integer quantity, String referenceId) {
        InventoryAction action = inventoryActionService.getByName(actionName);
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductId(productId);
        transaction.setAction(action);
        transaction.setQuantity(quantity);
        transaction.setReferenceId(referenceId);
        inventoryTransactionRepository.save(transaction);
    }

    @Override
    public List<InventoryTransactionDto> getTransactionsByProductId(Long productId) {
        return inventoryTransactionRepository
                .findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(InventoryTransactionMapper::toDto)
                .toList();
    }
}