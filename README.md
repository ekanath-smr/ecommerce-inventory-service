# 🧾 Inventory Service – E-Commerce Microservice

## 📌 Overview
The **Inventory Service** is a core microservice in a distributed e-commerce system responsible for managing product stock, ensuring consistency during checkout, and maintaining an audit trail of all inventory operations.

It is designed using **domain-driven principles**, **strong consistency guarantees**, and **fault-tolerant communication patterns**.

---

## 🔗 Links
- 📂 GitHub Repository: https://github.com/ekanath-smr/ecommerce-inventory-service
- 📘 Swagger UI: http://localhost:8070/swagger-ui/index.html
- 📄 OpenAPI Docs: http://localhost:8070/v3/api-docs

---

## 🚀 Key Features

### 🏪 Inventory Management
- Create inventory for valid products
- Add stock to existing inventory
- Fetch inventory details by product

### ⚙️ Domain-Driven Stock Operations
Inventory is managed using **explicit business commands** instead of generic CRUD:

- Reserve Stock → Temporarily lock stock during checkout
- Release Stock → Unlock reserved stock on failure/cancellation
- Confirm Sale → Move stock from reserved → sold
- Undo Sale → Compensation (sold → reserved)

This ensures **strict control over state transitions** and prevents invalid updates.

---

## 🔐 Security & Access Control
- JWT-based authentication
- Role-Based Access Control (RBAC)

| Role  | Permissions |
|------|------------|
| ADMIN | Create inventory, add stock |
| USER  | Reserve, release, confirm sale |

---

## 🔗 Inter-Service Communication
- Uses **OpenFeign** for communication with Product Service
- Validates product existence before creating inventory

**Design Principle:**
- Product Service → Source of Truth
- Inventory Service → Dependent validator

---

## ⚡ Fault Tolerance & Resilience
Integrated **Resilience4j**:
- Circuit Breaker → Prevents cascading failures
- Retry → Handles transient failures

---

## 📊 Inventory Transactions (Audit Trail)
All operations are recorded:

- STOCK_ADDED
- STOCK_RESERVED
- STOCK_RELEASED
- SALE_CONFIRMED
- UNDO_SALE

Enables debugging, reconciliation, and observability.

---

## 🧠 Consistency & Distributed Design

### Strong Consistency
- Product validation via synchronous API call

### Distributed Consistency (Saga Pattern)
Checkout flow:

1. Reserve stock
2. Confirm sale
3. On failure:
   - Undo confirmed sale
   - Release reserved stock

*Note: Currently synchronous compensation. Future → Kafka-based async Saga.*

---

## 🏗️ Tech Stack
- Java 21
- Spring Boot
- Spring Security (JWT + RBAC)
- Spring Data JPA (Hibernate)
- MySQL / H2
- OpenFeign
- Resilience4j
- Lombok
- Maven
- Swagger / OpenAPI

---

## 📂 Project Structure

```
src/main/java/com/example/ecommerce_inventory_service
├── controllers
├── services
├── clients
├── repositories
├── models
├── dtos
├── mappers
├── security
├── advices
└── configs

src/test/java
└── services
```

---

## 🔄 API Endpoints

### Inventory APIs
POST   /inventory  
GET    /inventory/{productId}

### Stock Operations
POST   /inventory/{productId}/add-stock  
POST   /inventory/{productId}/reserve  
POST   /inventory/{productId}/release  
POST   /inventory/{productId}/confirm-sale  
POST   /inventory/{productId}/undo-sale

### Stock Queries
GET    /inventory/{productId}/in-stock  
GET    /inventory/{productId}/available-stock

---

## ⚙️ Configuration

### Database
```
spring.datasource.url=jdbc:mysql://localhost:3306/inventoryService
spring.jpa.hibernate.ddl-auto=update
```

### Product Service (Feign)
```
product.service.url=http://localhost:8080
```

### Resilience4j
```
resilience4j.circuitbreaker.instances.productService.failureRateThreshold=50
resilience4j.retry.instances.productService.maxAttempts=3
```

---

## 🛡️ Error Handling
Centralized using `@RestControllerAdvice`.

Handles:
- InventoryNotFoundException
- InsufficientStockException
- ProductNotFoundException
- ProductServiceUnavailableException
- InvalidInventoryOperationException

---

## 📈 Logging Strategy

| Level | Usage |
|------|------|
| INFO | Business operations |
| WARN | Invalid operations |
| ERROR | Failures |

---

## 🔄 Example Checkout Flow

1. Reserve stock
2. Confirm sale
3. On failure:
   - Undo sale
   - Release stock

---

## 🚧 Future Enhancements
- Kafka-based event-driven architecture
- Distributed tracing (Zipkin)
- Centralized logging (ELK)
- Service discovery (Eureka)
- Idempotency support

---

## 💡 Design Highlights
- Domain-driven APIs (command-based design)
- Saga-based compensation logic
- Fault-tolerant communication
- Full audit trail for inventory

---

## 🏆 Resume Highlights
- Designed Inventory microservice using Spring Boot & MySQL
- Implemented JWT + RBAC security
- Built Feign-based inter-service communication
- Integrated Resilience4j (Circuit Breaker + Retry)
- Implemented Saga-based consistency model
- Added transaction logging for auditability

---

## 🧑‍💻 Author
**Ekanath S M R**  
Backend Engineer | Java + Spring Boot Developer

GitHub:  
https://github.com/ekanath-smr