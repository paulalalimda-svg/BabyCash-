# CONTEXT API Y STATE MANAGEMENT - BABY CASH

## 🎯 ¿Qué es Context API?

**Context API** es la solución de React para compartir estado entre componentes sin "prop drilling" (pasar props por múltiples niveles).

### Analogía Simple
> **Imagina una radio FM:**
> - La estación transmite (Provider)
> - Todos los radios en el área pueden sintonizar (Consumers)
> - No necesitas cables entre la estación y cada radio
> 
> En React:
> - Context Provider transmite datos
> - Cualquier componente puede consumir con `useContext`
> - No necesitas pasar props por cada nivel

---

## 💡 Context vs Props vs Redux

### Props (Default)
```tsx
// App.tsx
<Parent user={user} />

// Parent.tsx
<Child user={user} />

// Child.tsx
<GrandChild user={user} />

// GrandChild.tsx usa {user}
```
❌ **Problema:** Prop drilling (pasar user por 3 niveles)

### Context API
```tsx
// App.tsx
<AuthProvider>
  <Parent />
</AuthProvider>

// GrandChild.tsx
const { user } = useAuth();  // Acceso directo
```
✅ **Ventaja:** Acceso directo desde cualquier nivel

### Redux/Zustand (State Management Libraries)
```tsx
// store.ts
const store = createStore(...);

// GrandChild.tsx
const user = useSelector(state => state.user);
```
✅ **Mejor para:** Apps muy grandes, state complejo, devtools avanzadas

---

## 🏗️ AuthContext en Baby Cash

### Estructura Completa

```tsx
// src/contexts/AuthContext.tsx

import { createContext, useState, useContext, useEffect, ReactNode } from 'react';
import { authService, AuthResponse, User } from '../services/api';
import toast from 'react-hot-toast';

// ========== 1. DEFINIR TIPOS ==========
interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (userData: RegisterData) => Promise<void>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isModerator: boolean;
}

// ========== 2. CREAR CONTEXTO ==========
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// ========== 3. PROVIDER ==========
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // Cargar usuario desde localStorage al iniciar
  useEffect(() => {
    const loadUser = () => {
      try {
        const userData = authService.getCurrentUser();
        if (userData && authService.isAuthenticated()) {
          setUser(userData);
        }
      } catch (error) {
        console.error('Error al cargar usuario:', error);
        localStorage.removeItem('baby-cash-auth');
        localStorage.removeItem('baby-cash-user');
      } finally {
        setLoading(false);
      }
    };

    loadUser();
  }, []);

  // ========== LOGIN ==========
  const login = async (email: string, password: string) => {
    try {
      setLoading(true);
      const response: AuthResponse = await authService.login(email, password);
      
      const userData: User = {
        id: 0,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        role: response.role,
        enabled: true,
      };

      localStorage.setItem('baby-cash-user', JSON.stringify(userData));
      setUser(userData);
      toast.success(`¡Bienvenido, ${response.firstName}!`);
    } catch (error) {
      toast.error('Error al iniciar sesión');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // ========== REGISTER ==========
  const register = async (userData: RegisterData) => {
    try {
      setLoading(true);
      const response = await authService.register(userData);
      
      const newUser: User = {
        id: 0,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        role: response.role,
        enabled: true,
      };

      localStorage.setItem('baby-cash-user', JSON.stringify(newUser));
      setUser(newUser);
      toast.success('¡Cuenta creada exitosamente!');
    } catch (error) {
      toast.error('Error al registrarse');
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // ========== LOGOUT ==========
  const logout = async () => {
    try {
      await authService.logout();
      setUser(null);
      localStorage.removeItem('baby-cash-user');
      toast.success('Sesión cerrada');
    } catch (error) {
      console.error('Error al cerrar sesión:', error);
    }
  };

  // ========== COMPUTED VALUES ==========
  const isAuthenticated = !!user && authService.isAuthenticated();
  const isAdmin = user?.role === 'ADMIN';
  const isModerator = user?.role === 'MODERATOR' || isAdmin;

  // ========== MEMOIZAR VALUE ==========
  // Evita re-renders innecesarios
  const value = useMemo(
    () => ({
      user,
      loading,
      login,
      register,
      logout,
      isAuthenticated,
      isAdmin,
      isModerator,
    }),
    [user, loading, isAuthenticated, isAdmin, isModerator]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// ========== 4. CUSTOM HOOK ==========
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
};
```

### Uso en Componentes

```tsx
// Login.tsx
import { useAuth } from '../contexts/AuthContext';

export const Login = () => {
  const { login, loading, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await login(email, password);
      navigate('/');
    } catch (error) {
      // Error ya manejado en AuthContext
    }
  };

  if (isAuthenticated) {
    return <Navigate to="/" />;
  }

  return (
    <form onSubmit={handleSubmit}>
      {/* Form fields */}
      <button disabled={loading}>
        {loading ? 'Cargando...' : 'Iniciar Sesión'}
      </button>
    </form>
  );
};
```

