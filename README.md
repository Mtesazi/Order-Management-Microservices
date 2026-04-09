# Order Management Microservice (Spring Boot)

Small Spring Boot microservice demonstrating clean backend design for managing **Products** and **Orders**.

## Tech

- Java 21
- Spring Boot 3
- Maven
- In-memory repositories (`ConcurrentHashMap`)

## Project Structure

- `controller` - REST API endpoints
- `service` - business logic
- `repository` - in-memory persistence abstraction
- `model` - domain entities
- `dto` - API request/response contracts
- `mapper` - domain-to-DTO conversion
- `exception` - centralized API error handling

## API Endpoints

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

