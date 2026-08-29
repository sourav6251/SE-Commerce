# SE-Commerce API Documentation

Base URL: `http://localhost:4000`

---

## 🔐 Authentication & Session Flow

The server uses stateless JWT tokens transmitted either via **HttpOnly Cookies** or **HTTP Headers** (`Authorization: Bearer <token>` or custom header matching the token name).

| Token Name | Expiration | Purpose | Storage / Delivery |
|---|---|---|---|
| `otp_token` | 5 minutes | Proves email OTP request was initiated | Cookie (`otp_token`) & JSON body |
| `signup_token` | 30 minutes | Issued upon OTP verification for **new** users to complete signup | Cookie (`signup_token`) & JSON body |
| `AccessToken` | 7 days | Primary authentication token for logged-in users | Cookie (`AccessToken`) & JSON body |

---

## 1. OTP Endpoints (`/otp`)

### 1.1 Generate & Send OTP
Sends a 6-digit OTP code to the user's email address and sets the `otp_token` cookie.

- **URL:** `/otp/send`
- **Method:** `GET`
- **Access:** Public
- **Query Parameters:**
  | Parameter | Type | Required | Description |
  |---|---|---|---|
  | `email` | `string` | Yes | Target user email address (e.g. `user@example.com`) |

- **Response (`200 OK`):**
  - **Set-Cookie:** `otp_token=<jwt_token>; Max-Age=300; Path=/; HttpOnly; SameSite=Lax`
  ```json
  {
    "message": "success",
    "token": "eyJhbGciOiJIUzM4NCJ9..."
  }
  ```
- **Error Responses:**
  - `500 Internal Server Error`: `OTP generation failed`

---

### 1.2 Verify OTP for Signup / Login
Verifies the OTP code. If the user already exists in the database, logs them in directly with `AccessToken`. If the user is new, issues a `signup_token` to complete registration.

- **URL:** `/otp/verify/signup`
- **Method:** `PUT`
- **Access:** Public (requires `otp_token`)
- **Headers / Cookies:**
  | Header / Cookie | Required | Description |
  |---|---|---|
  | `Cookie: otp_token=<token>` or `Header: otp_token: <token>` | Yes | Token received from `/otp/send` |
- **Query Parameters:**
  | Parameter | Type | Required | Description |
  |---|---|---|---|
  | `otp` | `string` | Yes | 6-digit OTP code entered by the user |

- **Success Response - Existing User (`200 OK`):**
  - **Set-Cookie:** `AccessToken=<jwt_token>; Max-Age=604800; Path=/; HttpOnly; SameSite=Lax`
  ```json
  {
    "message": "success",
    "data": {
      "message": "User already exists. Logged in successfully.",
      "isNewUser": false,
      "token": "eyJhbGciOiJIUzM4NCJ9...",
      "user": {
        "id": "8a32b9c1-...",
        "email": "user@example.com",
        "firstName": "Sourav",
        "lastName": null,
        "phoneNumber": "+1234567890",
        "role": "CUSTOMER",
        "status": "ACTIVE",
        "createdAt": "2026-08-29T10:00:00Z",
        "updatedAt": "2026-08-29T10:00:00Z"
      }
    }
  }
  ```

- **Success Response - New User (`200 OK`):**
  - **Set-Cookie:** `signup_token=<jwt_token>; Max-Age=1800; Path=/; HttpOnly; SameSite=Lax`
  ```json
  {
    "message": "success",
    "data": {
      "message": "OTP verified. Please complete signup.",
      "isNewUser": true,
      "token": "eyJhbGciOiJIUzM4NCJ9..."
    }
  }
  ```

- **Error Responses:**
  - `400 Bad Request`: `{"message": "Invalid OTP"}` or `{"message": "OTP expired, please request again"}`
  - `401 Unauthorized`: `{"message": "OTP Expired request again"}`

---

### 1.3 General OTP Verification
Verifies OTP code for general use cases.

- **URL:** `/otp/verify`
- **Method:** `GET`
- **Access:** Public (requires `otp_token`)
- **Headers / Cookies:**
  | Header / Cookie | Required | Description |
  |---|---|---|
  | `Cookie: otp_token=<token>` or `Header: otp_token: <token>` | Yes | Token received from `/otp/send` |
- **Query Parameters:**
  | Parameter | Type | Required | Description |
  |---|---|---|---|
  | `otp` | `string` | Yes | 6-digit OTP code |

- **Response (`200 OK`):**
  ```json
  {
    "message": "success"
  }
  ```

---

## 2. User Authentication Endpoints (`/auth`)

### 2.1 User Signup
Completes registration for a verified user using the `signup_token`. Password is automatically hashed with BCrypt.

- **URL:** `/auth/signup`
- **Method:** `POST`
- **Content-Type:** `multipart/form-data` or `application/x-www-form-urlencoded`
- **Access:** Public (requires `signup_token`)
- **Headers / Cookies:**
  | Header / Cookie | Required | Description |
  |---|---|---|
  | `Cookie: signup_token=<token>` or `Header: signup_token: <token>` | Yes | Token received from `/otp/verify/signup` |

- **Form Fields (SignupDTO):**
  | Field | Type | Required | Description |
  |---|---|---|---|
  | `name` | `string` | Yes | User's full name / first name |
  | `password` | `string` | Yes | Plaintext password (will be BCrypt hashed) |
  | `phone` | `string` | No | Contact phone number |
  | `image` | `file` | No | Profile image upload |

