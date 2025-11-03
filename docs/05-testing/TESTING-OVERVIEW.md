# TESTING - VISIÓN GENERAL

## 🎯 Estrategia de Testing en Baby Cash

Baby Cash implementa una estrategia completa de testing que garantiza calidad, confiabilidad y mantenibilidad del código.

---

## 🏗️ Pirámide de Testing

```
        /\
       /  \      E2E Tests (10%)
      / 🌐 \     Cypress/Playwright
     /------\    
    /   🔗   \   Integration Tests (20%)
   /  Tests  \  Spring Boot Test, React Testing Library
  /___________\ 
 / 🧪 Unit    / Unit Tests (70%)
/   Tests    /  JUnit, Vitest, Jest
/___________/
```

### Distribución

| Tipo | Porcentaje | Cantidad | Tiempo Ejecución |
|------|------------|----------|------------------|
| **Unit** | 70% | ~200 tests | < 10 segundos |
| **Integration** | 20% | ~50 tests | 30-60 segundos |
| **E2E** | 10% | ~15 tests | 2-5 minutos |

---

## 🧪 Unit Tests (70%)

**Objetivo:** Testear funciones/componentes aislados

### Backend (JUnit)
- Services (lógica de negocio)
- Repositories (acceso a datos)
- Utils (funciones auxiliares)
- Validadores

### Frontend (Vitest)
- Componentes UI
- Hooks personalizados
- Utils y helpers
- Contexts (aislados)

**Características:**
- ✅ Rápidos (milisegundos)
- ✅ Muchos tests
- ✅ Fáciles de escribir
- ✅ Fáciles de mantener

---

## 🔗 Integration Tests (20%)

**Objetivo:** Testear interacción entre componentes

### Backend
- Controllers + Services + Repositories
- API endpoints completos
- Seguridad y autenticación
- Base de datos (H2 in-memory o TestContainers)

### Frontend
- Flujos completos de usuario
- Contexts + Components
- API calls (mocked con MSW)
- Routing

**Características:**
- ✅ Medianos (segundos)
- ✅ Menos tests
- ✅ Más confianza

---

## 🌐 E2E Tests (10%)

**Objetivo:** Testear flujo completo como usuario real

### Herramientas
- Playwright (recomendado)
- Cypress (alternativa)

### Flujos Críticos
1. **Registro y Login**
   - Usuario se registra
   - Usuario hace login
   - Usuario ve perfil

2. **Compra Completa**
   - Buscar producto
   - Agregar al carrito
   - Checkout
   - Confirmar orden

3. **Admin Panel**
   - Login como admin
   - Crear producto
   - Editar producto
   - Ver estadísticas

**Características:**
- ✅ Lentos (minutos)
- ✅ Pocos tests (solo críticos)
- ✅ Máxima confianza

---

## 📁 Estructura de Tests

### Backend
```
backend/
├── src/
│   ├── main/java/com/babycash/
│   │   ├── controller/
│   │   ├── service/
│   │   └── repository/
│   └── test/java/com/babycash/
│       ├── controller/      # Controller tests
│       │   ├── ProductControllerTest.java
│       │   └── OrderControllerTest.java
│       ├── service/         # Service tests
│       │   ├── ProductServiceTest.java
│       │   └── OrderServiceTest.java
│       ├── repository/      # Repository tests
│       │   └── ProductRepositoryTest.java
│       └── integration/     # Integration tests
│           └── ProductIntegrationTest.java
```

### Frontend
```
frontend/
├── src/
│   ├── components/
│   │   ├── ProductCard.tsx
│   │   └── ProductCard.test.tsx
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   └── useAuth.test.ts
│   ├── contexts/
│   │   ├── AuthContext.tsx
│   │   └── AuthContext.test.tsx
│   └── pages/
│       ├── Home.tsx
│       └── Home.test.tsx
└── e2e/
    ├── auth.spec.ts
    ├── checkout.spec.ts
    └── admin.spec.ts
```

---

## 🛠️ Herramientas de Testing

### Backend
| Herramienta | Uso |
|-------------|-----|
| **JUnit 5** | Framework de testing |
| **Mockito** | Mocking |
| **MockMvc** | Test controllers |
| **@DataJpaTest** | Test repositories |
| **@WebMvcTest** | Test web layer |
| **H2** | Base de datos en memoria |
| **TestContainers** | PostgreSQL real para tests |
| **AssertJ** | Assertions fluidas |

### Frontend
| Herramienta | Uso |
|-------------|-----|
| **Vitest** | Test runner (Unit) |
| **React Testing Library** | Test componentes |
| **@testing-library/user-event** | Simular interacciones |
| **MSW** | Mock API calls |
| **Playwright** | E2E tests |
| **@testing-library/jest-dom** | Matchers adicionales |

---

## 📊 Coverage

