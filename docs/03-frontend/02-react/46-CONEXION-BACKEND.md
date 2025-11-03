================================================================================
ARCHIVO 46: CONEXION CON BACKEND
================================================================================

COMO SE COMUNICA EL FRONTEND CON EL BACKEND
============================================

El frontend (React) y el backend (Spring Boot) se comunican mediante HTTP.

ANALOGIA:
Frontend es como un cliente en un restaurante.
Backend es como la cocina.
HTTP es como el mesero que lleva los pedidos y trae la comida.


ARQUITECTURA CLIENTE-SERVIDOR
==============================

FRONTEND (Cliente)              BACKEND (Servidor)
React en el navegador    <-->   Spring Boot en el servidor
http://localhost:5173           http://localhost:8080


FLUJO COMPLETO:
1. Usuario hace click en "Ver productos"
2. React hace una peticion HTTP GET a http://localhost:8080/api/products
3. Spring Boot procesa la peticion
4. Spring Boot consulta la base de datos
5. Spring Boot devuelve los datos en formato JSON
6. React recibe los datos
7. React muestra los productos en pantalla


METODOS HTTP
============

GET     - Obtener datos (leer)
POST    - Crear datos
PUT     - Actualizar datos completos
PATCH   - Actualizar datos parciales
DELETE  - Eliminar datos


AXIOS - CLIENTE HTTP
====================

Axios es una libreria para hacer peticiones HTTP desde JavaScript.

INSTALACION:
npm install axios

IMPORTAR:
import axios from 'axios'


CONFIGURACION EN BABYCASH
==========================

Tenemos un archivo centralizado: src/services/api.ts

// Configuracion base
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

// Crear instancia de axios
const api = axios.create({
  baseURL: API_URL,
  timeout: 10000,  // 10 segundos
  headers: {
    'Content-Type': 'application/json'
  }
})


PETICIONES BASICAS CON AXIOS
=============================

1. GET - OBTENER DATOS:
-----------------------
// Obtener todos los productos
axios.get('http://localhost:8080/api/products')
  .then(response => {
    console.log(response.data)  // Los datos estan aqui
  })
  .catch(error => {
    console.error('Error:', error)
  })


2. POST - CREAR DATOS:
----------------------
// Crear un nuevo producto
const nuevoProducto = {
  name: 'Pañales Huggies',
  description: 'Pañales para bebe',
  price: 25000,
  stock: 100
}

axios.post('http://localhost:8080/api/products', nuevoProducto)
  .then(response => {
    console.log('Producto creado:', response.data)
  })
  .catch(error => {
    console.error('Error:', error)
  })


3. PUT - ACTUALIZAR DATOS:
--------------------------
// Actualizar producto
const productoActualizado = {
  name: 'Pañales Huggies Premium',
  description: 'Pañales premium para bebe',
  price: 30000,
  stock: 50
}

axios.put('http://localhost:8080/api/products/1', productoActualizado)
  .then(response => {
    console.log('Producto actualizado:', response.data)
  })


4. DELETE - ELIMINAR DATOS:
---------------------------
// Eliminar producto
axios.delete('http://localhost:8080/api/products/1')
  .then(response => {
    console.log('Producto eliminado')
  })


USAR ASYNC/AWAIT (MAS MODERNO)
===============================

En vez de .then().catch(), usamos async/await:

// Funcion asincrona
const obtenerProductos = async () => {
  try {
    const response = await axios.get('/api/products')
    console.log(response.data)
  } catch (error) {
    console.error('Error:', error)
  }
}


EJEMPLO COMPLETO EN COMPONENTE REACT:
--------------------------------------
import { useState, useEffect } from 'react'
import axios from 'axios'

function ProductosLista() {
  const [productos, setProductos] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  
  useEffect(() => {
    const obtenerProductos = async () => {
      try {
        setLoading(true)
        const response = await axios.get('http://localhost:8080/api/products')
        setProductos(response.data)
        setError(null)
      } catch (error) {
        setError('Error al cargar productos')
        console.error(error)
      } finally {
        setLoading(false)
      }
    }
    
    obtenerProductos()
  }, [])
  
  if (loading) return <p>Cargando productos...</p>
  if (error) return <p className="text-red-500">{error}</p>
  
  return (
    <div className="grid grid-cols-3 gap-4">
      {productos.map(producto => (
        <div key={producto.id} className="border p-4">
          <h3>{producto.name}</h3>
          <p>${producto.price}</p>
        </div>
      ))}
    </div>
  )
}


