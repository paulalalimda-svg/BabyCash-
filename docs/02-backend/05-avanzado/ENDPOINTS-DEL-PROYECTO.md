# 📍 ENDPOINTS DEL PROYECTO BABYCASH

## 🎯 Lista Completa de Endpoints

### Base URL

```
http://localhost:8080/api
```

---

## 🔐 Auth (Autenticación)

### POST /api/auth/register

**Descripción:** Registrar nuevo usuario

**Request:**
```json
{
  "email": "maria@gmail.com",
  "password": "password123",
  "name": "María García",
  "phone": "3001234567",
  "address": "Calle 123 #45-67, Bogotá"
}
```

**Response:** `201 Created`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "maria@gmail.com",
    "name": "María García",
    "role": "USER"
  }
}
```

---

### POST /api/auth/login

**Descripción:** Iniciar sesión

**Request:**
```json
{
  "email": "maria@gmail.com",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "maria@gmail.com",
    "name": "María García",
    "role": "USER"
  }
}
```

---

### POST /api/auth/refresh

**Descripción:** Renovar token de acceso

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 🛍️ Products (Productos)

### GET /api/products

**Descripción:** Obtener todos los productos disponibles

**Headers:**
```
Authorization: Bearer {token}  (opcional)
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Pañales Huggies",
    "description": "Pañales para bebés recién nacidos",
    "price": 45000,
    "stock": 50,
    "imageUrl": "https://...",
    "available": true
  },
  {
    "id": 2,
    "name": "Leche NAN",
    "description": "Leche de fórmula para bebés",
    "price": 15000,
    "stock": 100,
    "imageUrl": "https://...",
    "available": true
  }
]
```

---

### GET /api/products/{id}

**Descripción:** Obtener producto específico por ID

**Ejemplo:** `GET /api/products/1`

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Pañales Huggies",
  "description": "Pañales para bebés recién nacidos",
  "price": 45000,
  "stock": 50,
  "imageUrl": "https://...",
  "available": true
}
```

---

### GET /api/products/search

**Descripción:** Buscar productos

**Query Parameters:**
- `name` (opcional): Buscar por nombre
- `minPrice` (opcional): Precio mínimo
- `maxPrice` (opcional): Precio máximo

**Ejemplo:** `GET /api/products/search?name=pañal&minPrice=10000&maxPrice=50000`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Pañales Huggies",
    "price": 45000,
    "stock": 50
  }
]
```

---

### POST /api/products

**Descripción:** Crear nuevo producto (ADMIN)

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
```json
{
  "name": "Pañales Huggies Supreme",
  "description": "Pañales premium para bebés",
  "price": 50000,
  "stock": 30,
  "imageUrl": "https://..."
}
```

**Response:** `201 Created`
```json
{
  "id": 3,
  "name": "Pañales Huggies Supreme",
  "description": "Pañales premium para bebés",
  "price": 50000,
  "stock": 30,
  "imageUrl": "https://...",
  "available": true
}
```

---

### PUT /api/products/{id}

**Descripción:** Actualizar producto (ADMIN)

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo:** `PUT /api/products/1`

**Request:**
```json
{
  "name": "Pañales Huggies Actualizado",
  "description": "Nueva descripción",
  "price": 48000,
  "stock": 60,
  "available": true
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Pañales Huggies Actualizado",
  "description": "Nueva descripción",
  "price": 48000,
  "stock": 60,
  "available": true
}
```

---

### DELETE /api/products/{id}

**Descripción:** Eliminar producto (ADMIN)

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo:** `DELETE /api/products/1`

**Response:** `204 No Content`

---

## 🛒 Cart (Carrito)

### GET /api/cart

**Descripción:** Obtener carrito del usuario autenticado

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Pañales Huggies",
      "productPrice": 45000,
      "quantity": 2,
      "subtotal": 90000
    },
    {
      "id": 2,
      "productId": 2,
      "productName": "Leche NAN",
      "productPrice": 15000,
      "quantity": 3,
      "subtotal": 45000
    }
  ],
  "total": 135000
}
```

