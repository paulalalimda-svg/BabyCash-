# 💾 CAPA REPOSITORY

## 🎯 ¿Qué es un Repository?

El **Repository** es la capa que **accede a la base de datos**. Es el único lugar donde se hacen consultas SQL (a través de JPA).

### Analogía

Es como el **bibliotecario**:
- Tú le pides un libro (datos)
- Él busca en los estantes (base de datos)
- Te entrega el libro
- No necesitas saber dónde está guardado

---

## 📚 Interface vs Implementación

### En JPA: Solo Interface

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Solo declaras métodos
    // Spring Data JPA genera la implementación automáticamente ✅
    
    List<Product> findByAvailableTrue();
    Optional<Product> findByName(String name);
    boolean existsByName(String name);
}
```

**No necesitas crear una clase de implementación.** Spring lo hace por ti.

### ¿Cómo funciona?

```
1. Declaras la interface
   ↓
2. Spring Data JPA escanea tu código
   ↓
3. Spring crea una clase que implementa tu interface
   ↓
4. Spring genera el SQL automáticamente
   ↓
5. Puedes usar el Repository en tu Service
```

### Ejemplo: Sin JPA (Antes)

```java
// ❌ ANTES: Tenías que crear clase de implementación
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            product.setPrice(rs.getBigDecimal("price"));
            // ... mapear 10 campos más
            return product;
        });
    }
    
    @Override
    public Product findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            // ... mapear 10 campos más
            return product;
        });
    }
    
    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            // INSERT
            String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getStock());
        } else {
            // UPDATE
            String sql = "UPDATE products SET name = ?, price = ?, stock = ? WHERE id = ?";
            jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getStock(), product.getId());
        }
        return product;
    }
}
```

### Ejemplo: Con JPA (Ahora)

```java
// ✅ AHORA: Solo interface
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring genera toda la implementación automáticamente ✅
}

// Uso en Service
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();  // ✅ Funciona sin implementación
    }
}
```

---

## 🔧 Spring Data JPA

### Jerarquía de Interfaces

```
Repository (marcador)
    ↓
CrudRepository (CRUD básico)
    ↓
PagingAndSortingRepository (paginación y ordenamiento)
    ↓
JpaRepository (más métodos + JPQL)
    ↓
Tu Interface (ProductRepository)
```

### JpaRepository

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Ya incluye métodos:
    // - save()
    // - findById()
    // - findAll()
    // - deleteById()
    // - count()
    // - existsById()
    // + muchos más
}
```

### Métodos Heredados

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public void examples() {
        // Guardar
        Product product = new Product();
        productRepository.save(product);  // INSERT o UPDATE
        
        // Guardar múltiples
        List<Product> products = List.of(product1, product2);
        productRepository.saveAll(products);
        
        // Buscar por ID
        Optional<Product> optional = productRepository.findById(1L);
        Product product = productRepository.findById(1L).orElse(null);
        
        // Buscar todos
        List<Product> all = productRepository.findAll();
        
        // Buscar con ordenamiento
        List<Product> sorted = productRepository.findAll(Sort.by("name").ascending());
        
        // Buscar con paginación
        Page<Product> page = productRepository.findAll(PageRequest.of(0, 10));
        
        // Contar
        long count = productRepository.count();
        
        // Verificar existencia
        boolean exists = productRepository.existsById(1L);
        
        // Eliminar por ID
        productRepository.deleteById(1L);
        
        // Eliminar por objeto
        productRepository.delete(product);
        
        // Eliminar todos
        productRepository.deleteAll();
    }
}
```

---

## 🔍 Métodos de Consulta por Nombre

### Query Methods

Spring Data JPA genera SQL automáticamente basándose en el **nombre del método**.

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // findBy + Campo
    List<Product> findByName(String name);
    // SQL: SELECT * FROM products WHERE name = ?
    
    // findBy + Campo + Operador
    List<Product> findByPriceLessThan(BigDecimal price);
    // SQL: SELECT * FROM products WHERE price < ?
    
    List<Product> findByPriceGreaterThan(BigDecimal price);
    // SQL: SELECT * FROM products WHERE price > ?
    
    List<Product> findByNameContaining(String keyword);
    // SQL: SELECT * FROM products WHERE name LIKE '%keyword%'
    
    List<Product> findByNameStartingWith(String prefix);
    // SQL: SELECT * FROM products WHERE name LIKE 'prefix%'
    
    List<Product> findByAvailableTrue();
    // SQL: SELECT * FROM products WHERE available = TRUE
    
    List<Product> findByAvailableFalse();
    // SQL: SELECT * FROM products WHERE available = FALSE
    
    // Múltiples condiciones con AND
    List<Product> findByAvailableTrueAndStockGreaterThan(Integer stock);
    // SQL: SELECT * FROM products WHERE available = TRUE AND stock > ?
    
    // Múltiples condiciones con OR
    List<Product> findByNameOrDescription(String name, String description);
    // SQL: SELECT * FROM products WHERE name = ? OR description = ?
    
    // Ordenamiento
    List<Product> findByAvailableTrueOrderByPriceAsc();
    // SQL: SELECT * FROM products WHERE available = TRUE ORDER BY price ASC
    
    // Contar
    long countByAvailableTrue();
    // SQL: SELECT COUNT(*) FROM products WHERE available = TRUE
    
    // Verificar existencia
    boolean existsByName(String name);
    // SQL: SELECT COUNT(*) > 0 FROM products WHERE name = ?
    
    // Eliminar
    void deleteByAvailableFalse();
    // SQL: DELETE FROM products WHERE available = FALSE
}
```

