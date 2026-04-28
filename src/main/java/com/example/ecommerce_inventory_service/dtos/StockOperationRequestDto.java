package com.example.ecommerce_inventory_service.dtos;

import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockOperationRequestDto {
    @Positive
    private Integer quantity;
    private String referenceId; // optional, useful for order/payment linkage
}