AUTENTICACION CON JWT
=====================

JWT (JSON Web Token) es un token que el backend genera al hacer login.
Se envia en cada peticion para identificar al usuario.


FLUJO DE AUTENTICACION:
------------------------
1. Usuario ingresa email y password
2. Frontend envia POST /api/auth/login con las credenciales
3. Backend valida y genera un JWT token
4. Frontend guarda el token (en localStorage o sessionStorage)
5. Frontend envia el token en cada peticion subsiguiente


EJEMPLO - LOGIN:
----------------
const login = async (email, password) => {
  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      email,
      password
    })
    
    const { token, user } = response.data
    
    // Guardar token en localStorage
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    
    return { token, user }
  } catch (error) {
    throw new Error('Credenciales invalidas')
  }
}


EJEMPLO - ENVIAR TOKEN EN PETICIONES:
--------------------------------------
const obtenerPerfil = async () => {
  const token = localStorage.getItem('token')
  
  const response = await axios.get('http://localhost:8080/api/users/me', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  
  return response.data
}


INTERCEPTORES DE AXIOS
=======================

Los interceptores permiten modificar TODAS las peticiones o respuestas automaticamente.

USO EN BABYCASH:
- Agregar token a todas las peticiones automaticamente
- Manejar errores globalmente
- Refresh token cuando expira


REQUEST INTERCEPTOR (Antes de enviar):
---------------------------------------
api.interceptors.request.use(
  (config) => {
    // Agregar token a todas las peticiones
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)


RESPONSE INTERCEPTOR (Al recibir respuesta):
---------------------------------------------
api.interceptors.response.use(
  (response) => {
    // Respuesta exitosa, devolver tal cual
    return response
  },
  async (error) => {
    const originalRequest = error.config
    
    // Si el error es 401 (no autorizado) y no hemos intentado refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      try {
        // Intentar refrescar el token
        const refreshToken = localStorage.getItem('refreshToken')
        const response = await axios.post('/api/auth/refresh', { refreshToken })
        const { token } = response.data
        
        // Guardar nuevo token
        localStorage.setItem('token', token)
        
        // Reintentar la peticion original con el nuevo token
        originalRequest.headers.Authorization = `Bearer ${token}`
        return api(originalRequest)
      } catch (refreshError) {
        // Si falla el refresh, cerrar sesion
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }
    
    return Promise.reject(error)
  }
)


ESTRUCTURA DE API SERVICE EN BABYCASH
======================================

// services/api.ts

import axios from 'axios'

const API_URL = 'http://localhost:8080/api'

// Crear instancia
const api = axios.create({
  baseURL: API_URL,
  timeout: 10000
})

// Interceptor para agregar token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Funciones de autenticacion
export const authAPI = {
  login: async (email: string, password: string) => {
    const response = await api.post('/auth/login', { email, password })
    return response.data
  },
  
  register: async (data: RegisterData) => {
    const response = await api.post('/auth/register', data)
    return response.data
  },
  
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}

// Funciones de productos
export const productsAPI = {
  getAll: async () => {
    const response = await api.get('/products')
    return response.data
  },
  
  getById: async (id: number) => {
    const response = await api.get(`/products/${id}`)
    return response.data
  },
  
  create: async (data: ProductData) => {
    const response = await api.post('/products', data)
    return response.data
  },
  
  update: async (id: number, data: ProductData) => {
    const response = await api.put(`/products/${id}`, data)
    return response.data
  },
  
  delete: async (id: number) => {
    await api.delete(`/products/${id}`)
  }
}

// Funciones de carrito
export const cartAPI = {
  get: async () => {
    const response = await api.get('/cart')
    return response.data
  },
  
  addItem: async (productId: number, quantity: number) => {
    const response = await api.post('/cart/items', { productId, quantity })
    return response.data
  },
  
  updateItem: async (itemId: number, quantity: number) => {
    const response = await api.put(`/cart/items/${itemId}`, { quantity })
    return response.data
  },
  
  removeItem: async (itemId: number) => {
    await api.delete(`/cart/items/${itemId}`)
  },
  
  clear: async () => {
    await api.delete('/cart')
  }
}

// Funciones de ordenes
export const ordersAPI = {
  getAll: async () => {
    const response = await api.get('/orders')
    return response.data
  },
  
  getById: async (id: number) => {
    const response = await api.get(`/orders/${id}`)
    return response.data
  },
  
  create: async (data: OrderData) => {
    const response = await api.post('/orders', data)
    return response.data
  }
}

export default api


USAR EL API SERVICE EN COMPONENTES:
====================================

import { useState, useEffect } from 'react'
import { productsAPI } from '../services/api'

function ProductosPage() {
  const [productos, setProductos] = useState([])
  const [loading, setLoading] = useState(true)
  
  useEffect(() => {
    const cargarProductos = async () => {
      try {
        const data = await productsAPI.getAll()
        setProductos(data)
      } catch (error) {
        console.error('Error al cargar productos:', error)
      } finally {
        setLoading(false)
      }
    }
    
    cargarProductos()
  }, [])
  
  const eliminarProducto = async (id) => {
    try {
      await productsAPI.delete(id)
      // Actualizar lista
      setProductos(productos.filter(p => p.id !== id))
    } catch (error) {
      console.error('Error al eliminar producto:', error)
    }
  }
  
  if (loading) return <p>Cargando...</p>
  
  return (
    <div>
      {productos.map(producto => (
        <div key={producto.id}>
          <h3>{producto.name}</h3>
          <p>${producto.price}</p>
          <button onClick={() => eliminarProducto(producto.id)}>
            Eliminar
          </button>
        </div>
      ))}
    </div>
  )
}


MANEJO DE ERRORES
=================

ERRORES COMUNES:
----------------
200 - OK (exitoso)
201 - Created (creado exitosamente)
400 - Bad Request (datos invalidos)
401 - Unauthorized (no autenticado)
403 - Forbidden (no autorizado)
404 - Not Found (no encontrado)
500 - Internal Server Error (error del servidor)


MANEJAR ERRORES EN COMPONENTE:
-------------------------------
const crearProducto = async (data) => {
  try {
    const producto = await productsAPI.create(data)
    alert('Producto creado exitosamente')
    return producto
  } catch (error) {
    if (error.response) {
      // El servidor respondio con un error
      const status = error.response.status
      const message = error.response.data.message
      
      switch (status) {
        case 400:
          alert('Datos invalidos: ' + message)
          break
        case 401:
          alert('Debes iniciar sesion')
          break
        case 403:
          alert('No tienes permisos')
          break
        case 404:
          alert('No encontrado')
          break
        case 500:
          alert('Error del servidor')
          break
        default:
          alert('Error desconocido')
      }
    } else if (error.request) {
      // La peticion se hizo pero no hubo respuesta
      alert('No hay respuesta del servidor. Verifica tu conexion.')
    } else {
      // Error al configurar la peticion
      alert('Error: ' + error.message)
    }
  }
}


CORS (CROSS-ORIGIN RESOURCE SHARING)
=====================================

CORS es una seguridad del navegador que impide que un sitio en un dominio
haga peticiones a otro dominio.

PROBLEMA:
Frontend en http://localhost:5173
Backend en http://localhost:8080
El navegador bloquea las peticiones.


SOLUCION EN BACKEND (Spring Boot):
-----------------------------------
@Configuration
public class CorsConfig {
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
          .allowedOrigins("http://localhost:5173")
          .allowedMethods("GET", "POST", "PUT", "DELETE")
          .allowedHeaders("*")
          .allowCredentials(true);
      }
    };
  }
}


