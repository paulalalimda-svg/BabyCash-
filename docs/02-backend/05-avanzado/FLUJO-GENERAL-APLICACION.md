# 🔄 FLUJO GENERAL DE LA APLICACIÓN BABYCASH

## 📱 Arquitectura General

```
┌─────────────────────────────────────────────────────────────────┐
│                          USUARIO                                 │
│                    (Navegador Web)                               │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                       FRONTEND                                   │
│                  React + TypeScript                              │
│                    Puerto: 5173                                  │
│                                                                  │
│  • Páginas (Home, Products, Cart, Checkout)                     │
│  • Componentes (Navbar, ProductCard, CartItem)                  │
│  • Contextos (AuthContext, CartContext)                         │
│  • Servicios (authService, productService, cartService)         │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ HTTP + JSON
                         │ Authorization: Bearer JWT
                         │
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                       BACKEND                                    │
│                   Spring Boot + Java                             │
│                    Puerto: 8080                                  │
│                                                                  │
│  ┌──────────────────────────────────────────────────┐          │
│  │  SECURITY LAYER                                   │          │
│  │  • JwtAuthenticationFilter                        │          │
│  │  • SecurityConfig                                 │          │
│  │  • Rate Limiting                                  │          │
│  └──────────────────────────────────────────────────┘          │
│                         │                                        │
│                         ↓                                        │
│  ┌──────────────────────────────────────────────────┐          │
│  │  CONTROLLERS (API REST)                           │          │
│  │  • AuthController      • ProductController        │          │
│  │  • CartController      • OrderController          │          │
│  │  • PaymentController   • UserController           │          │
│  └──────────────────────────────────────────────────┘          │
│                         │                                        │
│                         ↓                                        │
│  ┌──────────────────────────────────────────────────┐          │
│  │  SERVICES (Lógica de Negocio)                     │          │
│  │  • AuthService         • ProductService           │          │
│  │  • CartService         • OrderService             │          │
│  │  • PaymentService      • EmailService             │          │
│  └──────────────────────────────────────────────────┘          │
│                         │                                        │
│                         ↓                                        │
│  ┌──────────────────────────────────────────────────┐          │
│  │  REPOSITORIES (Acceso a Datos)                    │          │
│  │  • UserRepository      • ProductRepository        │          │
│  │  • CartRepository      • OrderRepository          │          │
│  └──────────────────────────────────────────────────┘          │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ SQL (JDBC)
                         │
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                   BASE DE DATOS                                  │
│                    PostgreSQL 14                                 │
│                    Puerto: 5432                                  │
│                                                                  │
│  Tablas:                                                         │
│  • users            • products        • carts                    │
│  • cart_items       • orders          • order_items              │
│  • payments         • blog_posts      • testimonials             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo de Operaciones Comunes

### 1. Registro de Usuario

```
1. Usuario completa formulario → Frontend valida
2. Frontend: POST /api/auth/register { email, password, name }
3. Backend: AuthController recibe petición
4. Backend: AuthService procesa:
   - Valida que email no exista
   - Encripta password con BCrypt
   - Guarda en PostgreSQL (tabla users)
   - Genera JWT token
   - Envía email de bienvenida
5. Backend retorna: { token, email, role }
6. Frontend guarda token en localStorage
7. Frontend redirige a Home
```

### 2. Login

```
1. Usuario ingresa email/password → Frontend
2. Frontend: POST /api/auth/login { email, password }
3. Backend: AuthController → AuthService
4. Backend valida:
   - Busca usuario en PostgreSQL
   - Verifica password con BCrypt
   - Genera JWT token
5. Backend retorna: { token, email, role }
6. Frontend guarda token y actualiza AuthContext
7. Frontend redirige a Home
```

### 3. Ver Productos

```
1. Usuario accede a /products → Frontend
2. Frontend: GET /api/products (sin autenticación)
3. Backend: ProductController → ProductService
4. Backend: ProductRepository consulta PostgreSQL
5. Backend retorna: [ { id, name, price, image, stock }, ... ]
6. Frontend muestra grid de productos
```

### 4. Agregar al Carrito

```
1. Usuario click "Agregar al carrito" → Frontend
2. Frontend: POST /api/cart/items 
   Header: Authorization: Bearer <token>
   Body: { productId: 5, quantity: 2 }
