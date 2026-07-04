# StockFlow — Use Case Diagram

Actor–use-case map for StockFlow. Mermaid has no native UML use-case notation, so this is modelled
as a directed graph: **actors** (left) connect to the **use cases** they can perform, grouped by
module. Roles are hierarchical — `ADMIN` inherits everything `MANAGER` can do, and `MANAGER`
inherits the operational use cases of `CLERK`.

```mermaid
graph LR
    %% ===== Actors =====
    admin([👤 Admin])
    manager([👤 Manager])
    clerk([👤 Clerk])
    ai{{🤖 AI Engine / Ollama}}

    admin -. inherits .-> manager
    manager -. inherits .-> clerk

    %% ===== Security =====
    subgraph Security
        login([Login / Refresh token])
        manageUsers([Manage users & roles])
    end

    %% ===== Catalog & Warehouse =====
    subgraph Catalog_Warehouse[Catalog & Warehouse]
        manageProducts([Manage products])
        manageWarehouses([Manage warehouses])
        manageSuppliers([Manage suppliers])
    end

    %% ===== Inventory =====
    subgraph Inventory
        viewInventory([View inventory / availability])
        adjustInventory([Adjust / write-off stock])
        transferStock([Transfer stock between warehouses])
    end

    %% ===== Procurement =====
    subgraph Procurement
        createPO([Create purchase order])
        approvePO([Approve purchase order])
        receiveGoods([Receive goods])
    end

    %% ===== Sales =====
    subgraph Sales
        createSO([Create sales order])
        confirmSO([Confirm order / reserve stock])
        shipSO([Ship order])
    end

    %% ===== Reporting & Insights =====
    subgraph Insights[Reporting & AI Insights]
        viewDashboard([View dashboard & KPIs])
        viewReports([View reports])
        viewAudit([View audit trail])
        askAI([Ask AI insight / NL query])
    end

    %% ===== Clerk capabilities =====
    clerk --> login
    clerk --> viewInventory
    clerk --> adjustInventory
    clerk --> transferStock
    clerk --> createPO
    clerk --> receiveGoods
    clerk --> createSO
    clerk --> confirmSO
    clerk --> shipSO

    %% ===== Manager capabilities =====
    manager --> manageProducts
    manager --> manageWarehouses
    manager --> manageSuppliers
    manager --> approvePO
    manager --> viewDashboard
    manager --> viewReports
    manager --> viewAudit
    manager --> askAI

    %% ===== Admin capabilities =====
    admin --> manageUsers

    %% ===== AI dependency =====
    askAI -. uses .-> ai
```

## Role responsibility summary

| Use case | Clerk | Manager | Admin |
|---|:---:|:---:|:---:|
| Login / refresh token | ✅ | ✅ | ✅ |
| View inventory / availability | ✅ | ✅ | ✅ |
| Adjust / write-off stock | ✅ | ✅ | ✅ |
| Transfer stock between warehouses | ✅ | ✅ | ✅ |
| Create purchase order | ✅ | ✅ | ✅ |
| Receive goods | ✅ | ✅ | ✅ |
| Create / confirm / ship sales order | ✅ | ✅ | ✅ |
| Manage products / warehouses / suppliers | | ✅ | ✅ |
| Approve purchase order | | ✅ | ✅ |
| Dashboard, reports, audit trail | | ✅ | ✅ |
| Ask AI insight (NL query) | | ✅ | ✅ |
| Manage users & roles | | | ✅ |

> Inheritance arrows (`inherits`) mean a higher role can also perform every use case of the roles
> below it, so the concrete role→use-case arrows above only show each role's **additional**
> capabilities.
