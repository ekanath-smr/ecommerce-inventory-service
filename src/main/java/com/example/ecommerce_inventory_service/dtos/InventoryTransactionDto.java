package com.example.ecommerce_inventory_service.dtos;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class InventoryTransactionDto {
    private Long id;
    private Long productId;
    private String action;
    private Integer quantity;
    private String referenceId;
    private LocalDateTime createdAt;
}