package com.example.ecommerce_inventory_service.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "inventories")
@Table(
        name = "inventories",
        indexes = {
                @Index(name = "idx_inventory_product_id", columnList = "productId")
        }
)
public class Inventory extends BaseModel {
    @Column(nullable = false, unique = true)
    private Long productId;
    @Column(nullable = false)
    private Integer totalStock;
    @Column(nullable = false)
    private Integer reservedStock = 0;
    @Column(nullable = false)
    private Integer soldStock = 0;
    @Column(nullable = false)
    private Integer damagedStock = 0;
    // “I use optimistic locking with JPA’s @Version field.
    // Concurrent updates include the version in the WHERE clause.
    // If another transaction modifies the row first, the second update fails with OptimisticLockException,
    // preventing lost updates and overselling.”
    @Version
    private Long version;
}