- **Response (`200 OK`):**
  - **Set-Cookie:** `AccessToken=<jwt_token>; Max-Age=604800; Path=/; HttpOnly; SameSite=Lax`
  ```json
  {
    "message": "success",
    "data": {
      "message": "Signup successfully.",
      "isNewUser": true,
      "token": "eyJhbGciOiJIUzM4NCJ9...",
      "user": {
        "id": "8a32b9c1-...",
        "email": "user@example.com",
        "firstName": "Sourav",
        "lastName": null,
        "phoneNumber": "+1234567890",
        "role": "CUSTOMER",
        "status": "ACTIVE",
        "createdAt": "2026-08-29T11:00:00Z",
        "updatedAt": "2026-08-29T11:00:00Z"
      }
    }
  }
  ```

- **Error Responses:**
  - `400 Bad Request`: `{"message": "User exists with this email"}`
  - `400 Bad Request`: `{"message": "Your sign-up session has expired. Please start the sign-up process again."}`

---

### 2.2 User Login (Email & Password)
Authenticates an existing user via Email & Password and issues an `AccessToken`.

- **URL:** `/auth/login`
- **Method:** `PUT` (or `POST`)
- **Content-Type:** `application/json`
- **Access:** Public
- **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "password": "mySecurePassword123"
  }
  ```

- **Response (`200 OK`):**
  - **Set-Cookie:** `AccessToken=<jwt_token>; Max-Age=604800; Path=/; HttpOnly; SameSite=Lax`
  ```json
  {
    "message": "success",
    "data": {
      "message": "Logged in successfully.",
      "token": "eyJhbGciOiJIUzM4NCJ9...",
      "user": {
        "id": "8a32b9c1-...",
        "email": "user@example.com",
        "firstName": "Sourav",
        "lastName": null,
        "phoneNumber": "+1234567890",
        "role": "CUSTOMER",
        "status": "ACTIVE",
        "createdAt": "2026-08-29T10:00:00Z",
        "updatedAt": "2026-08-29T10:00:00Z"
      }
    }
  }
  ```

- **Error Responses:**
  - `400 Bad Request`: `{"message": "Invalid email or password"}`
  - `400 Bad Request`: `{"message": "Your account is suspended. Please contact support."}`

---

## 3. Category Management Endpoints (`/category`)

### 3.1 Create Category
Creates a new product category.

- **URL:** `/category`
- **Method:** `POST`
- **Content-Type:** `application/json`
- **Request Body:**
  ```json
  {
    "categoryCode": "CAT_ELEC_01",
    "name": "Electronics",
    "slug": "electronics",
    "description": "Smartphones, laptops, and gadgets"
  }
  ```

- **Response (`200 OK`):**
  ```json
  {
    "id": "c1f729b8-37c2-4a7b-a012-1f4864c39f10",
    "categoryCode": "CAT_ELEC_01",
    "name": "Electronics",
    "slug": "electronics",
    "description": "Smartphones, laptops, and gadgets",
    "parent": null,
    "subCategories": [],
    "createdAt": "2026-08-29T15:30:00Z"
  }
  ```

- **Error Responses:**
  - `400 Bad Request`: `"Enter category code"`
  - `400 Bad Request`: `"Category code already exists"`

---

### 3.2 Get All Categories
Fetches all product categories.

- **URL:** `/category`
- **Method:** `GET`
- **Response (`200 OK`):**
  ```json
  [
    {
      "id": "c1f729b8-37c2-4a7b-a012-1f4864c39f10",
      "categoryCode": "CAT_ELEC_01",
      "name": "Electronics",
      "slug": "electronics",
      "description": "Smartphones, laptops, and gadgets",
      "parent": null,
      "subCategories": [],
      "createdAt": "2026-08-29T15:30:00Z"
    }
  ]
  ```

---

### 3.3 Update Category
Updates an existing category by its unique ID. Validates that the updated category code is not already in use by another category.

- **URL:** `/category`
- **Method:** `PUT`
- **Content-Type:** `application/json`
- **Request Body:**
  ```json
  {
    "id": "c1f729b8-37c2-4a7b-a012-1f4864c39f10",
    "categoryCode": "CAT_ELEC_01",
    "name": "Consumer Electronics",
    "slug": "consumer-electronics",
    "description": "Updated category description"
  }
  ```

- **Response (`200 OK`):**
  ```json
  {
    "id": "c1f729b8-37c2-4a7b-a012-1f4864c39f10",
    "categoryCode": "CAT_ELEC_01",
    "name": "Consumer Electronics",
    "slug": "consumer-electronics",
    "description": "Updated category description",
    "parent": null,
    "subCategories": [],
    "createdAt": "2026-08-29T15:30:00Z"
  }
  ```

- **Error Responses:**
  - `400 Bad Request`: `"Category ID is required for update"`
  - `400 Bad Request`: `"Category not found"`
  - `400 Bad Request`: `"Category code already exists in another category"`

---

### 3.4 Delete Category
Deletes an existing category by ID.

- **URL:** `/category`
- **Method:** `DELETE`
- **Content-Type:** `application/json`
- **Request Body:**
  ```json
  {
    "id": "c1f729b8-37c2-4a7b-a012-1f4864c39f10"
  }
  ```
- **Response (`204 No Content`):** Empty response body.
- **Error Responses:**
  - `400 Bad Request`: `"Category not found"`

---

## 4. Schemas & Models

### UserDTO
```json
{
  "id": "string (UUID)",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "phoneNumber": "string",
  "role": "CUSTOMER | ADMIN",
  "status": "ACTIVE | SUSPENDED | UNDER_REVIEW",
  "createdAt": "string (ISO-8601)",
  "updatedAt": "string (ISO-8601)"
}
```

### CategoryDTO
```json
{
  "id": "string (UUID)",
  "categoryCode": "string",
  "name": "string",
  "slug": "string",
  "description": "string",
  "parent": "CategoryEntity (nullable)",
  "subCategories": "array of CategoryEntity",
  "createdAt": "string (ISO-8601)"
}
```