SOLUCION TEMPORAL EN DESARROLLO (Proxy en Vite):
-------------------------------------------------
// vite.config.ts
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})

Ahora puedes llamar a '/api/products' directamente sin especificar el puerto.


ESTADOS DE CARGA Y ERROR
=========================

PATRON COMUN:
-------------
function Component() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        setError(null)
        const result = await api.get('/endpoint')
        setData(result.data)
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }
    
    fetchData()
  }, [])
  
  if (loading) return <LoadingSpinner />
  if (error) return <ErrorMessage error={error} />
  if (!data) return <p>No hay datos</p>
  
  return <div>{/* Mostrar datos */}</div>
}


CUSTOM HOOK PARA FETCH:
------------------------
function useFetch(url) {
  const [state, setState] = useState({
    data: null,
    loading: true,
    error: null
  })
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        setState({ data: null, loading: true, error: null })
        const response = await axios.get(url)
        setState({ data: response.data, loading: false, error: null })
      } catch (error) {
        setState({ data: null, loading: false, error: error.message })
      }
    }
    
    fetchData()
  }, [url])
  
  return state
}

// Uso:
function Component() {
  const { data, loading, error } = useFetch('/api/products')
  
  if (loading) return <p>Cargando...</p>
  if (error) return <p>Error: {error}</p>
  
  return <div>{data.map(...)}</div>
}


