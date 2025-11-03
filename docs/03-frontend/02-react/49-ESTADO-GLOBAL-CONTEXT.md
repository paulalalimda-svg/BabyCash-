================================================================================
ARCHIVO 49: ESTADO GLOBAL CON CONTEXT API
================================================================================

QUE ES EL ESTADO GLOBAL
========================

Hasta ahora hemos visto estado local (useState dentro de un componente).
Pero hay datos que MUCHOS componentes necesitan compartir.

EJEMPLOS:
- Usuario logueado
- Carrito de compras
- Tema (light/dark)
- Idioma

ANALOGIA:
Es como una pizarra que todos en el salon pueden ver.
En vez de pasar informacion de persona en persona (props),
todos miran la misma pizarra (Context).


PROBLEMA: PROPS DRILLING
=========================

Sin estado global:

App
 └── Header (necesita user)
      └── Navbar (necesita user)
           └── UserMenu (necesita user)

Debes pasar user como prop a traves de 3 componentes.

function App() {
  const [user, setUser] = useState(null)
  return <Header user={user} />
}

function Header({ user }) {
  return <Navbar user={user} />
}

function Navbar({ user }) {
  return <UserMenu user={user} />
}

function UserMenu({ user }) {
  return <div>{user?.name}</div>
}

PROBLEMA: Header y Navbar NO usan user, solo lo pasan.
Esto es "props drilling" (perforar props).


SOLUCION: CONTEXT API
======================

Con Context, cualquier componente puede acceder al user directamente.

App (provee user)
 └── Header
      └── Navbar
           └── UserMenu (consume user)

UserMenu accede directo al user, sin pasar por Header y Navbar.


COMO CREAR UN CONTEXT
======================

PASO 1: Crear el Context
-------------------------
import { createContext } from 'react'

const UserContext = createContext(null)


PASO 2: Crear el Provider
--------------------------
function UserProvider({ children }) {
  const [user, setUser] = useState(null)
  
  return (
    <UserContext.Provider value={{ user, setUser }}>
      {children}
    </UserContext.Provider>
  )
}


PASO 3: Envolver la app con el Provider
----------------------------------------
import { UserProvider } from './contexts/UserContext'

function App() {
  return (
    <UserProvider>
      <Header />
      <Main />
      <Footer />
    </UserProvider>
  )
}


PASO 4: Consumir el Context
----------------------------
import { useContext } from 'react'
import { UserContext } from './contexts/UserContext'

function UserMenu() {
  const { user } = useContext(UserContext)
  
  return <div>{user?.name}</div>
}


EJEMPLO COMPLETO: THEME CONTEXT
================================

// contexts/ThemeContext.jsx
import { createContext, useContext, useState } from 'react'

const ThemeContext = createContext()

export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState('light')
  
  const toggleTheme = () => {
    setTheme(prev => prev === 'light' ? 'dark' : 'light')
  }
  
  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  )
}

export function useTheme() {
  const context = useContext(ThemeContext)
  if (!context) {
    throw new Error('useTheme debe usarse dentro de ThemeProvider')
  }
  return context
}


// App.jsx
import { ThemeProvider } from './contexts/ThemeContext'

function App() {
  return (
    <ThemeProvider>
      <Header />
      <Main />
    </ThemeProvider>
  )
}


// Header.jsx
import { useTheme } from './contexts/ThemeContext'

function Header() {
  const { theme, toggleTheme } = useTheme()
  
  return (
    <header className={theme === 'dark' ? 'bg-gray-900 text-white' : 'bg-white'}>
      <button onClick={toggleTheme}>
        Cambiar a {theme === 'dark' ? 'Light' : 'Dark'}
      </button>
    </header>
  )
}


AUTH CONTEXT EN BABYCASH
=========================

// contexts/AuthContext.tsx
import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import { authAPI } from '../services/api'

interface User {
  id: number
  nombre: string
  email: string
  role: 'USER' | 'ADMIN' | 'MODERATOR'
  telefono?: string
  direccion?: string
}

