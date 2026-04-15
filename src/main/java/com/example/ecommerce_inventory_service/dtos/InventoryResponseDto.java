package com.example.ecommerce_inventory_service.dtos;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponseDto {
    private Long productId;
    private Integer totalStock;
    private Integer reservedStock;
    private Integer soldStock;
    private Integer damagedStock;
    private Integer availableStock;
}
