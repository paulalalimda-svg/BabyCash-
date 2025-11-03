# PRODUCT CARD COMPONENT - TARJETA DE PRODUCTO

## 🎯 Visión General

**ProductCard** es la tarjeta que muestra un producto con:
- Imagen del producto
- Badge de descuento (si aplica)
- Nombre y descripción
- Precio (con descuento si hay)
- Botón "Agregar al Carrito"
- Hover effects y animaciones

---

## 📁 Ubicación

```
frontend/src/components/products/ProductCard.tsx
```

---

## 🏗️ Estructura del Componente

```tsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Product } from '../../types/product.types';
import { useCart } from '../../hooks/useCart';
import { formatPrice } from '../../utils/formatters';
import Button from '../common/Button';

interface ProductCardProps {
  product: Product;
  onQuickView?: (product: Product) => void;
}

export default function ProductCard({ product, onQuickView }: ProductCardProps) {
  const { addToCart } = useCart();
  const navigate = useNavigate();
  const [isAdding, setIsAdding] = useState(false);
  
  // ✅ Calcular descuento
  const hasDiscount = product.discount && product.discount > 0;
  const discountedPrice = hasDiscount
    ? product.price * (1 - product.discount / 100)
    : product.price;
  
  // ✅ Agregar al carrito
  const handleAddToCart = async (e: React.MouseEvent) => {
    e.stopPropagation(); // Prevenir navegación
    
    setIsAdding(true);
    
    try {
      await addToCart(product);
      // Opcional: mostrar toast de éxito
    } catch (error) {
      alert('Error al agregar producto');
    } finally {
      setIsAdding(false);
    }
  };
  
  // ✅ Ver detalle
  const handleClick = () => {
    navigate(`/productos/${product.id}`);
  };
  
  return (
    <div
      onClick={handleClick}
      className="bg-white rounded-lg shadow-md hover:shadow-xl transition-shadow cursor-pointer group overflow-hidden"
    >
      {/* Contenedor de imagen */}
      <div className="relative overflow-hidden">
        <img
          src={product.imageUrl}
          alt={product.name}
          className="w-full h-64 object-cover group-hover:scale-110 transition-transform duration-300"
        />
        
        {/* Badge de descuento */}
        {hasDiscount && (
          <div className="absolute top-2 right-2 bg-red-500 text-white px-2 py-1 rounded-full text-sm font-semibold">
            -{product.discount}%
          </div>
        )}
        
        {/* Badge de stock bajo */}
        {product.stock > 0 && product.stock <= 5 && (
          <div className="absolute top-2 left-2 bg-yellow-500 text-white px-2 py-1 rounded-full text-xs">
            ¡Últimas unidades!
          </div>
        )}
        
        {/* Badge sin stock */}
        {product.stock === 0 && (
          <div className="absolute top-2 left-2 bg-gray-500 text-white px-2 py-1 rounded-full text-xs">
            Agotado
          </div>
        )}
        
        {/* Quick View Button */}
        {onQuickView && (
          <button
            onClick={(e) => {
              e.stopPropagation();
              onQuickView(product);
            }}
            className="absolute bottom-2 right-2 bg-white p-2 rounded-full shadow-md opacity-0 group-hover:opacity-100 transition-opacity"
            title="Vista rápida"
          >
            👁️
          </button>
        )}
      </div>
      
      {/* Contenido */}
      <div className="p-4">
        {/* Categoría */}
        <p className="text-xs text-gray-500 uppercase mb-1">
          {product.category.name}
        </p>
        
        {/* Nombre */}
        <h3 className="font-semibold text-lg mb-2 line-clamp-2">
          {product.name}
        </h3>
        
        {/* Descripción */}
        <p className="text-gray-600 text-sm mb-3 line-clamp-2">
          {product.description}
        </p>
        
        {/* Precio */}
        <div className="flex items-center gap-2 mb-3">
          {hasDiscount ? (
            <>
              <span className="text-2xl font-bold text-baby-pink">
                {formatPrice(discountedPrice)}
              </span>
              <span className="text-sm text-gray-400 line-through">
                {formatPrice(product.price)}
              </span>
            </>
          ) : (
            <span className="text-2xl font-bold text-baby-pink">
              {formatPrice(product.price)}
            </span>
          )}
        </div>
        
        {/* Rating (simulado) */}
        <div className="flex items-center gap-1 mb-3">
          <span className="text-yellow-400">⭐⭐⭐⭐⭐</span>
          <span className="text-xs text-gray-500">(45 reviews)</span>
        </div>
        
        {/* Botón agregar al carrito */}
        <Button
          onClick={handleAddToCart}
          disabled={isAdding || product.stock === 0}
          className="w-full"
        >
          {isAdding ? (
            '⏳ Agregando...'
          ) : product.stock === 0 ? (
            '❌ Agotado'
          ) : (
            '🛒 Agregar al Carrito'
          )}
        </Button>
      </div>
    </div>
  );
}
```

---

## 🎨 Detalles de Diseño

### 1️⃣ Imagen con Zoom

```tsx
<img
  className="group-hover:scale-110 transition-transform duration-300"
/>
```

**Características:**
- Zoom suave al hover con `scale-110`
- Transición de 300ms
- `group-hover:` aplica cuando hover sobre card completo

---

### 2️⃣ Badges Condicionales

