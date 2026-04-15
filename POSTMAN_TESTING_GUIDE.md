# Postman Testing Guide for JWT Authentication

## Prerequisites

1. **Postman installed** - Download from https://www.postman.com/downloads/
2. **Application running** - Start with `mvn spring-boot:run` on port 8083
3. **Create a new Postman Collection** - For organizing your requests

---

## Method 1: Manual Testing (Step by Step)

### Step 1: Get JWT Token

**1. Create a new POST request**
- Click **+** to create new request
- Set request name: `Login - Get JWT Token`
- Select **POST** method
- Enter URL: `http://localhost:8083/api/auth/login`

**2. Add request body**
- Go to **Body** tab
- Select **raw** → **JSON**
- Paste this JSON:
```json
{
  "username": "apiuser",
  "password": "apipassword"
}
```

**3. Send request**
- Click **Send**
- You should receive a response like:
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcGl1c2VyIiwiaWF0IjoxNjE2MjM5MDIyLCJleHAiOjE2MTYzMjU0MjJ9.signature...",
  "tokenType": "Bearer"
}
```

**4. Copy the token**
- Select the entire `accessToken` value (without quotes)
- Copy it for the next steps

---

### Step 2: List Products Using JWT Token

**1. Create a new GET request**
- Click **+** to create new request
- Set request name: `Get Products - With JWT`
- Select **GET** method
- Enter URL: `http://localhost:8083/api/products`

**2. Add Authorization header**
- Go to **Headers** tab
- Add new header:
  - Key: `Authorization`
  - Value: `Bearer <paste-your-token-here>`

Example:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcGl1c2VyIiwiaWF0IjoxNjE2MjM5MDIyLCJleHAiOjE2MTYzMjU0MjJ9.signature...
```

**3. Send request**
- Click **Send**
- Should get 200 OK with products list (or empty array if no products)

---

### Step 3: Create a Product Using JWT Token

**1. Create a new POST request**
- Click **+** to create new request
- Set request name: `Create Product - With JWT`
- Select **POST** method
- Enter URL: `http://localhost:8083/api/products`

**2. Add headers**
- Go to **Headers** tab
- Add header:
  - Key: `Authorization`
  - Value: `Bearer <your-token>`
- Add header:
  - Key: `Content-Type`
  - Value: `application/json`

**3. Add request body**
- Go to **Body** tab
- Select **raw** → **JSON**
- Paste this JSON:
```json
{
  "name": "Gaming Laptop",
  "description": "High-performance gaming laptop with RTX 4090",
  "price": 2499.99,
  "stockQuantity": 5
}
```

**4. Send request**
- Click **Send**
- Should get 201 Created with product details

---

### Step 4: Create an Order Using JWT Token

**1. Create a new POST request**
- Click **+** to create new request
- Set request name: `Create Order - With JWT`
- Select **POST** method
- Enter URL: `http://localhost:8083/api/orders`

**2. Add headers**
- Key: `Authorization`
- Value: `Bearer <your-token>`

**3. Add request body**
- Go to **Body** tab
- Select **raw** → **JSON**
- Paste this JSON (use product ID from previous step):
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

**4. Send request**
- Click **Send**
- Should get 201 Created with order details

---

## Method 2: Using Postman Environments (Advanced)

This method allows you to store the token automatically and reuse it across requests.

### Step 1: Create Environment

**1. Create new environment**
- Click **Environments** on the left sidebar
- Click **+** or **Create**
- Name it: `JWT Local Development`

**2. Add variables**
- Add variable `base_url`:
  - Initial value: `http://localhost:8083`
  - Current value: `http://localhost:8083`
- Add variable `token`:
  - Initial value: `` (empty)
  - Current value: `` (empty)

**3. Save the environment**
- Click **Save**
- Select this environment in the top right dropdown

---

### Step 2: Set Up Login Request with Token Extraction

**1. Create POST login request**
- URL: `{{base_url}}/api/auth/login`
- Body (raw JSON):
```json
{
  "username": "apiuser",
  "password": "apipassword"
}
```

**2. Add Tests tab (for auto token extraction)**
- Click **Tests** tab (next to Body)
- Paste this JavaScript code:
```javascript
// Extract token from response and save to environment variable
var jsonData = pm.response.json();
pm.environment.set("token", jsonData.accessToken);
console.log("Token saved: " + jsonData.accessToken.substring(0, 20) + "...");
```

**3. Save the request**
- Click **Save** (Ctrl+S)

---

### Step 3: Create Requests Using Environment Variables

**1. Get Products request**
- Method: **GET**
- URL: `{{base_url}}/api/products`
- Headers tab:
  - Key: `Authorization`
  - Value: `Bearer {{token}}`

**2. Create Product request**
- Method: **POST**
- URL: `{{base_url}}/api/products`
- Headers:
  - Key: `Authorization`
  - Value: `Bearer {{token}}`
  - Key: `Content-Type`
  - Value: `application/json`
- Body (raw JSON):
```json
{
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse",
  "price": 49.99,
  "stockQuantity": 20
}
```

**3. Create Order request**
- Method: **POST**
- URL: `{{base_url}}/api/orders`
- Headers:
  - Key: `Authorization`
  - Value: `Bearer {{token}}`
  - Key: `Content-Type`
  - Value: `application/json`
