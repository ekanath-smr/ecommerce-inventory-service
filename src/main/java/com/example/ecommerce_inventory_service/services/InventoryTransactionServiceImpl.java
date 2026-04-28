package com.example.ecommerce_inventory_service.services;

import com.example.ecommerce_inventory_service.dtos.InventoryTransactionDto;
import com.example.ecommerce_inventory_service.mappers.InventoryTransactionMapper;
import com.example.ecommerce_inventory_service.models.InventoryAction;
import com.example.ecommerce_inventory_service.models.InventoryTransaction;
import com.example.ecommerce_inventory_service.repositories.InventoryTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryActionService inventoryActionService;

    public InventoryTransactionServiceImpl(InventoryTransactionRepository inventoryTransactionRepository, InventoryActionService inventoryActionService) {
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryActionService = inventoryActionService;
    }

    @Override
    public void recordTransaction(Long productId, String actionName, Integer quantity, String referenceId) {
        log.debug("Recording inventory transaction | productId={} | action={} | quantity={} | referenceId={}",
                productId, actionName, quantity, referenceId);
        InventoryAction action = inventoryActionService.getByName(actionName);
        InventoryTransaction transaction = InventoryTransaction.builder()
                .productId(productId)
                .action(action)
                .quantity(quantity)
                .referenceId(referenceId)
                .build();
        inventoryTransactionRepository.save(transaction);
        log.info("Inventory transaction recorded successfully | productId={} | action={} | quantity={}",
                productId, actionName, quantity);
    }

    @Override
    public List<InventoryTransactionDto> getTransactionsByProductId(Long productId) {
        log.debug("Fetching inventory transactions | productId={}", productId);
        List<InventoryTransactionDto> transactions = inventoryTransactionRepository
                .findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(InventoryTransactionMapper::toDto)
                .toList();
        log.info("Fetched {} transactions | productId={}", transactions.size(), productId);
        return transactions;
    }
}