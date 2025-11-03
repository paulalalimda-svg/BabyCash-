# TESTING STRATEGIES - BABY CASH

## 🎯 ¿Por Qué Testear?

**Sin tests:**
- ❌ Cambios rompen código existente
- ❌ Bugs descubiertos en producción
- ❌ Refactoring peligroso
- ❌ Miedo a hacer cambios

**Con tests:**
- ✅ Cambios seguros (detecta regressions)
- ✅ Bugs descubiertos temprano
- ✅ Refactoring confiado
- ✅ Documentación viva del código

---

## 🏗️ Pirámide de Testing

```
        /\
       /  \  E2E Tests (Pocos, lentos, completos)
      /    \
     /------\
    / Unit  \ Integration Tests (Medianos)
   /  Tests  \
  /___________\ Unit Tests (Muchos, rápidos, específicos)
```

### 1️⃣ Unit Tests (70%)
- Testear funciones/componentes aislados
- Rápidos (milisegundos)
- Muchos tests

### 2️⃣ Integration Tests (20%)
- Testear interacción entre componentes
- Medianos (segundos)
- Menos tests

### 3️⃣ E2E Tests (10%)
- Testear flujo completo de usuario
- Lentos (minutos)
- Pocos tests críticos

---

## 🧪 Herramientas en Baby Cash

### Frontend
- **Vitest**: Test runner (más rápido que Jest)
- **React Testing Library**: Testear componentes
- **MSW**: Mock Service Worker (mock APIs)
- **Playwright/Cypress**: E2E tests

### Backend
- **JUnit 5**: Framework de testing
- **Mockito**: Mocking
- **Spring Boot Test**: Testing de Spring
- **TestContainers**: Base de datos real para tests

---

## 🎨 Unit Tests - Frontend

### Setup Vitest

```bash
npm install -D vitest @testing-library/react @testing-library/jest-dom @testing-library/user-event jsdom
```

```typescript
// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
  },
});
```

```typescript
// src/test/setup.ts
import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';

afterEach(() => {
  cleanup();
});
```

### Test de Componente Simple

```typescript
// src/components/Button.tsx
interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  variant?: 'primary' | 'secondary';
  disabled?: boolean;
}

export const Button = ({ children, onClick, variant = 'primary', disabled }: ButtonProps) => {
  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={`btn btn-${variant}`}
    >
      {children}
    </button>
  );
};
```

```typescript
// src/components/Button.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from './Button';

describe('Button', () => {
  it('renders children correctly', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });

  it('calls onClick when clicked', () => {
    const handleClick = vi.fn();
    render(<Button onClick={handleClick}>Click me</Button>);
    
    fireEvent.click(screen.getByText('Click me'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('does not call onClick when disabled', () => {
    const handleClick = vi.fn();
    render(<Button onClick={handleClick} disabled>Click me</Button>);
    
    fireEvent.click(screen.getByText('Click me'));
    expect(handleClick).not.toHaveBeenCalled();
  });

  it('applies correct variant class', () => {
    render(<Button variant="secondary">Button</Button>);
    const button = screen.getByText('Button');
    expect(button).toHaveClass('btn-secondary');
  });
});
```

### Test de Componente con Estado

```typescript
// src/components/Counter.tsx
import { useState } from 'react';

export const Counter = () => {
  const [count, setCount] = useState(0);

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
      <button onClick={() => setCount(count - 1)}>Decrement</button>
      <button onClick={() => setCount(0)}>Reset</button>
    </div>
  );
};
```

```typescript
// src/components/Counter.test.tsx
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Counter } from './Counter';

describe('Counter', () => {
  it('starts with count 0', () => {
    render(<Counter />);
    expect(screen.getByText('Count: 0')).toBeInTheDocument();
  });

  it('increments count when clicking increment', async () => {
    const user = userEvent.setup();
    render(<Counter />);
    
    await user.click(screen.getByText('Increment'));
    expect(screen.getByText('Count: 1')).toBeInTheDocument();
    
    await user.click(screen.getByText('Increment'));
    expect(screen.getByText('Count: 2')).toBeInTheDocument();
  });

  it('decrements count when clicking decrement', async () => {
    const user = userEvent.setup();
    render(<Counter />);
    
    await user.click(screen.getByText('Decrement'));
    expect(screen.getByText('Count: -1')).toBeInTheDocument();
  });

  it('resets count to 0', async () => {
    const user = userEvent.setup();
    render(<Counter />);
    
    await user.click(screen.getByText('Increment'));
    await user.click(screen.getByText('Increment'));
    expect(screen.getByText('Count: 2')).toBeInTheDocument();
    
    await user.click(screen.getByText('Reset'));
    expect(screen.getByText('Count: 0')).toBeInTheDocument();
  });
});
```

