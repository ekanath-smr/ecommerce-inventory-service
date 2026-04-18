package com.example.ecommerce_inventory_service.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAvailabilityResponseDto {
    private Long productId;
    private Integer availableStock;
}
