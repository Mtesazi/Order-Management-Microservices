#!/bin/bash

# JWT Quick Reference - Common Commands

# ============================================================================
# BUILD AND RUN
# ============================================================================

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# ============================================================================
# JWT AUTHENTICATION - CURL EXAMPLES
# ============================================================================

# 1. LOGIN - Get JWT Token
echo "=== Step 1: Login and get JWT token ==="
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "apiuser",
    "password": "apipassword"
  }' | jq

# Store token in variable (for scripting)
TOKEN=$(curl -s -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"apiuser","password":"apipassword"}' | jq -r '.accessToken')

echo "Token: $TOKEN"

# 2. LIST PRODUCTS - Using JWT Token
echo "=== Step 2: List products with JWT ==="
curl -X GET http://localhost:8083/api/products \
  -H "Authorization: Bearer $TOKEN" | jq

# 3. CREATE PRODUCT - Using JWT Token
echo "=== Step 3: Create product with JWT ==="
curl -X POST http://localhost:8083/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "Gaming laptop",
    "price": 1200.00,
    "stockQuantity": 10
  }' | jq

# 4. GET PRODUCT - Using JWT Token
echo "=== Step 4: Get specific product ==="
curl -X GET http://localhost:8083/api/products/1 \
  -H "Authorization: Bearer $TOKEN" | jq

# 5. CREATE ORDER - Using JWT Token
echo "=== Step 5: Create order ==="
curl -X POST http://localhost:8083/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"productId": 1, "quantity": 2}
    ]
  }' | jq

# ============================================================================
# BACKWARD COMPATIBILITY - HTTP BASIC AUTH (Still Works)
# ============================================================================

# List products using HTTP Basic auth (credentials: apiuser:apipassword)
echo "=== Using HTTP Basic Auth (backward compatible) ==="
curl -X GET http://localhost:8083/api/products \
  -H "Authorization: Basic YXBpdXNlcjphcGlwYXNzd29yZA==" | jq

# Or with curl's -u flag
curl -u apiuser:apipassword http://localhost:8083/api/products | jq

# ============================================================================
# CONFIGURATION
# ============================================================================

# Set custom JWT secret (must be at least 32 characters)
export JWT_SECRET="my-very-long-secret-key-at-least-32-characters-long-here"

# Set token expiration (in milliseconds, 86400000 = 24 hours)
export JWT_EXPIRATION="86400000"

# Set custom API user credentials
export APP_BASIC_USER="admin"
export APP_BASIC_PASSWORD="securepassword"

# Run with custom settings
mvn spring-boot:run \
  -Djwt.secret="$JWT_SECRET" \
  -Djwt.expiration="$JWT_EXPIRATION" \
  -Dspring.security.user.name="$APP_BASIC_USER" \
  -Dspring.security.user.password="$APP_BASIC_PASSWORD"

# ============================================================================
# TROUBLESHOOTING
# ============================================================================

# Test if API requires authentication (should get 401)
echo "=== Test unauthenticated access (should be 401) ==="
curl -v http://localhost:8083/api/products

# Test invalid JWT token
echo "=== Test invalid token (should be 401) ==="
curl -X GET http://localhost:8083/api/products \
  -H "Authorization: Bearer invalid.token.here"

# Test with wrong credentials
echo "=== Test wrong credentials ==="
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "wronguser",
    "password": "wrongpass"
  }'

# Check H2 console (no auth required)
# Open browser: http://localhost:8083/h2-console

# ============================================================================
# ENVIRONMENT VARIABLES FOR PRODUCTION
# ============================================================================

# Example Docker deployment with JWT
# docker run -e JWT_SECRET="secure-secret-key-here" \
#            -e JWT_EXPIRATION="86400000" \
#            -e APP_BASIC_USER="apiuser" \
#            -e APP_BASIC_PASSWORD="apipassword" \
#            -p 8083:8083 \
#            order-management-microservices:1.0-SNAPSHOT

# ============================================================================
# DECODE JWT TOKEN (for debugging)
# ============================================================================

# Install jq if not available: apt-get install jq

# Decode JWT token (shows payload)
echo $TOKEN | cut -d'.' -f2 | base64 -d | jq

# Full JWT structure
echo "=== Full JWT Structure ==="
echo "Header:"
echo $TOKEN | cut -d'.' -f1 | base64 -d | jq

echo "Payload:"
echo $TOKEN | cut -d'.' -f2 | base64 -d | jq

echo "Signature: (cannot decode)"
echo $TOKEN | cut -d'.' -f3

# ============================================================================
# SECURITY CHECKLIST
# ============================================================================

# ✅ Before production deployment:
# - Set JWT_SECRET to a long random string (minimum 32 characters)
# - Use HTTPS/TLS for all communications
# - Store tokens securely on client (HttpOnly cookies recommended)
# - Set appropriate JWT_EXPIRATION based on security requirements
# - Use strong API user passwords
# - Enable audit logging
# - Monitor for suspicious authentication attempts
# - Implement token refresh mechanism
# - Consider implementing rate limiting

# ============================================================================