### Test con Context

```typescript
// src/contexts/AuthContext.test.tsx
import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AuthProvider, useAuth } from './AuthContext';

// Componente de prueba
const TestComponent = () => {
  const { user, login, logout, isAuthenticated } = useAuth();
  
  return (
    <div>
      <p>Status: {isAuthenticated ? 'Logged in' : 'Logged out'}</p>
      {user && <p>User: {user.firstName}</p>}
      <button onClick={() => login('test@test.com', 'password')}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
};

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('starts logged out', () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    expect(screen.getByText('Status: Logged out')).toBeInTheDocument();
  });

  it('logs in user', async () => {
    const user = userEvent.setup();
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    await user.click(screen.getByText('Login'));
    
    await waitFor(() => {
      expect(screen.getByText('Status: Logged in')).toBeInTheDocument();
    });
  });

  it('logs out user', async () => {
    const user = userEvent.setup();
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    );
    
    await user.click(screen.getByText('Login'));
    await waitFor(() => {
      expect(screen.getByText('Status: Logged in')).toBeInTheDocument();
    });
    
    await user.click(screen.getByText('Logout'));
    expect(screen.getByText('Status: Logged out')).toBeInTheDocument();
  });
});
```

---

## 🔌 Mocking API Calls

### MSW (Mock Service Worker)

```bash
npm install -D msw
```

```typescript
// src/test/mocks/handlers.ts
import { http, HttpResponse } from 'msw';

export const handlers = [
  // GET products
  http.get('/api/products', () => {
    return HttpResponse.json([
      { id: 1, name: 'Product 1', price: 50000 },
      { id: 2, name: 'Product 2', price: 75000 },
    ]);
  }),

  // POST login
  http.post('/api/auth/login', async ({ request }) => {
    const { email, password } = await request.json();
    
    if (email === 'test@test.com' && password === 'password') {
      return HttpResponse.json({
        token: 'fake-jwt-token',
        email: 'test@test.com',
        firstName: 'Test',
        lastName: 'User',
        role: 'USER',
      });
    }
    
    return HttpResponse.json(
      { message: 'Invalid credentials' },
      { status: 401 }
    );
  }),

  // GET cart
  http.get('/api/cart', () => {
    return HttpResponse.json({
      id: 1,
      items: [
        { productId: 1, quantity: 2, product: { name: 'Product 1', price: 50000 } },
      ],
      total: 100000,
    });
  }),
];
```

```typescript
// src/test/setup.ts
import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach, beforeAll, afterAll } from 'vitest';
import { setupServer } from 'msw/node';
import { handlers } from './mocks/handlers';

// Setup MSW server
const server = setupServer(...handlers);

beforeAll(() => server.listen());
afterEach(() => {
  cleanup();
  server.resetHandlers();
});
afterAll(() => server.close());
```

### Test con API Mock

```typescript
// src/components/ProductList.test.tsx
import { describe, it, expect } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { ProductList } from './ProductList';

describe('ProductList', () => {
  it('loads and displays products', async () => {
    render(<ProductList />);
    
    // Inicialmente muestra loading
    expect(screen.getByText('Loading...')).toBeInTheDocument();
    
    // Espera a que carguen los productos
    await waitFor(() => {
      expect(screen.getByText('Product 1')).toBeInTheDocument();
      expect(screen.getByText('Product 2')).toBeInTheDocument();
    });
  });

  it('handles API errors', async () => {
    // Override handler para simular error
    server.use(
      http.get('/api/products', () => {
        return HttpResponse.json(
          { message: 'Server error' },
          { status: 500 }
        );
      })
    );
    
    render(<ProductList />);
    
    await waitFor(() => {
      expect(screen.getByText(/error/i)).toBeInTheDocument();
    });
  });
});
```

