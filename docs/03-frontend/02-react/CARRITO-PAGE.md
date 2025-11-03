# CARRITO PAGE - CARRITO DE COMPRAS

## 🎯 Visión General

La **Carrito Page** muestra el carrito de compras con:
- Lista de productos agregados
- Controles de cantidad
- Resumen de compra
- Proceso de checkout

---

## 📁 Ubicación

```
frontend/src/pages/Carrito.tsx
```

---

## 🏗️ Estructura del Componente

```tsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../hooks/useCart';
import { useAuth } from '../hooks/useAuth';
import MainLayout from '../components/layout/MainLayout';
import CartItem from '../components/cart/CartItem';
import CartSummary from '../components/cart/CartSummary';
import Button from '../components/common/Button';
import { ordersAPI } from '../services/ordersAPI';

export default function Carrito() {
  const { cart, updateQuantity, removeFromCart, clearCart, total } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  
  // ✅ Procesar checkout
  const handleCheckout = async () => {
    // Verificar autenticación
    if (!user) {
      alert('Debes iniciar sesión para continuar');
      navigate('/login?redirect=/carrito');
      return;
    }
    
    // Validar carrito no vacío
    if (cart.length === 0) {
      alert('El carrito está vacío');
      return;
    }
    
    setLoading(true);
    
    try {
      // Crear orden
      const orderData = {
        items: cart.map(item => ({
          productId: item.product.id,
          quantity: item.quantity,
          price: item.product.price,
        })),
        total,
      };
      
      const response = await ordersAPI.create(orderData);
      
      // Limpiar carrito
      clearCart();
      
      // Redirigir a confirmación
      navigate(`/orders/${response.data.id}`);
      
      alert('¡Compra realizada con éxito! ✅');
    } catch (error: any) {
      console.error('Error al crear orden:', error);
      alert(error.response?.data?.message || 'Error al procesar compra');
    } finally {
      setLoading(false);
    }
  };
  
  // ✅ Si carrito está vacío
  if (cart.length === 0) {
    return (
      <MainLayout>
        <div className="container mx-auto px-4 py-16 text-center">
          <div className="text-8xl mb-4">🛒</div>
          <h2 className="text-3xl font-bold mb-4">Tu carrito está vacío</h2>
          <p className="text-gray-600 mb-8">
            Agrega productos para comenzar tu compra
          </p>
          <Button onClick={() => navigate('/productos')}>
            Ver Productos
          </Button>
        </div>
      </MainLayout>
    );
  }
  
  return (
    <MainLayout>
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-4xl font-bold mb-8">Carrito de Compras</h1>
        
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Lista de items */}
          <div className="lg:w-2/3">
            <div className="bg-white rounded-lg shadow-md p-6">
              {cart.map(item => (
                <CartItem
                  key={item.product.id}
                  item={item}
                  onUpdateQuantity={(quantity) =>
                    updateQuantity(item.product.id, quantity)
                  }
                  onRemove={() => removeFromCart(item.product.id)}
                />
              ))}
            </div>
          </div>
          
          {/* Resumen */}
          <div className="lg:w-1/3">
            <CartSummary
              total={total}
              itemCount={cart.length}
              onCheckout={handleCheckout}
              loading={loading}
            />
          </div>
        </div>
      </div>
    </MainLayout>
  );
}
```

---

## 🎨 Componente: CartItem

