package com.babycash.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para documentación interactiva de la API
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "BabyCash API",
                version = "1.0.0",
                description = """
                        # BabyCash E-Commerce Backend API
                        
                        API RESTful completa para una tienda de productos para bebés con las siguientes características:
                        
                        ## 🎯 Funcionalidades Principales
                        
                        ### 🔐 Autenticación y Seguridad
                        - Registro de usuarios con validaciones completas
                        - Login con JWT (JSON Web Tokens)
                        - Tokens válidos por 24 horas
                        - Encriptación de contraseñas con BCrypt
                        - Roles de usuario (USER, ADMIN)
                        
                        ### 🛍️ Catálogo de Productos
                        - 27 productos precargados en 8 categorías
                        - Búsqueda por texto y filtros por categoría
                        - Paginación y ordenamiento flexible
                        - Productos destacados con caché para alto rendimiento
                        - Sistema de ratings y reviews
                        
                        ### 🛒 Carrito de Compras
                        - Gestión completa del carrito por usuario
                        - Validación de stock en tiempo real
                        - Cálculo automático de totales
                        - Persistencia de carrito por sesión
                        
                        ### 📦 Órdenes y Pedidos
                        - Creación de órdenes desde el carrito
                        - Estados: PENDING, PROCESSING, COMPLETED, CANCELLED, REFUNDED
                        - Historial completo de órdenes del usuario
                        - Tracking por número de orden único
                        - Cancelación con restauración de stock
                        
                        ### 💳 Procesamiento de Pagos
                        - Múltiples métodos: Tarjeta (Crédito/Débito), PayPal, Stripe, MercadoPago
                        - Validación completa de datos de pago
                        - Transaction IDs únicos para tracking
                        - Metadata de transacciones
                        
                        ## 🚀 Características Técnicas
                        
                        - **Framework**: Spring Boot 3.5.7 con Java 21
                        - **Base de Datos**: PostgreSQL 17.6 con HikariCP
                        - **Seguridad**: Spring Security + JWT
                        - **Caché**: Spring Cache para productos frecuentes
                        - **Performance**: Índices optimizados, @EntityGraph para N+1 queries
                        - **Validación**: Jakarta Bean Validation con mensajes en español
                        - **Testing**: 65 tests (unit + integration) con >75% coverage
                        
                        ## 📝 Cómo Usar Esta API
                        
                        ### 1. Autenticación
                        
                        Para usar endpoints protegidos necesitas un token JWT:
                        
                        1. Usa `/api/auth/register` para crear una cuenta o
                        2. Usa `/api/auth/login` con usuarios de prueba:
                           - Admin: `admin@babycash.com` / `Admin123!`
                           - Demo: `demo@babycash.com` / `Demo123!`
                           - Test: `maria.garcia@example.com` / `Maria123!`
                        
                        3. Copia el token del response
                        4. Haz clic en el botón **"Authorize"** 🔒 arriba
                        5. Ingresa: `Bearer {tu-token-aqui}`
                        6. Ahora puedes usar todos los endpoints protegidos
                        
                        ### 2. Flujo Típico de Compra
                        
                        1. **Explorar productos**: `GET /api/products` o `GET /api/products/featured`
                        2. **Ver detalle**: `GET /api/products/{id}`
                        3. **Agregar al carrito**: `POST /api/cart/add` (requiere login)
                        4. **Ver carrito**: `GET /api/cart`
                        5. **Crear orden**: `POST /api/orders`
                        6. **Procesar pago**: `POST /api/payments/process`
                        7. **Ver orden**: `GET /api/orders/{id}`
                        
                        ### 3. Consultas Públicas (Sin Autenticación)
                        
                        - Todos los endpoints de `/api/products/**`
                        - Health check: `/api/health`
                        - Login y registro: `/api/auth/**`
                        
                        ### 4. Códigos de Respuesta
                        
                        - **200**: Operación exitosa
                        - **201**: Recurso creado
                        - **204**: Operación exitosa sin contenido
                        - **400**: Datos inválidos o error de validación
                        - **401**: No autenticado (token faltante/inválido)
                        - **403**: Sin permisos (recurso de otro usuario)
                        - **404**: Recurso no encontrado
                        - **500**: Error interno del servidor
                        
                        ## 🔍 Datos de Prueba
                        
                        El sistema carga automáticamente:
                        - 3 usuarios (admin, demo, test)
                        - 27 productos en 8 categorías
                        - Todos los productos tienen stock, precios, imágenes y descripciones
                        
                        ## 📧 Soporte
                        
                        Para preguntas o problemas, contacta al equipo de desarrollo.
                        
                        ## 🎨 Interfaz Swagger
                        
                        Esta interfaz te permite:
                        - Ver todos los endpoints disponibles organizados por categorías
                        - Probar cada endpoint directamente desde el navegador
                        - Ver ejemplos de request/response
                        - Leer documentación detallada de cada operación
                        - Autenticarte y probar endpoints protegidos
                        
                        ¡Explora los endpoints abajo y prueba la API! 👇
                        """,
                contact = @Contact(
                        name = "Equipo BabyCash",
                        email = "soporte@babycash.com",
                        url = "https://babycash.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        description = "Servidor de Desarrollo Local",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Servidor de Producción",
                        url = "https://api.babycash.com"
                )
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = """
                Autenticación mediante JWT (JSON Web Token).
                
                **Cómo obtener el token:**
                1. Usa POST /api/auth/login con email y password
                2. Copia el valor del campo "token" del response
                3. Haz clic en el botón "Authorize" 🔒
                4. Ingresa: Bearer {token}
                5. Haz clic en "Authorize" y luego "Close"
                
                **Formato del header:**
                ```
                Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                ```
                
                **Duración del token:** 24 horas
                
                **Usuarios de prueba:**
                - admin@babycash.com / Admin123!
                - demo@babycash.com / Demo123!
                - maria.garcia@example.com / Maria123!
                """,
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // La configuración se hace mediante anotaciones
    // No necesitamos beans adicionales gracias a springdoc-openapi-starter-webmvc-ui
}