### Objetivos de Coverage

| Métrica | Objetivo | Baby Cash |
|---------|----------|-----------|
| **Lines** | > 80% | 85% |
| **Branches** | > 75% | 78% |
| **Functions** | > 80% | 82% |
| **Statements** | > 80% | 84% |

### Generar Reportes

**Backend:**
```bash
./mvnw test jacoco:report
open target/site/jacoco/index.html
```

**Frontend:**
```bash
npm run test:coverage
open coverage/index.html
```

---

## 🎯 Qué Testear

### ✅ Sí Testear

**Lógica de Negocio:**
- Calcular total del carrito
- Validar stock disponible
- Aplicar descuentos
- Procesar pedidos

**Funciones Críticas:**
- Autenticación
- Autorización
- Pagos
- Envío de emails

**Edge Cases:**
- Valores límite
- Null/undefined
- Errores de red
- Estados inválidos

### ❌ No Testear

**Código Trivial:**
- Getters/setters
- Constructores simples
- Código generado

**Librerías Externas:**
- React (ya testeado)
- Spring Boot (ya testeado)
- PostgreSQL (ya testeado)

**UI Styling:**
- Colores
- Tamaños
- Posiciones
- (Usar screenshot tests si es crítico)

---

## 🚀 Best Practices

### 1. **Arrange, Act, Assert (AAA)**
```typescript
test('calcula total del carrito', () => {
  // Arrange
  const items = [
    { price: 10000, quantity: 2 },
    { price: 15000, quantity: 1 }
  ];
  
  // Act
  const total = calculateTotal(items);
  
  // Assert
  expect(total).toBe(35000);
});
```

### 2. **Descriptive Test Names**
```typescript
// ❌ Malo
test('test1', () => { ... });

// ✅ Bueno
test('adds product to cart and updates total', () => { ... });
```

### 3. **Test One Thing**
```typescript
// ❌ Malo - testea 3 cosas
test('cart functionality', () => {
  expect(addToCart()).toBeTruthy();
  expect(removeFromCart()).toBeTruthy();
  expect(calculateTotal()).toBe(1000);
});

// ✅ Bueno - un test por funcionalidad
test('adds product to cart', () => { ... });
test('removes product from cart', () => { ... });
test('calculates cart total', () => { ... });
```

### 4. **Mock Dependencies**
```typescript
// ✅ Mock API calls
server.use(
  http.get('/api/products', () => {
    return HttpResponse.json([{ id: 1, name: 'Test' }]);
  })
);
```

### 5. **Clean Up**
```typescript
afterEach(() => {
  cleanup(); // Limpiar DOM
  localStorage.clear(); // Limpiar storage
});
```

---

## 📝 Naming Conventions

### Backend (Java)
```java
// Clase de tests
ProductServiceTest.java

// Métodos de test
@Test
void findById_WhenExists_ShouldReturnProduct() { }

@Test
void findById_WhenNotExists_ShouldReturnEmpty() { }

@Test
void create_WithValidData_ShouldSaveProduct() { }
```

### Frontend (TypeScript)
```typescript
// Archivo de tests
ProductCard.test.tsx

// Tests
describe('ProductCard', () => {
  it('renders product name', () => { });
  
  it('calls onClick when button clicked', () => { });
  
  it('shows out of stock message when stock is 0', () => { });
});
```

---

## 🎓 Para la Evaluación del SENA

**1. "¿Cuál es la pirámide de testing?"**

> "70% Unit (rápidos, aislados), 20% Integration (interacción entre componentes), 10% E2E (flujo completo usuario). Más tests en la base = más rápido feedback."

**2. "¿Qué herramientas usa Baby Cash?"**

> "Backend: JUnit, Mockito, MockMvc, TestContainers. Frontend: Vitest, React Testing Library, MSW, Playwright. Coverage: Jacoco (backend), Vitest (frontend)."

**3. "¿Qué es coverage?"**

> "Porcentaje de código cubierto por tests. Baby Cash: >80% lines, >75% branches. Se genera con `./mvnw test jacoco:report` (backend) y `npm run test:coverage` (frontend)."

**4. "¿Cuándo usar cada tipo de test?"**

> "Unit: funciones puras, lógica negocio. Integration: controllers+services+DB, contexts+components. E2E: flujos críticos (login, checkout, admin)."

---

## 📖 Siguiente

Explora cada tipo de test en detalle:

- **[Backend Tests](BACKEND-TESTS.md)** - JUnit, Mockito, Spring Boot Test
- **[Frontend Tests](FRONTEND-TESTS.md)** - Vitest, React Testing Library
- **[E2E Tests](E2E-TESTS.md)** - Playwright, Cypress
- **[Mocking Strategies](MOCKING-STRATEGIES.md)** - MSW, Mockito

---

**¡Testing = Confianza en tu código!** 🚀