---

## 📝 @Query - Consultas Personalizadas

### JPQL

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Consulta básica
    @Query("SELECT p FROM Product p WHERE p.available = true")
    List<Product> findAvailableProducts();
    
    // Con parámetros posicionales
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN ?1 AND ?2")
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
    
    // Con parámetros nombrados (recomendado)
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
    
    // Con JOIN
    @Query("""
        SELECT p FROM Product p 
        LEFT JOIN FETCH p.categories 
        WHERE p.id = :id
        """)
    Optional<Product> findByIdWithCategories(@Param("id") Long id);
    
    // Agregaciones
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.available = true")
    BigDecimal calculateAveragePrice();
    
    @Query("SELECT SUM(p.stock) FROM Product p")
    Long calculateTotalStock();
    
    // COUNT
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock > 0")
    long countInStock();
    
    // Proyección (solo algunos campos)
    @Query("SELECT p.name, p.price FROM Product p WHERE p.available = true")
    List<Object[]> findNameAndPrice();
}
```

### SQL Nativo

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // SQL nativo básico
    @Query(value = "SELECT * FROM products WHERE available = true", nativeQuery = true)
    List<Product> findAvailableProductsNative();
    
    // Con parámetros
    @Query(value = "SELECT * FROM products WHERE stock < :threshold", nativeQuery = true)
    List<Product> findLowStockNative(@Param("threshold") Integer threshold);
    
    // Con JOIN
    @Query(value = """
        SELECT p.* 
        FROM products p 
        INNER JOIN product_categories pc ON p.id = pc.product_id 
        WHERE pc.category_id = :categoryId
        """, nativeQuery = true)
    List<Product> findByCategoryIdNative(@Param("categoryId") Long categoryId);
}
```

---

## 🔄 @Modifying - UPDATE y DELETE

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Actualizar
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id")
    void reduceStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Product p SET p.available = false WHERE p.stock = 0")
    void markOutOfStockAsUnavailable();
    
    // Eliminar
    @Modifying
    @Query("DELETE FROM Product p WHERE p.available = false")
    void deleteUnavailableProducts();
}

