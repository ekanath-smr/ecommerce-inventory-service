package com.example.ecommerce_inventory_service.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "inventory_transactions")
public class InventoryTransaction extends BaseModel {
    @Column(nullable = false)
    private Long productId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    private InventoryAction action;
    @Column(nullable = false)
    private Integer quantity;
    private String referenceId; // orderId/paymentId/etc
}
