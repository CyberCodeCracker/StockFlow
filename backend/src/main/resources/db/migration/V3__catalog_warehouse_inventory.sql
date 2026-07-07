-- Core domain: products, warehouses, and the inventory rows that link them.
-- Inventory quantities are mutated only via movements (Week 3); rows here hold the running balance.

CREATE TABLE products (
    id              UUID PRIMARY KEY,
    sku             VARCHAR(64)   NOT NULL UNIQUE,
    name            VARCHAR(255)  NOT NULL,
    description     VARCHAR(1000),
    unit_price      NUMERIC(19, 2) NOT NULL,
    min_stock_level INTEGER       NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL,
    updated_at      TIMESTAMPTZ   NOT NULL
);

CREATE TABLE warehouses (
    id         UUID PRIMARY KEY,
    code       VARCHAR(64)  NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL,
    location   VARCHAR(255),
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL
);

CREATE TABLE inventory (
    id           UUID PRIMARY KEY,
    product_id   UUID    NOT NULL REFERENCES products (id),
    warehouse_id UUID    NOT NULL REFERENCES warehouses (id),
    on_hand      INTEGER NOT NULL DEFAULT 0,
    reserved     INTEGER NOT NULL DEFAULT 0,
    version      BIGINT  NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_inventory_product_warehouse UNIQUE (product_id, warehouse_id)
);

CREATE INDEX idx_inventory_product_id ON inventory (product_id);
CREATE INDEX idx_inventory_warehouse_id ON inventory (warehouse_id);
