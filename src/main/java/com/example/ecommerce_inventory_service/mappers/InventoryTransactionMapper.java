package com.example.ecommerce_inventory_service.mappers;

import com.example.ecommerce_inventory_service.dtos.InventoryTransactionDto;
import com.example.ecommerce_inventory_service.models.InventoryTransaction;

public final class InventoryTransactionMapper {

    private InventoryTransactionMapper() {}

    public static InventoryTransactionDto toDto(InventoryTransaction tx) {
        return InventoryTransactionDto.builder()
                .id(tx.getId())
                .productId(tx.getProductId())
                .action(tx.getAction().getName())
                .quantity(tx.getQuantity())
                .referenceId(tx.getReferenceId())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
