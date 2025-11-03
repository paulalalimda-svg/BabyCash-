================================================================================
ARCHIVO 47: RUTAS - REACT ROUTER
================================================================================

QUE ES REACT ROUTER
====================

React Router es una libreria que permite crear aplicaciones de una sola pagina
(SPA - Single Page Application) con multiples "paginas" o vistas.

ANALOGIA:
Imagina un libro con muchos capitulos. React Router es como el indice que te
permite saltar entre capitulos sin cerrar el libro.

En una web tradicional, cada pagina es un archivo HTML diferente.
En React con Router, todo esta en un solo archivo HTML, pero React Router
cambia lo que se muestra segun la URL.


SPA (SINGLE PAGE APPLICATION)
==============================

TRADICIONAL:
/index.html           - Pagina de inicio
/productos.html       - Pagina de productos
/contacto.html        - Pagina de contacto

Cada vez que cambias de pagina, el navegador carga un nuevo archivo HTML.


CON REACT ROUTER (SPA):
/                     - Muestra componente Home
/productos            - Muestra componente Productos
/contacto             - Muestra componente Contacto

Todo esta en el mismo HTML, solo cambia el componente que se renderiza.

VENTAJAS:
- MAS RAPIDO (no recarga toda la pagina)
- EXPERIENCIA MAS FLUIDA
- FACIL AGREGAR ANIMACIONES entre paginas


INSTALACION
===========

npm install react-router-dom


CONFIGURACION BASICA EN BABYCASH
=================================

1. ENVOLVER LA APP CON BROWSERROUTER:
--------------------------------------
// main.tsx
import { BrowserRouter } from 'react-router-dom'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
)


2. DEFINIR LAS RUTAS:
---------------------
// AppRouter.tsx
import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Productos from './pages/Productos'
import Contacto from './pages/Contacto'

function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/productos" element={<Productos />} />
      <Route path="/contacto" element={<Contacto />} />
    </Routes>
  )
}


COMPONENTES PRINCIPALES
========================

1. BrowserRouter
   - Envuelve toda la aplicacion
   - Maneja el historial del navegador

2. Routes
   - Contenedor de todas las rutas
   - Solo uno se renderiza a la vez

3. Route
   - Define una ruta especifica
   - path: la URL
   - element: el componente a renderizar

4. Link
   - Para navegar entre paginas
   - Como un <a> pero sin recargar la pagina

5. NavLink
   - Como Link pero con clase activa
   - Para menus de navegacion

6. Navigate
   - Para redireccionar programaticamente


NAVEGACION CON LINK
====================

NO USAR <a href="..."> porque recarga la pagina.
USAR <Link to="..."> para navegacion SPA.


EJEMPLO:
--------
import { Link } from 'react-router-dom'

function Navbar() {
  return (
    <nav>
      <Link to="/">Inicio</Link>
      <Link to="/productos">Productos</Link>
      <Link to="/contacto">Contacto</Link>
      <Link to="/blog">Blog</Link>
    </nav>
  )
}


CON ESTILOS:
------------
<Link 
  to="/productos"
  className="text-blue-500 hover:text-blue-700"
>
  Productos
</Link>


NAVLINK - LINK CON CLASE ACTIVA
================================

NavLink agrega automaticamente una clase cuando la ruta esta activa.

EJEMPLO:
--------
import { NavLink } from 'react-router-dom'

function Navbar() {
  return (
    <nav className="flex gap-4">
      <NavLink 
        to="/"
        className={({ isActive }) => 
          isActive ? "text-blue-600 font-bold" : "text-gray-600"
        }
      >
        Inicio
      </NavLink>
      
      <NavLink 
        to="/productos"
        className={({ isActive }) => 
          isActive ? "text-blue-600 font-bold" : "text-gray-600"
        }
      >
        Productos
      </NavLink>
    </nav>
  )
}


ESTILO CON TAILWIND:
--------------------
<NavLink 
  to="/blog"
  className={({ isActive }) => 
    `px-4 py-2 rounded ${isActive 
      ? 'bg-blue-500 text-white' 
      : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
    }`
  }
>
  Blog
</NavLink>


PARAMETROS EN RUTAS
====================

Puedes pasar parametros en la URL.