---

## 🔄 Integration Tests

### Test de Flujo Completo

```typescript
// src/features/checkout/Checkout.test.tsx
import { describe, it, expect } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../../contexts/AuthContext';
import { CartProvider } from '../../contexts/CartContext';
import { Checkout } from './Checkout';

const AllProviders = ({ children }: { children: React.ReactNode }) => (
  <BrowserRouter>
    <AuthProvider>
      <CartProvider>
        {children}
      </CartProvider>
    </AuthProvider>
  </BrowserRouter>
);

describe('Checkout Flow', () => {
  it('completes full checkout process', async () => {
    const user = userEvent.setup();
    
    render(
      <AllProviders>
        <Checkout />
      </AllProviders>
    );
    
    // 1. Verificar items en carrito
    await waitFor(() => {
      expect(screen.getByText('Product 1')).toBeInTheDocument();
    });
    
    // 2. Llenar formulario de envío
    await user.type(screen.getByLabelText(/dirección/i), 'Calle 123');
    await user.type(screen.getByLabelText(/ciudad/i), 'Bogotá');
    await user.type(screen.getByLabelText(/teléfono/i), '3001234567');
    
    // 3. Seleccionar método de pago
    await user.click(screen.getByLabelText(/tarjeta de crédito/i));
    
    // 4. Confirmar orden
    await user.click(screen.getByText(/confirmar orden/i));
    
    // 5. Verificar confirmación
    await waitFor(() => {
      expect(screen.getByText(/orden confirmada/i)).toBeInTheDocument();
    });
  });
});
```

---

## 🌐 E2E Tests con Playwright

### Setup Playwright

```bash
npm install -D @playwright/test
npx playwright install
```

```typescript
// playwright.config.ts
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
  },
});
```

### E2E Test Example

```typescript
// e2e/checkout.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Checkout Flow', () => {
  test('user can complete purchase', async ({ page }) => {
    // 1. Ir a home
    await page.goto('/');
    await expect(page).toHaveTitle(/Baby Cash/);
    
    // 2. Agregar producto al carrito
    await page.click('text=Productos');
    await page.click('button:has-text("Agregar al Carrito")').first();
    await expect(page.locator('.cart-badge')).toHaveText('1');
    
    // 3. Ir al carrito
    await page.click('a:has-text("Carrito")');
    await expect(page.locator('h1')).toHaveText('Carrito de Compras');
    
    // 4. Proceder al checkout
    await page.click('button:has-text("Proceder al Checkout")');
    
    // 5. Login (si no está autenticado)
    if (await page.locator('text=Iniciar Sesión').isVisible()) {
      await page.fill('input[type="email"]', 'test@test.com');
      await page.fill('input[type="password"]', 'password');
      await page.click('button:has-text("Iniciar Sesión")');
    }
    
    // 6. Llenar información de envío
    await page.fill('input[name="address"]', 'Calle 123');
    await page.fill('input[name="city"]', 'Bogotá');
    await page.fill('input[name="phone"]', '3001234567');
    
    // 7. Confirmar orden
    await page.click('button:has-text("Confirmar Orden")');
    
    // 8. Verificar confirmación
    await expect(page.locator('h1')).toHaveText(/Orden Confirmada/);
    await expect(page.locator('text=/Número de orden/i')).toBeVisible();
  });
  
  test('shows error with invalid card', async ({ page }) => {
    await page.goto('/checkout');
    
    // Login
    await page.fill('input[type="email"]', 'test@test.com');
    await page.fill('input[type="password"]', 'password');
    await page.click('button:has-text("Iniciar Sesión")');
    
    // Información de envío
    await page.fill('input[name="address"]', 'Calle 123');
    
    // Tarjeta inválida
    await page.fill('input[name="cardNumber"]', '1234');
    await page.click('button:has-text("Confirmar")');
    
    // Verificar error
    await expect(page.locator('text=/número de tarjeta inválido/i')).toBeVisible();
  });
});
```

---

