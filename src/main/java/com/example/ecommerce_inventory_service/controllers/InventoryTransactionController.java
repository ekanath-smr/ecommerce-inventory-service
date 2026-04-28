package com.example.ecommerce_inventory_service.controllers;

import com.example.ecommerce_inventory_service.dtos.InventoryTransactionDto;
import com.example.ecommerce_inventory_service.services.InventoryTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventory Transactions", description = "Inventory audit and transaction history APIs")
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @Operation(
            summary = "Get inventory transactions",
            description = "Fetches transaction history (audit logs) for a given productId. Accessible only by ADMIN."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{productId}/transactions")
    public ResponseEntity<List<InventoryTransactionDto>> getTransactions(
            @PathVariable @Positive Long productId) {
        return ResponseEntity.ok(
                inventoryTransactionService.getTransactionsByProductId(productId)
        );
    }
}