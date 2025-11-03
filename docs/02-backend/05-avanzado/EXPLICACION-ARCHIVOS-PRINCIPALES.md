# 📝 EXPLICACIÓN DETALLADA DE ARCHIVOS PRINCIPALES - BACKEND

## 📌 Índice
1. [Archivos de Configuración Raíz](#archivos-de-configuración-raíz)
2. [Punto de Entrada](#punto-de-entrada)
3. [Controladores (Controllers)](#controladores-controllers)
4. [Servicios (Services)](#servicios-services)
5. [Repositorios (Repositories)](#repositorios-repositories)
6. [Modelos (Entities)](#modelos-entities)

---

## 📁 ARCHIVOS DE CONFIGURACIÓN RAÍZ

### 📄 pom.xml
**Ubicación**: `/backend/pom.xml`
**Función**: Define las dependencias (librerías) que usa el proyecto

**¿Qué contiene?**
```xml
<!-- Información del proyecto -->
<groupId>com.babycash</groupId>
<artifactId>backend</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>BabyCash</name>

<!-- Dependencias principales -->
- Spring Boot Web (para crear APIs REST)
- Spring Security (para autenticación JWT)
- PostgreSQL Driver (para conectar a la base de datos)
- Spring Data JPA (para consultas a BD)
- Jakarta Mail (para enviar emails)
- Lombok (para reducir código repetitivo)
- Swagger/OpenAPI (para documentar la API)
```

**¿Cuándo modificar?**
- ✅ Cuando necesites agregar una nueva librería
- ✅ Cuando quieras actualizar versiones de dependencias
- ❌ NO tocar si no sabes qué hace cada dependencia

**Ejemplo**: Agregar soporte para enviar SMS
```xml
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.2.0</version>
</dependency>
```

---

### 📄 .env
**Ubicación**: `/backend/.env`
**Función**: Almacena variables de entorno SECRETAS

**Contenido**:
```env
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/babycash
DB_USERNAME=postgres
DB_PASSWORD=admin123

# JWT (Tokens de autenticación)
JWT_SECRET=BabyCashSecretKey2024...
JWT_EXPIRATION_MS=86400000  # 24 horas
JWT_REFRESH_EXPIRATION_MS=604800000  # 7 días

# Email (Gmail)
MAIL_USERNAME=babycashnoreply@gmail.com
MAIL_PASSWORD=pcsguuqqlmfvjhaf  # App password de Gmail
MAIL_FROM=babycashnoreply@gmail.com
MAIL_ADMIN=202215.clv@gmail.com

# Frontend
FRONTEND_URL=http://localhost:5173
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

**⚠️ IMPORTANTE**:
- 🔒 NUNCA subir este archivo a Git
- 🔒 Cada desarrollador debe tener su propio `.env`
- 🔒 En producción, usar variables de entorno del servidor

---

### 📄 application.properties
**Ubicación**: `/backend/src/main/resources/application.properties`
**Función**: Configuración principal de Spring Boot

**Contenido**:
```properties
# Nombre de la aplicación
spring.application.name=BabyCash

# Puerto del servidor
server.port=8080

# Base de datos - Lee variables desde .env
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA - Cómo manejar las tablas
spring.jpa.hibernate.ddl-auto=update  # Actualiza tablas automáticamente
spring.jpa.show-sql=true  # Muestra las consultas SQL en consola

# JWT
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=${JWT_EXPIRATION_MS}

# Email (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

# Frontend URL (para CORS y emails)
app.frontend.url=${FRONTEND_URL}
```

**¿Cuándo modificar?**
- ✅ Cambiar puerto del servidor
- ✅ Ajustar configuración de BD
- ✅ Modificar configuración de email
- ❌ NO cambiar `spring.jpa.hibernate.ddl-auto` en producción

---

## 🚀 PUNTO DE ENTRADA

### 📄 BabyCashApplication.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/BabyCashApplication.java`
**Función**: Punto de entrada de la aplicación - Método `main()`

**Código completo**:
```java
package com.babycash.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication  // Indica que es una app Spring Boot
@EnableAsync           // Habilita tareas asíncronas (emails)
@EnableScheduling      // Habilita tareas programadas (limpieza de tokens)
public class BabyCashApplication {

    public static void main(String[] args) {
        SpringApplication.run(BabyCashApplication.class, args);
        System.out.println("🚀 BabyCash Backend iniciado en http://localhost:8080");
    }
}
```

**¿Qué hace al ejecutarse?**
1. Inicia el servidor Tomcat en puerto 8080
2. Conecta a la base de datos PostgreSQL
3. Carga las configuraciones de `application.properties`
4. Escanea todos los `@Controller`, `@Service`, `@Repository`
5. Configura la seguridad (JWT, CORS)
6. Ejecuta `DataLoader` para cargar datos iniciales
7. Expone los endpoints de la API

**⚠️ NO MODIFICAR** este archivo a menos que necesites:
- Cambiar el puerto
- Agregar configuraciones globales
- Habilitar/deshabilitar funcionalidades

---

## 🎮 CONTROLADORES (CONTROLLERS)

Los controladores reciben peticiones HTTP y retornan respuestas JSON.

### 📄 AuthController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/AuthController.java`
**Función**: Maneja autenticación (login, registro, logout)

**Endpoints**:
```java
POST /api/auth/register          // Registrar nuevo usuario
POST /api/auth/login             // Iniciar sesión
POST /api/auth/logout            // Cerrar sesión
POST /api/auth/refresh-token     // Renovar token JWT
POST /api/auth/forgot-password   // Solicitar recuperación de contraseña
POST /api/auth/reset-password    // Restablecer contraseña
```

**Ejemplo de método**:
```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    // 1. Recibe email y password del frontend
    // 2. Llama al AuthService para validar
    // 3. Si es correcto, genera token JWT
    // 4. Retorna token + datos del usuario
    
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
}
```

**¿Qué hace internamente?**
1. Recibe JSON: `{"email": "user@example.com", "password": "123456"}`
2. Valida que el email y password no estén vacíos
3. Llama a `AuthService.login()`
4. Si las credenciales son correctas:
   - Genera token JWT
   - Retorna: `{"token": "eyJhbGci...", "email": "...", "role": "USER"}`
5. Si son incorrectas:
   - Retorna error 401 Unauthorized

---

### 📄 ProductController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/ProductController.java`
**Función**: Endpoints públicos de productos (para clientes)

**Endpoints**:
```java
GET  /api/products              // Listar todos los productos
GET  /api/products/{id}         // Obtener un producto por ID
GET  /api/products/category/{cat} // Filtrar por categoría
GET  /api/products/search?q=    // Buscar productos
```

**Ejemplo**:
```java
@GetMapping("/products")
public ResponseEntity<List<ProductResponse>> getAllProducts() {
    List<ProductResponse> products = productService.getAllProducts();
    return ResponseEntity.ok(products);
}

// Retorna JSON:
// [
//   {
//     "id": 1,
//     "name": "Pañales Huggies",
//     "price": 45000.0,
//     "imageUrl": "/productos/panales.jpg",
//     "stock": 100
//   },
//   ...
// ]
```

---

### 📄 AdminProductController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/AdminProductController.java`
**Función**: Endpoints protegidos para administradores (CRUD de productos)

**Endpoints** (requieren rol ADMIN):
```java
POST   /api/admin/products          // Crear producto
PUT    /api/admin/products/{id}     // Actualizar producto
DELETE /api/admin/products/{id}     // Eliminar producto
```

**Seguridad**:
```java
@PreAuthorize("hasRole('ADMIN')")  // Solo usuarios con rol ADMIN
@PostMapping("/products")
public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
    // Solo se ejecuta si el JWT tiene role: "ADMIN"
    ProductResponse product = productService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(product);
}
```

---

### 📄 CartController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/CartController.java`
**Función**: Gestión del carrito de compras

**Endpoints**:
```java
GET    /api/cart                // Obtener carrito del usuario
POST   /api/cart/items          // Agregar producto al carrito
PUT    /api/cart/items/{id}     // Actualizar cantidad
DELETE /api/cart/items/{id}     // Eliminar item
DELETE /api/cart                // Vaciar carrito
```

**Ejemplo: Agregar al carrito**
```java
@PostMapping("/cart/items")
public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request) {
    // request: { "productId": 5, "quantity": 2 }
    
    // 1. Obtiene el usuario autenticado desde el JWT
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    
    // 2. Busca o crea el carrito del usuario
    // 3. Agrega el producto con la cantidad especificada
    // 4. Calcula el precio total
    
    CartResponse cart = cartService.addToCart(email, request);
    return ResponseEntity.ok(cart);
}
```

---

### 📄 OrderController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/OrderController.java`
**Función**: Gestión de órdenes de compra

**Endpoints**:
```java
POST /api/orders                 // Crear orden desde carrito
GET  /api/orders                 // Listar mis órdenes
GET  /api/orders/{id}            // Ver detalles de una orden
```

**Flujo de creación de orden**:
```java
@PostMapping("/orders")
public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
    // request: {
    //   "shippingAddress": "Calle 123 #45-67",
    //   "city": "Bogotá",
    //   "phone": "3001234567"
    // }
    
    // 1. Obtiene el carrito del usuario
    // 2. Valida que haya items en el carrito
    // 3. Calcula el total
    // 4. Crea la orden con estado PENDING
    // 5. Vacía el carrito
    // 6. Envía email de confirmación
    
    OrderResponse order = orderService.createOrder(email, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
}
```

---

### 📄 PaymentController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/PaymentController.java`
**Función**: Procesamiento de pagos

**Endpoints**:
```java
POST /api/payments/process       // Procesar pago
GET  /api/payments/{id}          // Consultar estado de pago
```

**Ejemplo**:
```java
@PostMapping("/payments/process")
public ResponseEntity<PaymentResponse> processPayment(@RequestBody ProcessPaymentRequest request) {
    // request: {
    //   "orderId": 123,
    //   "paymentMethod": "CREDIT_CARD",
    //   "amount": 150000.0,
    //   "cardNumber": "****1234"  // Últimos 4 dígitos
    // }
    
    // 1. Valida la orden
    // 2. Procesa el pago (en este caso simula el pago)
    // 3. Actualiza el estado de la orden a CONFIRMED
    // 4. Genera puntos de lealtad
    // 5. Envía email con recibo
    
    PaymentResponse payment = paymentService.processPayment(request);
    return ResponseEntity.ok(payment);
}
```

---

### 📄 BlogPostController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/BlogPostController.java`
**Función**: Gestión de publicaciones del blog

**Endpoints**:
```java
GET    /api/blog                 // Listar posts (paginado)
GET    /api/blog/{id}            // Ver post completo
POST   /api/blog                 // Crear post (ADMIN)
PUT    /api/blog/{id}            // Editar post (ADMIN)
DELETE /api/blog/{id}            // Eliminar post (ADMIN)
```

---

### 📄 TestimonialController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/TestimonialController.java`
**Función**: Gestión de testimonios de clientes

**Endpoints**:
```java
GET    /api/testimonials              // Listar testimonios aprobados
POST   /api/testimonials              // Crear testimonio
PUT    /api/testimonials/{id}/approve // Aprobar testimonio (ADMIN)
DELETE /api/testimonials/{id}         // Eliminar testimonio (ADMIN)
```

---

### 📄 ContactMessageController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/ContactMessageController.java`
**Función**: Mensajes de contacto del formulario

**Endpoints**:
```java
POST /api/contact/messages       // Enviar mensaje de contacto
GET  /api/contact/messages       // Listar mensajes (ADMIN)
```

---

### 📄 LoyaltyController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/LoyaltyController.java`
**Función**: Sistema de puntos de lealtad

**Endpoints**:
```java
GET  /api/loyalty/points         // Consultar mis puntos
GET  /api/loyalty/transactions   // Ver historial de puntos
POST /api/loyalty/redeem         // Canjear puntos
```

---

### 📄 HealthController.java
**Ubicación**: `/backend/src/main/java/com/babycash/backend/controller/HealthController.java`
**Función**: Verificar estado del servidor

**Endpoint**:
```java
GET /api/health                  // Retorna {"status": "UP"}
```

**Uso**: Para monitoreo, verificar que el servidor está funcionando.

---

## 📊 RESUMEN DE CONTROLADORES

| Controller | Endpoints | Autenticación | Rol |
|-----------|-----------|---------------|-----|
| **AuthController** | 6 | ❌ Público | - |
| **ProductController** | 4 | ❌ Público | - |
| **AdminProductController** | 3 | ✅ Requerida | ADMIN |
| **CartController** | 5 | ✅ Requerida | USER |
| **OrderController** | 3 | ✅ Requerida | USER |
| **PaymentController** | 2 | ✅ Requerida | USER |
| **BlogPostController** | 5 | 🟡 Mixto | ADMIN (crear/editar) |
| **TestimonialController** | 4 | 🟡 Mixto | ADMIN (aprobar) |
| **ContactMessageController** | 2 | 🟡 Mixto | ADMIN (leer) |
| **LoyaltyController** | 3 | ✅ Requerida | USER |
| **HealthController** | 1 | ❌ Público | - |

---

**Continúa en**: `EXPLICACION-SERVICES.md`

---

**Última actualización**: Octubre 2025
**Versión**: 1.0