---

### POST /api/cart/add

**Descripción:** Agregar producto al carrito

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Pañales Huggies",
      "quantity": 2,
      "subtotal": 90000
    }
  ],
  "total": 90000
}
```

---

### PUT /api/cart/items/{itemId}

**Descripción:** Actualizar cantidad de item en carrito

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo:** `PUT /api/cart/items/1`

**Request:**
```json
{
  "quantity": 5
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "productId": 1,
  "quantity": 5,
  "subtotal": 225000
}
```

---

### DELETE /api/cart/items/{itemId}

**Descripción:** Eliminar item del carrito

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo:** `DELETE /api/cart/items/1`

**Response:** `204 No Content`

---

### DELETE /api/cart/clear

**Descripción:** Vaciar carrito

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `204 No Content`

---

## 📦 Orders (Órdenes)

### GET /api/orders

**Descripción:** Obtener órdenes del usuario autenticado

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "orderNumber": "ORD-1698765432000",
    "total": 135000,
    "status": "PENDING",
    "createdAt": "2025-10-30T19:30:00",
    "itemCount": 3
  },
  {
    "id": 2,
    "orderNumber": "ORD-1698765433000",
    "total": 90000,
    "status": "DELIVERED",
    "createdAt": "2025-10-28T15:20:00",
    "itemCount": 2
  }
]
```

---

### GET /api/orders/{id}

**Descripción:** Obtener detalle de orden específica

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo:** `GET /api/orders/1`

**Response:** `200 OK`
```json
{
  "id": 1,
  "orderNumber": "ORD-1698765432000",
  "total": 135000,
  "status": "PENDING",
  "shippingAddress": "Calle 123 #45-67, Bogotá",
  "paymentMethod": "Credit Card",
  "createdAt": "2025-10-30T19:30:00",
  "user": {
    "id": 1,
    "name": "María García",
    "email": "maria@gmail.com"
  },
  "items": [
    {
      "productId": 1,
      "productName": "Pañales Huggies",
      "quantity": 2,
      "price": 45000,
      "subtotal": 90000
    },
    {
      "productId": 2,
      "productName": "Leche NAN",
      "quantity": 3,
      "price": 15000,
      "subtotal": 45000
    }
  ]
}
```

---

### POST /api/orders

**Descripción:** Crear nueva orden desde el carrito

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
```json
{
  "shippingAddress": "Calle 123 #45-67, Bogotá",
  "paymentMethod": "Credit Card"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "orderNumber": "ORD-1698765432000",
  "total": 135000,
  "status": "PENDING",
  "shippingAddress": "Calle 123 #45-67, Bogotá",
  "createdAt": "2025-10-30T19:30:00"
}
```

---

### PUT /api/orders/{id}/status

**Descripción:** Actualizar estado de orden (ADMIN)

**Headers:**
```
Authorization: Bearer {token}
```

**Ejemplo:** `PUT /api/orders/1/status`

**Request:**
```json
{
  "status": "SHIPPED"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "orderNumber": "ORD-1698765432000",
  "status": "SHIPPED"
}
```

---

## 👤 Users (Usuarios)

### GET /api/users/me

**Descripción:** Obtener perfil del usuario autenticado

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "email": "maria@gmail.com",
  "name": "María García",
  "phone": "3001234567",
  "address": "Calle 123 #45-67, Bogotá",
  "role": "USER",
  "active": true,
  "createdAt": "2025-10-15T10:30:00"
}
```

---

### PUT /api/users/me

**Descripción:** Actualizar perfil del usuario autenticado

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
```json
{
  "name": "María García López",
  "phone": "3009876543",
  "address": "Carrera 50 #20-30, Medellín"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "email": "maria@gmail.com",
  "name": "María García López",
  "phone": "3009876543",
  "address": "Carrera 50 #20-30, Medellín"
}
```

---

### GET /api/users (ADMIN)

**Descripción:** Obtener todos los usuarios (ADMIN)

**Headers:**
```
Authorization: Bearer {token}
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "email": "maria@gmail.com",
    "name": "María García",
    "role": "USER",
    "active": true
  },
  {
    "id": 2,
    "email": "admin@babycash.com",
    "name": "Admin",
    "role": "ADMIN",
    "active": true
  }
]
```

---

## 📝 Blog Posts

### GET /api/blog

**Descripción:** Obtener todos los posts del blog

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Cómo elegir los mejores pañales",
    "content": "...",
    "author": "Admin",
    "createdAt": "2025-10-20T10:00:00"
  }
]
```