DEFINIR RUTA CON PARAMETRO:
----------------------------
<Route path="/productos/:id" element={<ProductoDetalle />} />

:id es un parametro dinamico


ACCEDER AL PARAMETRO EN EL COMPONENTE:
---------------------------------------
import { useParams } from 'react-router-dom'

function ProductoDetalle() {
  const { id } = useParams()
  
  // Ahora puedes usar el id para cargar el producto
  useEffect(() => {
    fetch(`/api/products/${id}`)
      .then(res => res.json())
      .then(data => setProducto(data))
  }, [id])
  
  return <div>Producto ID: {id}</div>
}


EJEMPLO COMPLETO:
-----------------
// Definir rutas
<Routes>
  <Route path="/productos" element={<ProductosLista />} />
  <Route path="/productos/:id" element={<ProductoDetalle />} />
  <Route path="/blog/:slug" element={<BlogPost />} />
  <Route path="/usuarios/:userId/ordenes/:orderId" element={<OrdenDetalle />} />
</Routes>

// Navegar con Link
<Link to="/productos/123">Ver Producto 123</Link>
<Link to="/blog/mi-primer-post">Leer Post</Link>

// En el componente
function ProductoDetalle() {
  const { id } = useParams()  // id = "123"
}

function BlogPost() {
  const { slug } = useParams()  // slug = "mi-primer-post"
}

function OrdenDetalle() {
  const { userId, orderId } = useParams()
}


QUERY PARAMS (PARAMETROS DE BUSQUEDA)
======================================

Los query params van despues del ? en la URL.
/productos?categoria=bebes&precio=max

ACCEDER A QUERY PARAMS:
------------------------
import { useSearchParams } from 'react-router-dom'

function Productos() {
  const [searchParams, setSearchParams] = useSearchParams()
  
  // Obtener valores
  const categoria = searchParams.get('categoria')  // "bebes"
  const precio = searchParams.get('precio')        // "max"
  
  // Cambiar query params
  const filtrar = (nuevaCategoria) => {
    setSearchParams({ categoria: nuevaCategoria })
  }
  
  return (
    <div>
      <p>Categoria: {categoria}</p>
      <p>Precio: {precio}</p>
      <button onClick={() => filtrar('juguetes')}>
        Ver Juguetes
      </button>
    </div>
  )
}


NAVEGACION PROGRAMATICA
========================

A veces necesitas navegar desde JavaScript (despues de un login, al enviar
un formulario, etc).

USAR useNavigate:
-----------------
import { useNavigate } from 'react-router-dom'

function Login() {
  const navigate = useNavigate()
  
  const handleLogin = async (email, password) => {
    try {
      await login(email, password)
      // Navegar al dashboard despues de login exitoso
      navigate('/dashboard')
    } catch (error) {
      alert('Error en login')
    }
  }
  
  return (
    <form onSubmit={handleLogin}>
      {/* formulario */}
    </form>
  )
}


NAVEGAR CON REEMPLAZO DE HISTORIAL:
------------------------------------
navigate('/dashboard', { replace: true })

Esto reemplaza la entrada actual en el historial.
El usuario no puede volver atras con el boton del navegador.


NAVEGAR HACIA ATRAS:
---------------------
navigate(-1)  // Volver una pagina atras
navigate(-2)  // Volver dos paginas atras


CON STATE:
----------
navigate('/perfil', { state: { from: 'login' } })

// En el componente destino:
import { useLocation } from 'react-router-dom'

function Perfil() {
  const location = useLocation()
  const from = location.state?.from  // "login"
}


RUTAS ANIDADAS
==============

Las rutas anidadas permiten tener layouts compartidos.

EJEMPLO:
--------
<Routes>
  <Route path="/dashboard" element={<DashboardLayout />}>
    <Route index element={<DashboardHome />} />
    <Route path="productos" element={<DashboardProductos />} />
    <Route path="ordenes" element={<DashboardOrdenes />} />
    <Route path="usuarios" element={<DashboardUsuarios />} />
  </Route>
</Routes>


LAYOUT COMPONENT:
-----------------
import { Outlet } from 'react-router-dom'