## ☕ Unit Tests - Backend (Java)

### Test de Service

```java
// src/test/java/com/babycash/service/ProductServiceTest.java
package com.babycash.service;

import com.babycash.model.Product;
import com.babycash.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(50000.0);
        testProduct.setStock(10);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productService.getProductById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
    }

    @Test
    void getProductById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productService.getProductById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void createProduct_ShouldSaveAndReturnProduct() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.createProduct(testProduct);

        // Then
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void updateProduct_WhenExists_ShouldUpdate() {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(60000.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Optional<Product> result = productService.updateProduct(1L, updatedProduct);

        // Then
        assertTrue(result.isPresent());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldCallRepository() {
        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void reduceStock_WhenSufficientStock_ShouldReduce() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.reduceStock(1L, 5);

        // Then
        assertEquals(5, testProduct.getStock());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void reduceStock_WhenInsufficientStock_ShouldThrowException() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            productService.reduceStock(1L, 20); // Más del stock disponible
        });
    }
}
```

### Test de Controller

```java
// src/test/java/com/babycash/controller/ProductControllerTest.java
package com.babycash.controller;

import com.babycash.model.Product;
import com.babycash.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void getAllProducts_ShouldReturnProducts() throws Exception {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(50000.0);

        when(productService.getAllProducts()).thenReturn(Arrays.asList(product));

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].price").value(50000.0));
    }

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() throws Exception {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getProductById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Given
        Product product = new Product();
        product.setName("New Product");
        product.setPrice(50000.0);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("New Product");
        savedProduct.setPrice(50000.0);

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Product"));
    }
}
```

---

## 📊 Test Coverage

### Ejecutar con Reporte

```bash
# Frontend
npm run test:coverage

# Backend (Maven)
mvn test jacoco:report
```

### Configurar Vitest Coverage

```typescript
// vite.config.ts
export default defineConfig({
  test: {
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'src/test/',
        '**/*.test.tsx',
        '**/*.spec.ts',
      ],
    },
  },
});
```

### Meta de Coverage

- ✅ **Statements:** > 80%
- ✅ **Branches:** > 75%
- ✅ **Functions:** > 80%
- ✅ **Lines:** > 80%

---

## 🎓 Para la Evaluación del SENA

**1. "¿Qué tipos de tests existen?"**

> "Tres tipos principales:
> 
> **Unit Tests:**
> - Testean funciones/componentes aislados
> - Rápidos (milisegundos)
> - Ejemplo: Testear función `calculateTotal(items)`
> 
> **Integration Tests:**
> - Testean interacción entre componentes
> - Ejemplo: Login → añadir al carrito → checkout
> 
> **E2E Tests:**
> - Testean flujo completo desde UI
> - Ejemplo: Usuario completa compra end-to-end
> 
> Baby Cash: 70% unit, 20% integration, 10% E2E."

---

**2. "¿Qué es mocking?"**

> "Simular dependencias externas:
> 
> ```typescript
> // Mock de API
> http.get('/api/products', () => {
>   return HttpResponse.json([{ id: 1, name: 'Test' }]);
> });
> ```
> 
> **Beneficios:**
> - Tests rápidos (no llaman backend real)
> - Tests confiables (no dependen de red)
> - Testear casos extremos (errores 500, timeouts)
> 
> Baby Cash: MSW para frontend, Mockito para backend."

---

**3. "¿Qué es test coverage?"**

> "Porcentaje de código cubierto por tests:
> 
> - **80% coverage:** 80% del código tiene tests
> - **Statements:** Líneas ejecutadas
> - **Branches:** if/else cubiertos
> - **Functions:** Funciones testeadas
> 
> ```bash
> npm run test:coverage
> ```
> 
> Meta: > 80% coverage en Baby Cash."

---

## 🚀 Conclusión

**Testing en Baby Cash:**
- ✅ Unit tests con Vitest/JUnit
- ✅ Integration tests
- ✅ E2E tests con Playwright
- ✅ Mocking con MSW/Mockito
- ✅ Coverage > 80%

**Resultado: Código confiable y mantenible.**

---

**Ahora lee:** `SECURITY.md` para proteger tu aplicación. 🚀
