# 🧾 Inventory Service – E-Commerce Microservice

## 📌 Overview

The **Inventory Service** is a core microservice in a distributed e-commerce platform responsible for managing product stock, ensuring consistency during checkout, and maintaining an audit trail of all inventory operations.

The service follows **domain-driven design principles**, implements **reservation-based stock management**, and supports **fault-tolerant communication** within a distributed microservices ecosystem.

It is fully integrated with:

* Service Discovery (Eureka)
* API Gateway
* Client-Side Load Balancing
* OpenFeign
* Resilience4j
* JWT Authentication & RBAC

---

## 🔗 Links

* 📂 GitHub Repository: https://github.com/ekanath-smr/ecommerce-inventory-service
* 📘 Swagger UI: http://localhost:8070/swagger-ui/index.html
* 📄 OpenAPI Docs: http://localhost:8070/v3/api-docs

---

# 🚀 Features

## 🏪 Inventory Management

* Create inventory for valid products
* Add stock to existing inventory
* Fetch inventory details by product
* Real-time stock availability checks

---

## ⚙️ Domain-Driven Stock Operations

Inventory is managed through explicit business commands instead of generic CRUD operations.

### Supported Operations

* Reserve Stock → Temporarily lock stock during checkout
* Release Stock → Unlock reserved stock on cancellation/failure
* Confirm Sale → Move stock from reserved → sold
* Undo Sale → Compensation operation (sold → reserved)

This approach ensures:

* Strict control over stock transitions
* Prevention of invalid inventory states
* Better auditability
* Easier implementation of distributed transactions

---

## 🔐 Security & Access Control

### Authentication

* JWT-based Authentication

### Authorization

Role-Based Access Control (RBAC)

| Role  | Permissions                                |
| ----- | ------------------------------------------ |
| ADMIN | Create Inventory, Add Stock                |
| USER  | Reserve Stock, Release Stock, Confirm Sale |

---

## 🔗 Inter-Service Communication

### Product Service Integration

The Inventory Service communicates with Product Service using OpenFeign.

Before inventory creation:

1. Product existence is validated
2. Inventory is created only for valid products

### Design Principle

* Product Service → Source of Truth
* Inventory Service → Dependent Validator

---

## ⚡ Fault Tolerance & Resilience

Integrated with Resilience4j.

### Circuit Breaker

Prevents cascading failures when Product Service becomes unavailable.

### Retry

Automatically retries transient failures before failing requests.

Benefits:

* Improved reliability
* Better user experience
* Reduced service outages

---

## 🌐 Service Discovery

Integrated with Eureka Service Discovery.

### Benefits

* Automatic service registration
* Dynamic service lookup
* No hardcoded service URLs
* Better scalability

Inventory Service registers itself with Eureka at startup and can be discovered by other services.

---

## 🚪 API Gateway Integration

All external requests can be routed through the API Gateway.

Benefits:

* Single entry point for clients
* Centralized routing
* Security enforcement
* Future rate limiting support
* Request filtering and monitoring

Example:

```http
http://localhost:8080/inventory/**
```

instead of directly calling:

```http
http://localhost:8070/**
```

---

## ⚖️ Client-Side Load Balancing

The service supports client-side load balancing through Spring Cloud LoadBalancer.

### Example Scenario

Multiple Product Service instances:

```text
Product-Service-1 : 8081
Product-Service-2 : 8082
```

Feign Client automatically distributes requests across available instances.

Benefits:

* Horizontal scalability
* High availability
* Better fault tolerance

---

## 📊 Inventory Transactions (Audit Trail)

Every inventory operation is recorded.

Supported transaction types:

* STOCK_ADDED
* STOCK_RESERVED
* STOCK_RELEASED
* SALE_CONFIRMED
* UNDO_SALE

Benefits:

* Auditing
* Debugging
* Reconciliation
* Operational observability

---

## 🧠 Consistency & Distributed Design

### Strong Consistency

Inventory creation validates products synchronously via Product Service.

### Distributed Consistency

Checkout flow follows a Saga-style compensation pattern.

### Checkout Workflow

1. Reserve Stock
2. Confirm Sale