```tsx
{/* Descuento */}
{hasDiscount && (
  <div className="absolute top-2 right-2 bg-red-500 text-white px-2 py-1 rounded-full">
    -{product.discount}%
  </div>
)}

{/* Stock bajo */}
{product.stock > 0 && product.stock <= 5 && (
  <div className="absolute top-2 left-2 bg-yellow-500 text-white px-2 py-1 rounded-full">
    ¡Últimas unidades!
  </div>
)}

{/* Sin stock */}
{product.stock === 0 && (
  <div className="absolute top-2 left-2 bg-gray-500 text-white px-2 py-1 rounded-full">
    Agotado
  </div>
)}
```

---

### 3️⃣ Precio con Descuento

```tsx
const hasDiscount = product.discount && product.discount > 0;
const discountedPrice = hasDiscount
  ? product.price * (1 - product.discount / 100)
  : product.price;

// Render
{hasDiscount ? (
  <>
    <span className="text-2xl font-bold text-baby-pink">
      {formatPrice(discountedPrice)}
    </span>
    <span className="text-sm text-gray-400 line-through">
      {formatPrice(product.price)}
    </span>
  </>
) : (
  <span className="text-2xl font-bold text-baby-pink">
    {formatPrice(product.price)}
  </span>
)}
```

---

### 4️⃣ Botón con Estados

```tsx
<Button
  onClick={handleAddToCart}
  disabled={isAdding || product.stock === 0}
>
  {isAdding ? (
    '⏳ Agregando...'
  ) : product.stock === 0 ? (
    '❌ Agotado'
  ) : (
    '🛒 Agregar al Carrito'
  )}
</Button>
```

**Estados:**
- **Normal**: "🛒 Agregar al Carrito"
- **Loading**: "⏳ Agregando..." (disabled)
- **Sin Stock**: "❌ Agotado" (disabled)

---

## 🔄 Flujo de Agregar al Carrito

```tsx
const handleAddToCart = async (e: React.MouseEvent) => {
  // ✅ Prevenir navegación al producto
  e.stopPropagation();
  
  // ✅ Mostrar loading
  setIsAdding(true);
  
  try {
    // ✅ Agregar al carrito
    await addToCart(product);
    
    // Opcional: mostrar toast de éxito
  } catch (error) {
    alert('Error al agregar producto');
  } finally {
    // ✅ Ocultar loading
    setIsAdding(false);
  }
};
```

**`e.stopPropagation()` es crítico:**
- Card tiene `onClick` que navega a detalle
- Botón también tiene `onClick` que agrega al carrito
- Sin `stopPropagation()`, ambos se ejecutarían
- Con `stopPropagation()`, solo se ejecuta el del botón

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué hace e.stopPropagation()?"**

> "`e.stopPropagation()` previene que evento se propague al padre:
> - Card tiene `onClick` para ir a detalle
> - Botón tiene `onClick` para agregar al carrito
> - Sin `stopPropagation()`, ambos clicks se ejecutarían
> - Con `stopPropagation()`, solo se ejecuta el del botón
> 
> Esto permite tener elementos clickeables dentro de elementos clickeables."

---

**2. "¿Cómo calculas el precio con descuento?"**

> "Calculo precio final aplicando descuento:
> ```tsx
> const discountedPrice = product.price * (1 - product.discount / 100);
> ```
> - Si precio es $100.000 y descuento es 20%
> - `discountedPrice = 100000 * (1 - 20/100) = 100000 * 0.8 = 80000`
> - Resultado: $80.000
> 
> Muestro precio original tachado y precio final destacado."

---

**3. "¿Qué es line-clamp-2?"**

> "`line-clamp-2` es clase de Tailwind que:
> - Limita texto a 2 líneas
> - Agrega '...' si texto es más largo
> - Previene cards de diferentes alturas
> 
> ```tsx
> <h3 className='line-clamp-2'>{product.name}</h3>
> ```
> Esto mantiene diseño consistente en grid."

---

**4. "¿Por qué usar group-hover?"**

> "`group-hover` aplica estilos al hover sobre elemento padre:
> ```tsx
> <div className='group'>
>   <img className='group-hover:scale-110' />
> </div>
> ```
> - Cuando haces hover sobre card (grupo)
> - Imagen dentro hace zoom
> - Botón Quick View aparece
> 
> Es forma elegante de coordinar efectos hover entre padre e hijos."

---

## 📝 Checklist de ProductCard

```
✅ Imagen con zoom al hover
✅ Badge de descuento (si aplica)
✅ Badge de stock bajo/agotado
✅ Categoría del producto
✅ Nombre (limitado a 2 líneas)
✅ Descripción (limitada a 2 líneas)
✅ Precio con descuento (tachado si hay)
✅ Rating simulado
✅ Botón agregar con estados (normal/loading/agotado)
✅ e.stopPropagation() en botón
✅ Click en card navega a detalle
✅ Quick View opcional
✅ Shadow que crece al hover
```

---

## 🚀 Conclusión

**ProductCard:**
- ✅ Componente reutilizable y completo
- ✅ Diseño atractivo con hover effects
- ✅ Estados claros (loading, agotado)
- ✅ UX fluida (stopPropagation, transiciones)

**Es el componente principal en catálogo y home.**

---

**Ahora lee:** `AUTH-COMPONENTS.md` para login/register. 🚀
