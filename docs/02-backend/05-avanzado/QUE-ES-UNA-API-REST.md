# 🌐 ¿QUÉ ES UNA API REST?

## 🎯 Definición Simple

**API** = **A**pplication **P**rogramming **I**nterface (Interfaz de Programación de Aplicaciones)

Es un **puente** que permite que dos aplicaciones se comuniquen entre sí.

### Analogía

Es como el **menú de un restaurante**:
- Frontend (cliente) ve el menú
- Backend (cocina) prepara los platos
- API es el menú que define qué puedes pedir y cómo

---

## 🔤 ¿Qué es REST?

**REST** = **RE**presentational **S**tate **T**ransfer

Es un **estilo de arquitectura** para diseñar APIs.

### Principios REST

1. **Cliente-Servidor**: Frontend y Backend separados
2. **Sin estado (Stateless)**: Cada petición es independiente
3. **Cacheable**: Respuestas pueden ser guardadas en caché
4. **Interfaz uniforme**: URLs consistentes
5. **Sistema en capas**: Arquitectura escalable

---

## 🏗️ Cliente-Servidor

### Separación de Responsabilidades

```
┌─────────────────────────────────────┐
│         CLIENTE (Frontend)          │
│                                     │
│  - React                            │
│  - HTML/CSS/JavaScript              │
│  - Interfaz de usuario              │
│  - Validación de formularios        │
│  - Navegación                       │
│                                     │
└──────────────┬──────────────────────┘
               │
               │ HTTP Request (JSON)
               │ GET /api/products
               │
┌──────────────▼──────────────────────┐
│         SERVIDOR (Backend)          │
│                                     │
│  - Spring Boot (Java)               │
│  - Lógica de negocio                │
│  - Validaciones                     │
│  - Acceso a base de datos           │
│  - Autenticación y autorización     │
│                                     │
└──────────────┬──────────────────────┘
               │
               │ HTTP Response (JSON)
               │ { "products": [...] }
               │
┌──────────────▼──────────────────────┐
│       BASE DE DATOS (PostgreSQL)    │
│                                     │
│  - Almacenamiento de datos          │
│  - Tablas                           │
│  - Relaciones                       │
│                                     │
└─────────────────────────────────────┘
```

### Ventajas de la Separación

1. ✅ **Independencia**: Frontend y Backend pueden desarrollarse por separado
2. ✅ **Reutilización**: El mismo Backend puede servir múltiples frontends (web, móvil, desktop)
3. ✅ **Escalabilidad**: Puedes escalar Frontend y Backend independientemente
4. ✅ **Mantenimiento**: Cambios en uno no afectan al otro

### Ejemplo en BabyCash

```javascript
// FRONTEND (React)
const ProductList = () => {
    const [products, setProducts] = useState([]);
    
    useEffect(() => {
        // Petición HTTP al Backend
        axios.get('http://localhost:8080/api/products')
            .then(response => {
                setProducts(response.data);
            });
    }, []);
    
    return (
        <div>
            {products.map(product => (
                <div key={product.id}>
                    <h3>{product.name}</h3>
                    <p>${product.price}</p>
                </div>
            ))}
        </div>
    );
};
```

```java
// BACKEND (Spring Boot)
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}
```

---

## 📄 JSON como Formato

### ¿Qué es JSON?

**JSON** = **J**ava**S**cript **O**bject **N**otation

Es un formato de texto para **intercambiar datos** entre aplicaciones.

### Estructura JSON

```json
{
  "id": 1,
  "name": "Pañales Huggies",
  "price": 45000,
  "stock": 50,
  "available": true,
  "tags": ["bebé", "higiene"]
}
```

### Tipos de Datos JSON

| Tipo | Ejemplo | Descripción |
|------|---------|-------------|
| **String** | `"Hola"` | Texto entre comillas |
| **Number** | `45000`, `3.14` | Números enteros o decimales |
| **Boolean** | `true`, `false` | Verdadero o falso |
| **Array** | `[1, 2, 3]` | Lista de valores |
| **Object** | `{"key": "value"}` | Objeto con pares clave-valor |
| **null** | `null` | Valor nulo |

### JSON en REST

```
Frontend                  Backend
   │                         │
   │  GET /api/products      │
   │────────────────────────>│
   │                         │
   │   200 OK + JSON         │
   │<────────────────────────│
   │                         │
   │  {                      │
   │    "id": 1,             │
   │    "name": "Pañales",   │
   │    "price": 45000       │
   │  }                      │
```

---

## 🔗 Recursos y URLs

### Recurso

Un **recurso** es una entidad del sistema (productos, usuarios, órdenes, etc.).

### URL (Uniform Resource Locator)

La **URL** identifica un recurso específico.

```
https://api.babycash.com/api/products/1
│      │                  │         │  │
│      │                  │         │  └─ ID del recurso
│      │                  │         └──── Tipo de recurso
│      │                  └────────────── Path base del API
│      └───────────────────────────────── Dominio
└──────────────────────────────────────── Protocolo
```

### Convenciones REST

```
Recurso: products

GET     /api/products         → Obtener todos los productos
GET     /api/products/1       → Obtener producto con ID 1
POST    /api/products         → Crear nuevo producto
PUT     /api/products/1       → Actualizar producto con ID 1
DELETE  /api/products/1       → Eliminar producto con ID 1

GET     /api/products?name=pañal  → Buscar productos por nombre
GET     /api/products?page=0&size=10  → Obtener con paginación
```

### ✅ Buenas Prácticas

```
✅ BIEN:
GET /api/products
GET /api/users/5/orders
POST /api/auth/register
PUT /api/products/1

❌ MAL:
GET /api/getProducts
GET /api/user_details
POST /api/createNewProduct
GET /api/deleteUser/5
```

