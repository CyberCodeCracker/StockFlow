package com.amouri_dev.stockflow.catalog.dto;

import com.amouri_dev.stockflow.catalog.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal unitPrice,
        int minStockLevel
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getUnitPrice(),
                product.getMinStockLevel()
        );
    }
}
