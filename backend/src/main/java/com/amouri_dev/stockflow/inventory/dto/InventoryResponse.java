package com.amouri_dev.stockflow.inventory.dto;

import com.amouri_dev.stockflow.inventory.Inventory;

import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID productId,
        String productSku,
        String productName,
        UUID warehouseId,
        String warehouseCode,
        String warehouseName,
        int onHand,
        int reserved,
        int available
) {
    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getSku(),
                inventory.getProduct().getName(),
                inventory.getWarehouse().getId(),
                inventory.getWarehouse().getCode(),
                inventory.getWarehouse().getName(),
                inventory.getOnHand(),
                inventory.getReserved(),
                inventory.getAvailable()
        );
    }
}
