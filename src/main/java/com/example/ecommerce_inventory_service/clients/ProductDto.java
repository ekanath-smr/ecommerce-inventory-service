package com.example.ecommerce_inventory_service.clients;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String category;
    private String image;
}
