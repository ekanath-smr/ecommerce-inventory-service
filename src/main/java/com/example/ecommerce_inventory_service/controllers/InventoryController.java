package com.example.ecommerce_inventory_service.controllers;

import com.example.ecommerce_inventory_service.dtos.CreateInventoryRequestDto;
import com.example.ecommerce_inventory_service.dtos.InventoryResponseDto;
import com.example.ecommerce_inventory_service.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponseDto> createInventory(
            @Valid @RequestBody CreateInventoryRequestDto request) {
        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDto> getInventory(
            @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }
}
