# NAVBAR COMPONENT - BARRA DE NAVEGACIÓN

## 🎯 Visión General

El **Navbar** es la barra de navegación superior con:
- Logo y enlaces principales
- Búsqueda rápida
- Carrito con badge
- Menú de usuario (login/logout)
- Responsive con menú móvil

---

## 📁 Ubicación

```
frontend/src/components/layout/Navbar.tsx
```

---

## 🏗️ Estructura del Componente

```tsx
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import CartDropdown from '../cart/CartDropdown';

export default function Navbar() {
  const { user, logout } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();
  const [showCartDropdown, setShowCartDropdown] = useState(false);
  const [showMobileMenu, setShowMobileMenu] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  
  // ✅ Total de items en carrito
  const cartItemsCount = cart.reduce((total, item) => total + item.quantity, 0);
  
  // ✅ Manejar búsqueda
  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/productos?q=${searchQuery}`);
      setSearchQuery('');
    }
  };
  
  return (
    <nav className="bg-white shadow-md sticky top-0 z-50">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center">
            <span className="text-2xl">👶</span>
            <span className="ml-2 text-xl font-baby font-bold text-baby-pink">
              Baby Cash
            </span>
          </Link>
          
          {/* Links de navegación (Desktop) */}
          <div className="hidden md:flex items-center space-x-6">
            <Link
              to="/"
              className="text-gray-700 hover:text-baby-pink transition"
            >
              Inicio
            </Link>
            <Link
              to="/productos"
              className="text-gray-700 hover:text-baby-pink transition"
            >
              Productos
            </Link>
            <Link
              to="/nosotros"
              className="text-gray-700 hover:text-baby-pink transition"
            >
              Nosotros
            </Link>
            <Link
              to="/contacto"
              className="text-gray-700 hover:text-baby-pink transition"
            >
              Contacto
            </Link>
          </div>
          
          {/* Búsqueda */}
          <form onSubmit={handleSearch} className="hidden md:block">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Buscar productos..."
              className="border rounded-full px-4 py-2 w-64 focus:outline-none focus:ring-2 focus:ring-baby-pink"
            />
          </form>
          
          {/* Acciones */}
          <div className="flex items-center space-x-4">
            {/* Carrito */}
            <div className="relative">
              <button
                onClick={() => setShowCartDropdown(!showCartDropdown)}
                className="relative p-2 text-gray-700 hover:text-baby-pink transition"
              >
                🛒
                {cartItemsCount > 0 && (
                  <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                    {cartItemsCount}
                  </span>
                )}
              </button>
              
              {/* Dropdown del carrito */}
              {showCartDropdown && (
                <CartDropdown onClose={() => setShowCartDropdown(false)} />
              )}
            </div>
            
            {/* Usuario */}
            {user ? (
              <div className="relative group">
                <button className="flex items-center space-x-2 p-2 text-gray-700 hover:text-baby-pink transition">
                  <span>👤</span>
                  <span className="hidden md:inline">{user.name}</span>
                </button>
                
                {/* Dropdown de usuario */}
                <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-2 hidden group-hover:block">
                  <Link
                    to="/mi-cuenta"
                    className="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                  >
                    Mi Cuenta
                  </Link>
                  <Link
                    to="/mis-pedidos"
                    className="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                  >
                    Mis Pedidos
                  </Link>
                  {user.role === 'ADMIN' && (
                    <Link
                      to="/admin"
                      className="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                    >
                      Panel Admin
                    </Link>
                  )}
                  <hr className="my-2" />
                  <button
                    onClick={logout}
                    className="block w-full text-left px-4 py-2 text-red-600 hover:bg-gray-100"
                  >
                    Cerrar Sesión
                  </button>
                </div>
              </div>
            ) : (
              <Link
                to="/login"
                className="bg-baby-pink text-white px-4 py-2 rounded-full hover:bg-baby-pink-dark transition"
              >
                Iniciar Sesión
              </Link>
            )}
            
            {/* Menú móvil */}
            <button
              onClick={() => setShowMobileMenu(!showMobileMenu)}
              className="md:hidden p-2 text-gray-700"
            >
              ☰
            </button>
          </div>
        </div>
        
        {/* Menú móvil */}
        {showMobileMenu && (
          <div className="md:hidden py-4 border-t">
            <Link to="/" className="block py-2 text-gray-700">Inicio</Link>
            <Link to="/productos" className="block py-2 text-gray-700">Productos</Link>
            <Link to="/nosotros" className="block py-2 text-gray-700">Nosotros</Link>
            <Link to="/contacto" className="block py-2 text-gray-700">Contacto</Link>
            
            {/* Búsqueda móvil */}
            <form onSubmit={handleSearch} className="mt-4">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Buscar..."
                className="w-full border rounded px-4 py-2"
              />
            </form>
          </div>
        )}
      </div>
    </nav>
  );
}
```

---

## 🎨 Componente: CartDropdown

```tsx
// components/cart/CartDropdown.tsx
import { Link } from 'react-router-dom';
import { useCart } from '../../hooks/useCart';
import { formatPrice } from '../../utils/formatters';

