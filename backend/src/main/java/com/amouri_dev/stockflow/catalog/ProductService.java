package com.amouri_dev.stockflow.catalog;

import com.amouri_dev.stockflow.catalog.dto.ProductRequest;
import com.amouri_dev.stockflow.catalog.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (this.productRepository.existsBySku(request.sku())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
        }
        Product product = new Product();
        applyRequest(product, request);
        return ProductResponse.from(this.productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {
        return this.productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findProduct(UUID id) {
        return ProductResponse.from(getProductOrThrow(id));
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = getProductOrThrow(id);
        if (!product.getSku().equals(request.sku()) && this.productRepository.existsBySku(request.sku())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
        }
        applyRequest(product, request);
        return ProductResponse.from(product); // managed entity — flushed on commit
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = getProductOrThrow(id);
        this.productRepository.delete(product);
    }

    private Product getProductOrThrow(UUID id) {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private void applyRequest(Product product, ProductRequest request) {
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setUnitPrice(request.unitPrice());
        product.setMinStockLevel(request.minStockLevel());
    }
}
