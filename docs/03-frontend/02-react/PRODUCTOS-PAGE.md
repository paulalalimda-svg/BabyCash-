# PRODUCTOS PAGE - CATÁLOGO

## 🎯 Visión General

La **Productos Page** muestra el catálogo completo con:
- Grid de productos
- Filtros (categoría, precio, búsqueda)
- Ordenamiento
- Paginación

---

## 📁 Ubicación

```
frontend/src/pages/Productos.tsx
```

---

## 🏗️ Estructura del Componente

```tsx
import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { productsAPI } from '../services/productsAPI';
import { Product } from '../types/product.types';
import MainLayout from '../components/layout/MainLayout';
import ProductGrid from '../components/products/ProductGrid';
import ProductFilter from '../components/products/ProductFilter';
import Loader from '../components/common/Loader';

export default function Productos() {
  const [products, setProducts] = useState<Product[]>([]);
  const [filteredProducts, setFilteredProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchParams] = useSearchParams();
  
  // ✅ Filtros
  const [filters, setFilters] = useState({
    category: searchParams.get('category') || null,
    minPrice: 0,
    maxPrice: 1000000,
    query: '',
    sortBy: 'name', // name, price-asc, price-desc
  });
  
  // ✅ Cargar productos al montar
  useEffect(() => {
    productsAPI.getAll()
      .then(response => setProducts(response.data))
      .finally(() => setLoading(false));
  }, []);
  
  // ✅ Aplicar filtros cuando cambian
  useEffect(() => {
    let result = [...products];
    
    // Filtrar por categoría
    if (filters.category) {
      result = result.filter(p => p.category.slug === filters.category);
    }
    
    // Filtrar por rango de precio
    result = result.filter(
      p => p.price >= filters.minPrice && p.price <= filters.maxPrice
    );
    
    // Filtrar por búsqueda
    if (filters.query) {
      result = result.filter(p =>
        p.name.toLowerCase().includes(filters.query.toLowerCase()) ||
        p.description.toLowerCase().includes(filters.query.toLowerCase())
      );
    }
    
    // Ordenar
    switch (filters.sortBy) {
      case 'price-asc':
        result.sort((a, b) => a.price - b.price);
        break;
      case 'price-desc':
        result.sort((a, b) => b.price - a.price);
        break;
      case 'name':
      default:
        result.sort((a, b) => a.name.localeCompare(b.name));
        break;
    }
    
    setFilteredProducts(result);
  }, [products, filters]);
  
  return (
    <MainLayout>
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-4xl font-bold mb-8">Nuestros Productos</h1>
        
        <div className="flex flex-col md:flex-row gap-8">
          {/* Sidebar con filtros */}
          <aside className="md:w-64">
            <ProductFilter filters={filters} onChange={setFilters} />
          </aside>
          
          {/* Grid de productos */}
          <main className="flex-grow">
            {/* Header con resultados y ordenamiento */}
            <div className="flex justify-between items-center mb-6">
              <p className="text-gray-600">
                {filteredProducts.length} productos encontrados
              </p>
              <select
                value={filters.sortBy}
                onChange={(e) => setFilters({ ...filters, sortBy: e.target.value })}
                className="border rounded px-3 py-2"
              >
                <option value="name">Ordenar por nombre</option>
                <option value="price-asc">Precio: menor a mayor</option>
                <option value="price-desc">Precio: mayor a menor</option>
              </select>
            </div>
            
            {/* Grid */}
            {loading ? (
              <Loader />
            ) : filteredProducts.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500 text-xl">
                  No se encontraron productos
                </p>
                <button
                  onClick={() => setFilters({
                    category: null,
                    minPrice: 0,
                    maxPrice: 1000000,
                    query: '',
                    sortBy: 'name',
                  })}
                  className="mt-4 text-baby-pink underline"
                >
                  Limpiar filtros
                </button>
              </div>
            ) : (
              <ProductGrid products={filteredProducts} />
            )}
          </main>
        </div>
      </div>
    </MainLayout>
  );
}
```

---

## 🎨 Componente: ProductFilter

```tsx
// components/products/ProductFilter.tsx
import { useState, useEffect } from 'react';
import { categoriesAPI } from '../../services/categoriesAPI';

interface ProductFilterProps {
  filters: {
    category: string | null;
    minPrice: number;
    maxPrice: number;
    query: string;
    sortBy: string;
  };
  onChange: (filters: any) => void;
}

export default function ProductFilter({ filters, onChange }: ProductFilterProps) {
  const [categories, setCategories] = useState([]);
  
  useEffect(() => {
    categoriesAPI.getAll()
      .then(response => setCategories(response.data));
  }, []);
  
  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <h3 className="text-xl font-semibold mb-4">Filtros</h3>
      
      {/* Búsqueda */}
      <div className="mb-6">
        <label className="block text-sm font-medium mb-2">Buscar</label>
        <input
          type="text"
          value={filters.query}
          onChange={(e) => onChange({ ...filters, query: e.target.value })}
          placeholder="Buscar productos..."
          className="w-full border rounded px-3 py-2"
        />
      </div>
      
      {/* Categorías */}
      <div className="mb-6">
        <label className="block text-sm font-medium mb-2">Categoría</label>
        <select
          value={filters.category || ''}
          onChange={(e) => onChange({ ...filters, category: e.target.value || null })}
          className="w-full border rounded px-3 py-2"
        >
          <option value="">Todas las categorías</option>
          {categories.map((cat: any) => (
            <option key={cat.id} value={cat.slug}>
              {cat.name}
            </option>
          ))}
        </select>
      </div>
      
      {/* Rango de precio */}
      <div className="mb-6">
        <label className="block text-sm font-medium mb-2">Rango de Precio</label>
        <div className="flex gap-2">
          <input
            type="number"
            value={filters.minPrice}
            onChange={(e) => onChange({ ...filters, minPrice: Number(e.target.value) })}
            placeholder="Min"
            className="w-full border rounded px-3 py-2"
          />
          <input
            type="number"
            value={filters.maxPrice}
            onChange={(e) => onChange({ ...filters, maxPrice: Number(e.target.value) })}
            placeholder="Max"
            className="w-full border rounded px-3 py-2"
          />
        </div>
      </div>
      
      {/* Limpiar filtros */}
      <button
        onClick={() => onChange({
          category: null,
          minPrice: 0,
          maxPrice: 1000000,
          query: '',
          sortBy: 'name',
        })}
        className="w-full bg-gray-200 hover:bg-gray-300 py-2 rounded"
      >
        Limpiar Filtros
      </button>
    </div>
  );
}
```

