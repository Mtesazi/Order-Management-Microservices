<<<<<<< HEAD
# Order Management Microservice (Spring Boot)

Small Spring Boot microservice demonstrating clean backend design for managing **Products** and **Orders**.

## Tech

- Java 21
- Spring Boot 3
- Maven
- Spring Data JPA
- H2 in-memory database

## Project Structure

- `controller` - REST API endpoints
- `service` - business logic
- `repository` - persistence abstraction backed by H2 (in-memory)
- `model` - domain entities
- `dto` - API request/response contracts
- `mapper` - domain-to-DTO conversion
- `exception` - centralized API error handling

## API Endpoints

All API endpoints require Basic Authentication.

- Username: `apiuser`
- Password: `apipassword`
- Config keys: `spring.security.user.name`, `spring.security.user.password`

### Products

- `POST /api/products` - create a product
- `GET /api/products` - list products
- `GET /api/products/{id}` - get one product
- `PATCH /api/products/{id}/stock` - update stock

Example create request:

```json
{
  "name": "Laptop",
  "description": "14-inch business laptop",
  "price": 1200.00,
  "stockQuantity": 5
}
```

### Orders

- `POST /api/orders` - create an order from one or more product items
- `GET /api/orders` - list orders
- `GET /api/orders/{id}` - get one order

Example create request:

```json
{
  "items": [
    {"productId": 1, "quantity": 2}
  ]
}
```

## Run

```bash
mvn spring-boot:run
```

Default port is configured in `src/main/resources/application.properties`.

H2 console (while app is running):

- URL: `http://localhost:8083/h2-console`
- JDBC URL: `jdbc:h2:mem:orderdb`

## Schema Decisions

### Tables

- `products`: product catalog with `name`, `description`, `price`, `stock_quantity`, `created_at`, `updated_at`
- `orders`: order header with `status`, `total_amount`, `created_at`
- `order_items`: line items with `order_id`, `product_id`, `product_name`, `unit_price`, `quantity`, `line_total`

### Relationships

- One order has many order items (`orders.id` -> `order_items.order_id`)
- `Order` owns `OrderItem` lifecycle (`cascade = ALL`, `orphanRemoval = true`)
- `order_items.product_id` is stored as a reference value (not an enforced foreign key)

### Design Rationale

- Minimal schema focused on required endpoints (create/get/list for products and orders)
- Order items snapshot product name and unit price at order time to preserve historical accuracy
- H2 is in-memory for fast local/test execution; schema is created and dropped automatically
- Stock updates and order creation run transactionally so failed orders do not leave partial stock changes

## Test Harness

`src/test/java/com/pollinate/ProductOrderFlowTest.java` validates a full flow:

1. Create product
2. Create order
3. Verify stock decrement
4. Verify insufficient-stock error handling

Run tests:

```bash
mvn test
```

=======
# Order-Management-Microservices
Microservices that manages Product and Orders
>>>>>>> origin/main
