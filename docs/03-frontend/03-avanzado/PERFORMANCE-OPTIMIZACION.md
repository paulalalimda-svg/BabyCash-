# PERFORMANCE Y OPTIMIZACIÓN - BABY CASH

## 🎯 ¿Por Qué es Importante el Performance?

**Performance** afecta directamente la experiencia del usuario:
- Sitios lentos → usuarios se van
- Google penaliza sitios lentos en SEO
- Mejor performance → más conversiones/ventas

### Métricas Clave

**LCP (Largest Contentful Paint):** Tiempo hasta que el contenido principal carga
- ✅ Bueno: < 2.5s
- ⚠️ Mejorable: 2.5s - 4s
- ❌ Malo: > 4s

**FID (First Input Delay):** Tiempo hasta que la página responde a interacción
- ✅ Bueno: < 100ms
- ⚠️ Mejorable: 100ms - 300ms
- ❌ Malo: > 300ms

**CLS (Cumulative Layout Shift):** Cuánto se "mueve" el contenido al cargar
- ✅ Bueno: < 0.1
- ⚠️ Mejorable: 0.1 - 0.25
- ❌ Malo: > 0.25

---

## 🚀 Técnicas de Optimización en Baby Cash

### 1️⃣ Code Splitting con React.lazy

**Problema:** Bundle.js de 500KB se carga TODO al inicio.

**Solución:** Dividir código en chunks más pequeños.

```tsx
// ❌ MAL: Importar todo al inicio
import AdminPanel from './pages/AdminPanel';
import Productos from './pages/Productos';
import Checkout from './pages/Checkout';

// Bundle.js: 500KB (tarda 3 segundos en cargar)

// ✅ BIEN: Lazy loading
import { lazy, Suspense } from 'react';

const AdminPanel = lazy(() => import('./pages/AdminPanel'));
const Productos = lazy(() => import('./pages/Productos'));
const Checkout = lazy(() => import('./pages/Checkout'));

// Resultado:
// - Bundle inicial: 150KB (carga rápido)
// - AdminPanel.chunk.js: 80KB (solo si usuario va a /admin)
// - Productos.chunk.js: 120KB (solo si usuario va a /productos)
// - Checkout.chunk.js: 150KB (solo si usuario va a /checkout)
```

**Implementación en AppRouter:**

```tsx
// src/router/AppRouter.tsx

import { lazy, Suspense } from 'react';
import { Routes, Route } from 'react-router-dom';

// Páginas que siempre se necesitan (NO lazy)
import Home from '../pages/Home';
import Layout from '../components/layout/Layout';

// Páginas lazy (se cargan solo cuando se necesitan)
const Productos = lazy(() => import('../pages/Productos'));
const Carrito = lazy(() => import('../pages/Carrito'));
const Checkout = lazy(() => import('../pages/Checkout'));
const AdminPanel = lazy(() => import('../pages/AdminPanel'));
const Perfil = lazy(() => import('../pages/Perfil'));
const Login = lazy(() => import('../pages/Login'));
const Register = lazy(() => import('../pages/Register'));

// Loading fallback
const PageLoader = () => (
  <div className="flex items-center justify-center min-h-screen">
    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500" />
  </div>
);

const AppRouter = () => {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Home />} />
        
        {/* Lazy routes con Suspense */}
        <Route 
          path="productos" 
          element={
            <Suspense fallback={<PageLoader />}>
              <Productos />
            </Suspense>
          } 
        />
        
        <Route 
          path="carrito" 
          element={
            <Suspense fallback={<PageLoader />}>
              <Carrito />
            </Suspense>
          } 
        />
        
        <Route 
          path="checkout" 
          element={
            <Suspense fallback={<PageLoader />}>
              <Checkout />
            </Suspense>
          } 
        />
        
        <Route 
          path="admin" 
          element={
            <Suspense fallback={<PageLoader />}>
              <AdminPanel />
            </Suspense>
          } 
        />
      </Route>
    </Routes>
  );
};

export default AppRouter;
```

**Beneficio:**
- Bundle inicial: 150KB → carga en 0.5s
- Usuario ve home page RÁPIDO
- Chunks adicionales cargan en background

---

### 2️⃣ React.memo para Evitar Re-renders

**Problema:** Componente re-renderiza aunque sus props no cambien.

