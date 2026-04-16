package com.example.ecommerce_inventory_service.clients;

import lombok.Data;

@Data
public class ProductResponseDto {
    private ProductDto product;
    private ResponseStatus status;
}
