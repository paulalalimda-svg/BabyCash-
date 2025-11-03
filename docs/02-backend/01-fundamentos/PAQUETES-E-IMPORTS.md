# 📦 PAQUETES E IMPORTS EN JAVA

## 🎯 ¿Qué es un Package (Paquete)?

**Explicación Simple:**
Un package es como una **carpeta** que organiza clases relacionadas. Es la forma de mantener el código ordenado.

**Explicación Técnica:**
Un package es un **namespace** que agrupa clases e interfaces relacionadas, evitando conflictos de nombres.

---

## 📝 Sintaxis

```java
// Primera línea del archivo (SIEMPRE)
package com.babycash.backend.service;

// Luego los imports
import com.babycash.backend.model.User;
import java.util.List;

// Luego la clase
public class UserService {
    // ...
}
```

---

## 🏗️ Estructura de Packages en el Proyecto

```
com.babycash.backend
├── controller              # Controllers (API REST)
├── service                 # Services (lógica de negocio)
├── repository              # Repositories (acceso a BD)
├── model                   # Entities (tablas BD)
│   ├── entity
│   ├── dto
│   │   ├── request
│   │   └── response
│   └── enums
├── security                # JWT, autenticación
├── config                  # Configuraciones
│   └── security
├── exception               # Excepciones personalizadas
└── util                    # Utilidades
```

---

## 📋 Convención de Nombres

### Estructura Estándar

```
com.empresa.proyecto.modulo.submodulo

Ejemplo:
com.babycash.backend.service
│   │        │        │
│   │        │        └─ Módulo (service, controller, model)
│   │        └─ Nombre del proyecto
│   └─ Nombre de la empresa
└─ Siempre empieza con 'com' (comercial)
```

### Reglas

- ✅ Todo en **minúsculas**
- ✅ Puntos separan niveles
- ✅ Sin espacios ni caracteres especiales
- ✅ Nombres descriptivos

```java
// ✅ CORRECTO
package com.babycash.backend.service;
package com.babycash.backend.controller;
package com.babycash.backend.model.dto.request;

// ❌ INCORRECTO
package Service;
package com.BabyCash.Backend.Service;
package com.babycash.backend.Service_Package;
```

---

## 📥 Imports

### ¿Qué son los Imports?

**Explicación Simple:**
`import` es como decir **"voy a usar algo que está en otro archivo"**.

### Ejemplo

```java
package com.babycash.backend.service;

// Importar clase de otro package del proyecto
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.entity.Product;
import com.babycash.backend.repository.UserRepository;

// Importar clases de Java estándar
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

// Importar clases de librerías externas (Spring, etc.)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // Usamos UserRepository (importado arriba)
    
    public List<User> getAllUsers() {  // Usamos List y User (importados arriba)
        return userRepository.findAll();
    }
}
```

---

## 🔍 Tipos de Imports

### 1. Import Específico

```java
import com.babycash.backend.model.entity.User;
import com.babycash.backend.model.entity.Product;
import com.babycash.backend.model.entity.Order;
```

### 2. Import con Wildcard (*)

```java
// Importar TODAS las clases del package
import com.babycash.backend.model.entity.*;

// Ahora puedes usar: User, Product, Order, etc.
```

**⚠️ Buena práctica:** Evita `*` en producción, usa imports específicos.

### 3. Import Estático

```java
// Importar métodos/constantes estáticas
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

public class Calculator {
    public double calculateCircle(double radius) {
        return PI * radius * radius;  // No necesitas Math.PI
    }
}
```

---

## 🎯 Ejemplo Completo: ProductService.java

```java
// 1. DECLARAR PACKAGE (primera línea)
package com.babycash.backend.service;

// 2. IMPORTS DEL PROYECTO
import com.babycash.backend.model.entity.Product;
import com.babycash.backend.model.dto.request.ProductRequest;
import com.babycash.backend.model.dto.response.ProductResponse;
import com.babycash.backend.repository.ProductRepository;
import com.babycash.backend.exception.NotFoundException;
import com.babycash.backend.exception.BadRequestException;

// 3. IMPORTS DE JAVA ESTÁNDAR
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 4. IMPORTS DE SPRING
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 5. IMPORTS DE LOMBOK
import lombok.extern.slf4j.Slf4j;

// 6. CLASE
@Service
@Slf4j
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        
        return products.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        
        return convertToResponse(product);
    }
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Precio debe ser mayor a 0");
        }
        
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCreatedAt(LocalDateTime.now());
        
        product = productRepository.save(product);
        
        log.info("Producto creado: {}", product.getName());
        
        return convertToResponse(product);
    }
    
    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        return response;
    }
}
```

---