3. Backend: JwtAuthenticationFilter valida token
4. Backend: CartController → CartService
5. Backend:
   - Busca carrito del usuario (tabla carts)
   - Verifica stock del producto
   - Crea/actualiza CartItem (tabla cart_items)
   - Calcula total
6. Backend retorna: { id, items, total }
7. Frontend actualiza CartContext y badge del carrito
```

### 5. Crear Orden

```
1. Usuario en Checkout click "Finalizar Compra" → Frontend
2. Frontend: POST /api/orders
   Header: Authorization: Bearer <token>
   Body: { shippingAddress, paymentMethod }
3. Backend: OrderController → OrderService
4. Backend (Transacción):
   a. Busca carrito del usuario
   b. Verifica stock de todos los productos
   c. Crea Order (tabla orders)
   d. Crea OrderItems (tabla order_items)
   e. Reduce stock de productos
   f. Limpia carrito
   g. Crea Payment (tabla payments)
   h. Agrega puntos de lealtad (tabla loyalty_points)
   i. Envía email de confirmación
5. Backend retorna: { orderNumber, total, status }
6. Frontend redirige a página de confirmación
```

---

## 🔐 Seguridad en las Peticiones

### Peticiones Públicas (sin JWT)
```
GET /api/products          → ✅ Permitido
GET /api/blog              → ✅ Permitido
POST /api/auth/login       → ✅ Permitido
POST /api/auth/register    → ✅ Permitido
```

### Peticiones Autenticadas (con JWT)
```
GET /api/cart              → 🔒 Requiere token
POST /api/orders           → 🔒 Requiere token
PUT /api/users/profile     → 🔒 Requiere token
```

### Peticiones de Admin (JWT + rol ADMIN)
```
POST /api/admin/products   → 👑 Requiere ADMIN
DELETE /api/admin/users    → 👑 Requiere ADMIN
GET /api/admin/orders      → 👑 Requiere ADMIN
```

---

## 📊 Formato de Comunicación

### Frontend → Backend
```http
POST /api/cart/items HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "productId": 5,
  "quantity": 2
}
```

### Backend → Frontend
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "items": [
    {
      "id": 10,
      "productName": "Pañales Huggies",
      "quantity": 2,
      "price": 45000,
      "subtotal": 90000
    }
  ],
  "total": 90000
}
```

---

## ⚙️ Tecnologías y Puertos

| Componente | Tecnología | Puerto | Protocolo |
|------------|------------|--------|-----------|
| Frontend | React 18 + Vite | 5173 | HTTP |
| Backend | Spring Boot 3 + Java 17 | 8080 | HTTP |
| Base de Datos | PostgreSQL 14 | 5432 | TCP |
| Email | Gmail SMTP | 587 | SMTP/TLS |

---

## 🔄 Ciclo de Vida de una Petición

```
1. USUARIO
   └─→ Click en botón / Envía formulario

2. FRONTEND (React)
   └─→ Ejecuta función (ej: addToCart)
   └─→ Llama servicio (ej: cartService.addItem)
   └─→ Hace petición HTTP con fetch/axios

3. INTERNET
   └─→ HTTP Request viaja por red
   └─→ Llega a localhost:8080

4. BACKEND - Security Layer
   └─→ JwtAuthenticationFilter intercepta
   └─→ Valida JWT token
   └─→ Rate Limiting verifica límite de peticiones
   └─→ CORS valida origen

5. BACKEND - Controller
   └─→ @PostMapping("/cart/items")
   └─→ Recibe y valida datos (DTO)

6. BACKEND - Service
   └─→ Lógica de negocio
   └─→ Validaciones complejas
   └─→ Cálculos

7. BACKEND - Repository
   └─→ Spring Data JPA
   └─→ Genera SQL automáticamente

8. BASE DE DATOS (PostgreSQL)
   └─→ Ejecuta SQL
   └─→ Retorna resultados

9. REGRESO
   └─→ Repository → Service → Controller
   └─→ Controller convierte a JSON
   └─→ HTTP Response

10. FRONTEND (React)
    └─→ Recibe JSON
    └─→ Actualiza estado (Context/useState)
    └─→ React re-renderiza componente

11. USUARIO
    └─→ Ve cambios en pantalla
```

---

**Última actualización**: Octubre 2025
