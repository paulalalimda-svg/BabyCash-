# 📂 ESTRUCTURA COMPLETA DEL BACKEND - BABYCASH

## 📋 Índice
- [Estructura Raíz](#estructura-raíz)
- [Carpeta src/main/java](#carpeta-srcmainjava)
- [Carpeta src/main/resources](#carpeta-srcmainresources)
- [Carpeta src/test](#carpeta-srctest)
- [Archivos de Configuración](#archivos-de-configuración)

---

## 📁 Estructura Raíz del Backend

```
backend/
│
├── 📂 .mvn/                          # Configuración de Maven Wrapper
│   └── wrapper/
│       └── maven-wrapper.properties  # Propiedades del wrapper de Maven
│
├── 📂 src/                           # Código fuente del proyecto
│   ├── main/                         # Código principal de la aplicación
│   └── test/                         # Pruebas unitarias e integración
│
├── 📂 target/                        # Archivos compilados (generado automáticamente)
│   └── [NO REVISAR - archivos .class y .jar]
│
├── 📂 estructura/                    # 📚 DOCUMENTACIÓN (esta carpeta)
│
├── 📄 .env                          # 🔒 Variables de entorno SECRETAS
│                                    # Contiene: contraseñas, tokens, claves
│                                    # ⚠️ NUNCA subir a Git
│
├── 📄 .env.example                  # Plantilla de variables de entorno
│                                    # Muestra qué variables se necesitan
│
├── 📄 .gitignore                    # Archivos que Git debe ignorar
│                                    # (logs, .env, target/, etc.)
│
├── 📄 pom.xml                       # 📦 ARCHIVO PRINCIPAL DE MAVEN
│                                    # Define dependencias y configuración
│
├── 📄 mvnw                          # Maven Wrapper para Linux/Mac
│                                    # Permite ejecutar Maven sin instalarlo
│
├── 📄 mvnw.cmd                      # Maven Wrapper para Windows
│
├── 📄 start.sh                      # Script para iniciar la aplicación
│
└── 📄 *.log                         # Archivos de registro (logs)
    ├── app.log
    ├── backend.log
    └── startup.log
```

### Explicación de Archivos Raíz

| Archivo | Función | ¿Modificable? |
|---------|---------|---------------|
| **pom.xml** | Define dependencias (Spring Boot, PostgreSQL, etc.) y plugins |  Sí |
| **.env** | Contiene passwords, JWT secret, configuración de BD |  Sí (NO subir a Git) |
| **.env.example** | Plantilla para crear .env | Sí |
| **.gitignore** | Lista de archivos que Git ignora | Sí |
| **mvnw** / **mvnw.cmd** | Ejecutar Maven sin instalarlo |  No tocar |
| **start.sh** | Script bash para iniciar el servidor | Sí |
| **target/** | Archivos compilados (.class, .jar) |  Auto-generado |

---

## 📂 CARPETA: src/main/java

```
src/main/java/com/babycash/backend/
│
├── 📄 BabyCashApplication.java      # 🚀 PUNTO DE ENTRADA DE LA APLICACIÓN
│                                    # Contiene el método main()
│                                    # Inicia Spring Boot
│
├── 📂 config/                       # ⚙️ CONFIGURACIÓN DE LA APLICACIÓN
│   ├── AsyncConfig.java             # Configuración de tareas asíncronas
│   ├── CorsConfig.java              # Configuración de CORS (permitir frontend)
│   ├── DataLoader.java              # Carga datos iniciales en la BD
│   ├── OpenApiConfig.java           # Configuración de documentación API
│   │
│   ├── 📂 security/                 # 🔐 Configuración de seguridad
│   │   ├── SecurityConfig.java      # Configuración principal de seguridad
│   │   ├── AuditAspect.java         # Registro de acciones (auditoría)
│   │   ├── RateLimitConfig.java     # Límite de peticiones por IP
│   │   ├── RateLimitFilter.java     # Filtro para rate limiting
│   │   ├── SecurityHeadersFilter.java   # Headers de seguridad HTTP
│   │   └── SecurityScheduledTasks.java  # Tareas programadas de seguridad
│   │
│   └── 📂 swagger/                  # 📖 Documentación Swagger
│       └── SwaggerConfig.java       # Configuración de Swagger UI
│
├── 📂 controller/                   # 🎮 CONTROLADORES (Endpoints REST)
│   │                                # Reciben peticiones HTTP y retornan JSON
│   │
│   ├── AuthController.java          # /api/auth/* - Login, registro, logout
│   ├── UserController.java          # /api/users/* - Gestión de usuarios
│   ├── ProductController.java       # /api/products/* - Listar productos
│   ├── AdminProductController.java  # /api/admin/products/* - CRUD productos
│   ├── CartController.java          # /api/cart/* - Carrito de compras
│   ├── OrderController.java         # /api/orders/* - Órdenes de usuario
│   ├── AdminOrderController.java    # /api/admin/orders/* - Gestión órdenes
│   ├── PaymentController.java       # /api/payments/* - Procesar pagos
│   ├── BlogPostController.java      # /api/blog/* - Publicaciones de blog
│   ├── BlogCommentController.java   # /api/comments/* - Comentarios del blog
│   ├── TestimonialController.java   # /api/testimonials/* - Testimonios
│   ├── ContactMessageController.java # /api/contact/messages/* - Mensajes contacto
│   ├── ContactInfoController.java   # /api/contact/info/* - Info de contacto
│   ├── LoyaltyController.java       # /api/loyalty/* - Puntos de lealtad
│   └── HealthController.java        # /api/health - Estado del servidor
│
├── 📂 service/                      # 💼 SERVICIOS (Lógica de Negocio)
│   │                                # Contienen la lógica principal
│   │
│   ├── AuthService.java             # Lógica de autenticación
│   ├── UserService.java             # Gestión de usuarios
│   ├── ProductService.java          # Lógica de productos
│   ├── CartService.java             # Lógica del carrito
│   ├── OrderService.java            # Procesamiento de órdenes
│   ├── PaymentService.java          # Procesamiento de pagos
│   ├── BlogPostService.java         # Gestión de posts de blog
│   ├── IBlogPostService.java        # Interfaz de BlogPostService
│   ├── BlogCommentService.java      # Gestión de comentarios
│   ├── TestimonialService.java      # Gestión de testimonios
│   ├── ContactMessageService.java   # Mensajes de contacto
│   ├── ContactInfoService.java      # Información de contacto
│   ├── LoyaltyService.java          # Sistema de puntos
│   ├── ILoyaltyService.java         # Interfaz de LoyaltyService
│   ├── EmailService.java            # Envío de correos electrónicos
│   ├── RefreshTokenService.java     # Gestión de tokens de refresco
│   └── AuditService.java            # Registro de auditoría
│
├── 📂 repository/                   # 🗄️ REPOSITORIOS (Acceso a Base de Datos)
│   │                                # Interfaces que extienden JpaRepository
│   │
│   ├── UserRepository.java          # Consultas de tabla 'users'
│   ├── ProductRepository.java       # Consultas de tabla 'products'
│   ├── CartRepository.java          # Consultas de tabla 'carts'
│   ├── CartItemRepository.java      # Consultas de tabla 'cart_items'
│   ├── OrderRepository.java         # Consultas de tabla 'orders'
│   ├── OrderItemRepository.java     # Consultas de tabla 'order_items'
│   ├── PaymentRepository.java       # Consultas de tabla 'payments'
│   ├── BlogPostRepository.java      # Consultas de tabla 'blog_posts'
│   ├── BlogCommentRepository.java   # Consultas de tabla 'blog_comments'
│   ├── TestimonialRepository.java   # Consultas de tabla 'testimonials'
│   ├── ContactMessageRepository.java # Consultas de tabla 'contact_messages'
│   ├── ContactInfoRepository.java   # Consultas de tabla 'contact_info'
│   ├── LoyaltyPointRepository.java  # Consultas de tabla 'loyalty_points'
│   ├── RefreshTokenRepository.java  # Consultas de tabla 'refresh_tokens'
│   └── AuditLogRepository.java      # Consultas de tabla 'audit_logs'
│
├── 📂 model/                        # 📊 MODELOS (Entidades de Base de Datos)
│   │
│   ├── 📂 entity/                   # Clases que representan tablas de BD
│   │   ├── User.java                # Tabla: users (usuarios del sistema)
│   │   ├── Product.java             # Tabla: products (productos)
│   │   ├── Cart.java                # Tabla: carts (carritos)
│   │   ├── CartItem.java            # Tabla: cart_items (items del carrito)
│   │   ├── Order.java               # Tabla: orders (órdenes)
│   │   ├── OrderItem.java           # Tabla: order_items (items de orden)
│   │   ├── Payment.java             # Tabla: payments (pagos)
│   │   ├── BlogPost.java            # Tabla: blog_posts (publicaciones)
│   │   ├── BlogComment.java         # Tabla: blog_comments (comentarios)
│   │   ├── Testimonial.java         # Tabla: testimonials (testimonios)
│   │   ├── ContactMessage.java      # Tabla: contact_messages (mensajes)
│   │   ├── ContactInfo.java         # Tabla: contact_info (info contacto)
│   │   ├── LoyaltyPoint.java        # Tabla: loyalty_points (puntos)
│   │   └── RefreshToken.java        # Tabla: refresh_tokens (tokens JWT)
│   │
│   └── 📂 enums/                    # Enumeraciones (valores constantes)
│       ├── UserRole.java            # Roles: USER, ADMIN
│       ├── OrderStatus.java         # Estados: PENDING, CONFIRMED, SHIPPED, etc.
│       ├── PaymentStatus.java       # Estados: PENDING, COMPLETED, FAILED
│       ├── PaymentMethod.java       # Métodos: CREDIT_CARD, DEBIT_CARD, etc.
│       ├── ProductCategory.java     # Categorías de productos
│       └── LoyaltyTransactionType.java # Tipos: EARNED, REDEEMED
│
├── 📂 dto/                          # 📦 DTOs (Data Transfer Objects)
│   │                                # Objetos para transferir datos entre capas
│   │
│   ├── 📂 request/                  # Peticiones que llegan del frontend
│   │   ├── LoginRequest.java        # {email, password}
│   │   ├── RegisterRequest.java     # {email, password, firstName, lastName}
│   │   ├── RefreshTokenRequest.java # {refreshToken}
│   │   ├── ProductRequest.java      # {name, description, price, stock}
│   │   ├── AddToCartRequest.java    # {productId, quantity}
│   │   ├── CreateOrderRequest.java  # {cartId, shippingAddress, ...}
│   │   ├── ProcessPaymentRequest.java # {orderId, paymentMethod, amount}
│   │   └── BlogPostRequest.java     # {title, content, imageUrl}
│   │
│   ├── 📂 response/                 # Respuestas que se envían al frontend
│   │   ├── AuthResponse.java        # {token, email, role, firstName}
│   │   ├── ProductResponse.java     # {id, name, price, imageUrl, stock}
│   │   ├── CartResponse.java        # {id, items[], totalPrice}
│   │   ├── OrderResponse.java       # {id, status, items[], total}
│   │   ├── PaymentResponse.java     # {id, status, amount, method}
│   │   └── BlogPostResponse.java    # {id, title, content, author}
│   │
│   ├── 📂 auth/                     # DTOs específicos de autenticación
│   │   ├── ForgotPasswordRequest.java   # {email}
│   │   └── ResetPasswordRequest.java    # {token, newPassword}
│   │
│   ├── 📂 contact/                  # DTOs de contacto
│   │   ├── ContactMessageRequest.java   # {name, email, message}
│   │   ├── ContactMessageResponse.java
│   │   ├── ContactInfoRequest.java
│   │   └── ContactInfoResponse.java
│   │
│   ├── 📂 testimonial/              # DTOs de testimonios
│   │   ├── TestimonialRequest.java
│   │   └── TestimonialResponse.java
│   │
│   ├── 📂 comment/                  # DTOs de comentarios
│   │   ├── CommentRequest.java
│   │   └── CommentResponse.java
│   │
│   ├── UpdateProfileRequest.java    # Actualizar perfil de usuario
│   ├── UserStatsResponse.java       # Estadísticas de usuario
│   ├── LoyaltyPointsResponse.java   # Respuesta de puntos
│   └── LoyaltyTransactionResponse.java # Transacción de puntos
│
├── 📂 security/                     # 🔐 SEGURIDAD Y JWT
│   ├── JwtUtil.java                 # Utilidades para generar/validar JWT
│   ├── JwtAuthenticationFilter.java # Filtro que valida JWT en cada petición
│   └── CustomUserDetailsService.java # Carga detalles de usuario para Spring Security
│
├── 📂 exception/                    # ❌ MANEJO DE ERRORES
│   ├── GlobalExceptionHandler.java  # Captura todas las excepciones
│   │
│   └── 📂 custom/                   # Excepciones personalizadas
│       ├── AuthenticationException.java     # Error de autenticación
│       ├── ResourceNotFoundException.java   # Recurso no encontrado
│       └── BusinessException.java           # Error de lógica de negocio
│
├── 📂 scheduler/                    # ⏰ TAREAS PROGRAMADAS
│   └── ScheduledTasks.java          # Tareas que se ejecutan automáticamente
│                                    # Ejemplo: limpiar tokens expirados
│
├── 📂 entity/                       # (Duplicado de model/entity/)
│   └── AuditLog.java                # Registro de auditoría
│
├── 📂 mapper/                       # 🔄 MAPPERS (Conversión de objetos)
│   └── [Vacía - posiblemente para MapStruct]
│
└── 📂 util/                         # 🛠️ UTILIDADES
    └── [Vacía - utilidades generales]
```

---

## 📂 CARPETA: src/main/resources

```
src/main/resources/
│
├── 📄 application.properties        # 🔧 CONFIGURACIÓN PRINCIPAL
│                                    # Puerto del servidor, configuración de BD, email
│
├── 📄 application.yml               # Configuración en formato YAML (alternativa)
│
├── 📄 application-prod.yml          # Configuración para producción
│
├── 📂 db/                           # Scripts de base de datos
│   ├── indexes.sql                  # Índices para optimizar consultas
│   ├── refresh_tokens.sql           # Script para tabla refresh_tokens
│   └── audit_logs.sql               # Script para tabla audit_logs
│
├── 📄 data-seed.sql                 # 🌱 Datos iniciales (productos, usuarios)
├── 📄 data-seed-final.sql           # Datos finales optimizados
├── 📄 data-seed-postgres.sql        # Datos específicos para PostgreSQL
└── 📄 data-seed-corrected.sql       # Datos corregidos

```

### 📝 Explicación de resources/

| Archivo | Función |
|---------|---------|
| **application.properties** | Configuración principal: puerto (8080), base de datos, JWT, email |
| **application.yml** | Misma configuración pero en formato YAML |
| **application-prod.yml** | Configuración específica para ambiente de producción |
| **data-seed*.sql** | Scripts para insertar datos de prueba en la BD |
| **db/*.sql** | Scripts para crear índices y optimizar BD |

---

## 📂 CARPETA: src/test

```
src/test/java/com/babycash/backend/
│
├── 📄 BabyCashApplicationTests.java # Test básico de arranque de Spring
│
├── 📂 service/                      # 🧪 Tests de servicios (lógica de negocio)
│   ├── AuthServiceTest.java         # Tests de autenticación
│   ├── ProductServiceTest.java      # Tests de productos
│   ├── CartServiceTest.java         # Tests de carrito
│   ├── OrderServiceTest.java        # Tests de órdenes
│   ├── PaymentServiceTest.java      # Tests de pagos
│   └── BlogPostServiceTest.java     # Tests de blog
│
├── 📂 controller/                   # 🧪 Tests de controladores (endpoints)
│   ├── BlogPostControllerTest.java
│   ├── ContactInfoControllerTest.java
│   ├── ContactMessageControllerTest.java
│   └── TestimonialControllerTest.java
│
├── 📂 integration/                  # 🧪 Tests de integración (end-to-end)
│   └── AuthenticationIntegrationTest.java
│
└── 📂 resources/
    └── application-test.yml         # Configuración para tests
```

### 📝 Explicación de tests/

| Carpeta | Función |
|---------|---------|
| **service/** | Tests unitarios de la lógica de negocio |
| **controller/** | Tests de endpoints HTTP |
| **integration/** | Tests que prueban flujos completos |
| **resources/** | Configuración para ambiente de testing |

---

## 📋 RESUMEN DE RESPONSABILIDADES

### 🎯 Flujo de una Petición HTTP

```
1. CLIENTE (Frontend)
   ↓ HTTP POST /api/products
   
2. CONTROLLER (ProductController.java)
   ↓ Recibe petición, valida datos
   
3. SERVICE (ProductService.java)
   ↓ Ejecuta lógica de negocio
   
4. REPOSITORY (ProductRepository.java)
   ↓ Consulta base de datos
   
5. DATABASE (PostgreSQL)
   ↓ Retorna datos
   
6. DTO (ProductResponse.java)
   ↓ Formatea respuesta
   
7. CONTROLLER
   ↓ HTTP 200 OK + JSON
   
8. CLIENTE (Frontend)
   ✓ Recibe datos y actualiza UI
```

---

## 📊 ESTADÍSTICAS DEL BACKEND

| Categoría | Cantidad | Ubicación |
|-----------|----------|-----------|
| **Controladores** | 14 | `/controller/` |
| **Servicios** | 15 | `/service/` |
| **Repositorios** | 14 | `/repository/` |
| **Entidades** | 14 | `/model/entity/` |
| **Enums** | 6 | `/model/enums/` |
| **DTOs Request** | 9+ | `/dto/request/` |
| **DTOs Response** | 6+ | `/dto/response/` |
| **Clases de Seguridad** | 3 | `/security/` |
| **Excepciones Custom** | 3 | `/exception/custom/` |
| **Tests** | 10+ | `/test/` |

---

## 🔑 ARCHIVOS MÁS IMPORTANTES

### Para entender la aplicación:
1. ✅ **BabyCashApplication.java** - Punto de entrada
2. ✅ **application.properties** - Configuración
3. ✅ **SecurityConfig.java** - Configuración de seguridad
4. ✅ **AuthController.java** - Login y registro
5. ✅ **User.java** - Modelo de usuario
6. ✅ **pom.xml** - Dependencias

### Para modificar funcionalidad:
- **Controllers** → Agregar/modificar endpoints
- **Services** → Cambiar lógica de negocio
- **Repositories** → Agregar consultas personalizadas
- **Entities** → Modificar estructura de tablas
- **DTOs** → Cambiar formato de peticiones/respuestas

---

## 🚀 COMANDOS ÚTILES

```bash
# Compilar el proyecto
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Iniciar la aplicación
./mvnw spring-boot:run

# Compilar y generar JAR
./mvnw clean package

# Ver dependencias
./mvnw dependency:tree
```

---

## 📝 NOTAS IMPORTANTES

⚠️ **NUNCA modificar:**
- Archivos en `/target/` (auto-generados)
- `mvnw` y `mvnw.cmd` (Maven wrapper)
- Archivos `.class` (compilados)

✅ **Seguro modificar:**
- Todos los archivos `.java` en `/src/main/java/`
- Archivos de configuración (`.properties`, `.yml`)
- Scripts SQL en `/resources/`
- Tests en `/src/test/`

🔒 **Archivos sensibles:**
- `.env` → NUNCA subir a Git
- `application.properties` → Revisar antes de subir a producción

---

**Última actualización**: Octubre 2025
**Versión**: 1.0