function DashboardLayout() {
  return (
    <div className="flex">
      <aside className="w-64 bg-gray-800 text-white">
        <nav>
          <Link to="/dashboard">Home</Link>
          <Link to="/dashboard/productos">Productos</Link>
          <Link to="/dashboard/ordenes">Ordenes</Link>
        </nav>
      </aside>
      
      <main className="flex-1 p-6">
        <Outlet />  {/* Aqui se renderizan las rutas hijas */}
      </main>
    </div>
  )
}

URLs resultantes:
/dashboard                  -> DashboardHome
/dashboard/productos        -> DashboardProductos
/dashboard/ordenes          -> DashboardOrdenes


RUTAS PROTEGIDAS
================

Rutas que solo pueden acceder usuarios autenticados.

CREAR COMPONENTE ProtectedRoute:
---------------------------------
import { Navigate } from 'react-router-dom'

function ProtectedRoute({ children }) {
  const token = localStorage.getItem('token')
  
  if (!token) {
    // Redirigir a login si no esta autenticado
    return <Navigate to="/login" replace />
  }
  
  return children
}


USAR EN RUTAS:
--------------
<Routes>
  <Route path="/login" element={<Login />} />
  
  <Route 
    path="/dashboard" 
    element={
      <ProtectedRoute>
        <Dashboard />
      </ProtectedRoute>
    } 
  />
  
  <Route 
    path="/perfil" 
    element={
      <ProtectedRoute>
        <Perfil />
      </ProtectedRoute>
    } 
  />
</Routes>


CON ROLES:
----------
function ProtectedRoute({ children, requiredRole }) {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  
  if (!user.token) {
    return <Navigate to="/login" />
  }
  
  if (requiredRole && user.role !== requiredRole) {
    return <Navigate to="/no-autorizado" />
  }
  
  return children
}

// Uso:
<Route 
  path="/admin" 
  element={
    <ProtectedRoute requiredRole="ADMIN">
      <AdminPanel />
    </ProtectedRoute>
  } 
/>


GUARDAR UBICACION PARA REDIRECT:
---------------------------------
import { Navigate, useLocation } from 'react-router-dom'

function ProtectedRoute({ children }) {
  const token = localStorage.getItem('token')
  const location = useLocation()
  
  if (!token) {
    // Guardar la ubicacion a la que queria ir
    return <Navigate to="/login" state={{ from: location }} replace />
  }
  
  return children
}

// En Login, despues de autenticar:
function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from?.pathname || '/'
  
  const handleLogin = async () => {
    await login()
    navigate(from, { replace: true })  // Ir a donde queria ir originalmente
  }
}


404 - PAGINA NO ENCONTRADA
===========================

Ruta catch-all para URLs que no existen.

<Routes>
  <Route path="/" element={<Home />} />
  <Route path="/productos" element={<Productos />} />
  {/* Otras rutas... */}
  
  <Route path="*" element={<NotFound />} />  {/* Siempre al final */}
</Routes>


COMPONENTE NotFound:
--------------------
function NotFound() {
  const navigate = useNavigate()
  
  return (
    <div className="flex flex-col items-center justify-center min-h-screen">
      <h1 className="text-6xl font-bold text-gray-800">404</h1>
      <p className="text-xl text-gray-600 mt-4">Pagina no encontrada</p>
      <button 
        onClick={() => navigate('/')}
        className="mt-6 bg-blue-500 text-white px-6 py-2 rounded"
      >
        Volver al inicio
      </button>
    </div>
  )
}


LAZY LOADING (CARGA DIFERIDA)
==============================

Cargar componentes solo cuando se necesitan, no todos al inicio.

BENEFICIO: La app carga mas rapido inicialmente.

CONFIGURACION:
--------------
import { lazy, Suspense } from 'react'

// Lazy loading de componentes
const Home = lazy(() => import('./pages/Home'))
const Productos = lazy(() => import('./pages/Productos'))
const Contacto = lazy(() => import('./pages/Contacto'))


ENVOLVER EN SUSPENSE:
---------------------
<Suspense fallback={<LoadingSpinner />}>
  <Routes>
    <Route path="/" element={<Home />} />
    <Route path="/productos" element={<Productos />} />
    <Route path="/contacto" element={<Contacto />} />
  </Routes>
