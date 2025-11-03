# PATRÓN REPOSITORY

## 🎯 Definición

**Repository** proporciona una **abstracción** entre la lógica de negocio y la capa de acceso a datos.

Es como un **bibliotecario**: tú pides un libro (dato), el bibliotecario sabe dónde buscarlo, pero tú NO necesitas saber cómo está organizada la biblioteca.

---

## ❓ ¿Para Qué Sirve?

### Sin Repository (Problema)

```java
❌ Service con SQL directo:
@Service
public class ProductService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public Product getProductById(Long id) {
        // ❌ Service conoce detalles de SQL
        String sql = "SELECT * FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ProductRowMapper());
    }
    
    public List<Product> findByCategory(String category) {
        // ❌ SQL en el servicio
        String sql = "SELECT * FROM products WHERE category = ?";
        return jdbcTemplate.query(sql, new Object[]{category}, new ProductRowMapper());
    }
}
```

**Problemas:**
- ❌ Service conoce SQL
- ❌ Difícil cambiar de base de datos
- ❌ Difícil de testear
- ❌ Violates Single Responsibility

---

## ✅ Con Repository

```java
// ✅ Interfaz Repository (abstracción)
public interface ProductRepository {
    Product findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    Product save(Product product);
    void deleteById(Long id);
}

// ✅ Implementación (puede ser JDBC, JPA, MongoDB, etc.)
@Repository
public class JpaProductRepository implements ProductRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Product findById(Long id) {
        return entityManager.find(Product.class, id);
    }
    
    @Override
    public List<Product> findAll() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class)
            .getResultList();
    }
    
    @Override
    public List<Product> findByCategory(String category) {
        return entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.category = :category", Product.class)
            .setParameter("category", category)
            .getResultList();
    }
    
    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }
    
    @Override
    public void deleteById(Long id) {
        Product product = findById(id);
        if (product != null) {
            entityManager.remove(product);
        }
    }
}

// ✅ Service usa Repository (NO conoce SQL)
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;  // ✅ Interfaz, no implementación
    
    public Product getProductById(Long id) {
        return productRepository.findById(id);  // ✅ Simple
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);  // ✅ Simple
    }
}
```

**Ventajas:**
- ✅ Service NO conoce SQL
- ✅ Fácil cambiar implementación (JDBC → JPA → MongoDB)
- ✅ Fácil de testear (mock del repository)
- ✅ Single Responsibility

---

## 🏗️ Repository en Spring Data JPA

Spring Data JPA **genera implementaciones automáticamente**:

### ✅ Spring Data JPA Repository

```java
// ✅ Solo defines la interfaz, Spring genera implementación
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ✅ Métodos básicos heredados (save, findById, findAll, delete, etc.)
    
    // ✅ Query methods (Spring genera SQL automáticamente)
    List<Product> findByEnabled(Boolean enabled);
    List<Product> findByCategory(Category category);
    Optional<Product> findBySlug(String slug);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // ✅ @Query personalizado
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );
    
    // ✅ Native query
    @Query(value = "SELECT * FROM products WHERE stock > 0", nativeQuery = true)
    List<Product> findProductsInStock();
}

// ✅ Service usa Repository
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public List<ProductResponse> getAllActiveProducts() {
        List<Product> products = productRepository.findByEnabled(true);
        return products.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapToResponse(product);
    }
}
```

---

## 📊 Métodos de JpaRepository

Spring Data JPA proporciona métodos **automáticamente**:

```java
public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
    
    // ✅ CRUD básico
    <S extends T> S save(S entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void delete(T entity);
    boolean existsById(ID id);
    long count();
    
    // ✅ Batch operations
    <S extends T> List<S> saveAll(Iterable<S> entities);
    void deleteAll();
    
    // ✅ Paginación
    Page<T> findAll(Pageable pageable);
    
    // ✅ Sorting
    List<T> findAll(Sort sort);
}
```

---

## 🎯 Query Methods