```tsx
// components/cart/CartItem.tsx
import { CartItem as CartItemType } from '../../types/cart.types';
import { formatPrice } from '../../utils/formatters';
import Button from '../common/Button';

interface CartItemProps {
  item: CartItemType;
  onUpdateQuantity: (quantity: number) => void;
  onRemove: () => void;
}

export default function CartItem({ item, onUpdateQuantity, onRemove }: CartItemProps) {
  const { product, quantity } = item;
  const subtotal = product.price * quantity;
  
  return (
    <div className="flex gap-4 border-b py-4 last:border-b-0">
      {/* Imagen */}
      <img
        src={product.imageUrl}
        alt={product.name}
        className="w-24 h-24 object-cover rounded"
      />
      
      {/* Info */}
      <div className="flex-grow">
        <h3 className="font-semibold text-lg">{product.name}</h3>
        <p className="text-gray-600">{formatPrice(product.price)}</p>
        
        {/* Controles de cantidad */}
        <div className="flex items-center gap-2 mt-2">
          <button
            onClick={() => onUpdateQuantity(quantity - 1)}
            disabled={quantity <= 1}
            className="w-8 h-8 border rounded hover:bg-gray-100 disabled:opacity-50"
          >
            -
          </button>
          <span className="w-12 text-center font-semibold">{quantity}</span>
          <button
            onClick={() => onUpdateQuantity(quantity + 1)}
            disabled={quantity >= product.stock}
            className="w-8 h-8 border rounded hover:bg-gray-100 disabled:opacity-50"
          >
            +
          </button>
          
          <button
            onClick={onRemove}
            className="ml-auto text-red-500 hover:text-red-700"
          >
            🗑️ Eliminar
          </button>
        </div>
      </div>
      
      {/* Subtotal */}
      <div className="text-right">
        <p className="font-semibold text-lg">{formatPrice(subtotal)}</p>
      </div>
    </div>
  );
}
```

---

## 🎨 Componente: CartSummary

```tsx
// components/cart/CartSummary.tsx
import { formatPrice } from '../../utils/formatters';
import Button from '../common/Button';

interface CartSummaryProps {
  total: number;
  itemCount: number;
  onCheckout: () => void;
  loading: boolean;
}

export default function CartSummary({
  total,
  itemCount,
  onCheckout,
  loading,
}: CartSummaryProps) {
  const shipping = 10000; // $10.000 fijo
  const tax = total * 0.19; // IVA 19%
  const grandTotal = total + shipping + tax;
  
  return (
    <div className="bg-white rounded-lg shadow-md p-6 sticky top-4">
      <h3 className="text-xl font-semibold mb-4">Resumen de Compra</h3>
      
      <div className="space-y-2 mb-4">
        <div className="flex justify-between">
          <span className="text-gray-600">Subtotal ({itemCount} items)</span>
          <span>{formatPrice(total)}</span>
        </div>
        
        <div className="flex justify-between">
          <span className="text-gray-600">Envío</span>
          <span>{formatPrice(shipping)}</span>
        </div>
        
        <div className="flex justify-between">
          <span className="text-gray-600">IVA (19%)</span>
          <span>{formatPrice(tax)}</span>
        </div>
        
        <div className="border-t pt-2 mt-2">
          <div className="flex justify-between font-bold text-lg">
            <span>Total</span>
            <span className="text-baby-pink">{formatPrice(grandTotal)}</span>
          </div>
        </div>
      </div>
      
      <Button
        onClick={onCheckout}
        disabled={loading}
        className="w-full"
      >
        {loading ? 'Procesando...' : 'Procesar Compra'}
      </Button>
      
      <p className="text-xs text-gray-500 text-center mt-4">
        Al continuar, aceptas nuestros términos y condiciones
      </p>
    </div>
  );
}
```

---

## 🔄 Flujo de Checkout

### 1️⃣ Usuario Hace Clic en "Procesar Compra"

```tsx
const handleCheckout = async () => {
  // ✅ Verificar autenticación
  if (!user) {
    alert('Debes iniciar sesión');
    navigate('/login?redirect=/carrito');
    return;
  }
  
  // ✅ Validar carrito no vacío
  if (cart.length === 0) {
    alert('El carrito está vacío');
    return;
  }
```

---

### 2️⃣ Crear Orden en Backend