```tsx
// ❌ MAL: ProductCard re-renderiza cuando cart cambia
const ProductCard = ({ product }: { product: Product }) => {
  console.log('ProductCard render'); // Se ejecuta SIEMPRE
  
  return (
    <div>
      <img src={product.imageUrl} />
      <h3>{product.name}</h3>
      <p>${product.price}</p>
    </div>
  );
};

// Cada vez que cart cambia:
// - ProductList re-renderiza
// - TODOS los ProductCard re-renderizan
// - 50 productos = 50 re-renders innecesarios ❌
```

**Solución: React.memo**

```tsx
// ✅ BIEN: ProductCard solo re-renderiza si product cambia
import { memo } from 'react';

const ProductCard = memo(({ product }: { product: Product }) => {
  console.log('ProductCard render'); // Solo si product cambia
  
  return (
    <div>
      <img src={product.imageUrl} />
      <h3>{product.name}</h3>
      <p>${product.price}</p>
      <button onClick={() => addToCart(product)}>
        Agregar al Carrito
      </button>
    </div>
  );
});

ProductCard.displayName = 'ProductCard';

export default ProductCard;
```

**Cuándo usar React.memo:**
- ✅ Listas grandes (productos, órdenes)
- ✅ Componentes con renderizado costoso (charts, maps)
- ✅ Props raramente cambian
- ❌ Componentes simples (botones, texto)
- ❌ Props siempre cambian

---

### 3️⃣ useMemo para Cálculos Costosos

**Problema:** Cálculo pesado se ejecuta en cada render.

```tsx
// ❌ MAL: Filtrar 1000 productos en cada render
const ProductList = () => {
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('all');
  const { items: cartItems } = useCart(); // Cambia frecuentemente
  
  // Este filtro se ejecuta CADA render (incluso si solo cart cambió)
  const filteredProducts = products.filter(p => 
    p.name.toLowerCase().includes(search.toLowerCase()) &&
    (category === 'all' || p.category === category)
  );
  
  return (
    <div>
      {filteredProducts.map(p => <ProductCard key={p.id} product={p} />)}
    </div>
  );
};
```

**Solución: useMemo**

```tsx
// ✅ BIEN: Filtrar solo cuando search o category cambian
import { useMemo } from 'react';

const ProductList = () => {
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');
  const { items: cartItems } = useCart();
  
  // Solo recalcula si search o category cambian
  const filteredProducts = useMemo(() => {
    console.log('Filtrando productos...'); // Solo cuando es necesario
    return products.filter(p => 
      p.name.toLowerCase().includes(search.toLowerCase()) &&
      (category === 'all' || p.category === category)
    );
  }, [search, category]); // Dependencias
  
  return (
    <div>
      {filteredProducts.map(p => <ProductCard key={p.id} product={p} />)}
    </div>
  );
};
```

**Otro Ejemplo: Cálculo de Total**

```tsx
const Carrito = () => {
  const { items } = useCart();
  
  // ✅ Solo recalcula si items cambia
  const total = useMemo(() => {
    return items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }, [items]);
  
  const descuento = useMemo(() => {
    return total > 100000 ? total * 0.1 : 0;
  }, [total]);
  
  const totalFinal = useMemo(() => {
    return total - descuento;
  }, [total, descuento]);
  
  return (
    <div>
      <p>Subtotal: ${total.toLocaleString()}</p>
      {descuento > 0 && <p>Descuento: -${descuento.toLocaleString()}</p>}
      <p>Total: ${totalFinal.toLocaleString()}</p>
    </div>
  );
};
```

---

### 4️⃣ useCallback para Funciones Estables

**Problema:** Función nueva en cada render rompe React.memo.

```tsx
// ❌ MAL: handleAddToCart es nueva función cada render
const ProductList = () => {
  const { addToCart } = useCart();
  
  const handleAddToCart = (product: Product) => {
    addToCart({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: 1,
    });
  };
  
  return (
    <div>
      {products.map(p => (
        <ProductCard 
          key={p.id} 
          product={p} 
          onAddToCart={handleAddToCart}  // Nueva función cada vez
        />
      ))}
    </div>
  );
};

// ProductCard con React.memo NO funciona porque onAddToCart siempre cambia
```

**Solución: useCallback**

