package com.example.ecommerce_inventory_service.mappers;
import com.example.ecommerce_inventory_service.dtos.InventoryResponseDto;
import com.example.ecommerce_inventory_service.models.Inventory;

public final class InventoryMapper {

    private InventoryMapper() {}

    public static InventoryResponseDto toDto(Inventory inventory) {
        return InventoryResponseDto.builder()
                .productId(inventory.getProductId())
                .totalStock(inventory.getTotalStock())
                .reservedStock(inventory.getReservedStock())
                .soldStock(inventory.getSoldStock())
                .damagedStock(inventory.getDamagedStock())
                .availableStock(inventory.getTotalStock() - inventory.getReservedStock() - inventory.getSoldStock() - inventory.getDamagedStock())
                .build();
    }
}