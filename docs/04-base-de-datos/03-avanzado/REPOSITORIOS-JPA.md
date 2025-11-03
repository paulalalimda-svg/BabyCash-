# 📦 REPOSITORIOS JPA

## 🎯 ¿Qué es un Repository?

Un **Repository** es una interfaz que permite **acceder y manipular datos** en la base de datos sin escribir SQL.

### Analogía

Es como un **bibliotecario**:
- Tú le pides un libro (datos)
- Él sabe dónde está y te lo trae
- No necesitas saber cómo está organizada la biblioteca (BD)

---

## 📚 JpaRepository

### Interfaz Base

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Ya incluye métodos automáticos
}
```

**Parámetros:**
- `User` → Entidad que gestiona
- `Long` → Tipo del ID

---

## 🔧 Métodos Automáticos

### Guardar

```java
// Crear nuevo usuario
User user = new User();
user.setEmail("maria@gmail.com");
user.setName("María");
userRepository.save(user);  // INSERT INTO users...

// Actualizar usuario existente
User user = userRepository.findById(1L).orElse(null);
user.setName("María García");
userRepository.save(user);  // UPDATE users SET name = 'María García' WHERE id = 1
```

### Buscar

```java
// Por ID
Optional<User> user = userRepository.findById(1L);
User user = userRepository.findById(1L).orElse(null);  // Con null si no existe
User user = userRepository.findById(1L).orElseThrow(() -> 
    new RuntimeException("Usuario no encontrado"));

// Todos
List<User> users = userRepository.findAll();

// Con paginación
Page<User> users = userRepository.findAll(PageRequest.of(0, 10));
// Página 0, 10 registros por página

// Con ordenamiento
List<User> users = userRepository.findAll(Sort.by("name").ascending());

// Verificar existencia
boolean exists = userRepository.existsById(1L);
```

### Contar

```java
long total = userRepository.count();  // Total de usuarios
```

### Eliminar

```java
// Por ID
userRepository.deleteById(1L);

// Por objeto
User user = userRepository.findById(1L).orElse(null);
userRepository.delete(user);

// Todos
userRepository.deleteAll();

// Múltiples
List<User> users = userRepository.findByActiveTrue();
userRepository.deleteAll(users);
```

---

## 🔍 Métodos de Consulta por Nombre

### Sintaxis

```
findBy + Campo + Operador
```

### Operadores

| Operador | Ejemplo | SQL |
|----------|---------|-----|
| **Equals** | `findByEmail(String email)` | `WHERE email = ?` |
| **Not** | `findByActiveNot(Boolean active)` | `WHERE active != ?` |
| **Like** | `findByNameLike(String pattern)` | `WHERE name LIKE ?` |
| **Containing** | `findByNameContaining(String keyword)` | `WHERE name LIKE '%keyword%'` |
| **StartingWith** | `findByNameStartingWith(String prefix)` | `WHERE name LIKE 'prefix%'` |
| **EndingWith** | `findByNameEndingWith(String suffix)` | `WHERE name LIKE '%suffix'` |
| **IgnoreCase** | `findByEmailIgnoreCase(String email)` | `WHERE LOWER(email) = LOWER(?)` |
| **LessThan** | `findByPriceLessThan(BigDecimal price)` | `WHERE price < ?` |
| **GreaterThan** | `findByPriceGreaterThan(BigDecimal price)` | `WHERE price > ?` |
| **Between** | `findByPriceBetween(BigDecimal min, BigDecimal max)` | `WHERE price BETWEEN ? AND ?` |
| **Before** | `findByCreatedAtBefore(LocalDateTime date)` | `WHERE created_at < ?` |
| **After** | `findByCreatedAtAfter(LocalDateTime date)` | `WHERE created_at > ?` |
| **IsNull** | `findByPhoneIsNull()` | `WHERE phone IS NULL` |
| **IsNotNull** | `findByPhoneIsNotNull()` | `WHERE phone IS NOT NULL` |
| **True** | `findByActiveTrue()` | `WHERE active = TRUE` |
| **False** | `findByActiveFalse()` | `WHERE active = FALSE` |
| **OrderBy** | `findByActiveOrderByNameAsc(Boolean active)` | `WHERE active = ? ORDER BY name ASC` |

### Ejemplos

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Buscar por email
    Optional<User> findByEmail(String email);
    // SQL: SELECT * FROM users WHERE email = ?
    
    // Buscar usuarios activos
    List<User> findByActiveTrue();
    // SQL: SELECT * FROM users WHERE active = TRUE
    
    // Buscar por nombre (ignora mayúsculas)
    List<User> findByNameContainingIgnoreCase(String keyword);
    // SQL: SELECT * FROM users WHERE LOWER(name) LIKE LOWER('%keyword%')
    
    // Buscar por rol y activo
    List<User> findByRoleAndActiveTrue(Role role);
    // SQL: SELECT * FROM users WHERE role = ? AND active = TRUE
    
    // Buscar por email o nombre
    List<User> findByEmailOrNameContaining(String email, String name);
    // SQL: SELECT * FROM users WHERE email = ? OR name LIKE ?
    
    // Contar usuarios por rol
    long countByRole(Role role);
    // SQL: SELECT COUNT(*) FROM users WHERE role = ?
    
    // Verificar si existe email
    boolean existsByEmail(String email);
    // SQL: SELECT COUNT(*) FROM users WHERE email = ? LIMIT 1
    
    // Eliminar por email
    void deleteByEmail(String email);
    // SQL: DELETE FROM users WHERE email = ?
}
```