---

## 🔄 Flujo de Filtrado

### 1️⃣ Usuario Carga Página

```tsx
useEffect(() => {
  // ✅ Cargar todos los productos
  productsAPI.getAll()
    .then(response => setProducts(response.data))
    .finally(() => setLoading(false));
}, []);
```

---

### 2️⃣ Usuario Selecciona Categoría

```tsx
// ProductFilter cambia filters.category
onChange({ ...filters, category: 'ropa' });

// useEffect detecta cambio y aplica filtro
useEffect(() => {
  let result = [...products];
  
  if (filters.category) {
    result = result.filter(p => p.category.slug === filters.category);
  }
  
  setFilteredProducts(result);
}, [products, filters]);
```

---

### 3️⃣ Usuario Escribe en Búsqueda

```tsx
// ProductFilter cambia filters.query
onChange({ ...filters, query: 'biberon' });

// useEffect aplica filtro de texto
result = result.filter(p =>
  p.name.toLowerCase().includes(filters.query.toLowerCase()) ||
  p.description.toLowerCase().includes(filters.query.toLowerCase())
);
```

---

### 4️⃣ Usuario Cambia Ordenamiento

```tsx
// Select de ordenamiento cambia filters.sortBy
setFilters({ ...filters, sortBy: 'price-asc' });

// useEffect ordena resultados
switch (filters.sortBy) {
  case 'price-asc':
    result.sort((a, b) => a.price - b.price);
    break;
  case 'price-desc':
    result.sort((a, b) => b.price - a.price);
    break;
  case 'name':
  default:
    result.sort((a, b) => a.name.localeCompare(b.name));
    break;
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Cómo funcionan los filtros?"**

> "Los filtros usan `useEffect` con dependencia en `filters`:
> 1. Usuario cambia filtro (categoría, precio, búsqueda)
> 2. `setFilters` actualiza estado
> 3. `useEffect` detecta cambio
> 4. Aplico filtros sobre `products` original
> 5. Guardo resultado en `filteredProducts`
> 6. ProductGrid muestra resultados filtrados
> 
> Esto es filtrado client-side. En producción, sería server-side con paginación."

---

**2. "¿Por qué usar dos estados (products y filteredProducts)?"**

> "Por eficiencia:
> - `products`: Array original de backend (no cambia)
> - `filteredProducts`: Resultado después de aplicar filtros
> 
> Si solo tuviera `products`, al cambiar filtro perdería datos originales y tendría que volver a llamar API. Con dos estados, filtro localmente sin re-fetch."

---

**3. "¿Cómo funciona el ordenamiento?"**

> "Uso `Array.sort()` con comparador:
> - **Nombre**: `a.name.localeCompare(b.name)` (alfabético)
> - **Precio ascendente**: `a.price - b.price`
> - **Precio descendente**: `b.price - a.price`
> 
> El ordenamiento se aplica después de filtrar."

---

**4. "¿Qué pasa si no hay resultados?"**

> "Muestro mensaje 'No se encontraron productos' con botón 'Limpiar filtros':
> ```tsx
> {filteredProducts.length === 0 ? (
>   <div>
>     <p>No se encontraron productos</p>
>     <button onClick={resetFilters}>Limpiar filtros</button>
>   </div>
> ) : (
>   <ProductGrid products={filteredProducts} />
> )}
> ```
> Esto mejora UX."

---

## 📝 Checklist de Productos Page

```
✅ Carga productos al montar
✅ Filtros (categoría, precio, búsqueda)
✅ Ordenamiento (nombre, precio)
✅ Contador de resultados
✅ Mensaje si no hay resultados
✅ Botón limpiar filtros
✅ Loading state
✅ Responsive layout (sidebar + grid)
✅ URL params (category desde Home)
```

---

## 🚀 Conclusión

**Productos Page:**
- ✅ Catálogo completo con filtros
- ✅ Búsqueda en tiempo real
- ✅ Ordenamiento flexible
- ✅ UX clara (contador, mensajes)

**Es la página principal para descubrir productos.**

---

**Ahora lee:** `CARRITO-PAGE.md` para carrito. 🚀
