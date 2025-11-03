# 🔄 MAPPERS - CONVERSIÓN ENTITY ↔ DTO

## 🎯 ¿Qué es un Mapper?

Un **Mapper** es una clase que convierte objetos de un tipo a otro:
- **Entity → DTO** (enviar al frontend)
- **DTO → Entity** (recibir del frontend)

### Analogía

Es como un **traductor**:
- Entity habla "lenguaje de base de datos"
- DTO habla "lenguaje de API"
- Mapper traduce entre ambos

---

## 🔄 Tipos de Mappers

### 1. Manual (Propio)

```java
@Component
public class ProductMapper {
    
    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        return dto;
    }
    
    public Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return product;
    }
}
```

**Ventajas:**
- ✅ Control total
- ✅ Fácil de entender
- ✅ Sin dependencias externas

**Desventajas:**
- ❌ Mucho código repetitivo
- ❌ Propenso a errores

---

### 2. MapStruct (Librería)

```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductDTO toDTO(Product product);
    
    Product toEntity(ProductDTO dto);
    
    List<ProductDTO> toDTOList(List<Product> products);
}
```

**Ventajas:**
- ✅ Genera código automáticamente
- ✅ Menos errores
- ✅ Performance (compilación)

**Desventajas:**
- ❌ Dependencia externa
- ❌ Curva de aprendizaje

---

## 🛠️ Mapper Manual

### Ejemplo Básico: ProductMapper

```java
package com.babycash.mapper;

import com.babycash.dto.ProductDTO;
import com.babycash.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    
    // Entity → DTO
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        
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
    
    // DTO → Entity
    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Product product = new Product();
        // ⚠️ NO setear el ID al crear
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setAvailable(dto.getAvailable());
        
        return product;
    }
    
    // Actualizar Entity existente con DTO
    public void updateEntity(Product product, ProductDTO dto) {
        if (product == null || dto == null) {
            return;
        }
        
        // ⚠️ NO actualizar el ID
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImageUrl(dto.getImageUrl());
        product.setAvailable(dto.getAvailable());
    }
    
    // Lista Entity → Lista DTO
    public List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) {
            return null;
        }
        
        return products.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
```

### Uso en Service

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductMapper productMapper;  // Inyectar mapper
    
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        return productMapper.toDTO(product);  // Entity → DTO
    }
    
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toDTOList(products);  // Lista Entity → Lista DTO
    }
    
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);  // DTO → Entity
        Product saved = productRepository.save(product);
        return productMapper.toDTO(saved);  // Entity → DTO
    }
    
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        productMapper.updateEntity(product, productDTO);  // Actualizar existente
        Product updated = productRepository.save(product);
        return productMapper.toDTO(updated);
    }
}
```

---

## 🎯 Ejemplo Completo: UserMapper

```java
package com.babycash.mapper;

import com.babycash.dto.user.RegisterUserDTO;
import com.babycash.dto.user.UserDTO;
import com.babycash.dto.user.UserSummaryDTO;
import com.babycash.model.Role;
import com.babycash.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Entity → DTO (completo)
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        // ✅ NO incluir password
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole().name());
        dto.setActive(user.getActive());
        dto.setCreatedAt(user.getCreatedAt());
        
        return dto;
    }
    
    // Entity → DTO (resumen)
    public UserSummaryDTO toSummaryDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        
        return dto;
    }
    
    // RegisterDTO → Entity
    public User toEntity(RegisterUserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));  // ✅ Encriptar
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(Role.USER);  // Por defecto USER
        user.setActive(true);
        
        return user;
    }
    
    // Actualizar Entity con DTO
    public void updateEntity(User user, UserDTO dto) {
        if (user == null || dto == null) {
            return;
        }
        
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        // ⚠️ NO actualizar email, password, role, active aquí
    }
    
    // Lista Entity → Lista DTO
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public List<UserSummaryDTO> toSummaryDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
            .map(this::toSummaryDTO)
            .collect(Collectors.toList());
    }
}
```

---

## 🎯 Ejemplo Completo: OrderMapper

```java
package com.babycash.mapper;

import com.babycash.dto.order.OrderDetailDTO;
import com.babycash.dto.order.OrderItemDTO;
import com.babycash.dto.order.OrderSummaryDTO;
import com.babycash.model.Order;
import com.babycash.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    @Autowired
    private UserMapper userMapper;
    
    // Entity → DTO (resumen para lista)
    public OrderSummaryDTO toSummaryDTO(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItemCount(order.getItems() != null ? order.getItems().size() : 0);
        
        return dto;
    }
    
    // Entity → DTO (detalle completo)
    public OrderDetailDTO toDetailDTO(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus().name());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        
        // Usuario (resumen)
        dto.setUser(userMapper.toSummaryDTO(order.getUser()));
        
        // Items
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    // OrderItem Entity → OrderItemDTO
    public OrderItemDTO toOrderItemDTO(OrderItem item) {
        if (item == null) {
            return null;
        }
        
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        
        return dto;
    }
    
    // Lista Entity → Lista DTO (resumen)
    public List<OrderSummaryDTO> toSummaryDTOList(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        
        return orders.stream()
            .map(this::toSummaryDTO)
            .collect(Collectors.toList());
    }
}
```

---

## 🎯 Mapper con Relaciones Complejas

### CartMapper

```java
package com.babycash.mapper;