**Reglas:**
- Usa **sustantivos** (products, users), no verbos (getProducts)
- Usa **plural** (products, users)
- Usa **minúsculas** y **guiones** si es necesario (product-categories)
- Usa **jerarquías** para relaciones (users/5/orders)

---

## 🔄 Sin Estado (Stateless)

### ¿Qué significa?

Cada petición HTTP es **independiente**. El servidor NO guarda información entre peticiones.

### Ejemplo

```
❌ CON ESTADO (Stateful):

Petición 1: POST /login { email, password }
Respuesta:  Usuario autenticado (servidor guarda sesión en memoria)

Petición 2: GET /api/products
Respuesta:  Productos (servidor sabe que estás autenticado)

PROBLEMA: Si el servidor se reinicia, pierdes la sesión ❌
```

```
✅ SIN ESTADO (Stateless):

Petición 1: POST /login { email, password }
Respuesta:  { "token": "eyJhbGc..." } (token JWT)

Petición 2: GET /api/products
Headers:    Authorization: Bearer eyJhbGc...
Respuesta:  Productos (el token demuestra que estás autenticado)

VENTAJA: Si el servidor se reinicia, sigues autenticado ✅
```

---

## 📊 Ejemplo Completo: BabyCash API

### Request (Frontend)

```javascript
// GET - Obtener todos los productos
axios.get('http://localhost:8080/api/products')

// GET - Obtener producto específico
axios.get('http://localhost:8080/api/products/1')

// POST - Crear producto
axios.post('http://localhost:8080/api/products', {
    name: 'Pañales Huggies',
    price: 45000,
    stock: 50
})

// PUT - Actualizar producto
axios.put('http://localhost:8080/api/products/1', {
    name: 'Pañales Huggies Supreme',
    price: 50000,
    stock: 40
})

// DELETE - Eliminar producto
axios.delete('http://localhost:8080/api/products/1')
```

### Response (Backend)

```json
// GET /api/products (200 OK)
[
  {
    "id": 1,
    "name": "Pañales Huggies",
    "price": 45000,
    "stock": 50,
    "available": true
  },
  {
    "id": 2,
    "name": "Leche NAN",
    "price": 15000,
    "stock": 100,
    "available": true
  }
]

// GET /api/products/1 (200 OK)
{
  "id": 1,
  "name": "Pañales Huggies",
  "description": "Pañales para bebés recién nacidos",
  "price": 45000,
  "stock": 50,
  "imageUrl": "https://...",
  "available": true
}

// POST /api/products (201 Created)
{
  "id": 3,
  "name": "Pañales Huggies",
  "price": 45000,
  "stock": 50,
  "available": true
}

// DELETE /api/products/1 (204 No Content)
// Sin cuerpo de respuesta
```

---

## 🎯 REST vs Otras Arquitecturas

### REST vs SOAP

| Característica | REST | SOAP |
|----------------|------|------|
| **Formato** | JSON (principalmente) | XML |
| **Complejidad** | Simple | Complejo |
| **Performance** | Rápido | Más lento |
| **Uso** | APIs web modernas | Sistemas empresariales legacy |

### REST vs GraphQL

| Característica | REST | GraphQL |
|----------------|------|----------|
| **Endpoints** | Múltiples (`/products`, `/users`) | Uno solo (`/graphql`) |
| **Datos** | Fijos (devuelve todo) | Flexibles (pides lo que necesitas) |
| **Complejidad** | Simple | Más complejo |
| **Uso** | APIs estándar | APIs complejas con muchos datos |

---

## 🔧 Herramientas para Probar APIs

### Postman

```
GET http://localhost:8080/api/products

Headers:
  Authorization: Bearer eyJhbGc...

Response:
  Status: 200 OK
  Body:
    [
      { "id": 1, "name": "Pañales", "price": 45000 }
    ]
```

### cURL (Terminal)

```bash
# GET
curl http://localhost:8080/api/products

# POST
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Pañales","price":45000,"stock":50}'

# Con autenticación
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer eyJhbGc..."
```

### Thunder Client (VS Code Extension)

```
GET http://localhost:8080/api/products
Authorization: Bearer {{token}}
```

---

## 📋 Ventajas de REST

1. ✅ **Simple y fácil de entender**
2. ✅ **Usa HTTP estándar** (no necesita protocolos adicionales)
3. ✅ **Stateless** (escalabilidad)
4. ✅ **JSON** (formato ligero y legible)
5. ✅ **Caché** (respuestas pueden ser cacheadas)
6. ✅ **Multiplataforma** (web, móvil, IoT)

---

## 📋 Resumen

| Concepto | Definición |
|----------|------------|
| **API** | Interfaz para comunicación entre aplicaciones |
| **REST** | Estilo de arquitectura para APIs |
| **Cliente-Servidor** | Frontend y Backend separados |
| **JSON** | Formato de datos (texto) |
| **Recurso** | Entidad del sistema (productos, usuarios) |
| **URL** | Dirección del recurso |
| **Stateless** | Sin estado entre peticiones |
| **HTTP** | Protocolo de comunicación |

### Flujo Completo

```
1. Frontend (React) hace petición HTTP
   ↓
   GET http://localhost:8080/api/products

2. Backend (Spring Boot) recibe petición
   ↓
   @GetMapping("/api/products")

3. Service procesa lógica de negocio
   ↓
   productService.getAllProducts()

4. Repository consulta base de datos
   ↓
   SELECT * FROM products

5. Backend convierte Entity → DTO
   ↓
   productMapper.toDTOList(products)

6. Backend devuelve JSON
   ↓
   200 OK
   [{"id":1,"name":"Pañales","price":45000}]

7. Frontend recibe y renderiza
   ↓
   setProducts(response.data)
```

---

**Última actualización**: Octubre 2025