```tsx
// ✅ BIEN: handleAddToCart es la misma función
import { useCallback } from 'react';

const ProductList = () => {
  const { addToCart } = useCart();
  
  // Función memoizada
  const handleAddToCart = useCallback((product: Product) => {
    addToCart({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: 1,
    });
  }, [addToCart]); // Solo recrea si addToCart cambia
  
  return (
    <div>
      {products.map(p => (
        <ProductCard 
          key={p.id} 
          product={p} 
          onAddToCart={handleAddToCart}  // Misma función siempre
        />
      ))}
    </div>
  );
};

// Ahora ProductCard con React.memo SÍ funciona
const ProductCard = memo(({ product, onAddToCart }) => {
  return (
    <div>
      <h3>{product.name}</h3>
      <button onClick={() => onAddToCart(product)}>Agregar</button>
    </div>
  );
});
```

---

### 5️⃣ Virtualización de Listas (React Window)

**Problema:** Renderizar 1000 productos es lento.

```tsx
// ❌ MAL: Renderizar 1000 DOM nodes
const ProductList = () => {
  return (
    <div>
      {products.map(p => <ProductCard key={p.id} product={p} />)}
      {/* 1000 elementos en DOM = lento */}
    </div>
  );
};
```

**Solución: React Window (virtualización)**

```bash
npm install react-window
```

```tsx
// ✅ BIEN: Solo renderiza elementos visibles
import { FixedSizeList } from 'react-window';

const ProductList = () => {
  const Row = ({ index, style }) => (
    <div style={style}>
      <ProductCard product={products[index]} />
    </div>
  );
  
  return (
    <FixedSizeList
      height={600}          // Altura del contenedor
      itemCount={products.length}  // Total de items
      itemSize={200}        // Altura de cada item
      width="100%"
    >
      {Row}
    </FixedSizeList>
  );
};

// Solo renderiza ~6 elementos visibles (en lugar de 1000)
```

---

### 6️⃣ Optimistic Updates (UI Rápida)

**Problema:** Esperar respuesta del backend hace UI lenta.

```tsx
// ❌ MAL: Esperar backend (200ms delay)
const addToCart = async (item) => {
  setLoading(true);
  await cartService.addToCart(item);  // Espera 200ms
  setItems([...items, item]);         // Actualiza UI
  setLoading(false);
};

// Usuario hace click → espera 200ms → ve cambio
```

**Solución: Optimistic Update**

```tsx
// ✅ BIEN: Actualizar UI inmediatamente
const addToCart = async (item) => {
  // 1. Actualizar UI YA (0ms)
  setItems([...items, item]);
  toast.success('Agregado al carrito');
  
  // 2. Llamada backend en background
  try {
    await cartService.addToCart(item);
  } catch (error) {
    // 3. Revertir si falla
    setItems(items.filter(i => i.id !== item.id));
    toast.error('Error al agregar');
  }
};

// Usuario hace click → ve cambio INMEDIATAMENTE
```

---

### 7️⃣ Debouncing de Búsquedas

**Problema:** Búsqueda hace llamada API en cada tecla.

```tsx
// ❌ MAL: Llamada API por cada letra
const SearchBar = () => {
  const [search, setSearch] = useState('');
  
  const handleSearch = async (value: string) => {
    setSearch(value);
    await api.searchProducts(value);  // Llamada API
  };
  
  return (
    <input 
      onChange={(e) => handleSearch(e.target.value)}
      // Usuario escribe "laptop" → 6 llamadas API
    />
  );
};
```

**Solución: Debounce**

```tsx
// ✅ BIEN: Solo llamada después de 300ms de inactividad
import { useState, useEffect } from 'react';

const SearchBar = () => {
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  
  // Debounce: esperar 300ms sin cambios
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 300);
    
    return () => clearTimeout(timer);
  }, [search]);
  
  // Llamada API solo cuando debouncedSearch cambia
  useEffect(() => {
    if (debouncedSearch) {
      api.searchProducts(debouncedSearch);
    }
  }, [debouncedSearch]);
  
  return (
    <input 
      value={search}
      onChange={(e) => setSearch(e.target.value)}
      // Usuario escribe "laptop" → 1 llamada API (después de 300ms)
    />
  );
};
```

**Custom Hook para Reutilizar:**

