package com.amouri_dev.stockflow.inventory;

import com.amouri_dev.stockflow.catalog.Product;
import com.amouri_dev.stockflow.catalog.ProductRepository;
import com.amouri_dev.stockflow.inventory.dto.InventoryInitRequest;
import com.amouri_dev.stockflow.inventory.dto.InventoryResponse;
import com.amouri_dev.stockflow.warehouse.Warehouse;
import com.amouri_dev.stockflow.warehouse.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> findInventory(UUID warehouseId, UUID productId) {
        List<Inventory> rows;
        if (warehouseId != null) {
            rows = this.inventoryRepository.findByWarehouseId(warehouseId);
        } else if (productId != null) {
            rows = this.inventoryRepository.findByProductId(productId);
        } else {
            rows = this.inventoryRepository.findAll();
        }
        return rows.stream().map(InventoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponse findInventoryItem(UUID id) {
        return InventoryResponse.from(getInventoryOrThrow(id));
    }

    /**
     * Registers a product in a warehouse as a zeroed inventory row. Stock is added later via movements.
     */
    @Transactional
    public InventoryResponse initInventory(InventoryInitRequest request) {
        if (this.inventoryRepository.existsByProductIdAndWarehouseId(request.productId(), request.warehouseId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inventory already exists for this product and warehouse");
        }
        Product product = this.productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        Warehouse warehouse = this.warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setOnHand(0);
        inventory.setReserved(0);
        return InventoryResponse.from(this.inventoryRepository.save(inventory));
    }

    private Inventory getInventoryOrThrow(UUID id) {
        return this.inventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found"));
    }
}