interface CartDropdownProps {
  onClose: () => void;
}

export default function CartDropdown({ onClose }: CartDropdownProps) {
  const { cart, total } = useCart();
  
  if (cart.length === 0) {
    return (
      <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-xl p-6">
        <p className="text-center text-gray-500">Tu carrito está vacío</p>
      </div>
    );
  }
  
  return (
    <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-xl p-4">
      <h3 className="font-semibold mb-4">Mi Carrito ({cart.length})</h3>
      
      {/* Items (máximo 3) */}
      <div className="space-y-3 max-h-64 overflow-y-auto">
        {cart.slice(0, 3).map(item => (
          <div key={item.product.id} className="flex gap-3">
            <img
              src={item.product.imageUrl}
              alt={item.product.name}
              className="w-16 h-16 object-cover rounded"
            />
            <div className="flex-grow">
              <p className="font-medium text-sm">{item.product.name}</p>
              <p className="text-xs text-gray-600">
                {item.quantity} x {formatPrice(item.product.price)}
              </p>
            </div>
          </div>
        ))}
      </div>
      
      {cart.length > 3 && (
        <p className="text-sm text-gray-500 mt-2">
          y {cart.length - 3} producto(s) más...
        </p>
      )}
      
      {/* Total */}
      <div className="border-t mt-4 pt-4">
        <div className="flex justify-between font-semibold">
          <span>Total:</span>
          <span className="text-baby-pink">{formatPrice(total)}</span>
        </div>
      </div>
      
      {/* Acciones */}
      <div className="mt-4 space-y-2">
        <Link
          to="/carrito"
          onClick={onClose}
          className="block w-full bg-baby-pink text-white text-center py-2 rounded hover:bg-baby-pink-dark transition"
        >
          Ver Carrito
        </Link>
        <button
          onClick={onClose}
          className="block w-full border border-gray-300 py-2 rounded hover:bg-gray-50 transition"
        >
          Continuar Comprando
        </button>
      </div>
    </div>
  );
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Cómo funciona el badge del carrito?"**

> "El badge usa `useCart` para contar items:
> ```tsx
> const { cart } = useCart();
> const cartItemsCount = cart.reduce((total, item) => total + item.quantity, 0);
> ```
> - Si cart tiene 2 items con quantity 3 y 1, badge muestra 4
> - Badge solo aparece si `cartItemsCount > 0`
> - Badge es `position: absolute` sobre icono de carrito"

---

**2. "¿Cómo funciona el menú de usuario?"**

> "El menú usa `useAuth` para obtener user:
> ```tsx
> const { user, logout } = useAuth();
> ```
> - Si `user` existe, muestra nombre y dropdown con opciones (Mi Cuenta, Mis Pedidos, Cerrar Sesión)
> - Si `user.role === 'ADMIN'`, agrega link a Panel Admin
> - Si no hay `user`, muestra botón 'Iniciar Sesión'
> - Dropdown usa `group-hover:block` de Tailwind para mostrar al hover"

---

**3. "¿Por qué sticky top-0 z-50?"**

> "Para que navbar permanezca visible al hacer scroll:
> - `sticky top-0`: Navbar se pega al top cuando scrolleas
> - `z-50`: z-index alto para estar sobre otros elementos
> - Mejora navegación (siempre accesible) y UX"

---

**4. "¿Cómo funciona el responsive?"**

> "Con Tailwind breakpoints:
> - **Desktop**: Links visibles con `hidden md:flex`
> - **Mobile**: Links ocultos, botón ☰ visible con `md:hidden`
> - Al hacer clic en ☰, `setShowMobileMenu(true)` muestra menú vertical
> - Búsqueda también cambia: desktop usa `w-64`, mobile usa `w-full`"

---

## 📝 Checklist de Navbar

```
✅ Logo con link a home
✅ Links de navegación (Inicio, Productos, Nosotros, Contacto)
✅ Búsqueda rápida (desktop + mobile)
✅ Carrito con badge y dropdown
✅ Menú de usuario (condicional autenticación)
✅ Dropdown de usuario (Mi Cuenta, Pedidos, Admin, Logout)
✅ Link admin solo si role === 'ADMIN'
✅ Sticky navbar (siempre visible)
✅ Responsive (menú móvil con ☰)
✅ Hover effects y transiciones
```

---

## 🚀 Conclusión

**Navbar:**
- ✅ Componente crítico presente en todas las páginas
- ✅ Integra useAuth y useCart
- ✅ UX fluida (sticky, dropdowns, badge)
- ✅ Responsive con menú móvil

**Es la navegación principal de Baby Cash.**

---

**Ahora lee:** `PRODUCT-CARD-COMPONENT.md` para tarjetas de producto. 🚀
