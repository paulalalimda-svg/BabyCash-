# 🧠 CAPA SERVICE

## 🎯 ¿Qué es un Service?

El **Service** contiene la **lógica de negocio** de tu aplicación. Es donde ocurren las validaciones, cálculos y orquestación de datos.

### Analogía

Es como el **chef** de un restaurante:
- El mesero (Controller) recibe el pedido
- El chef (Service) prepara la comida (lógica de negocio)
- El chef coordina con la despensa (Repository) para obtener ingredientes
- El mesero entrega el plato terminado al cliente

---

## 📦 @Service

### Anotación Principal

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Métodos de negocio aquí
}
```

**¿Qué hace @Service?**
- Marca la clase como **Service** (componente de Spring)
- Spring la detecta y la gestiona automáticamente
- Permite inyección de dependencias

---

## 🎓 Lógica de Negocio

### ¿Qué es la Lógica de Negocio?

Son las **reglas específicas** de tu aplicación. No es código genérico, es código que refleja cómo funciona tu negocio.

### Ejemplos en BabyCash

#### 1. Validaciones de Negocio

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public ProductDTO createProduct(ProductDTO productDTO) {
        // ✅ Validación de negocio: Producto no puede duplicarse
        if (productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("Ya existe un producto con ese nombre");
        }
        
        // ✅ Validación de negocio: Precio mínimo
        if (productDTO.getPrice().compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new RuntimeException("El precio mínimo es $1,000");
        }
        
        // ✅ Validación de negocio: Stock no puede ser negativo
        if (productDTO.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        
        Product product = convertToEntity(productDTO);
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }
}
```

#### 2. Cálculos

```java
@Service
public class CartService {
    
    public BigDecimal calculateCartTotal(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        // ✅ Lógica de negocio: Calcular total del carrito
        return cart.getItems().stream()
            .map(item -> {
                BigDecimal price = item.getProduct().getPrice();
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                return price.multiply(quantity);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal calculateDiscount(Long userId, BigDecimal total) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // ✅ Lógica de negocio: Descuento por nivel de usuario
        if (user.getRole() == Role.ADMIN) {
            return total.multiply(BigDecimal.valueOf(0.20));  // 20% descuento
        } else if (user.getLoyaltyPoints() > 1000) {
            return total.multiply(BigDecimal.valueOf(0.10));  // 10% descuento
        }
        
        return BigDecimal.ZERO;  // Sin descuento
    }
}
```

#### 3. Orquestación (Coordinar Múltiples Repositorios)

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private LoyaltyService loyaltyService;
    
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        // 1. Obtener carrito
        Cart cart = cartRepository.findById(request.getCartId())
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        // 2. Validar que el carrito no esté vacío
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }
        
        // 3. Validar stock de cada producto
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException(
                    "Stock insuficiente para: " + product.getName()
                );
            }
            if (!product.getAvailable()) {
                throw new RuntimeException(
                    "Producto no disponible: " + product.getName()
                );
            }
        }
        
        // 4. Calcular total
        BigDecimal total = calculateTotal(cart);
        
        // 5. Crear orden
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderNumber(generateOrderNumber());
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.getAddress());
        
        // 6. Crear items de orden
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            order.getItems().add(orderItem);
        }
        
        // 7. Reducir stock de productos
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
        
        // 8. Guardar orden
        Order saved = orderRepository.save(order);
        
        // 9. Agregar puntos de lealtad
        loyaltyService.addPoints(cart.getUser().getId(), total);
        
        // 10. Limpiar carrito
        cart.getItems().clear();
        cartRepository.save(cart);
        
        return convertToDTO(saved);
    }
    
    private BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
            .map(item -> item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}
```

---

## 🔄 ¿Por Qué Separar del Controller?

### ❌ MAL: Lógica en el Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // ❌ Validación en Controller
        if (productRepository.existsByName(product.getName())) {
            return ResponseEntity.badRequest().build();
        }
        
        // ❌ Lógica de negocio en Controller
        if (product.getPrice().compareTo(BigDecimal.valueOf(1000)) < 0) {
            return ResponseEntity.badRequest().build();
        }
        
        // ❌ Acceso directo a Repository
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }
}
```

**Problemas:**
- ❌ Controller hace demasiado
- ❌ No se puede reutilizar la lógica
- ❌ Difícil de testear
- ❌ Difícil de mantener

### ✅ BIEN: Lógica en el Service

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public ProductDTO createProduct(ProductDTO productDTO) {
        // ✅ Validaciones en Service
        validateProduct(productDTO);
        
        // ✅ Lógica de negocio en Service
        Product product = convertToEntity(productDTO);
        Product saved = productRepository.save(product);
        
        return convertToDTO(saved);
    }
    
    private void validateProduct(ProductDTO productDTO) {
        if (productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("Producto ya existe");
        }
        
        if (productDTO.getPrice().compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new RuntimeException("Precio mínimo es $1,000");
        }
    }
}
```

**Ventajas:**
- ✅ Controller limpio y simple
- ✅ Lógica reutilizable
- ✅ Fácil de testear
- ✅ Fácil de mantener

---

## 🔄 @Transactional

### ¿Qué es una Transacción?

Una **transacción** es un conjunto de operaciones que se ejecutan **todas o ninguna**.

### Analogía

Es como pagar con tarjeta en una tienda:
1. Verificar saldo
2. Descontar dinero
3. Registrar compra
4. Imprimir recibo

Si **algo falla** (ej. sin saldo), **TODO se cancela**. No se queda a medias.

### Sin @Transactional

```java
@Service
public class OrderService {
    