PAGINACION
==========

Cuando hay muchos datos, el backend los devuelve en paginas.

PETICION CON PAGINACION:
GET /api/products?page=0&size=10

RESPUESTA:
{
  "content": [ productos... ],
  "totalElements": 100,
  "totalPages": 10,
  "number": 0,
  "size": 10
}


COMPONENTE CON PAGINACION:
--------------------------
function ProductosConPaginacion() {
  const [productos, setProductos] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const size = 10
  
  useEffect(() => {
    const cargar = async () => {
      const response = await api.get(`/products?page=${page}&size=${size}`)
      setProductos(response.data.content)
      setTotalPages(response.data.totalPages)
    }
    cargar()
  }, [page])
  
  return (
    <div>
      {productos.map(p => <div key={p.id}>{p.name}</div>)}
      
      <div>
        <button 
          onClick={() => setPage(p => p - 1)}
          disabled={page === 0}
        >
          Anterior
        </button>
        
        <span>Pagina {page + 1} de {totalPages}</span>
        
        <button 
          onClick={() => setPage(p => p + 1)}
          disabled={page >= totalPages - 1}
        >
          Siguiente
        </button>
      </div>
    </div>
  )
}


BUSQUEDA Y FILTROS
==================

PETICION CON FILTROS:
GET /api/products?search=pañales&category=higiene&minPrice=10000&maxPrice=50000


COMPONENTE CON BUSQUEDA:
-------------------------
function BuscadorProductos() {
  const [busqueda, setBusqueda] = useState('')
  const [resultados, setResultados] = useState([])
  
  useEffect(() => {
    const buscar = async () => {
      if (busqueda.length > 2) {  // Buscar solo si hay mas de 2 caracteres
        const response = await api.get(`/products?search=${busqueda}`)
        setResultados(response.data)
      }
    }
    
    // Debounce: esperar 500ms despues de dejar de escribir
    const timer = setTimeout(buscar, 500)
    return () => clearTimeout(timer)
  }, [busqueda])
  
  return (
    <div>
      <input 
        value={busqueda}
        onChange={(e) => setBusqueda(e.target.value)}
        placeholder="Buscar productos..."
      />
      
      {resultados.map(p => (
        <div key={p.id}>{p.name}</div>
      ))}
    </div>
  )
}


SUBIR ARCHIVOS (IMAGES)
========================

Para subir imagenes o archivos, usar FormData.

const subirImagen = async (file) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('productId', '123')
  
  const response = await axios.post('/api/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
  
  return response.data.url
}

// En componente:
function SubirImagen() {
  const [imagen, setImagen] = useState(null)
  const [preview, setPreview] = useState(null)
  
  const handleFileChange = (e) => {
    const file = e.target.files[0]
    setImagen(file)
    
    // Preview de la imagen
    const reader = new FileReader()
    reader.onloadend = () => {
      setPreview(reader.result)
    }
    reader.readAsDataURL(file)
  }
  
  const handleSubmit = async () => {
    if (imagen) {
      const url = await subirImagen(imagen)
      alert('Imagen subida: ' + url)
    }
  }
  
  return (
    <div>
      <input type="file" onChange={handleFileChange} accept="image/*" />
      {preview && <img src={preview} alt="Preview" />}
      <button onClick={handleSubmit}>Subir</button>
    </div>
  )
}


RESUMEN
=======

Conexion Frontend-Backend:
✓ Frontend (React) se comunica con Backend (Spring Boot) via HTTP
✓ Usar Axios para hacer peticiones
✓ Metodos: GET (obtener), POST (crear), PUT (actualizar), DELETE (eliminar)
✓ JWT para autenticacion (token en headers)
✓ Interceptores para agregar token automaticamente
✓ Manejo de errores 400, 401, 403, 404, 500
✓ Estados de loading, error, data
✓ Paginacion, busqueda, filtros
✓ CORS configurado en el backend

Estructura en BabyCash:
✓ services/api.ts - Configuracion centralizada
✓ authAPI, productsAPI, cartAPI, ordersAPI
✓ Interceptores para token y refresh
✓ Custom hooks (useFetch, useAuth)

================================================================================
