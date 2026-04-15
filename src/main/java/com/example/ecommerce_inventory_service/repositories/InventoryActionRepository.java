package com.example.ecommerce_inventory_service.repositories;
import com.example.ecommerce_inventory_service.models.InventoryAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryActionRepository extends JpaRepository<InventoryAction, Long> {
    Optional<InventoryAction> findByName(String name);
    boolean existsByName(String name);
}