## 📊 Packages por Tipo de Clase

### Controllers

```java
package com.babycash.backend.controller;

import com.babycash.backend.service.ProductService;
import com.babycash.backend.model.dto.request.ProductRequest;
import com.babycash.backend.model.dto.response.ProductResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    // ...
}
```

### Services

```java
package com.babycash.backend.service;

import com.babycash.backend.model.entity.Product;
import com.babycash.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    // ...
}
```

### Repositories

```java
package com.babycash.backend.repository;

import com.babycash.backend.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // ...
}
```

### Entities

```java
package com.babycash.backend.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    // ...
}
```

### DTOs

```java
// REQUEST
package com.babycash.backend.model.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private BigDecimal price;
    private int stock;
}
```

```java
// RESPONSE
package com.babycash.backend.model.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
}
```

---

## 🔄 Imports Automáticos en IDEs

### IntelliJ IDEA / VS Code

```
1. Escribe el nombre de la clase
2. Alt + Enter (IntelliJ) o Ctrl + . (VS Code)
3. Selecciona "Import class"
```

### Organizar Imports

```
Ctrl + Alt + O (IntelliJ)
Shift + Alt + O (VS Code)
```

---

## ⚠️ Errores Comunes

### 1. No Declarar Package

```java
// ❌ INCORRECTO - Falta declaración de package
import com.babycash.backend.model.User;

public class UserService {
    // ...
}
```

```java
// ✅ CORRECTO
package com.babycash.backend.service;

import com.babycash.backend.model.entity.User;

public class UserService {
    // ...
}
```

### 2. Conflicto de Nombres

```java
// Ambos se llaman 'Date'
import java.util.Date;
import java.sql.Date;

// Solución: Usa el nombre completo
public void metodo() {
    java.util.Date utilDate = new java.util.Date();
    java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
}
```

### 3. Import Incorrecto

```java
// ❌ INCORRECTO - Clase no existe en ese package
import com.babycash.backend.model.User;

// ✅ CORRECTO
import com.babycash.backend.model.entity.User;
```

---

## 📚 Organización del Proyecto

### Estructura Real de BabyCash

```
src/main/java/com/babycash/backend/
│
├── BabyCashApplication.java          # Clase principal
│
├── controller/                        # Controllers
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   └── UserController.java
│
├── service/                           # Services
│   ├── AuthService.java
│   ├── ProductService.java
│   ├── CartService.java
│   ├── OrderService.java
│   ├── PaymentService.java
│   └── EmailService.java
│
├── repository/                        # Repositories
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   └── OrderRepository.java
│
├── model/
│   ├── entity/                        # Entidades (tablas BD)
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Cart.java
│   │   ├── Order.java
│   │   └── Payment.java
│   │
│   ├── dto/
│   │   ├── request/                   # DTOs de entrada
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   └── ProductRequest.java
│   │   │
│   │   └── response/                  # DTOs de salida
│   │       ├── AuthResponse.java
│   │       ├── ProductResponse.java
│   │       └── OrderResponse.java
│   │
│   └── enums/                         # Enumeraciones
│       ├── UserRole.java
│       ├── OrderStatus.java
│       └── PaymentStatus.java
│
├── security/                          # Seguridad
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
│
├── config/                            # Configuraciones
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── EmailConfig.java
│
├── exception/                         # Excepciones
│   ├── NotFoundException.java
│   ├── BadRequestException.java
│   └── GlobalExceptionHandler.java
│
└── util/                              # Utilidades
    └── DateUtil.java
```

---

## ✅ Buenas Prácticas

### 1. Un Archivo = Una Clase Pública

```java
// ✅ CORRECTO - User.java contiene solo User
package com.babycash.backend.model.entity;

public class User {
    // ...
}
```

### 2. Package Matches Carpeta

```
Archivo: src/main/java/com/babycash/backend/service/UserService.java
Package: com.babycash.backend.service

✅ Coincide
```

### 3. Imports Ordenados

```java
// 1. Imports del proyecto
import com.babycash.backend.model.User;
import com.babycash.backend.repository.UserRepository;

// 2. Imports de Java estándar
import java.util.List;
import java.time.LocalDateTime;

// 3. Imports de librerías externas
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
```

---

## 📋 Resumen

| Concepto | Definición | Ejemplo |
|----------|------------|---------|
| **Package** | Carpeta que agrupa clases | `com.babycash.backend.service` |
| **Import** | Usar clase de otro package | `import java.util.List;` |
| **Wildcard** | Importar todo un package | `import java.util.*;` |
| **Static Import** | Importar métodos estáticos | `import static Math.PI;` |

---

**Última actualización**: Octubre 2025