    public void createOrder(CreateOrderRequest request) {
        // 1. Crear orden
        Order order = new Order();
        orderRepository.save(order);  // ✅ Se guardó
        
        // 2. Reducir stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);  // ✅ Se guardó
        
        // 3. Error inesperado
        throw new RuntimeException("Error al procesar pago");  // ❌ ERROR
        
        // PROBLEMA: Orden y stock ya se guardaron, pero el pago falló
        // Quedó inconsistente ❌
    }
}
```

### Con @Transactional

```java
@Service
public class OrderService {
    
    @Transactional
    public void createOrder(CreateOrderRequest request) {
        // 1. Crear orden
        Order order = new Order();
        orderRepository.save(order);
        
        // 2. Reducir stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        
        // 3. Error inesperado
        throw new RuntimeException("Error al procesar pago");
        
        // ✅ ROLLBACK automático: orden y stock NO se guardaron
        // Base de datos queda consistente ✅
    }
}
```

### Propagación

```java
@Service
public class OrderService {
    
    @Transactional
    public void createOrder() {
        // Inicia transacción
        orderRepository.save(order);
        
        // Llama a otro método con @Transactional
        loyaltyService.addPoints(userId, points);  // Usa la misma transacción
        
        // Si addPoints falla, TODO se revierte
    }
}

@Service
public class LoyaltyService {
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addPoints(Long userId, BigDecimal amount) {
        // Crea una transacción NUEVA
        // Si esto falla, NO afecta a createOrder
    }
}
```

### ReadOnly

```java
@Service
public class ProductService {
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        // readOnly = true → Optimización para lecturas
        return productRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}
```

---

## 🎯 Ejemplo Completo: ProductService

```java
package com.babycash.service;

import com.babycash.dto.ProductDTO;
import com.babycash.exception.ResourceNotFoundException;
import com.babycash.model.Product;
import com.babycash.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // ========================================
    // CONSULTAS (sin modificación)
    // ========================================
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findByAvailableTrue()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return convertToDTO(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = productRepository.findAll();
        
        // Filtros dinámicos
        return products.stream()
            .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
            .filter(p -> minPrice == null || p.getPrice().compareTo(minPrice) >= 0)
            .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsPaginated(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findAll(pageable)
            .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockLessThan(threshold)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // ========================================
    // MODIFICACIONES (con transacción)
    // ========================================
    
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Validaciones de negocio
        validateProductName(productDTO.getName());
        validatePrice(productDTO.getPrice());
        validateStock(productDTO.getStock());
        
        // Conversión y guardado
        Product product = convertToEntity(productDTO);
        product.setAvailable(true);
        Product saved = productRepository.save(product);
        
        return convertToDTO(saved);
    }
    
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        // Validar si cambió el nombre
        if (!product.getName().equals(productDTO.getName())) {
            validateProductName(productDTO.getName());
        }
        
        validatePrice(productDTO.getPrice());
        validateStock(productDTO.getStock());
        
        // Actualizar campos
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setAvailable(productDTO.getAvailable());
        
        Product updated = productRepository.save(product);
        return convertToDTO(updated);
    }
    
    @Transactional
    public ProductDTO updateStock(Long id, Integer stock) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        validateStock(stock);
        
        product.setStock(stock);
        
        // Marcar como no disponible si stock = 0
        if (stock == 0) {
            product.setAvailable(false);
        }
        
        Product updated = productRepository.save(product);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        productRepository.deleteById(id);
    }
    
    @Transactional
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente: " + product.getName());
        }
        
        product.setStock(product.getStock() - quantity);
        
        // Marcar como no disponible si stock = 0
        if (product.getStock() == 0) {
            product.setAvailable(false);
        }
        
        productRepository.save(product);
    }
    
    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        product.setStock(product.getStock() + quantity);
        
        // Marcar como disponible si había stock 0
        if (!product.getAvailable() && product.getStock() > 0) {
            product.setAvailable(true);
        }
        
        productRepository.save(product);
    }
    
    // ========================================
    // VALIDACIONES PRIVADAS
    // ========================================
    
    private void validateProductName(String name) {
        if (productRepository.existsByName(name)) {
            throw new RuntimeException("Ya existe un producto con ese nombre");
        }
    }
    
    private void validatePrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new RuntimeException("El precio mínimo es $1,000");
        }
    }
    
    private void validateStock(Integer stock) {
        if (stock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
    }
    
    // ========================================
    // CONVERSIONES
    // ========================================
    
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        dto.setAvailable(product.getAvailable());
        return dto;
    }
    
    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : true);
        return product;
    }
}
```

---

## 📋 Resumen

| Concepto | Descripción | Ejemplo |
|----------|-------------|---------|
| **@Service** | Marca clase como Service | `@Service` |
| **Lógica de negocio** | Reglas específicas de tu app | Validaciones, cálculos |
| **@Transactional** | Todo o nada (rollback) | Crear orden + reducir stock |
| **readOnly** | Optimización para lecturas | `@Transactional(readOnly = true)` |
| **Validaciones** | Verificar reglas de negocio | `if (stock < 0) throw...` |
| **Orquestación** | Coordinar múltiples repos | Order + Product + Cart |

---

**Última actualización**: Octubre 2025