```tsx
// Navbar.tsx
export const Navbar = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();

  return (
    <nav>
      {isAuthenticated ? (
        <>
          <span>Hola, {user?.firstName}</span>
          {isAdmin && <Link to="/admin">Admin Panel</Link>}
          <button onClick={logout}>Cerrar Sesión</button>
        </>
      ) : (
        <>
          <Link to="/login">Login</Link>
          <Link to="/register">Registrarse</Link>
        </>
      )}
    </nav>
  );
};
```

---

## 🛒 CartContext en Baby Cash

### Estructura con Optimistic Updates

```tsx
// src/contexts/CartContext.tsx

import { createContext, useState, useContext, useEffect, useRef } from 'react';
import { cartService } from '../services/api';
import { useAuth } from './AuthContext';
import toast from 'react-hot-toast';

export interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  category?: string;
  image?: string;
}

export interface CartContextType {
  items: CartItem[];
  addToCart: (item: CartItem) => void;
  updateQuantity: (id: string, quantity: number) => void;
  removeFromCart: (id: string) => void;
  clearCart: () => void;
  getTotalPrice: () => number;
  getTotalItems: () => number;
  loading: boolean;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [items, setItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(false);
  const { isAuthenticated } = useAuth();
  
  // Prevenir toast duplicado
  const lastToastTime = useRef<number>(0);
  const lastToastItem = useRef<string>('');

  // ========== CARGAR CARRITO ==========
  useEffect(() => {
    const loadCart = async () => {
      if (isAuthenticated) {
        try {
          setLoading(true);
          // Cargar desde backend
          const backendCart = await cartService.getCart();
          const cartItems: CartItem[] = backendCart.items.map(item => ({
            id: String(item.productId),
            name: item.product?.name || 'Producto',
            price: item.product?.price || 0,
            quantity: item.quantity,
            image: item.product?.imageUrl,
          }));
          setItems(cartItems);
        } catch (error) {
          console.error('Error al cargar carrito:', error);
          loadFromLocalStorage();
        } finally {
          setLoading(false);
        }
      } else {
        loadFromLocalStorage();
      }
    };

    loadCart();
  }, [isAuthenticated]);

  // ========== SINCRONIZAR CON LOCALSTORAGE ==========
  useEffect(() => {
    if (items.length > 0) {
      localStorage.setItem('baby-cash-cart', JSON.stringify(items));
    } else {
      localStorage.removeItem('baby-cash-cart');
    }
  }, [items]);

  const loadFromLocalStorage = () => {
    const savedCart = localStorage.getItem('baby-cash-cart');
    if (savedCart) {
      try {
        setItems(JSON.parse(savedCart));
      } catch (error) {
        console.error('Error al parsear carrito:', error);
      }
    }
  };

  // ========== AGREGAR AL CARRITO (OPTIMISTIC UPDATE) ==========
  const addToCart = (item: CartItem) => {
    const existing = items.find((i) => i.id === item.id);
    
    // 1. Actualizar UI INMEDIATAMENTE (optimistic)
    if (existing) {
      setItems(
        items.map((i) => 
          i.id === item.id 
            ? { ...i, quantity: i.quantity + item.quantity } 
            : i
        )
      );
    } else {
      setItems([...items, item]);
    }

    // 2. Toast (evitar duplicados)
    const now = Date.now();
    if (
      now - lastToastTime.current > 500 || 
      lastToastItem.current !== item.id
    ) {
      toast.success(`${item.name} agregado al carrito`);
      lastToastTime.current = now;
      lastToastItem.current = item.id;
    }

    // 3. Sincronizar con backend (si está autenticado)
    if (isAuthenticated) {
      cartService
        .addToCart(Number(item.id), item.quantity)
        .catch(error => {
          console.error('Error al sincronizar carrito:', error);
          // Revertir cambio optimista
          if (existing) {
            setItems(
              items.map((i) => 
                i.id === item.id 
                  ? { ...i, quantity: i.quantity - item.quantity } 
                  : i
              )
            );
          } else {
            setItems(items.filter(i => i.id !== item.id));
          }
          toast.error('Error al agregar al carrito');
        });
    }
  };

  // ========== ACTUALIZAR CANTIDAD ==========
  const updateQuantity = (id: string, quantity: number) => {
    if (quantity <= 0) {
      removeFromCart(id);
      return;
    }

    // Optimistic update
    setItems(items.map((item) => (item.id === id ? { ...item, quantity } : item)));

    // Sincronizar con backend
    if (isAuthenticated) {
      cartService
        .updateCartItem(Number(id), quantity)
        .catch(error => {
          console.error('Error al actualizar cantidad:', error);
          toast.error('Error al actualizar cantidad');
        });
    }
  };

  // ========== REMOVER DEL CARRITO ==========
  const removeFromCart = (id: string) => {
    const item = items.find(i => i.id === id);
    
    // Optimistic update
    setItems(items.filter((item) => item.id !== id));
    
    if (item) {
      toast.success(`${item.name} removido del carrito`);
    }

    // Sincronizar con backend
    if (isAuthenticated) {
      cartService
        .removeFromCart(Number(id))
        .catch(error => {
          console.error('Error al remover del carrito:', error);
          // Revertir
          if (item) {
            setItems([...items]);
          }
        });
    }
  };

  // ========== VACIAR CARRITO ==========
  const clearCart = () => {
    setItems([]);
    toast.success('Carrito vaciado');

    if (isAuthenticated) {
      cartService.clearCart().catch(console.error);
    }
  };

  // ========== CÁLCULOS ==========
  const getTotalPrice = () => {
    return items.reduce((total, item) => total + item.price * item.quantity, 0);
  };

  const getTotalItems = () => {
    return items.reduce((total, item) => total + item.quantity, 0);
  };

  // ========== MEMOIZAR VALUE ==========
  const value = useMemo(
    () => ({
      items,
      addToCart,
      updateQuantity,
      removeFromCart,
      clearCart,
      getTotalPrice,
      getTotalItems,
      loading,
    }),
    [items, loading]
  );

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};

// ========== CUSTOM HOOK ==========
export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart debe usarse dentro de CartProvider');
  }
  return context;
};
```