---

## 🎯 @Query - Consultas Personalizadas

### JPQL (Java Persistence Query Language)

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Consulta básica
    @Query("SELECT p FROM Product p WHERE p.available = true")
    List<Product> findAvailableProducts();
    
    // Con parámetros posicionales
    @Query("SELECT p FROM Product p WHERE p.price < ?1")
    List<Product> findByPriceLessThan(BigDecimal price);
    
    // Con parámetros nombrados
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
    
    // Con JOIN
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.categories WHERE p.id = :id")
    Optional<Product> findByIdWithCategories(@Param("id") Long id);
    
    // Proyección (solo algunos campos)
    @Query("SELECT p.name, p.price FROM Product p WHERE p.available = true")
    List<Object[]> findNameAndPrice();
    
    // Agregación
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.available = true")
    BigDecimal calculateAveragePrice();
    
    // COUNT
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock > 0")
    long countInStock();
}
```

### SQL Nativo

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Consulta SQL nativa
    @Query(value = "SELECT * FROM orders WHERE user_id = ?1", nativeQuery = true)
    List<Order> findByUserIdNative(Long userId);
    
    // Con parámetros nombrados
    @Query(value = "SELECT * FROM orders WHERE status = :status AND total > :minTotal", nativeQuery = true)
    List<Order> findByStatusAndMinTotal(@Param("status") String status, @Param("minTotal") BigDecimal minTotal);
    
    // Con JOIN
    @Query(value = """
        SELECT o.* 
        FROM orders o 
        INNER JOIN users u ON o.user_id = u.id 
        WHERE u.email = :email
        """, nativeQuery = true)
    List<Order> findByUserEmail(@Param("email") String email);
}
```

---

## 🔄 Modificación de Datos

### @Modifying

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Actualizar
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = false WHERE u.id = :id")
    void deactivateUser(@Param("id") Long id);
    
    // Eliminar
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.active = false")
    void deleteInactiveUsers();
    
    // Actualizar múltiples campos
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.name = :name, u.phone = :phone WHERE u.id = :id")
    void updateNameAndPhone(@Param("id") Long id, @Param("name") String name, @Param("phone") String phone);
}
```

**⚠️ Siempre usa `@Modifying` y `@Transactional` para UPDATE/DELETE.**

---

## 📄 Paginación

### Configuración

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByAvailableTrue(Pageable pageable);
}
```

### Uso

```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    public Page<Product> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findByAvailableTrue(pageable);
    }
}

// En el controller
@GetMapping("/products")
public ResponseEntity<Page<Product>> getProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
) {
    Page<Product> products = productService.getProducts(page, size);
    return ResponseEntity.ok(products);
}

// Respuesta JSON
{
  "content": [...],  // Productos
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0,  // Página actual
  "first": true,
  "last": false
}
```

---

## 🎯 Ejemplo Completo: UserRepository