interface AuthContextType {
  user: User | null
  token: string | null
  login: (email: string, password: string) => Promise<void>
  register: (data: RegisterData) => Promise<void>
  logout: () => void
  isAuthenticated: boolean
  isAdmin: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  
  // Cargar usuario del localStorage al montar
  useEffect(() => {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')
    
    if (storedToken && storedUser) {
      setToken(storedToken)
      setUser(JSON.parse(storedUser))
    }
    
    setLoading(false)
  }, [])
  
  // Login
  const login = async (email: string, password: string) => {
    const response = await authAPI.login(email, password)
    
    setToken(response.token)
    setUser(response.user)
    
    localStorage.setItem('token', response.token)
    localStorage.setItem('user', JSON.stringify(response.user))
  }
  
  // Register
  const register = async (data: RegisterData) => {
    const response = await authAPI.register(data)
    
    setToken(response.token)
    setUser(response.user)
    
    localStorage.setItem('token', response.token)
    localStorage.setItem('user', JSON.stringify(response.user))
  }
  
  // Logout
  const logout = () => {
    setUser(null)
    setToken(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  const value = {
    user,
    token,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN' || user?.role === 'MODERATOR'
  }
  
  if (loading) {
    return <div>Cargando...</div>
  }
  
  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

// Custom hook
export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth debe usarse dentro de AuthProvider')
  }
  return context
}


USAR AUTH CONTEXT EN COMPONENTES
=================================

// Login.tsx
import { useAuth } from '../contexts/AuthContext'
import { useNavigate } from 'react-router-dom'

function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await login(email, password)
      navigate('/')
    } catch (error) {
      alert('Error al iniciar sesion')
    }
  }
  
  return (
    <form onSubmit={handleSubmit}>
      {/* ... */}
    </form>
  )
}


// Navbar.tsx
import { useAuth } from '../contexts/AuthContext'
import { useNavigate } from 'react-router-dom'

function Navbar() {
  const { user, isAuthenticated, logout } = useAuth()
  const navigate = useNavigate()
  
  const handleLogout = () => {
    logout()
    navigate('/login')
  }
  
  return (
    <nav>
      {isAuthenticated ? (
        <div>
          <span>Hola, {user.nombre}</span>
          <button onClick={handleLogout}>Cerrar sesion</button>
        </div>
      ) : (
        <a href="/login">Iniciar sesion</a>
      )}
    </nav>
  )
}


// ProtectedRoute.tsx
import { useAuth } from '../contexts/AuthContext'
import { Navigate } from 'react-router-dom'

function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth()
  
  if (!isAuthenticated) {
    return <Navigate to="/login" />
  }
  
  return children
}


CART CONTEXT
============

// contexts/CartContext.tsx
import { createContext, useContext, useState, useEffect, ReactNode } from 'react'

interface CartItem {
  productId: number
  nombre: string
  precio: number
  cantidad: number
  imagen: string
}

interface CartContextType {
  items: CartItem[]
  addItem: (product: Product) => void
  removeItem: (productId: number) => void
  updateQuantity: (productId: number, cantidad: number) => void
  clearCart: () => void
  total: number
  itemCount: number
}

const CartContext = createContext<CartContextType | undefined>(undefined)

export function CartProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<CartItem[]>([])
  
  // Cargar carrito del localStorage
  useEffect(() => {
    const storedCart = localStorage.getItem('cart')
    if (storedCart) {
      setItems(JSON.parse(storedCart))
    }
  }, [])
  
  // Guardar carrito en localStorage cuando cambia
  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(items))
  }, [items])
  
  // Agregar item
  const addItem = (product: Product) => {
    setItems(prev => {
      // Verificar si ya existe
      const existingItem = prev.find(item => item.productId === product.id)
      
      if (existingItem) {
        // Incrementar cantidad
        return prev.map(item =>
          item.productId === product.id
            ? { ...item, cantidad: item.cantidad + 1 }
            : item
        )
      } else {
        // Agregar nuevo item
        return [...prev, {
          productId: product.id,
          nombre: product.nombre,
          precio: product.precio,
          cantidad: 1,
          imagen: product.imagen
        }]
      }
    })
  }
  
  // Eliminar item
  const removeItem = (productId: number) => {
    setItems(prev => prev.filter(item => item.productId !== productId))
  }
  
  // Actualizar cantidad
  const updateQuantity = (productId: number, cantidad: number) => {
    if (cantidad <= 0) {
      removeItem(productId)
    } else {
      setItems(prev =>
        prev.map(item =>
          item.productId === productId
            ? { ...item, cantidad }
            : item
        )
      )
    }
  }
  
  // Limpiar carrito
  const clearCart = () => {
    setItems([])
  }
  
  // Calcular total
  const total = items.reduce((sum, item) => sum + item.precio * item.cantidad, 0)
  
  // Contar items
  const itemCount = items.reduce((sum, item) => sum + item.cantidad, 0)
  
  const value = {
    items,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    total,
    itemCount
  }
  
  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  )
}

