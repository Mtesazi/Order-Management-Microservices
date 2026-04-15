# Postman Quick Start - Visual Guide

## 🚀 Fastest Way to Get Started

### Option A: Import Ready-Made Collection (Recommended)

**1. Download the collection file**
```
File: Order_Management_JWT_API.postman_collection.json
Location: Project root directory
```

**2. Open Postman**
- Click **File** → **Import**
- Select the `.json` file
- Click **Import**

**3. Set Environment**
- Collections loaded! ✅
- Top right dropdown: select environment `JWT Local Development`

**4. Run Tests**
- Expand "Authentication" folder
- Click "Login - Get JWT Token"
- Click **Send** button
- Token automatically saved! 🎉

---

## ⚙️ Manual Setup (5 minutes)

### Step 1: Create Environment
```
Click: Environments (left sidebar)
      → + Create
Name: Local Dev
Add variables:
  base_url = http://localhost:8083
  token = (leave empty)
Save ✅
```

### Step 2: First Request - Login
```
+ New
POST
URL: http://localhost:8083/api/auth/login

Body → raw → JSON:
{
  "username": "apiuser",
  "password": "apipassword"
}

Send ✅
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ...",
  "tokenType": "Bearer"
}
```

### Step 3: Copy Token
1. Select `accessToken` value (without quotes)
2. Right-click → Copy
3. Store temporarily

### Step 4: Get Products with Token
```
+ New
GET
URL: http://localhost:8083/api/products

Headers:
  Authorization: Bearer [PASTE_TOKEN_HERE]

Send ✅
```

---

## 📋 Complete Testing Checklist

### ✅ Authentication Tests
```
[ ] Login with correct credentials → 200 ✅
[ ] Login with wrong credentials → 401 ❌
[ ] Get Products with token → 200 ✅
[ ] Get Products without token → 401 ❌
[ ] Get Products with invalid token → 401 ❌
```

### ✅ Product Tests
```
[ ] List all products → 200 ✅
[ ] Create product → 201 ✅
[ ] Get product by ID → 200 ✅
[ ] Get non-existent product → 404 ❌
```

### ✅ Order Tests
```
[ ] List all orders → 200 ✅
[ ] Create order → 201 ✅
[ ] Create order with insufficient stock → 400 ❌
```

### ✅ Security Tests
```
[ ] HTTP Basic Auth still works → 200 ✅
[ ] Expired token handling (optional)
[ ] CSRF protection disabled (expected) ✅
```

---

## 🎯 Step-by-Step Screenshots

### Login Request Setup
```
POST http://localhost:8083/api/auth/login
┌─────────────────────────────────────────────┐
│ Headers │ Body │ Tests │                    │
├─────────────────────────────────────────────┤
│ raw | JSON                                  │
│                                             │
│ {                                           │
│   "username": "apiuser",                    │
│   "password": "apipassword"                 │
│ }                                           │
└─────────────────────────────────────────────┘
                    ↓
                [ Send ]
                    ↓
        ✅ Status: 200 OK
        Token: eyJhbG...
```

### Use Token in Request
```
GET http://localhost:8083/api/products
┌─────────────────────────────────────────────┐
│ Params | Headers | Authorization | Body    │
├─────────────────────────────────────────────┤
│ Authorization: Bearer eyJhbGc...            │
│ Content-Type: application/json              │
└─────────────────────────────────────────────┘
                    ↓
                [ Send ]
                    ↓
        ✅ Status: 200 OK
        Response: [...]
```

---

## 🔧 Environment Variables Setup

### Create Variables
```
1. Click: Environment dropdown (top-right)
2. Click: Edit or Manage Environments
3. Click: Create
4. Name: "Local Dev"
5. Add variables:
   
   Variable Name    | Current Value
   ────────────────────────────────
   base_url         | http://localhost:8083
   token            | (auto-filled after login)
   product_id       | (auto-filled after create)
```

### Use Variables in Requests
```
Instead of: http://localhost:8083/api/products
Use:        {{base_url}}/api/products

Instead of: Authorization: Bearer xyz...
Use:        Authorization: Bearer {{token}}
```

---

## 💡 Time-Saving Tips

### Tip 1: Auto-Extract Token
Add this to **Tests** tab of Login request:
```javascript
var token = pm.response.json().accessToken;
pm.environment.set("token", token);
```

### Tip 2: Reuse Responses
After creating product, get ID:
```javascript
var productId = pm.response.json().id;
pm.environment.set("product_id", productId);
```

### Tip 3: Check Response Status
```javascript
pm.test("Status is 200", function () {
    pm.response.to.have.status(200);
});
```

### Tip 4: View Response Times
Look at bottom-right: `200 ms` = response time

---

## 📊 Request Examples

### 1️⃣ Login
```
POST /api/auth/login
Body: {"username":"apiuser","password":"apipassword"}
Expected: 200 + token
```

### 2️⃣ Create Product
```
POST /api/products
Header: Authorization: Bearer {{token}}
Body: 
{
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 1299.99,
  "stockQuantity": 10
}
Expected: 201 + product ID
```

### 3️⃣ Create Order
```
POST /api/orders
Header: Authorization: Bearer {{token}}
Body:
{
  "items": [
    {"productId": 1, "quantity": 2}
  ]
}
Expected: 201 + order ID
```

---

## 🐛 Troubleshooting in Postman

### Problem: 401 Unauthorized

**Check:**
1. Is token in Header? `Authorization: Bearer ...`
2. Is there a space after "Bearer"?
3. Is token complete? (should be 3 parts: `xxx.xxx.xxx`)
4. Get fresh token with login request

```
❌ Wrong: Authorization: Bearereysjwt...
✅ Right: Authorization: Bearer eysjwt...
```

### Problem: 400 Bad Request

**Check:**
1. Is Body valid JSON?
2. Are all required fields present?
3. Is Content-Type set to `application/json`?

### Problem: Connection Refused

**Check:**
1. Is app running? `mvn spring-boot:run`
2. Is port correct? `8083`
3. Can you ping? `localhost:8083`

---

## 🎬 Full Test Flow

```
START
  ↓
[1] Login Request
    • Method: POST
    • URL: {{base_url}}/api/auth/login
    • Send credentials
    • ✅ Get token
  ↓
[2] Extract Token
    • Token auto-saved to {{token}} variable
    • ✅ Ready to use
  ↓
[3] Get Products
    • Method: GET
    • URL: {{base_url}}/api/products
    • Header: Authorization: Bearer {{token}}
    • ✅ See products
  ↓
[4] Create Product
    • Method: POST
    • URL: {{base_url}}/api/products
    • Header: Authorization: Bearer {{token}}
    • Body: Product data
    • ✅ Get product ID
  ↓
[5] Create Order
    • Method: POST
    • URL: {{base_url}}/api/orders
    • Header: Authorization: Bearer {{token}}
    • Body: Order with product ID
    • ✅ Order created
  ↓
[6] Test Errors
    • Remove token header → 401
    • Invalid token → 401
    • Wrong credentials → 401
    • ✅ Security verified
  ↓
END
```

---

## 📚 Reference

| Action | Menu Path |
|--------|-----------|
| New Request | **+** or **Ctrl+Shift+N** |
| Import Collection | **File** → **Import** |
| Create Environment | **Environments** → **+** |
| View History | **History** tab (left) |
| Run Collection | **⏵ Run** next to collection |
| Export Collection | Right-click collection → **Export** |

---

## ✨ You're Ready!

1. ✅ Import collection file
2. ✅ Run login request
3. ✅ Use token in other requests
4. ✅ Test all endpoints
5. ✅ Verify security

**Happy testing! 🎉**

