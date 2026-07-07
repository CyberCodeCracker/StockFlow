package com.amouri_dev.stockflow.inventory;

import com.amouri_dev.stockflow.inventory.dto.InventoryInitRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<?> listInventory(
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(required = false) UUID productId) {
        return ResponseEntity.ok(inventoryService.findInventory(warehouseId, productId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryItem(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryService.findInventoryItem(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> initInventory(@RequestBody @Valid InventoryInitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.initInventory(request));
    }
}