---

### GET /api/blog/{id}

**Descripción:** Obtener post específico

**Ejemplo:** `GET /api/blog/1`

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Cómo elegir los mejores pañales",
  "content": "Lorem ipsum dolor sit amet...",
  "author": "Admin",
  "createdAt": "2025-10-20T10:00:00",
  "updatedAt": "2025-10-21T15:30:00"
}
```

---

### POST /api/blog (ADMIN)

**Descripción:** Crear nuevo post (ADMIN)

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
```json
{
  "title": "Nuevo artículo",
  "content": "Contenido del artículo..."
}
```

**Response:** `201 Created`

---

## ⭐ Testimonials (Reseñas)

### GET /api/testimonials

**Descripción:** Obtener todas las reseñas

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "userName": "María García",
    "rating": 5,
    "comment": "Excelente servicio",
    "createdAt": "2025-10-25T14:20:00"
  }
]
```

---

### POST /api/testimonials

**Descripción:** Crear nueva reseña

**Headers:**
```
Authorization: Bearer {token}
```

**Request:**
```json
{
  "rating": 5,
  "comment": "Muy buen servicio, productos de calidad"
}
```

**Response:** `201 Created`

---

## 📋 Resumen de Endpoints

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| **Auth** |
| POST | `/api/auth/register` | Registrar usuario | ❌ |
| POST | `/api/auth/login` | Iniciar sesión | ❌ |
| POST | `/api/auth/refresh` | Renovar token | ❌ |
| **Products** |
| GET | `/api/products` | Listar productos | ❌ |
| GET | `/api/products/{id}` | Ver producto | ❌ |
| GET | `/api/products/search` | Buscar productos | ❌ |
| POST | `/api/products` | Crear producto | ✅ ADMIN |
| PUT | `/api/products/{id}` | Actualizar producto | ✅ ADMIN |
| DELETE | `/api/products/{id}` | Eliminar producto | ✅ ADMIN |
| **Cart** |
| GET | `/api/cart` | Ver carrito | ✅ USER |
| POST | `/api/cart/add` | Agregar al carrito | ✅ USER |
| PUT | `/api/cart/items/{id}` | Actualizar cantidad | ✅ USER |
| DELETE | `/api/cart/items/{id}` | Quitar del carrito | ✅ USER |
| DELETE | `/api/cart/clear` | Vaciar carrito | ✅ USER |
| **Orders** |
| GET | `/api/orders` | Mis órdenes | ✅ USER |
| GET | `/api/orders/{id}` | Detalle de orden | ✅ USER |
| POST | `/api/orders` | Crear orden | ✅ USER |
| PUT | `/api/orders/{id}/status` | Actualizar estado | ✅ ADMIN |
| **Users** |
| GET | `/api/users/me` | Mi perfil | ✅ USER |
| PUT | `/api/users/me` | Actualizar perfil | ✅ USER |
| GET | `/api/users` | Listar usuarios | ✅ ADMIN |
| **Blog** |
| GET | `/api/blog` | Listar posts | ❌ |
| GET | `/api/blog/{id}` | Ver post | ❌ |
| POST | `/api/blog` | Crear post | ✅ ADMIN |
| **Testimonials** |
| GET | `/api/testimonials` | Listar reseñas | ❌ |
| POST | `/api/testimonials` | Crear reseña | ✅ USER |

---

**Última actualización**: Octubre 2025
