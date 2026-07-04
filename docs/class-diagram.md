# StockFlow — Class Diagram

Domain model for the StockFlow inventory consistency engine. Every persistent entity extends
`BaseEntity` (UUID id + audit timestamps). Inventory is **never mutated directly** — the current
`Inventory` row is a running balance projected from the append-only `InventoryMovement` ledger,
where `available = onHand − reserved`.

```mermaid
classDiagram
    direction LR

    class BaseEntity {
        <<MappedSuperclass>>
        +UUID id
        +Instant createdAt
        +Instant updatedAt
    }

    %% ===== Security =====
    class User {
        +String email
        +String password
        +String firstName
        +String lastName
        +Set~UserRole~ roles
        +boolean isDeleted
    }

    class UserRole {
        <<enumeration>>
        ADMIN
        MANAGER
        CLERK
    }

    class RefreshToken {
        +String token
        +Instant expires
        +boolean revoked
    }

    %% ===== Catalog & Warehouse =====
    class Product {
        +String sku
        +String name
        +String description
        +BigDecimal unitPrice
        +int minStockLevel
    }

    class Warehouse {
        +String code
        +String name
        +String location
    }

    %% ===== Inventory (the heart) =====
    class Inventory {
        +int onHand
        +int reserved
        +long version
        +int available()
    }

    class InventoryMovement {
        +int quantity
        +MovementType type
        +String reference
    }

    class MovementType {
        <<enumeration>>
        RECEIPT
        SHIPMENT
        RESERVATION
        RELEASE
        TRANSFER_OUT
        TRANSFER_IN
        ADJUSTMENT
        RETURN
        DAMAGED
        LOST
    }

    %% ===== Procurement =====
    class Supplier {
        +String name
        +String email
        +int leadTimeDays
    }

    class PurchaseOrder {
        +PurchaseOrderStatus status
        +LocalDate expectedDate
    }

    class PurchaseOrderItem {
        +int quantity
        +BigDecimal unitPrice
    }

    class PurchaseOrderStatus {
        <<enumeration>>
        DRAFT
        APPROVED
        RECEIVED
        CANCELLED
    }

    %% ===== Sales =====
    class SalesOrder {
        +String customerName
        +SalesOrderStatus status
    }

    class SalesOrderItem {
        +int quantity
        +BigDecimal unitPrice
    }

    class SalesOrderStatus {
        <<enumeration>>
        DRAFT
        CONFIRMED
        SHIPPED
        CANCELLED
    }

    %% ===== Transfers =====
    class Transfer {
        +int quantity
        +TransferStatus status
    }

    class TransferStatus {
        <<enumeration>>
        PENDING
        COMPLETED
        CANCELLED
    }

    %% ===== Inheritance =====
    BaseEntity <|-- User
    BaseEntity <|-- RefreshToken
    BaseEntity <|-- Product
    BaseEntity <|-- Warehouse
    BaseEntity <|-- Inventory
    BaseEntity <|-- InventoryMovement
    BaseEntity <|-- Supplier
    BaseEntity <|-- PurchaseOrder
    BaseEntity <|-- PurchaseOrderItem
    BaseEntity <|-- SalesOrder
    BaseEntity <|-- SalesOrderItem
    BaseEntity <|-- Transfer

    %% ===== Associations =====
    User "1" --> "*" UserRole : has
    User "1" --> "*" RefreshToken : owns

    Inventory "*" --> "1" Product : of
    Inventory "*" --> "1" Warehouse : at

    InventoryMovement "*" --> "1" Product : affects
    InventoryMovement "*" --> "1" Warehouse : in
    InventoryMovement "*" --> "0..1" User : createdBy
    InventoryMovement --> MovementType : typed

    PurchaseOrder "*" --> "1" Supplier : from
    PurchaseOrder "*" --> "1" Warehouse : deliverTo
    PurchaseOrder "1" *-- "*" PurchaseOrderItem : contains
    PurchaseOrderItem "*" --> "1" Product : for
    PurchaseOrder --> PurchaseOrderStatus : status

    SalesOrder "*" --> "1" Warehouse : from
    SalesOrder "1" *-- "*" SalesOrderItem : contains
    SalesOrderItem "*" --> "1" Product : for
    SalesOrder --> SalesOrderStatus : status

    Transfer "*" --> "1" Warehouse : source
    Transfer "*" --> "1" Warehouse : destination
    Transfer "*" --> "1" Product : moves
    Transfer --> TransferStatus : status
```

## Key invariants

- `Inventory.available() == onHand − reserved`, and for every `(product, warehouse)`
  `onHand == sum(InventoryMovement.quantity)`.
- `Inventory.version` (JPA `@Version`) enforces optimistic locking so concurrent reservations
  cannot oversell.
- A `Transfer` produces a paired `TRANSFER_OUT` + `TRANSFER_IN` movement inside one transaction.
- Confirming a `SalesOrder` creates `RESERVATION` movements; shipping converts them to `SHIPMENT`.
- Receiving a `PurchaseOrder` creates `RECEIPT` movements.
