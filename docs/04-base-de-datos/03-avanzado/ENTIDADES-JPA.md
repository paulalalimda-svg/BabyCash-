# 🏷️ ENTIDADES JPA

## 🎯 ¿Qué es una Entity?

Una **Entity** es una clase Java que representa una **tabla** en la base de datos.

```
Clase Java User  →  Tabla users
Objeto user1     →  Fila en la tabla
Atributo email   →  Columna email
```

---

## 📦 Anotación @Entity

### Uso Básico

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String name;
    
    // getters y setters
}
```

**Resultado en PostgreSQL:**

```sql
CREATE TABLE user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),
    name VARCHAR(255)
);
```

---

## 🏷️ @Table - Personalizar Nombre de Tabla

### Sin @Table

```java
@Entity
public class User {
    // Tabla se llama "user"
}
```

### Con @Table

```java
@Entity
@Table(name = "users")
public class User {
    // Tabla se llama "users"
}
```

### Con Schema

```java
@Entity
@Table(name = "users", schema = "ecommerce")
public class User {
    // Tabla se llama "ecommerce.users"
}
```

### Con Índices

```java
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_name", columnList = "name")
    }
)
public class User {
    // Crea índices automáticamente
}
```

---

## 🔑 @Id - Primary Key

### Auto-Incremento

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PostgreSQL: BIGSERIAL
}
```

### Estrategias de Generación

| Estrategia | Descripción | SQL |
|------------|-------------|-----|
| `IDENTITY` | Auto-incremento de BD | `BIGSERIAL` (PostgreSQL) |
| `SEQUENCE` | Usa secuencia | `NEXTVAL('user_seq')` |
| `TABLE` | Tabla auxiliar | Tabla `hibernate_sequences` |
| `AUTO` | Hibernate decide | Depende de BD |

**Ejemplo con SEQUENCE:**

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;
}
```

### ID Compuesto

```java
@Embeddable
public class OrderItemId implements Serializable {
    private Long orderId;
    private Long productId;
    
    // equals, hashCode
}

@Entity
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;
    
    @ManyToOne
    @MapsId("orderId")
    private Order order;
    
    @ManyToOne
    @MapsId("productId")
    private Product product;
}
```

---

## 📝 @Column - Mapeo de Columnas

### Uso Básico

```java
@Entity
public class User {
    @Column(name = "user_email")  // Columna se llama "user_email"
    private String email;
}
```

### Propiedades

```java
@Entity
public class Product {
    @Column(
        name = "product_name",     // Nombre en BD
        nullable = false,          // NOT NULL
        unique = true,             // UNIQUE
        length = 100,              // VARCHAR(100)
        columnDefinition = "TEXT"  // Tipo específico
    )
    private String name;
    
    @Column(precision = 10, scale = 2)  // DECIMAL(10, 2)
    private BigDecimal price;
}
```

**Resultado SQL:**

```sql
CREATE TABLE product (
    product_name TEXT UNIQUE NOT NULL,
    price DECIMAL(10, 2)
);
```

### Nombre Automático

```java
@Entity
public class User {
    private String email;  // Columna: "email"
    private String firstName;  // Columna: "first_name" (convención camelCase → snake_case)
}
```

---

## 🔄 Tipos de Datos Java → SQL

### Mapeo Automático

| Tipo Java | Tipo SQL (PostgreSQL) |
|-----------|-----------------------|
| `Long`, `Integer` | `BIGINT`, `INTEGER` |
| `String` | `VARCHAR(255)` |
| `BigDecimal` | `NUMERIC` |
| `Boolean` | `BOOLEAN` |
| `LocalDate` | `DATE` |
| `LocalDateTime` | `TIMESTAMP` |
| `LocalTime` | `TIME` |
| `byte[]` | `BYTEA` |
| `Enum` | `VARCHAR` o `INTEGER` |

### Enums

```java
public enum Role {
    USER, ADMIN, MODERATOR
}

@Entity
public class User {
    // Opción 1: Guardar como STRING ("USER", "ADMIN")
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;
    
    // Opción 2: Guardar como ORDINAL (0, 1, 2)
    @Enumerated(EnumType.ORDINAL)
    private Role role;
}
```

**⚠️ Usa `EnumType.STRING` siempre. `ORDINAL` rompe si cambias el orden.**

### Fechas

```java
@Entity
public class Order {
    @Column(name = "created_at")
    private LocalDateTime createdAt;  // TIMESTAMP
    
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;  // DATE
    
    @Column(name = "created_time")
    private LocalTime createdTime;  // TIME
}
```

### JSON (PostgreSQL)

```java
@Entity
public class Product {
    @Column(columnDefinition = "jsonb")
    private String metadata;  // Guarda JSON
}

// Uso
Product product = new Product();
product.setMetadata("{\"color\":\"rojo\",\"size\":\"M\"}");
```

---

## 🔄 @Transient - Campos No Persistentes

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String password;  // Se guarda en BD
    
    @Transient
    private String confirmPassword;  // NO se guarda en BD
}
```

---

## 📅 @CreatedDate y @LastModifiedDate

### Con JPA Auditing

```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**Configuración:**

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
```

### Con @PrePersist y @PreUpdate

```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

---

## 🎯 Ejemplo Completo: User Entity

```java
package com.babycash.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_role", columnList = "role")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 20)
    private String phone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    // Relaciones (se explican en RELACIONES-JPA.md)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;
}
```

**Enum Role:**

```java
public enum Role {
    USER,
    ADMIN,
    MODERATOR
}
```

---

## 🎯 Ejemplo Completo: Product Entity

```java
package com.babycash.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_price", columnList = "price"),
        @Index(name = "idx_product_available", columnList = "available")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock = 0;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Métodos auxiliares
    @Transient
    public boolean hasStock() {
        return stock != null && stock > 0;
    }
    
    @Transient
    public boolean hasStock(Integer quantity) {
        return stock != null && stock >= quantity;
    }
}
```

---

## 🎯 Ejemplo Completo: Order Entity

```java
package com.babycash.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_order_number", columnList = "order_number", unique = true),
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_user", columnList = "user_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    // Método auxiliar
    @PrePersist
    protected void generateOrderNumber() {
        if (orderNumber == null) {
            orderNumber = "ORD-" + System.currentTimeMillis();
        }
    }
}
```

**Enum OrderStatus:**

```java
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
```

---

## 📋 Resumen de Anotaciones

| Anotación | Propósito | Ejemplo |
|-----------|-----------|---------|
| `@Entity` | Marca clase como entidad | `@Entity` |
| `@Table` | Personaliza nombre de tabla | `@Table(name = "users")` |
| `@Id` | Primary key | `@Id` |
| `@GeneratedValue` | Generación automática de ID | `@GeneratedValue(strategy = IDENTITY)` |
| `@Column` | Personaliza columna | `@Column(nullable = false)` |
| `@Enumerated` | Mapeo de enum | `@Enumerated(EnumType.STRING)` |
| `@Transient` | Campo no persistente | `@Transient` |
| `@CreatedDate` | Fecha de creación automática | `@CreatedDate` |
| `@LastModifiedDate` | Fecha de actualización automática | `@LastModifiedDate` |
| `@PrePersist` | Antes de guardar | `@PrePersist void onCreate()` |
| `@PreUpdate` | Antes de actualizar | `@PreUpdate void onUpdate()` |

---

**Última actualización**: Octubre 2025
