package com.amouri_dev.stockflow.warehouse.dto;

import com.amouri_dev.stockflow.warehouse.Warehouse;

import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        String code,
        String name,
        String location
) {
    public static WarehouseResponse from(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getCode(),
                warehouse.getName(),
                warehouse.getLocation()
        );
    }
}
