package com.example.ecommerce_inventory_service.controllers;

import com.example.ecommerce_inventory_service.dtos.InventoryTransactionDto;
import com.example.ecommerce_inventory_service.services.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{productId}/transactions")
    public ResponseEntity<List<InventoryTransactionDto>> getTransactions(
            @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryTransactionService.getTransactionsByProductId(productId));
    }

}