// Uso en Service
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Transactional
    public void reduceStock(Long productId, Integer quantity) {
        productRepository.reduceStock(productId, quantity);
    }
}
```

**⚠️ Siempre usa `@Modifying` y `@Transactional` para UPDATE/DELETE.**

---

## 📄 Paginación y Ordenamiento

### Pageable

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByAvailableTrue(Pageable pageable);
    
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
}

// Uso en Service
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Page<ProductDTO> getProducts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return productRepository.findByAvailableTrue(pageable)
            .map(this::convertToDTO);
    }
}

// Uso en Controller
@GetMapping
public ResponseEntity<Page<ProductDTO>> getProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "name") String sortBy
) {
    Page<ProductDTO> products = productService.getProducts(page, size, sortBy);
    return ResponseEntity.ok(products);
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ========================================
    // QUERY METHODS (generados automáticamente)
    // ========================================
    
    List<Product> findByAvailableTrue();
    
    List<Product> findByName(String name);
    
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    List<Product> findByPriceLessThan(BigDecimal price);
    
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
    
    List<Product> findByStockGreaterThan(Integer stock);
    
    List<Product> findByStockLessThan(Integer threshold);
    
    List<Product> findByAvailableTrueAndStockGreaterThan(Integer stock);
    
    List<Product> findByAvailableTrueOrderByPriceAsc();
    
    boolean existsByName(String name);
    
    long countByAvailableTrue();
    
    // ========================================
    // QUERY METHODS con paginación
    // ========================================
    
    Page<Product> findByAvailableTrue(Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // ========================================
    // @Query con JPQL
    // ========================================
    
    @Query("SELECT p FROM Product p WHERE p.available = true AND p.stock > 0")
    List<Product> findAvailableInStock();
    
    @Query("""
        SELECT p FROM Product p 
        WHERE p.price < :maxPrice 
        AND p.stock > :minStock 
        ORDER BY p.price ASC
        """)
    List<Product> findByPriceAndStock(
        @Param("maxPrice") BigDecimal maxPrice, 
        @Param("minStock") Integer minStock
    );
    
    @Query("SELECT AVG(p.price) FROM Product p WHERE p.available = true")
    BigDecimal calculateAveragePrice();
    
    @Query("SELECT SUM(p.stock) FROM Product p")
    Long calculateTotalStock();
    
    @Query("""
        SELECT p FROM Product p 
        WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        """)
    List<Product> searchProducts(
        @Param("name") String name,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );
    
    // ========================================
    // @Query con SQL nativo
    // ========================================
    
    @Query(value = """
        SELECT * FROM products 
        WHERE available = true 
        AND stock > 0 
        ORDER BY RANDOM() 
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> findRandomProducts(@Param("limit") int limit);
    
    @Query(value = """
        SELECT p.* FROM products p
        WHERE p.id IN (
            SELECT oi.product_id FROM order_items oi
            GROUP BY oi.product_id
            ORDER BY COUNT(*) DESC
            LIMIT :limit
        )
        """, nativeQuery = true)
    List<Product> findMostOrderedProducts(@Param("limit") int limit);
    
    // ========================================
    // @Modifying - UPDATE/DELETE
    // ========================================
    
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id")
    void reduceStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :id")
    void increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Product p SET p.available = false WHERE p.stock = 0")
    void markOutOfStockAsUnavailable();
    
    @Modifying
    @Query("UPDATE Product p SET p.available = true WHERE p.stock > 0 AND p.available = false")
    void markInStockAsAvailable();
    
    @Modifying
    @Query("DELETE FROM Product p WHERE p.available = false AND p.stock = 0")
    void deleteUnavailableProducts();
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
    
    // Query methods
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    long countByStatus(OrderStatus status);
    
    long countByUserId(Long userId);
    
    // @Query JPQL
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
    
    @Query("""
        SELECT o FROM Order o 
        WHERE o.user.id = :userId 
        ORDER BY o.createdAt DESC
        """)
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("""
        SELECT o FROM Order o 
        WHERE o.status = :status 
        AND o.createdAt < :date
        """)
    List<Order> findOldOrdersByStatus(
        @Param("status") OrderStatus status, 
        @Param("date") LocalDateTime date
    );
    
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
    
    // SQL nativo
    @Query(value = """
        SELECT * FROM orders 
        WHERE DATE(created_at) = CURRENT_DATE 
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Order> findTodayOrders();
    
    @Query(value = """
        SELECT o.* FROM orders o
        WHERE o.user_id = :userId
        AND EXTRACT(MONTH FROM o.created_at) = :month
        AND EXTRACT(YEAR FROM o.created_at) = :year
        """, nativeQuery = true)
    List<Order> findByUserIdAndMonth(
        @Param("userId") Long userId,
        @Param("month") int month,
        @Param("year") int year
    );
}
```

---

## 📋 Resumen

| Concepto | Descripción | Ejemplo |
|----------|-------------|---------|
| **@Repository** | Marca interface como Repository | `@Repository` |
| **JpaRepository** | Interface base con métodos CRUD | `extends JpaRepository<Product, Long>` |
| **Query Methods** | SQL generado por nombre | `findByName(String name)` |
| **@Query** | Consultas JPQL personalizadas | `@Query("SELECT...")` |
| **nativeQuery** | Consultas SQL nativas | `@Query(value = "...", nativeQuery = true)` |
| **@Modifying** | UPDATE/DELETE | `@Modifying @Query("UPDATE...")` |
| **Pageable** | Paginación | `Page<Product> findAll(Pageable)` |

---

**Última actualización**: Octubre 2025
