package com.example.ecommerce_inventory_service.models;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class InventoryAction extends BaseModel {
    private String name;
}
