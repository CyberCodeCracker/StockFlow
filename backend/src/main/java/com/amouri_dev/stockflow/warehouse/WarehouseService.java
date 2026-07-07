package com.amouri_dev.stockflow.warehouse;

import com.amouri_dev.stockflow.warehouse.dto.WarehouseRequest;
import com.amouri_dev.stockflow.warehouse.dto.WarehouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        if (this.warehouseRepository.existsByCode(request.code())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Warehouse code already exists");
        }
        Warehouse warehouse = new Warehouse();
        applyRequest(warehouse, request);
        return WarehouseResponse.from(this.warehouseRepository.save(warehouse));
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> findAllWarehouses() {
        return this.warehouseRepository.findAll().stream()
                .map(WarehouseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public WarehouseResponse findWarehouse(UUID id) {
        return WarehouseResponse.from(getWarehouseOrThrow(id));
    }

    @Transactional
    public WarehouseResponse updateWarehouse(UUID id, WarehouseRequest request) {
        Warehouse warehouse = getWarehouseOrThrow(id);
        if (!warehouse.getCode().equals(request.code()) && this.warehouseRepository.existsByCode(request.code())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Warehouse code already exists");
        }
        applyRequest(warehouse, request);
        return WarehouseResponse.from(warehouse); // managed entity — flushed on commit
    }

    @Transactional
    public void deleteWarehouse(UUID id) {
        Warehouse warehouse = getWarehouseOrThrow(id);
        this.warehouseRepository.delete(warehouse);
    }

    private Warehouse getWarehouseOrThrow(UUID id) {
        return this.warehouseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));
    }

    private void applyRequest(Warehouse warehouse, WarehouseRequest request) {
        warehouse.setCode(request.code());
        warehouse.setName(request.name());
        warehouse.setLocation(request.location());
    }
}