### Uso en Componentes

```tsx
// ProductCard.tsx
export const ProductCard = ({ product }: { product: Product }) => {
  const { addToCart } = useCart();

  const handleAddToCart = () => {
    addToCart({
      id: String(product.id),
      name: product.name,
      price: product.price,
      quantity: 1,
      image: product.imageUrl,
    });
  };

  return (
    <div className="product-card">
      <img src={product.imageUrl} alt={product.name} />
      <h3>{product.name}</h3>
      <p>${product.price.toLocaleString()}</p>
      <button onClick={handleAddToCart}>Agregar al Carrito</button>
    </div>
  );
};
```

```tsx
// CartBadge.tsx
export const CartBadge = () => {
  const { getTotalItems } = useCart();
  const totalItems = getTotalItems();

  return (
    <div className="cart-badge">
      <ShoppingCart />
      {totalItems > 0 && <span className="badge">{totalItems}</span>}
    </div>
  );
};
```

---

## 🔧 Optimizaciones Avanzadas

### 1️⃣ useMemo para Value

**Problema:** Context re-renderiza consumidores cuando value cambia.

```tsx
// ❌ MAL: Crea nuevo objeto en cada render
<AuthContext.Provider value={{ user, login, logout }}>

// ✅ BIEN: Memoiza el value
const value = useMemo(
  () => ({ user, login, logout }),
  [user] // Solo recrea si user cambia
);
<AuthContext.Provider value={value}>
```

---

### 2️⃣ Separar Contextos por Dominio

**Problema:** Un contexto gigante re-renderiza todo.

```tsx
// ❌ MAL: Un contexto para todo
const AppContext = createContext({
  user,
  cart,
  theme,
  notifications,
  // ... 20 propiedades más
});

// ✅ BIEN: Contextos separados
<AuthProvider>
  <CartProvider>
    <ThemeProvider>
      <App />
    </ThemeProvider>
  </CartProvider>
</AuthProvider>
```

---

### 3️⃣ Lazy Initialization

**Problema:** Estado inicial costoso se calcula en cada render.

```tsx
// ❌ MAL: JSON.parse en cada render
const [cart, setCart] = useState(JSON.parse(localStorage.getItem('cart')));

// ✅ BIEN: Lazy initialization
const [cart, setCart] = useState(() => {
  const saved = localStorage.getItem('cart');
  return saved ? JSON.parse(saved) : [];
});
```

---

### 4️⃣ useCallback para Funciones

**Problema:** Funciones nuevas en cada render rompen memoización.

```tsx
// ❌ MAL: Nueva función cada vez
const value = useMemo(
  () => ({
    user,
    login: (email, password) => { ... }, // Nueva función cada render
  }),
  [user]
);

// ✅ BIEN: useCallback
const login = useCallback((email: string, password: string) => {
  // ...
}, []); // Memoizada

const value = useMemo(
  () => ({ user, login }),
  [user, login]
);
```

---

### 5️⃣ Context Selector Pattern

**Problema:** Consumir todo el contexto re-renderiza aunque solo uses una propiedad.