```tsx
// src/hooks/useDebounce.ts
import { useState, useEffect } from 'react';

export const useDebounce = <T,>(value: T, delay: number = 300): T => {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);
  
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);
    
    return () => clearTimeout(timer);
  }, [value, delay]);
  
  return debouncedValue;
};

// Uso:
const SearchBar = () => {
  const [search, setSearch] = useState('');
  const debouncedSearch = useDebounce(search, 300);
  
  useEffect(() => {
    if (debouncedSearch) {
      api.searchProducts(debouncedSearch);
    }
  }, [debouncedSearch]);
  
  return <input value={search} onChange={(e) => setSearch(e.target.value)} />;
};
```

---

### 8️⃣ Image Optimization

**Problema:** Imágenes pesadas (5MB) tardan mucho en cargar.

```tsx
// ❌ MAL: Cargar imagen original (5MB)
<img src="https://example.com/product-5mb.jpg" alt="Producto" />
```

**Solución 1: Lazy Loading Nativo**

```tsx
// ✅ BIEN: Cargar solo cuando visible
<img 
  src="https://example.com/product.jpg" 
  alt="Producto"
  loading="lazy"  // Carga cuando va a ser visible
/>
```

**Solución 2: Responsive Images**

```tsx
// ✅ MEJOR: Diferentes tamaños según dispositivo
<img 
  srcSet="
    product-small.jpg 300w,
    product-medium.jpg 600w,
    product-large.jpg 1200w
  "
  sizes="(max-width: 600px) 300px, (max-width: 1200px) 600px, 1200px"
  src="product-medium.jpg"
  alt="Producto"
  loading="lazy"
/>
```

**Solución 3: Componente Optimizado**

```tsx
// src/components/OptimizedImage.tsx
import { useState } from 'react';

interface Props {
  src: string;
  alt: string;
  className?: string;
}

const OptimizedImage = ({ src, alt, className }: Props) => {
  const [loaded, setLoaded] = useState(false);
  
  return (
    <div className={`relative ${className}`}>
      {/* Placeholder mientras carga */}
      {!loaded && (
        <div className="absolute inset-0 bg-gray-200 animate-pulse" />
      )}
      
      {/* Imagen real */}
      <img 
        src={src}
        alt={alt}
        loading="lazy"
        className={`transition-opacity duration-300 ${loaded ? 'opacity-100' : 'opacity-0'}`}
        onLoad={() => setLoaded(true)}
      />
    </div>
  );
};

export default OptimizedImage;
```

---

### 9️⃣ Bundle Size Optimization

**Analizar Bundle:**

```bash
npm install --save-dev vite-plugin-visualizer

# vite.config.ts
import { visualizer } from 'vite-plugin-visualizer';

export default defineConfig({
  plugins: [
    react(),
    visualizer({ open: true })  // Abre reporte en navegador
  ]
});

npm run build
```

**Eliminar Librerías Grandes:**

```tsx
// ❌ MAL: Importar toda lodash (70KB)
import _ from 'lodash';
_.debounce(fn, 300);

// ✅ BIEN: Importar solo función necesaria (5KB)
import debounce from 'lodash/debounce';
debounce(fn, 300);

// ✅ MEJOR: Implementar función (0KB adicional)
const debounce = (fn, delay) => {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
};
```

---

### 🔟 Caching con React Query / SWR

**Problema:** Llamada API cada vez que se monta componente.

```tsx
// ❌ MAL: Fetch cada vez
const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    api.getProducts().then(data => {
      setProducts(data);
      setLoading(false);
    });
  }, []);
  
  // Usuario navega: Productos → Home → Productos
  // Llama API 2 veces (aunque datos no cambiaron)
};
```

**Solución: React Query**

```bash
npm install @tanstack/react-query
```

```tsx
// main.tsx
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000,  // 5 minutos
      cacheTime: 10 * 60 * 1000, // 10 minutos
    },
  },
});

ReactDOM.render(
  <QueryClientProvider client={queryClient}>
    <App />
  </QueryClientProvider>
);
```

