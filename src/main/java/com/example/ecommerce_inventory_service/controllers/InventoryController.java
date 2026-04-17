package com.example.ecommerce_inventory_service.controllers;

import com.example.ecommerce_inventory_service.dtos.CreateInventoryRequestDto;
import com.example.ecommerce_inventory_service.dtos.InventoryResponseDto;
import com.example.ecommerce_inventory_service.dtos.StockOperationRequestDto;
import com.example.ecommerce_inventory_service.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<InventoryResponseDto> createInventory(
            @Valid @RequestBody CreateInventoryRequestDto request) {
        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDto> getInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{productId}/add-stock")
    public ResponseEntity<InventoryResponseDto> addStock(
            @PathVariable Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.addStock(productId, request));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{productId}/reserve")
    public ResponseEntity<InventoryResponseDto> reserveStock(
            @PathVariable Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.reserveStock(productId, request));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{productId}/release")
    public ResponseEntity<InventoryResponseDto> releaseReservedStock(
            @PathVariable Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.releaseReservedStock(productId, request));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{productId}/confirm-sale")
    public ResponseEntity<InventoryResponseDto> confirmSale(
            @PathVariable Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.confirmSale(productId, request));
    }

    @PostMapping("/undo-sale/{productId}")
    public ResponseEntity<InventoryResponseDto> undoSale(
            @PathVariable Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.undoConfirmedSale(productId, request));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{productId}/in-stock")
    public ResponseEntity<Boolean> isInStock(
            @PathVariable Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.isInStock(productId, quantity));
    }
}

// “Why use action endpoints instead of pure CRUD/PATCH?”
// “Because inventory operations like reserve/release/confirm-sale are domain-specific
// business commands with validation and side effects, not simple field updates. Modeling them as explicit
// command endpoints improves API clarity and preserves domain invariants.”