If downstream failure occurs:

3. Undo Sale
4. Release Reserved Stock

Current implementation:

* Synchronous compensation

Future enhancement:

* Kafka-based Event-Driven Saga

---

# 🏗️ Tech Stack

* Java 21
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* Hibernate
* MySQL
* H2 Database
* OpenFeign
* Eureka Discovery Client
* Spring Cloud LoadBalancer
* API Gateway Integration
* Resilience4j
* Lombok
* Maven
* Swagger/OpenAPI
* JUnit 5
* Mockito

---

# 📂 Project Structure

```text
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
├── configs
└── exceptions

src/test/java

└── services
```

---

# 🔄 API Endpoints

## Inventory APIs

### Create Inventory

```http
POST /inventory
```

### Get Inventory

```http
GET /inventory/{productId}
```

---

## Stock Operations

### Add Stock

```http
POST /inventory/{productId}/add-stock
```

### Reserve Stock

```http
POST /inventory/{productId}/reserve
```

### Release Reserved Stock

```http
POST /inventory/{productId}/release
```

### Confirm Sale

```http
POST /inventory/{productId}/confirm-sale
```

### Undo Sale

```http
POST /inventory/{productId}/undo-sale
```

---

## Stock Queries

### Check Stock Availability

```http
GET /inventory/{productId}/in-stock
```

### Get Available Stock

```http
GET /inventory/{productId}/available-stock
```

---

# 🛡️ Error Handling

Centralized exception handling using:

```java
@RestControllerAdvice
```

Handles:

* InventoryNotFoundException
* InventoryAlreadyExistsException
* InsufficientStockException
* InvalidInventoryOperationException
* ProductNotFoundException
* ProductServiceUnavailableException
* Validation Exceptions
* Runtime Exceptions

---

# 🧪 Testing

Unit tests implemented using:

* JUnit 5
* Mockito

Coverage includes:

* Inventory Creation
* Stock Addition
* Stock Reservation
* Sale Confirmation
* Sale Rollback
* Inventory Queries
* Failure Scenarios

Run tests:

```bash
mvn test
```

---

# 📈 Logging Strategy

Implemented structured logging using SLF4J.

| Level | Usage                 |
| ----- | --------------------- |
| INFO  | Business Operations   |
| DEBUG | Internal Processing   |
| WARN  | Invalid Requests      |
| ERROR | Failures & Exceptions |

---

# 🔄 Example Checkout Flow

```text
Order Created
      |
      v

Reserve Stock
      |
      v

Confirm Sale
      |
      v

Order Success
```

Failure Path:

```text
Reserve Stock
      |
      v

Confirm Sale
      |
      X Failure

Undo Sale
      |
      v

Release Stock
```

---

# 🚧 Future Enhancements

* Kafka Event-Driven Architecture
* Distributed Saga Orchestration
* Distributed Tracing (Zipkin)
* Centralized Logging (ELK)
* Redis Caching
* Prometheus + Grafana Monitoring
* Idempotent Inventory Operations

---

# 💡 Design Highlights

* Domain-Driven Inventory APIs
* Reservation-Based Stock Management
* Strong Consistency Guarantees
* Saga-Based Compensation Flow
* Feign-Based Service Communication
* Eureka Service Discovery
* API Gateway Routing
* Client-Side Load Balancing
* Resilience4j Fault Tolerance
* Full Inventory Audit Trail

---

# 🏆 Resume Highlights

* Designed and developed an Inventory Microservice using Spring Boot and MySQL
* Implemented reservation-based stock management to prevent overselling
* Integrated OpenFeign for inter-service communication
* Implemented Eureka Service Discovery and API Gateway architecture
* Enabled client-side load balancing using Spring Cloud LoadBalancer
* Integrated Resilience4j Circuit Breaker and Retry patterns
* Built JWT-based Authentication and Role-Based Authorization
* Implemented Saga-style compensation flow for distributed consistency
* Added transaction logging and audit trail for inventory operations

---

# 🧑‍💻 Author

**Ekanath S M R**

Backend Engineer | Java | Spring Boot | Microservices

GitHub:
https://github.com/ekanath-smr
