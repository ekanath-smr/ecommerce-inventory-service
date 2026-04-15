package com.example.ecommerce_inventory_service.dtos;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockOperationRequestDto {
    @Positive
    private Integer quantity;
    private String referenceId; // optional, useful for order/payment linkage
}