- Body (raw JSON):
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 1
    }
  ]
}
```

---

## Method 3: Using Postman Collection (Import Ready)

You can import this collection directly into Postman:

**1. Create collection**
- Right-click in left sidebar → **Create collection**
- Name: `Order Management JWT API`

**2. Add the following requests:**

### Request 1: Login
```
Method: POST
URL: {{base_url}}/api/auth/login
Body (raw JSON):
{
  "username": "apiuser",
  "password": "apipassword"
}
Tests script:
var jsonData = pm.response.json();
pm.environment.set("token", jsonData.accessToken);
```

### Request 2: Get Products
```
Method: GET
URL: {{base_url}}/api/products
Headers:
- Authorization: Bearer {{token}}
```

### Request 3: Create Product
```
Method: POST
URL: {{base_url}}/api/products
Headers:
- Authorization: Bearer {{token}}
- Content-Type: application/json
Body (raw JSON):
{
  "name": "Test Product",
  "description": "Test Description",
  "price": 99.99,
  "stockQuantity": 10
}
```

### Request 4: Get Product by ID
```
Method: GET
URL: {{base_url}}/api/products/1
Headers:
- Authorization: Bearer {{token}}
```

### Request 5: Create Order
```
Method: POST
URL: {{base_url}}/api/orders
Headers:
- Authorization: Bearer {{token}}
- Content-Type: application/json
Body (raw JSON):
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

### Request 6: Get Orders
```
Method: GET
URL: {{base_url}}/api/orders
Headers:
- Authorization: Bearer {{token}}
```

---

## Testing Error Scenarios

### Test 1: Access API Without Token (Should Return 401)

**1. Create GET request**
- URL: `http://localhost:8083/api/products`
- **Do NOT add Authorization header**

**2. Send request**
- Click **Send**
- Expected: **401 Unauthorized**

---

### Test 2: Access API With Invalid Token (Should Return 401)

**1. Create GET request**
- URL: `http://localhost:8083/api/products`
- Headers:
  - Key: `Authorization`
  - Value: `Bearer invalid.token.here`

**2. Send request**
- Click **Send**
- Expected: **401 Unauthorized**

---

### Test 3: Wrong Credentials (Should Return 401)

**1. Create POST request**
- URL: `http://localhost:8083/api/auth/login`
- Body (raw JSON):
```json
{
  "username": "wronguser",
  "password": "wrongpassword"
}
```

**2. Send request**
- Click **Send**
- Expected: **401 Unauthorized**

---

### Test 4: Test HTTP Basic Auth (Backward Compatibility)

**1. Create GET request**
- URL: `http://localhost:8083/api/products`
- Go to **Authorization** tab
- Select **Basic Auth**
- Username: `apiuser`
- Password: `apipassword`

**2. Send request**
- Click **Send**
- Expected: **200 OK** (HTTP Basic still works!)

---

## Complete Request Flow

Here's the recommended order to test:

1. **Login Request** → Get JWT token
   - Status: 200 OK
   - Copy token from response

2. **List Products** → Use JWT token
   - Status: 200 OK
   - Response: `[]` (empty array initially)

3. **Create Product** → Use JWT token
   - Status: 201 Created
   - Response: Product object with ID

4. **Get Product by ID** → Use JWT token
   - Status: 200 OK
   - Response: Product details

5. **Create Order** → Use JWT token
   - Status: 201 Created
   - Response: Order object

6. **Test 401 Errors** → No/Invalid token
   - Expected: Unauthorized errors

---

## Postman Tips & Tricks

### 1. View Response Headers
- After sending request, click **Headers** in response section
- You'll see the server's response headers

### 2. Pretty Print JSON
- Response automatically formats JSON
- Click the **Preview** tab for better formatting

### 3. Check Response Time
- Look at bottom right corner for response time
- Helps identify slow endpoints

### 4. Save Request History
- Postman automatically saves sent requests
- Click **History** in left sidebar to view

### 5. Use Pre-request Scripts
- Add this to login request's **Pre-request Script** tab:
```javascript
console.log("Attempting to login...");
console.log("Timestamp: " + new Date().toString());
```

### 6. Check Token Expiration
- Base64 decode the token's payload
- Copy the middle part (between dots)
- Decode it online at https://jwt.io/
- Check the `exp` field

---

## Troubleshooting

### Issue: 401 Unauthorized on protected endpoints

**Solutions:**
1. Check if token is copied completely (no extra spaces)
2. Verify header format: `Authorization: Bearer <token>`
3. Get a fresh token with login request
4. Check if token has expired (24-hour default)

### Issue: 400 Bad Request

**Solutions:**
1. Check JSON format is valid
2. Ensure all required fields are present
3. Verify Content-Type header is `application/json`

### Issue: 404 Not Found

**Solutions:**
1. Check base URL is correct: `http://localhost:8083`
2. Verify endpoint URL is correct
3. Ensure application is running

### Issue: Connection Error

**Solutions:**
1. Start application: `mvn spring-boot:run`
2. Check port is 8083
3. Verify firewall allows localhost access
4. Try `http://127.0.0.1:8083` instead of `localhost`

---

## Export/Share Collection

To share your Postman collection:

1. Right-click collection → **Export**
2. Choose format (JSON v2.1 recommended)
3. Share the exported file
4. Others can import it: **File** → **Import** → **Upload Files**

---

## Quick Reference Table

| Endpoint | Method | Auth Required | Body | Purpose |
|----------|--------|---------------|------|---------|
| `/api/auth/login` | POST | No | Username/Password | Get JWT token |
| `/api/products` | GET | Yes | None | List products |
| `/api/products` | POST | Yes | Product data | Create product |
| `/api/products/{id}` | GET | Yes | None | Get product details |
| `/api/orders` | GET | Yes | None | List orders |
| `/api/orders` | POST | Yes | Order items | Create order |
| `/h2-console` | GET | No | None | Database console |

---

## Next Steps

1. ✅ Test login endpoint
2. ✅ Extract token from response
3. ✅ Use token in API requests
4. ✅ Test error scenarios
5. ✅ Set up environment variables
6. ✅ Create reusable collection

Happy testing! 🚀