export function useCart() {
  const context = useContext(CartContext)
  if (context === undefined) {
    throw new Error('useCart debe usarse dentro de CartProvider')
  }
  return context
}


USAR CART CONTEXT
=================

// ProductCard.tsx
import { useCart } from '../contexts/CartContext'

function ProductCard({ product }) {
  const { addItem } = useCart()
  
  return (
    <div className="border rounded-lg p-4">
      <img src={product.imagen} alt={product.nombre} />
      <h3>{product.nombre}</h3>
      <p>${product.precio}</p>
      <button onClick={() => addItem(product)}>
        Agregar al carrito
      </button>
    </div>
  )
}


// Navbar.tsx
import { useCart } from '../contexts/CartContext'

function Navbar() {
  const { itemCount } = useCart()
  
  return (
    <nav>
      <a href="/carrito">
        Carrito ({itemCount})
      </a>
    </nav>
  )
}


// Carrito.tsx
import { useCart } from '../contexts/CartContext'

function Carrito() {
  const { items, removeItem, updateQuantity, total, clearCart } = useCart()
  
  if (items.length === 0) {
    return <p>Tu carrito esta vacio</p>
  }
  
  return (
    <div>
      <h1>Tu carrito</h1>
      
      {items.map(item => (
        <div key={item.productId} className="flex items-center gap-4 border-b py-4">
          <img src={item.imagen} alt={item.nombre} className="w-20 h-20 object-cover" />
          <div className="flex-1">
            <h3>{item.nombre}</h3>
            <p>${item.precio}</p>
          </div>
          <div className="flex items-center gap-2">
            <button onClick={() => updateQuantity(item.productId, item.cantidad - 1)}>
              -
            </button>
            <span>{item.cantidad}</span>
            <button onClick={() => updateQuantity(item.productId, item.cantidad + 1)}>
              +
            </button>
          </div>
          <button onClick={() => removeItem(item.productId)}>
            Eliminar
          </button>
        </div>
      ))}
      
      <div className="mt-6">
        <p className="text-2xl font-bold">Total: ${total}</p>
        <button className="bg-blue-500 text-white px-6 py-2 rounded mt-4">
          Proceder al pago
        </button>
        <button onClick={clearCart} className="ml-4">
          Vaciar carrito
        </button>
      </div>
    </div>
  )
}


MULTIPLES PROVIDERS
===================

Si tienes muchos Contexts, puedes anidarlos:

function App() {
  return (
    <AuthProvider>
      <ThemeProvider>
        <CartProvider>
          <Router>
            <Routes>
              {/* ... */}
            </Routes>
          </Router>
        </CartProvider>
      </ThemeProvider>
    </AuthProvider>
  )
}


O crear un componente AppProviders:

function AppProviders({ children }) {
  return (
    <AuthProvider>
      <ThemeProvider>
        <CartProvider>
          {children}
        </CartProvider>
      </ThemeProvider>
    </AuthProvider>
  )
}

function App() {
  return (
    <AppProviders>
      <Router>
        <Routes>
          {/* ... */}
        </Routes>
      </Router>
    </AppProviders>
  )
}


CONTEXT CON REDUCER
===================

Para estado complejo, combina Context con useReducer:

