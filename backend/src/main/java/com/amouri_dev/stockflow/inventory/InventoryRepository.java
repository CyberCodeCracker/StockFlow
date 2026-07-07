package com.amouri_dev.stockflow.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    List<Inventory> findByWarehouseId(UUID warehouseId);

    List<Inventory> findByProductId(UUID productId);

    boolean existsByProductIdAndWarehouseId(UUID productId, UUID warehouseId);
}