</Suspense>

fallback es lo que se muestra mientras carga el componente.


LOADING SPINNER:
----------------
function LoadingSpinner() {
  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-500" />
    </div>
  )
}


ESTRUCTURA COMPLETA EN BABYCASH
================================

// main.tsx
import { BrowserRouter } from 'react-router-dom'
import App from './App'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
)


// App.tsx
import AppRouter from './router/AppRouter'

function App() {
  return <AppRouter />
}


// router/AppRouter.tsx
import { lazy, Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'
import Navbar from '../components/layout/Navbar'
import Footer from '../components/layout/Footer'
import Preloader from '../components/ui/Preloader'
import ProtectedRoute from '../components/ProtectedRoute'

// Lazy loading
const Home = lazy(() => import('../pages/Home'))
const Nosotros = lazy(() => import('../pages/Nosotros'))
const Productos = lazy(() => import('../pages/Productos'))
const ProductoDetalle = lazy(() => import('../pages/ProductoDetalle'))
const Carrito = lazy(() => import('../pages/Carrito'))
const Checkout = lazy(() => import('../pages/Checkout'))
const Perfil = lazy(() => import('../pages/Perfil'))
const Blog = lazy(() => import('../pages/Blog'))
const BlogPost = lazy(() => import('../pages/BlogPostDetail'))
const Contacto = lazy(() => import('../pages/Contacto'))
const Login = lazy(() => import('../pages/Login'))
const Register = lazy(() => import('../pages/Register'))
const AdminPanel = lazy(() => import('../pages/AdminPanel'))
const NotFound = lazy(() => import('../pages/NotFound'))

function AppRouter() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      
      <main className="flex-1">
        <Suspense fallback={<Preloader />}>
          <Routes>
            {/* Rutas publicas */}
            <Route path="/" element={<Home />} />
            <Route path="/nosotros" element={<Nosotros />} />
            <Route path="/productos" element={<Productos />} />
            <Route path="/productos/:id" element={<ProductoDetalle />} />
            <Route path="/blog" element={<Blog />} />
            <Route path="/blog/:slug" element={<BlogPost />} />
            <Route path="/contacto" element={<Contacto />} />
            
            {/* Auth */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            
            {/* Rutas protegidas */}
            <Route 
              path="/carrito" 
              element={
                <ProtectedRoute>
                  <Carrito />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/checkout" 
              element={
                <ProtectedRoute>
                  <Checkout />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/perfil" 
              element={
                <ProtectedRoute>
                  <Perfil />
                </ProtectedRoute>
              } 
            />
            
            {/* Admin - solo para ADMIN */}
            <Route 
              path="/admin" 
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminPanel />
                </ProtectedRoute>
              } 
            />
            
            {/* 404 */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </main>
      
      <Footer />
    </div>
  )
}


HOOKS UTILES DE REACT ROUTER
=============================

1. useNavigate() - Navegar programaticamente
2. useParams() - Obtener parametros de URL
3. useSearchParams() - Obtener/setear query params
4. useLocation() - Obtener informacion de la ubicacion actual
5. useMatch() - Verificar si una ruta coincide


EJEMPLO useLocation:
--------------------
import { useLocation } from 'react-router-dom'

function Component() {
  const location = useLocation()
  
  console.log(location.pathname)   // "/productos"
  console.log(location.search)     // "?categoria=bebes"
  console.log(location.hash)       // "#seccion1"
  console.log(location.state)      // Estado pasado con navigate
}


EJEMPLO useMatch:
-----------------
import { useMatch } from 'react-router-dom'

function Navbar() {
  const isProductos = useMatch('/productos')
  
  return (
    <nav>
      <Link to="/productos" className={isProductos ? 'active' : ''}>
        Productos
      </Link>
    </nav>
  )
}


SCROLL TO TOP AL CAMBIAR DE RUTA
=================================

Por defecto, al cambiar de ruta, el scroll se queda donde estaba.
Para volver arriba automaticamente:

// ScrollToTop.tsx
import { useEffect } from 'react'
import { useLocation } from 'react-router-dom'

function ScrollToTop() {
  const { pathname } = useLocation()
  
  useEffect(() => {
    window.scrollTo(0, 0)
  }, [pathname])
  
  return null
}

// Uso en App:
<BrowserRouter>
  <ScrollToTop />
  <AppRouter />
</BrowserRouter>


TRANSICIONES ENTRE PAGINAS
===========================

Agregar animaciones al cambiar de ruta con Framer Motion.

import { motion } from 'framer-motion'

function PageWrapper({ children }) {
  return (
    <motion.div
      initial={{ opacity: 0, x: -50 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: 50 }}
      transition={{ duration: 0.3 }}
    >
      {children}
    </motion.div>
  )
}

// En cada pagina:
function Home() {
  return (
    <PageWrapper>
      <h1>Home</h1>
      {/* contenido */}
    </PageWrapper>
  )
}


BREADCRUMBS (MIGAS DE PAN)
==========================

Mostrar la ruta actual.

function Breadcrumbs() {
  const location = useLocation()
  const paths = location.pathname.split('/').filter(x => x)
  
  return (
    <nav className="flex gap-2 text-sm text-gray-600">
      <Link to="/">Inicio</Link>
      {paths.map((path, index) => {
        const to = `/${paths.slice(0, index + 1).join('/')}`
        return (
          <span key={to}>
            <span className="mx-2">/</span>
            <Link to={to} className="hover:text-blue-600">
              {path}
            </Link>
          </span>
        )
      })}
    </nav>
  )
}


REDIRECCIONES
=============

Redirigir de una ruta a otra.

import { Navigate } from 'react-router-dom'

<Routes>
  <Route path="/old-productos" element={<Navigate to="/productos" replace />} />
  <Route path="/productos" element={<Productos />} />
</Routes>


MULTIPLES LAYOUTS
=================

Diferentes layouts para diferentes secciones.

// MainLayout.tsx
function MainLayout() {
  return (
    <div>
      <Navbar />
      <Outlet />
      <Footer />
    </div>
  )
}

// AdminLayout.tsx
function AdminLayout() {
  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1">
        <Outlet />
      </main>
    </div>
  )
}

// Routes
<Routes>
  <Route element={<MainLayout />}>
    <Route path="/" element={<Home />} />
    <Route path="/productos" element={<Productos />} />
  </Route>
  
  <Route element={<AdminLayout />}>
    <Route path="/admin" element={<AdminDashboard />} />
    <Route path="/admin/productos" element={<AdminProductos />} />
  </Route>
</Routes>


ERRORES COMUNES
===============

1. OLVIDAR BrowserRouter
   ✗ Usar Routes sin BrowserRouter
   ✓ Envolver App con BrowserRouter

2. USAR <a> EN VEZ DE <Link>
   ✗ <a href="/productos">Productos</a>
   ✓ <Link to="/productos">Productos</Link>

3. OLVIDAR OUTLET EN RUTAS ANIDADAS
   ✗ Layout sin <Outlet />
   ✓ Layout con <Outlet /> donde se renderizaran las rutas hijas

4. RUTA 404 NO AL FINAL
   ✗ Route path="*" en medio de otras rutas
   ✓ Route path="*" siempre al final

5. NO USAR SUSPENSE CON LAZY
   ✗ lazy sin Suspense
   ✓ lazy envuelto en <Suspense fallback={...}>


RESUMEN
=======

React Router:
✓ Permite crear SPA (Single Page Application)
✓ Navegar sin recargar la pagina
✓ URL dinamicas con parametros
✓ Rutas protegidas para autenticacion
✓ Lazy loading para mejor performance
✓ Navegacion programatica con useNavigate

Componentes principales:
- BrowserRouter: Envuelve la app
- Routes: Contenedor de rutas
- Route: Define una ruta
- Link: Navegar sin recargar
- NavLink: Link con clase activa
- Navigate: Redireccionar

Hooks utiles:
- useNavigate: Navegar programaticamente
- useParams: Obtener parametros de URL
- useSearchParams: Query params
- useLocation: Info de ubicacion actual

En BabyCash:
✓ Lazy loading de todas las paginas
✓ Rutas protegidas para perfil, checkout, admin
✓ Navbar con NavLink activo
✓ 404 para rutas no encontradas
✓ Preloader mientras cargan componentes

================================================================================