// cartReducer.js
export const cartReducer = (state, action) => {
  switch (action.type) {
    case 'ADD_ITEM':
      // Logica para agregar item
      return { ...state, items: [...state.items, action.payload] }
    
    case 'REMOVE_ITEM':
      return {
        ...state,
        items: state.items.filter(item => item.id !== action.payload)
      }
    
    case 'UPDATE_QUANTITY':
      return {
        ...state,
        items: state.items.map(item =>
          item.id === action.payload.id
            ? { ...item, cantidad: action.payload.cantidad }
            : item
        )
      }
    
    case 'CLEAR_CART':
      return { ...state, items: [] }
    
    default:
      return state
  }
}


// CartContext.jsx
import { useReducer } from 'react'
import { cartReducer } from './cartReducer'

const initialState = {
  items: [],
  loading: false,
  error: null
}

export function CartProvider({ children }) {
  const [state, dispatch] = useReducer(cartReducer, initialState)
  
  const addItem = (product) => {
    dispatch({ type: 'ADD_ITEM', payload: product })
  }
  
  const removeItem = (id) => {
    dispatch({ type: 'REMOVE_ITEM', payload: id })
  }
  
  return (
    <CartContext.Provider value={{ state, addItem, removeItem }}>
      {children}
    </CartContext.Provider>
  )
}


CUANDO USAR CONTEXT
===================

USA CONTEXT PARA:
✓ Usuario autenticado
✓ Carrito de compras
✓ Tema (dark/light)
✓ Idioma
✓ Configuracion global

NO USES CONTEXT PARA:
✗ Estado local de un componente
✗ Estado que cambia muy rapido (cada tecla presionada)
✗ Estado que solo usan 2-3 componentes cercanos (usa props)


OPTIMIZACION DE CONTEXT
========================

PROBLEMA: Cuando el Context cambia, TODOS los componentes que lo usan se re-renderan.

SOLUCION 1: Dividir en multiples Contexts
------------------------------------------
// Antes
const UserContext = { user, setUser, theme, setTheme }

// Despues
const UserContext = { user, setUser }
const ThemeContext = { theme, setTheme }


SOLUCION 2: useMemo en el value
--------------------------------
export function UserProvider({ children }) {
  const [user, setUser] = useState(null)
  
  const value = useMemo(() => ({ user, setUser }), [user])
  
  return (
    <UserContext.Provider value={value}>
      {children}
    </UserContext.Provider>
  )
}


SOLUCION 3: React.memo en componentes
--------------------------------------
const UserMenu = React.memo(function UserMenu() {
  const { user } = useUser()
  return <div>{user?.name}</div>
})


PATRON: SEPARAR DATOS Y ACCIONES
=================================

// Dividir en 2 Contexts
const UserDataContext = createContext()
const UserActionsContext = createContext()

export function UserProvider({ children }) {
  const [user, setUser] = useState(null)
  
  const login = (email, password) => { /* ... */ }
  const logout = () => { /* ... */ }
  
  return (
    <UserDataContext.Provider value={user}>
      <UserActionsContext.Provider value={{ login, logout }}>
        {children}
      </UserActionsContext.Provider>
    </UserDataContext.Provider>
  )
}

// Hooks separados
export function useUserData() {
  return useContext(UserDataContext)
}

export function useUserActions() {
  return useContext(UserActionsContext)
}


// Ahora los componentes solo se re-renderan si necesitan los datos
function UserName() {
  const user = useUserData()  // Se re-renderiza si user cambia
  return <div>{user?.name}</div>
}

function LogoutButton() {
  const { logout } = useUserActions()  // NO se re-renderiza
  return <button onClick={logout}>Logout</button>
}


PERSISTIR CONTEXT EN LOCALSTORAGE
==================================

export function CartProvider({ children }) {
  const [items, setItems] = useState(() => {
    // Cargar del localStorage al iniciar
    const storedCart = localStorage.getItem('cart')
    return storedCart ? JSON.parse(storedCart) : []
  })
  
  // Guardar en localStorage cuando cambie
  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(items))
  }, [items])
  
  return (
    <CartContext.Provider value={{ items, setItems }}>
      {children}
    </CartContext.Provider>
  )
}