import com.babycash.dto.cart.CartDTO;
import com.babycash.dto.cart.CartItemDTO;
import com.babycash.model.Cart;
import com.babycash.model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    
    public CartDTO toDTO(Cart cart) {
        if (cart == null) {
            return null;
        }
        
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        
        // Convertir items
        if (cart.getItems() != null) {
            dto.setItems(cart.getItems().stream()
                .map(this::toCartItemDTO)
                .collect(Collectors.toList()));
        }
        
        // Calcular total
        dto.setTotal(calculateTotal(cart));
        
        return dto;
    }
    
    public CartItemDTO toCartItemDTO(CartItem item) {
        if (item == null) {
            return null;
        }
        
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductPrice(item.getProduct().getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getProduct().getPrice()
            .multiply(BigDecimal.valueOf(item.getQuantity())));
        
        return dto;
    }
    
    private BigDecimal calculateTotal(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return cart.getItems().stream()
            .map(item -> item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

---

## 📦 MapStruct (Alternativa)

### Configuración (pom.xml)

```xml
<dependencies>
    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>1.5.5.Final</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Mapper con MapStruct

```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    // Entity → DTO (nombres iguales se mapean automáticamente)
    ProductDTO toDTO(Product product);
    
    // DTO → Entity
    @Mapping(target = "id", ignore = true)  // No mapear ID
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductDTO dto);
    
    // Lista
    List<ProductDTO> toDTOList(List<Product> products);
    
    // Actualizar Entity existente
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(ProductDTO dto, @MappingTarget Product product);
}
```

### Mapper Complejo con MapStruct

```java
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface OrderMapper {
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "status", source = "status")
    OrderSummaryDTO toSummaryDTO(Order order);
    
    @Mapping(target = "user", source = "user")
    @Mapping(target = "items", source = "items")
    OrderDetailDTO toDetailDTO(Order order);
    
    List<OrderSummaryDTO> toSummaryDTOList(List<Order> orders);
}
```

---

## 🎯 BabyCash Mappers (Estructura)

### Carpeta de Mappers

```
src/main/java/com/babycash/
├── mapper/
│   ├── ProductMapper.java
│   ├── UserMapper.java
│   ├── OrderMapper.java
│   ├── CartMapper.java
│   ├── BlogPostMapper.java
│   └── TestimonialMapper.java
```

### Uso en Services

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductMapper productMapper;  // ✅ Inyectar mapper
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findByAvailableTrue();
        return productMapper.toDTOList(products);  // ✅ Usar mapper
    }
    
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return productMapper.toDTO(product);  // ✅ Usar mapper
    }
    
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);  // ✅ Usar mapper
        Product saved = productRepository.save(product);
        return productMapper.toDTO(saved);  // ✅ Usar mapper
    }
    
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        productMapper.updateEntity(product, productDTO);  // ✅ Usar mapper
        Product updated = productRepository.save(product);
        return productMapper.toDTO(updated);  // ✅ Usar mapper
    }
}
```

---

## ✅ Buenas Prácticas

### 1. Validar Null

```java
public ProductDTO toDTO(Product product) {
    if (product == null) {
        return null;  // ✅ Evitar NullPointerException
    }
    
    ProductDTO dto = new ProductDTO();
    // ...
    return dto;
}
```

### 2. No Mapear IDs al Crear

```java
public Product toEntity(ProductDTO dto) {
    Product product = new Product();
    // ❌ NO hacer: product.setId(dto.getId());
    product.setName(dto.getName());
    // ...
    return product;
}
```

### 3. Encriptar Passwords

```java
@Component
public class UserMapper {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User toEntity(RegisterUserDTO dto) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));  // ✅ Encriptar
        // ...
        return user;
    }
}
```

### 4. Separar Métodos

```java
@Component
public class OrderMapper {
    
    // Para listas (resumen)
    public OrderSummaryDTO toSummaryDTO(Order order) { }
    
    // Para detalle (completo)
    public OrderDetailDTO toDetailDTO(Order order) { }
}
```

### 5. Reutilizar Mappers

```java
@Component
public class OrderMapper {
    
    @Autowired
    private UserMapper userMapper;  // ✅ Reutilizar
    
    public OrderDetailDTO toDetailDTO(Order order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setUser(userMapper.toSummaryDTO(order.getUser()));  // ✅
        // ...
        return dto;
    }
}
```

---

## 📋 Resumen

| Tipo | Manual | MapStruct |
|------|--------|-----------|
| **Setup** | Fácil | Requiere configuración |
| **Código** | Más código | Menos código |
| **Control** | Total | Limitado |
| **Performance** | Bueno | Excelente |
| **Errores** | Más propenso | Menos propenso |
| **Aprendizaje** | Fácil | Curva de aprendizaje |

### Cuándo Usar Cada Uno

**Manual:**
- ✅ Proyecto pequeño
- ✅ Conversiones simples
- ✅ Control total necesario
- ✅ Sin dependencias externas

**MapStruct:**
- ✅ Proyecto grande
- ✅ Muchos DTOs
- ✅ Performance crítica
- ✅ Equipo experimentado

---

**Última actualización**: Octubre 2025
