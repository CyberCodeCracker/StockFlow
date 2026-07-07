package com.amouri_dev.stockflow.inventory;

import com.amouri_dev.stockflow.catalog.Product;
import com.amouri_dev.stockflow.common.BaseEntity;
import com.amouri_dev.stockflow.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Running stock balance for a (product, warehouse) pair. Quantities are mutated only through
 * inventory movements (Week 3). The {@code version} field enables optimistic locking so concurrent
 * reservations cannot oversell.
 */
@Entity
@Table(name = "inventory", uniqueConstraints =
        @UniqueConstraint(name = "uq_inventory_product_warehouse", columnNames = {"product_id", "warehouse_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inventory extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "on_hand", nullable = false)
    private int onHand;

    @Column(nullable = false)
    private int reserved;

    @Version
    @Column(nullable = false)
    private long version;

    @Transient
    public int getAvailable() {
        return onHand - reserved;
    }
}