```java
package com.babycash.repository;

import com.babycash.model.User;
import com.babycash.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Métodos automáticos por nombre
    Optional<User> findByEmail(String email);
    
    List<User> findByActiveTrue();
    
    List<User> findByRole(Role role);
    
    List<User> findByNameContainingIgnoreCase(String keyword);
    
    boolean existsByEmail(String email);
    
    long countByRole(Role role);
    
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    // Consultas personalizadas con @Query
    @Query("SELECT u FROM User u WHERE u.active = true AND u.role = :role")
    List<User> findActiveUsersByRole(@Param("role") Role role);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    Optional<User> findByIdWithOrders(@Param("id") Long id);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    long countNewUsers(@Param("startDate") LocalDateTime startDate);
    
    // Modificación
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLogin = :loginTime WHERE u.id = :id")
    void updateLastLogin(@Param("id") Long id, @Param("loginTime") LocalDateTime loginTime);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = false WHERE u.id = :id")
    void deactivateUser(@Param("id") Long id);
}
```

---

## 🎯 Ejemplo Completo: ProductRepository

```java
package com.babycash.repository;

import com.babycash.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Métodos automáticos
    List<Product> findByAvailableTrue();
    
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    List<Product> findByPriceLessThan(BigDecimal price);
    
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
    
    List<Product> findByStockGreaterThan(Integer stock);
    
    boolean existsByName(String name);
    
    // Con paginación
    Page<Product> findByAvailableTrue(Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // Consultas personalizadas
    @Query("SELECT p FROM Product p WHERE p.available = true AND p.stock > 0")
    List<Product> findAvailableInStock();
    
    @Query("SELECT p FROM Product p WHERE p.price < :maxPrice AND p.stock > :minStock")
    List<Product> findByPriceAndStock(@Param("maxPrice") BigDecimal maxPrice, @Param("minStock") Integer minStock);
    
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.available = true")
    BigDecimal calculateAveragePrice();
    
    @Query("SELECT p FROM Product p ORDER BY p.stock ASC")
    List<Product> findByLowStock(Pageable pageable);
    
    // Modificación
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id")
    void reduceStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :id")
    void increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.available = false WHERE p.stock = 0")
    void markOutOfStockAsUnavailable();
}
```

---

## 🎯 Ejemplo Completo: OrderRepository

```java
package com.babycash.repository;

import com.babycash.model.Order;
import com.babycash.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Métodos automáticos
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Consultas personalizadas
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :date")
    List<Order> findOldOrdersByStatus(@Param("status") OrderStatus status, @Param("date") LocalDateTime date);
    
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.user.id = :userId")
    BigDecimal calculateTotalSpent(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startDate")
    long countOrdersSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("""
        SELECT o FROM Order o 
        WHERE o.user.email = :email 
        AND o.status IN :statuses 
        ORDER BY o.createdAt DESC
        """)
    List<Order> findByUserEmailAndStatuses(
        @Param("email") String email, 
        @Param("statuses") List<OrderStatus> statuses
    );
}
```

---

## 📊 Especificaciones (Criteria API)

### Para Consultas Dinámicas

```java
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
}

// Service
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice, Boolean available) {
        Specification<Product> spec = Specification.where(null);
        
        if (name != null) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        
        if (available != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("available"), available));
        }
        
        return productRepository.findAll(spec);
    }
}
```

---

## 📋 Resumen

| Método | Descripción | Ejemplo |
|--------|-------------|---------|
| `save()` | Guardar o actualizar | `userRepository.save(user)` |
| `findById()` | Buscar por ID | `userRepository.findById(1L)` |
| `findAll()` | Buscar todos | `userRepository.findAll()` |
| `deleteById()` | Eliminar por ID | `userRepository.deleteById(1L)` |
| `existsById()` | Verificar existencia | `userRepository.existsById(1L)` |
| `count()` | Contar registros | `userRepository.count()` |
| `findBy...()` | Métodos de consulta por nombre | `findByEmail(email)` |
| `@Query` | Consultas personalizadas JPQL | `@Query("SELECT...")` |
| `nativeQuery` | Consultas SQL nativas | `@Query(value = "...", nativeQuery = true)` |
| `@Modifying` | Modificar datos (UPDATE/DELETE) | `@Modifying @Query("UPDATE...")` |

---

**Última actualización**: Octubre 2025
