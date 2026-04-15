# JWT Implementation Guide

## Overview
JWT (JSON Web Token) authentication has been successfully integrated into the Order Management Microservices project. The system now supports both JWT token-based authentication (primary) and HTTP Basic authentication (for backward compatibility).

## Features Added

### 1. JWT Dependencies
- `jjwt-api@0.12.1` - JWT API
- `jjwt-impl@0.12.1` - JWT implementation
- `jjwt-jackson@0.12.1` - Jackson support for JWT

### 2. New Components

#### JwtTokenProvider (`src/main/java/com/pollinate/conf/JwtTokenProvider.java`)
- `generateToken(Authentication)` - Generate JWT token from Spring Authentication object
- `generateTokenFromUsername(String)` - Generate JWT token from username
- `getUsernameFromToken(String)` - Extract username from JWT token
- `validateToken(String)` - Validate JWT token signature and expiration

#### JwtAuthenticationFilter (`src/main/java/com/pollinate/conf/JwtAuthenticationFilter.java`)
- Filters incoming HTTP requests to extract and validate JWT tokens
- Automatically sets authentication in Spring Security context if token is valid
- Expects tokens in the `Authorization` header with format: `Bearer <token>`

#### AuthenticationController (`src/main/java/com/pollinate/controller/AuthenticationController.java`)
- `POST /api/auth/login` - Authenticate user and receive JWT token

#### DTOs
- `LoginRequest` - Contains username and password
- `JwtResponse` - Contains JWT token and token type

### 3. Configuration Updates

#### SecurityConfig
- Stateless session management for JWT
- JWT token validation on all `/api/**` endpoints
- Public login endpoint at `/api/auth/login`
- Backward compatibility with HTTP Basic authentication
- H2 console exempted from authentication

#### Application Properties
New JWT configuration properties:
```properties
jwt.secret=${JWT_SECRET:mySecretKeyThatIsAtLeast32CharactersLongForHS256Algorithm}
jwt.expiration=${JWT_EXPIRATION:86400000}  # 24 hours in milliseconds
```

## Usage Guide

### 1. Get JWT Token

**Request:**
```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "apiuser",
    "password": "apipassword"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcGl1c2VyIiwiaWF0IjoxNjE2MjM5MDIyLCJleHAiOjE2MTYzMjU0MjJ9.signature...",
  "tokenType": "Bearer"
}
```

### 2. Use JWT Token for API Requests

**Request:**
```bash
curl -X GET http://localhost:8083/api/products \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcGl1c2VyIiwiaWF0IjoxNjE2MjM5MDIyLCJleXAiOjE2MTYzMjU0MjJ9.signature..."
```

### 3. HTTP Basic Authentication (Backward Compatible)

The system still supports HTTP Basic authentication for testing:

```bash
curl -X GET http://localhost:8083/api/products \
  -H "Authorization: Basic YXBpdXNlcjphcGlwYXNzd29yZA=="
```

## Configuration

### Environment Variables

Set these environment variables to customize JWT:

```bash
# Custom JWT secret (minimum 32 characters for HS256)
export JWT_SECRET="your-very-long-secret-key-at-least-32-chars-long"

# Token expiration in milliseconds (default: 86400000 = 24 hours)
export JWT_EXPIRATION="86400000"

# API user credentials
export APP_BASIC_USER="apiuser"
export APP_BASIC_PASSWORD="apipassword"
```

## Security Notes

1. **Secret Key**: The JWT secret should be at least 32 characters long for HS256 algorithm
2. **Token Storage**: Store tokens securely on the client side (HttpOnly cookies recommended)
3. **Token Expiration**: Tokens expire after 24 hours by default
4. **HTTPS**: Always use HTTPS in production
5. **CSRF Protection**: Disabled for stateless API (suitable for microservices)

## Testing

All existing tests continue to pass with HTTP Basic authentication. To test JWT:

```bash
# Get token
TOKEN=$(curl -s -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"apiuser","password":"apipassword"}' | jq -r '.accessToken')

# Use token
curl -X GET http://localhost:8083/api/products \
  -H "Authorization: Bearer $TOKEN"
```

## Endpoints

### Public Endpoints (No Authentication Required)
- `POST /api/auth/login` - Get JWT token
- `GET /h2-console/**` - H2 database console

### Protected Endpoints (Require JWT or HTTP Basic)
- `GET /api/products` - List products
- `POST /api/products` - Create product
- `GET /api/products/{id}` - Get product
- `POST /api/orders` - Create order
- `GET /api/orders` - List orders
- All other `/api/**` endpoints

## Migration from HTTP Basic to JWT

For existing clients using HTTP Basic authentication:

1. Call `POST /api/auth/login` with username and password
2. Extract the JWT token from the response
3. Use the token in the `Authorization: Bearer <token>` header for subsequent requests
4. Handle token expiration by getting a new token when needed

## Troubleshooting

### 401 Unauthorized
- Ensure the token is provided in the `Authorization: Bearer <token>` format
- Check if the token has expired
- Verify the JWT secret matches the one used to generate the token

### 403 Forbidden
- Ensure the user has the required permissions
- Check if the endpoint requires authentication

### Invalid Token
- Verify the token format and signature
- Check if the secret key matches
- Ensure the token has not been tampered with

## Architecture

```
HTTP Request with JWT Token
         ↓
JwtAuthenticationFilter
         ↓
Extract token from Authorization header
         ↓
JwtTokenProvider.validateToken()
         ↓
Extract username and set Spring Security context
         ↓
Request proceeds to controller with authentication
```

