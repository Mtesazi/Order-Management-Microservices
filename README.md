# Order Management Microservice (Spring Boot)

Small Spring Boot microservice that manages Products and Orders with a clean layered architecture.

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Security (HTTP Basic)
- Spring Data JPA + H2 (in-memory)
- Springdoc OpenAPI (Swagger UI)
- Lombok
- Maven

## Architecture

- `controller`: REST endpoints
- `service`: business logic and orchestration
- `repository`: persistence abstraction and JPA implementation
- `model`: JPA entities
- `dto`: API request/response contracts
- `mapper`: model-to-DTO conversion
- `exception`: centralized API error handling
- `conf`: security and OpenAPI configuration

## API Endpoints

### Products

- `POST /api/products` - create product
- `GET /api/products/{id}` - get product by ID
- `GET /api/products` - list all products
- `PATCH /api/products/{id}/stock` - update stock quantity

### Orders

- `POST /api/orders` - create order
- `GET /api/orders/{id}` - get order by ID
- `GET /api/orders` - list all orders

## Business Rules

When creating an order:

- all product IDs must exist
- total price is calculated from line totals (`price * quantity`)
- if any product is missing, the order is rejected with a clear `404` response
- if stock is insufficient, the order is rejected with a clear `400` response
- stock deduction and order persistence run in one transaction

## Persistence and Schema Decisions

H2 in-memory database is used (`jdbc:h2:mem:orderdb`). Schema is minimal and relational:

- `products`: catalog and stock (`name`, `description`, `price`, `stock_quantity`, `created_at`, `updated_at`)
- `orders`: order header (`status`, `total_amount`, `created_at`)
- `order_items`: line items (`order_id`, `product_id`, `product_name`, `unit_price`, `quantity`, `line_total`)

Relations:

- one-to-many from `orders` to `order_items` (`order_id`)
- `Order` owns lifecycle of `OrderItem` (`cascade = ALL`, `orphanRemoval = true`)
- `product_id` is stored on each order item as a business reference

## Security

- All `/api/**` endpoints require HTTP Basic authentication.
- Credentials are configurable via:
  - `spring.security.user.name`
  - `spring.security.user.password`
- Swagger is available for local exploration.
- H2 console is enabled only when the `dev` profile is active.

Default local credentials:

- Username: `apiuser`
- Password: `apipassword`

## API Documentation

- Swagger UI: `http://localhost:8083/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8083/v3/api-docs`

## Run

```bash
mvn spring-boot:run
```

Run with the dev profile to use the H2 console:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

When `dev` is active:

- H2 console: `http://localhost:8083/h2-console`

## Test

```bash
mvn test
```

Integration flow test: `src/test/java/com/pollinate/ProductOrderFlowTest.java`