ALTERNATIVAS A CONTEXT
=======================

Si Context se vuelve muy complejo, considera:

1. ZUSTAND (mas simple)
-----------------------
import create from 'zustand'

const useStore = create((set) => ({
  user: null,
  setUser: (user) => set({ user })
}))

function Component() {
  const user = useStore(state => state.user)
  return <div>{user?.name}</div>
}


2. REDUX (mas robusto)
----------------------
Para aplicaciones grandes con estado muy complejo


3. REACT QUERY (para datos de servidor)
----------------------------------------
import { useQuery } from 'react-query'

function Component() {
  const { data: user } = useQuery('user', fetchUser)
  return <div>{user?.name}</div>
}


ESTRUCTURA RECOMENDADA
=======================

/src
  /contexts
    AuthContext.tsx
    CartContext.tsx
    ThemeContext.tsx
  /hooks
    useAuth.ts  (hook personalizado)
    useCart.ts
    useTheme.ts


ERRORES COMUNES
===============

1. NO ENVOLVER CON PROVIDER
   ✗ useContext sin Provider
   ✓ Envolver App con Provider

2. CREAR CONTEXT EN CADA RENDER
   ✗ const Context = createContext() dentro del componente
   ✓ Crear Context fuera del componente

3. NO USAR useMemo EN VALUE
   ✗ value={{ user, setUser }}
   ✓ value={useMemo(() => ({ user, setUser }), [user])}

4. PONER TODO EN UN SOLO CONTEXT
   ✗ Un Context gigante con todo el estado
   ✓ Multiples Contexts pequenos

5. USAR CONTEXT PARA TODO
   ✗ Hasta estado local en Context
   ✓ Solo para estado global necesario


PATRON COMPLETO: AUTH CONTEXT
==============================

// contexts/AuthContext.tsx
import { 
  createContext, 
  useContext, 
  useState, 
  useEffect, 
  useMemo,
  ReactNode 
} from 'react'
import { authAPI } from '../services/api'

interface User {
  id: number
  nombre: string
  email: string
  role: 'USER' | 'ADMIN' | 'MODERATOR'
}

interface AuthContextType {
  user: User | null
  token: string | null
  login: (email: string, password: string) => Promise<void>
  logout: () => void
  isAuthenticated: boolean
  isAdmin: boolean
  loading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  
  // Cargar del localStorage
  useEffect(() => {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')
    
    if (storedToken && storedUser) {
      setToken(storedToken)
      setUser(JSON.parse(storedUser))
    }
    
    setLoading(false)
  }, [])
  
  // Login
  const login = async (email: string, password: string) => {
    const response = await authAPI.login(email, password)
    
    setToken(response.token)
    setUser(response.user)
    
    localStorage.setItem('token', response.token)
    localStorage.setItem('user', JSON.stringify(response.user))
  }
  
  // Logout
  const logout = () => {
    setUser(null)
    setToken(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  // Optimizar value con useMemo
  const value = useMemo(() => ({
    user,
    token,
    login,
    logout,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN' || user?.role === 'MODERATOR',
    loading
  }), [user, token, loading])
  
  if (loading) {
    return <div className="flex items-center justify-center h-screen">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500" />
    </div>
  }
  
  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}

// Custom hook
export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth debe usarse dentro de AuthProvider')
  }
  return context
}


RESUMEN
=======

Context API:
✓ Estado global compartido entre componentes
✓ Evita props drilling
✓ Ideal para: auth, cart, theme, idioma

Crear Context:
1. createContext()
2. Provider component
3. useContext() para consumir

Patrones:
✓ Custom hooks (useAuth, useCart)
✓ Separar datos y acciones
✓ useMemo para optimizar
✓ Persistir en localStorage

En BabyCash:
✓ AuthContext: usuario, login, logout
✓ CartContext: items, addItem, removeItem, total
✓ Multiples providers anidados

Alternativas:
- Zustand (mas simple)
- Redux (mas complejo)
- React Query (datos servidor)

================================================================================