```tsx
// ProductList.tsx
import { useQuery } from '@tanstack/react-query';

const ProductList = () => {
  const { data: products, isLoading } = useQuery({
    queryKey: ['products'],
    queryFn: () => api.getProducts(),
  });
  
  // Primera vez: llama API
  // Segunda vez: usa caché (instantáneo)
  // Después de 5 min: revalida en background
};
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es Code Splitting y por qué es importante?"**

> "Code Splitting divide el bundle en chunks más pequeños:
> 
> **Sin Code Splitting:**
> - Bundle.js: 500KB
> - Carga TODO al inicio (3 segundos)
> - Usuario espera para ver home page
> 
> **Con Code Splitting:**
> - Bundle inicial: 150KB (0.5 segundos)
> - AdminPanel.chunk.js: 80KB (solo si va a /admin)
> - Checkout.chunk.js: 150KB (solo si va a /checkout)
> 
> **Implementación:**
> ```tsx
> const AdminPanel = lazy(() => import('./pages/AdminPanel'));
> 
> <Suspense fallback={<Loader />}>
>   <AdminPanel />
> </Suspense>
> ```
> 
> Baby Cash: Home carga rápido, admin carga después."

---

**2. "¿Cuándo usar useMemo y useCallback?"**

> "**useMemo:** Memoizar resultados costosos
> ```tsx
> const filteredProducts = useMemo(() => {
>   return products.filter(p => p.name.includes(search));
> }, [products, search]);
> // Solo filtra si products o search cambian
> ```
> 
> **useCallback:** Memoizar funciones
> ```tsx
> const handleClick = useCallback(() => {
>   addToCart(product);
> }, [product]);
> // Misma función, evita re-renders en hijos
> ```
> 
> **Cuándo usar:**
> - ✅ Cálculos pesados (filtros, sorts)
> - ✅ Funciones pasadas a React.memo components
> - ❌ Cálculos simples (sumar 2 números)
> - ❌ Componentes sin hijos memoizados"

---

**3. "¿Qué es Optimistic Update?"**

> "Actualizar UI ANTES de confirmar con backend:
> 
> **Tradicional:**
> 1. Click
> 2. Esperar backend (200ms)
> 3. Actualizar UI
> 4. ❌ Lento
> 
> **Optimistic:**
> 1. Click
> 2. Actualizar UI (0ms) ✅ Rápido
> 3. Confirmar con backend
> 4. Revertir si falla
> 
> ```tsx
> const addToCart = (item) => {
>   setItems([...items, item]);  // UI inmediata
>   
>   api.addToCart(item).catch(() => {
>     setItems(items.filter(i => i.id !== item.id));  // Revertir
>   });
> };
> ```
> 
> Baby Cash: Cart usa optimistic updates."

---

**4. "¿Cómo optimizar imágenes en React?"**

> "Tres estrategias:
> 
> **1. Lazy Loading:**
> ```tsx
> <img src="..." loading="lazy" />
> // Carga solo cuando visible
> ```
> 
> **2. Responsive Images:**
> ```tsx
> <img 
>   srcSet="small.jpg 300w, large.jpg 1200w"
>   sizes="(max-width: 600px) 300px, 1200px"
> />
> // Móvil carga small.jpg, desktop carga large.jpg
> ```
> 
> **3. Placeholder:**
> ```tsx
> <OptimizedImage src="..." />
> // Muestra placeholder gris mientras carga
> ```
> 
> Baby Cash: Productos usan lazy loading."

---

## 📝 Resumen de Optimizaciones

| Técnica | Beneficio | Cuándo Usar | Baby Cash |
|---------|-----------|-------------|-----------|
| Code Splitting | Bundle más pequeño | Siempre (rutas) | ✅ Rutas lazy |
| React.memo | Evitar re-renders | Listas, componentes costosos | ✅ ProductCard |
| useMemo | Cache cálculos | Filtros, sorts, cálculos | ✅ Cart total |
| useCallback | Funciones estables | Props a memo components | ✅ Add to cart |
| Optimistic Updates | UI instantánea | Operaciones CRUD | ✅ Cart actions |
| Debouncing | Reducir API calls | Búsquedas, autocomplete | ✅ Search bar |
| Image Optimization | Carga más rápida | Todas las imágenes | ✅ Products |
| Virtualización | Listas grandes | 100+ elementos | ❌ No necesario aún |
| React Query | Caching automático | Datos remotos | ❌ Implementar futuro |

---

## 🚀 Conclusión

**Performance en Baby Cash:**
- ✅ Code splitting → Bundle inicial pequeño
- ✅ React.memo → Re-renders mínimos
- ✅ useMemo/useCallback → Optimizaciones específicas
- ✅ Optimistic updates → UI instantánea
- ✅ Debouncing → Menos API calls
- ✅ Image lazy loading → Carga progresiva

**Resultado: App rápida y responsive.**

---

**Ahora lee:** `MANEJO-ERRORES.md` para hacer tu app robusta. 🚀