Spring genera SQL basado en nombres de métodos:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // findBy + Atributo
    List<Product> findByName(String name);
    // SELECT * FROM products WHERE name = ?
    
    // findBy + Atributo + And + Atributo
    List<Product> findByNameAndCategory(String name, Category category);
    // SELECT * FROM products WHERE name = ? AND category = ?
    
    // findBy + Atributo + Or + Atributo
    List<Product> findByNameOrDescription(String name, String description);
    // SELECT * FROM products WHERE name = ? OR description = ?
    
    // findBy + Atributo + Containing
    List<Product> findByNameContaining(String keyword);
    // SELECT * FROM products WHERE name LIKE %keyword%
    
    // findBy + Atributo + IgnoreCase
    List<Product> findByNameIgnoreCase(String name);
    // SELECT * FROM products WHERE LOWER(name) = LOWER(?)
    
    // findBy + Atributo + OrderBy + Atributo + Asc/Desc
    List<Product> findByEnabledOrderByPriceAsc(Boolean enabled);
    // SELECT * FROM products WHERE enabled = ? ORDER BY price ASC
    
    // findBy + Atributo + Between
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    // SELECT * FROM products WHERE price BETWEEN ? AND ?
    
    // findBy + Atributo + LessThan / GreaterThan
    List<Product> findByPriceLessThan(BigDecimal price);
    List<Product> findByPriceGreaterThan(BigDecimal price);
    
    // countBy + Atributo
    long countByEnabled(Boolean enabled);
    // SELECT COUNT(*) FROM products WHERE enabled = ?
    
    // existsBy + Atributo
    boolean existsByName(String name);
    // SELECT EXISTS(SELECT 1 FROM products WHERE name = ?)
}
```

---

## 🏗️ Repository en Baby Cash

### ✅ ProductRepository

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByEnabled(Boolean enabled);
    
    Optional<Product> findBySlug(String slug);
    
    List<Product> findByCategory(Category category);
    
    @Query("SELECT p FROM Product p WHERE p.enabled = true AND p.stock > 0")
    List<Product> findAvailableProducts();
}
```

---

### ✅ OrderRepository

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUser(User user);
    
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findUserOrders(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);
}
```

---

### ✅ UserRepository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    long countNewUsers(@Param("startDate") LocalDateTime startDate);
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es el patrón Repository?"**

> "Es un patrón estructural que proporciona una abstracción entre la lógica de negocio y el acceso a datos. El servicio NO conoce SQL ni detalles de la base de datos. Solo usa métodos del repository como `findById()`, `save()`. Esto desacopla el servicio de la implementación de persistencia."

---

**2. "¿Cómo funciona Repository en Baby Cash?"**

> "Uso Spring Data JPA. Solo defino interfaces que extienden `JpaRepository`:
> ```java
> public interface ProductRepository extends JpaRepository<Product, Long> {
>     List<Product> findByEnabled(Boolean enabled);
> }
> ```
> Spring genera la implementación automáticamente. No escribo SQL manualmente."

---

**3. "¿Cuál es la ventaja de Repository?"**

> "Desacoplamiento y mantenibilidad:
> - **Sin Repository**: Service con SQL directo → difícil cambiar DB
> - **Con Repository**: Service usa interfaz → puedo cambiar de JPA a MongoDB sin tocar service
> 
> Además, es más fácil de testear porque puedo mockear el repository."

---

**4. "¿Qué métodos proporciona JpaRepository?"**

> "CRUD completo automático:
> - `save()`: Crear/actualizar
> - `findById()`, `findAll()`: Leer
> - `deleteById()`: Eliminar
> - `existsById()`, `count()`: Utilidades
> - `findAll(Pageable)`: Paginación
> 
> Además, puedo definir query methods como `findByEmail()` y Spring genera el SQL."

---

## 📝 Checklist de Repository

```
✅ Interfaz Repository (abstracción)
✅ Extiende JpaRepository<Entity, ID>
✅ Query methods con nombres descriptivos
✅ @Query para consultas complejas
✅ Service usa interfaz, no implementación
✅ Métodos específicos del dominio
```

---

## 🏆 Ventajas y Desventajas

### ✅ Ventajas

```
✅ Desacoplamiento (service no conoce SQL)
✅ Fácil cambiar implementación (JPA → MongoDB)
✅ Fácil de testear (mock repository)
✅ Spring genera implementaciones automáticamente
✅ Query methods expresivos
✅ Single Responsibility
```

---

### ❌ Desventajas

```
❌ Capa adicional (puede ser overkill para apps simples)
❌ Curva de aprendizaje (query method naming conventions)
```

---

## 🚀 Conclusión

**Repository:**
- ✅ Abstracción de acceso a datos
- ✅ Desacopla service de DB
- ✅ Spring Data JPA genera implementaciones

**En Baby Cash, TODOS los accesos a datos usan Repository.**

---

**Ahora lee:** `PATRON-DTO.md` para el siguiente patrón. 🚀
