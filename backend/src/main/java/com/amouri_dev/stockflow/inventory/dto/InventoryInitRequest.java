package com.amouri_dev.stockflow.inventory.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Registers a product as tracked in a warehouse, creating a zeroed inventory row.
 * Quantities are populated later through movements (Week 3), never set directly.
 */
public record InventoryInitRequest(
        @NotNull UUID productId,
        @NotNull UUID warehouseId
) {
}
