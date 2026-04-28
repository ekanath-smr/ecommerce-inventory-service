package com.example.ecommerce_inventory_service.dtos;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryRequestDto {
    @NotNull
    private Long productId;
    @PositiveOrZero
    private Integer initialStock;
}