```tsx
// ❌ MAL: Re-renderiza cuando user cambia (aunque solo necesitamos isAdmin)
const { isAdmin } = useAuth();

// ✅ MEJOR: Separar contextos granulares
const AuthStateContext = createContext({ user, loading });
const AuthActionsContext = createContext({ login, logout });

export const useAuthState = () => useContext(AuthStateContext);
export const useAuthActions = () => useContext(AuthActionsContext);

// Usar
const { isAdmin } = useAuthState(); // Solo re-renderiza si state cambia
const { login } = useAuthActions(); // Nunca re-renderiza (funciones estables)
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es Context API y cuándo usarla?"**

> "Context API permite compartir estado entre componentes sin prop drilling:
> 
> **Usar Context cuando:**
> - Estado global (user, theme, language)
> - Múltiples componentes necesitan el mismo dato
> - Prop drilling se vuelve tedioso
> 
> **NO usar Context cuando:**
> - Estado local (solo un componente)
> - Performance crítica (muchos re-renders)
> - App muy grande (considerar Redux/Zustand)
> 
> Baby Cash:
> - `AuthContext`: Usuario actual (muchos componentes lo necesitan)
> - `CartContext`: Carrito de compras (navbar, carrito, checkout)
> 
> ```tsx
> <AuthProvider>
>   <Navbar />  {/* usa useAuth() */}
>   <ProductList />  {/* usa useAuth() para admin */}
> </AuthProvider>
> ```"

---

**2. "¿Por qué memoizar el value del Context?"**

> "Sin memoización, Context crea nuevo objeto en cada render:
> 
> ```tsx
> // ❌ MAL: Nuevo objeto cada vez
> <Context.Provider value={{ user, login }}>
> // Todos los consumidores re-renderizan
> 
> // ✅ BIEN: Memoizado
> const value = useMemo(() => ({ user, login }), [user, login]);
> <Context.Provider value={value}>
> // Solo re-renderizan si user o login cambian
> ```
> 
> **Beneficio:** Evita re-renders innecesarios, mejora performance."

---

**3. "¿Qué es Optimistic Update?"**

> "Optimistic Update actualiza UI ANTES de confirmar con backend:
> 
> **Flujo tradicional:**
> 1. Usuario hace click
> 2. Llamada backend (espera 200ms)
> 3. Actualizar UI
> 4. ❌ Lento (usuario espera)
> 
> **Optimistic Update:**
> 1. Usuario hace click
> 2. Actualizar UI INMEDIATAMENTE ✅ Rápido
> 3. Llamada backend en background
> 4. Si falla → revertir cambio
> 
> Baby Cash CartContext:
> ```tsx
> const addToCart = (item) => {
>   // 1. Actualizar UI YA
>   setItems([...items, item]);
>   
>   // 2. Sincronizar con backend
>   cartService.addToCart(item).catch(() => {
>     // 3. Revertir si falla
>     setItems(items.filter(i => i.id !== item.id));
>   });
> };
> ```"

---

**4. "¿Cuándo usar useState vs useReducer en Context?"**

> "**useState:**
> - Estado simple
> - Pocas actualizaciones
> - Lógica straightforward
> 
> **useReducer:**
> - Estado complejo
> - Muchas acciones diferentes
> - Lógica centralizada
> 
> Ejemplo:
> ```tsx
> // useState (simple)
> const [user, setUser] = useState(null);
> 
> // useReducer (complejo)
> const cartReducer = (state, action) => {
>   switch (action.type) {
>     case 'ADD_ITEM': return { ...state, items: [...state.items, action.item] };
>     case 'REMOVE_ITEM': return { ...state, items: state.items.filter(...) };
>     case 'UPDATE_QUANTITY': return { ...state, items: state.items.map(...) };
>     case 'CLEAR': return { items: [], total: 0 };
>     default: return state;
>   }
> };
> const [state, dispatch] = useReducer(cartReducer, initialState);
> ```
> 
> Baby Cash usa useState porque el estado no es tan complejo."

---

## 📝 Resumen de Contexts en Baby Cash

| Context | Estado | Acciones | Re-renders Optimizados | Sincronización |
|---------|--------|----------|------------------------|----------------|
| `AuthContext` | user, loading | login, register, logout | useMemo | localStorage |
| `CartContext` | items, loading | addToCart, updateQuantity, removeFromCart | useMemo, optimistic updates | localStorage + backend |

---

## 🚀 Conclusión

**Context API en Baby Cash:**
- ✅ Evita prop drilling
- ✅ Estado global accesible desde cualquier componente
- ✅ Optimizado con useMemo/useCallback
- ✅ Optimistic updates para mejor UX
- ✅ Sincronización con backend y localStorage

**Patrones clave:**
1. Memoizar value del Context
2. Separar contextos por dominio
3. Custom hooks para consumir (useAuth, useCart)
4. Optimistic updates para operaciones rápidas

---

**Ahora lee:** `PERFORMANCE-OPTIMIZACION.md` para hacer tu app más rápida. 🚀