```tsx
  setLoading(true);
  
  try {
    // Preparar datos
    const orderData = {
      items: cart.map(item => ({
        productId: item.product.id,
        quantity: item.quantity,
        price: item.product.price,
      })),
      total,
    };
    
    // Llamar API
    const response = await ordersAPI.create(orderData);
```

---

### 3️⃣ Backend Procesa

```java
// Backend (OrderService.java)
@Transactional
public Order createOrder(CreateOrderRequest request) {
    // Obtener usuario autenticado
    User user = authService.getCurrentUser();
    
    // Crear orden
    Order order = new Order();
    order.setUser(user);
    order.setStatus(OrderStatus.PENDING);
    order.setTotal(request.getTotal());
    
    // Crear items
    for (OrderItemRequest itemRequest : request.getItems()) {
        Product product = productRepository.findById(itemRequest.getProductId())
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        
        // Validar stock
        if (product.getStock() < itemRequest.getQuantity()) {
            throw new BadRequestException("Stock insuficiente");
        }
        
        // Reducir stock
        product.setStock(product.getStock() - itemRequest.getQuantity());
        
        // Crear item
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(itemRequest.getQuantity());
        item.setPrice(itemRequest.getPrice());
        item.setOrder(order);
        
        order.getItems().add(item);
    }
    
    return orderRepository.save(order);
}
```

---

### 4️⃣ Frontend Recibe Respuesta

```tsx
    // Limpiar carrito
    clearCart();
    
    // Redirigir a confirmación
    navigate(`/orders/${response.data.id}`);
    
    alert('¡Compra realizada con éxito! ✅');
  } catch (error: any) {
    alert(error.response?.data?.message || 'Error al procesar compra');
  } finally {
    setLoading(false);
  }
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Cómo funciona el carrito?"**

> "El carrito usa el custom hook `useCart` que:
> 1. Guarda items en localStorage
> 2. Sincroniza con estado React
> 3. Calcula total automáticamente
> 4. Provee funciones (add, remove, update, clear)
> 
> Cuando usuario agrega producto, se guarda en localStorage y estado. Al recargar página, localStorage restaura el carrito."

---

**2. "¿Qué validaciones hay en checkout?"**

> "3 validaciones principales:
> 1. **Usuario autenticado**: Si no, redirige a login
> 2. **Carrito no vacío**: Si está vacío, muestra alerta
> 3. **Stock disponible** (backend): Si no hay stock, backend retorna error
> 
> Esto previene errores comunes."

---

**3. "¿Por qué limpiar carrito después de compra?"**

> "Porque el carrito es para compra ACTUAL. Una vez creada la orden:
> ```tsx
> clearCart(); // Elimina de localStorage y estado
> ```
> El usuario puede ver sus órdenes en historial, pero carrito queda limpio para nueva compra."

---

**4. "¿Cómo calculas el total?"**

> "CartSummary calcula:
> ```tsx
> const shipping = 10000; // Envío fijo
> const tax = total * 0.19; // IVA 19%
> const grandTotal = total + shipping + tax;
> ```
> - `total`: Suma de (precio × cantidad) de cada item
> - `shipping`: Costo de envío (puede ser variable según ubicación)
> - `tax`: IVA colombiano 19%
> - `grandTotal`: Suma de todo"

---

## 📝 Checklist de Carrito Page

```
✅ Muestra items del carrito
✅ Controles de cantidad (+/-)
✅ Botón eliminar item
✅ Resumen con subtotal/envío/IVA/total
✅ Validación de autenticación
✅ Validación de carrito vacío
✅ Loading state en checkout
✅ Mensaje de confirmación
✅ Limpiar carrito después de compra
✅ Redirigir a orden creada
```

---

## 🚀 Conclusión

**Carrito Page:**
- ✅ Gestiona proceso de compra
- ✅ Validaciones robustas
- ✅ UX clara (resumen, controles)
- ✅ Integración con backend (orders)

**Es la página crítica para conversión.**

---

**Ahora lee:** `ADMIN-PAGE.md` para administración. 🚀
