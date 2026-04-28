package com.example.ecommerce_inventory_service.controllers;

import com.example.ecommerce_inventory_service.dtos.CreateInventoryRequestDto;
import com.example.ecommerce_inventory_service.dtos.InventoryResponseDto;
import com.example.ecommerce_inventory_service.dtos.StockAvailabilityResponseDto;
import com.example.ecommerce_inventory_service.dtos.StockOperationRequestDto;
import com.example.ecommerce_inventory_service.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// “Why use action endpoints instead of pure CRUD/PATCH?”
// “Because inventory operations like reserve/release/confirm-sale are domain-specific
// business commands with validation and side effects, not simple field updates. Modeling them as explicit
// command endpoints improves API clarity and preserves domain invariants.”

@Tag(name = "Inventory", description = "Inventory Management APIs")
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(
            summary = "Create inventory",
            description = "Creates inventory for a product. Only ADMIN can perform this operation."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<InventoryResponseDto> createInventory(
            @Valid @RequestBody CreateInventoryRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(request));
    }

    @Operation(
            summary = "Get inventory",
            description = "Fetch inventory details for a given productId."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDto> getInventory(@PathVariable @Positive Long productId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    @Operation(
            summary = "Add stock",
            description = "Adds stock to an existing inventory. Only ADMIN can perform this operation."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{productId}/add-stock")
    public ResponseEntity<InventoryResponseDto> addStock(
            @PathVariable @Positive Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.addStock(productId, request));
    }

    @Operation(
            summary = "Reserve stock",
            description = "Reserves stock for a product during checkout process."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{productId}/reserve")
    public ResponseEntity<InventoryResponseDto> reserveStock(
            @PathVariable @Positive Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.reserveStock(productId, request));
    }

    @Operation(
            summary = "Release reserved stock",
            description = "Releases previously reserved stock back to available stock."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{productId}/release")
    public ResponseEntity<InventoryResponseDto> releaseReservedStock(
            @PathVariable @Positive Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.releaseReservedStock(productId, request));
    }

    @Operation(
            summary = "Confirm sale",
            description = "Moves stock from reserved to sold after successful checkout."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{productId}/confirm-sale")
    public ResponseEntity<InventoryResponseDto> confirmSale(
            @PathVariable @Positive Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.confirmSale(productId, request));
    }

    @Operation(
            summary = "Undo confirmed sale",
            description = "Rolls back a confirmed sale (moves stock from sold back to reserved). Used in failure scenarios."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{productId}/undo-sale")
    public ResponseEntity<InventoryResponseDto> undoSale(
            @PathVariable @Positive Long productId, @Valid @RequestBody StockOperationRequestDto request) {
        return ResponseEntity.ok(inventoryService.undoConfirmedSale(productId, request));
    }

    @Operation(
            summary = "Check stock availability",
            description = "Checks whether requested quantity is available for a product."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{productId}/in-stock")
    public ResponseEntity<Boolean> isInStock(
            @PathVariable @Positive Long productId, @RequestParam @Positive Integer quantity) {
        return ResponseEntity.ok(inventoryService.isInStock(productId, quantity));
    }

    @Operation(
            summary = "Get available stock",
            description = "Returns available stock for a product."
    )
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{productId}/get-stock")
    public ResponseEntity<StockAvailabilityResponseDto> getAvailableStock(@PathVariable @Positive Long productId) {
        return ResponseEntity.ok(inventoryService.getAvailableStock(productId));
    }
}
