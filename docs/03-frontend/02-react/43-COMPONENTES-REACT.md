================================================================================
ARCHIVO 43: COMPONENTES REACT
================================================================================

QUE SON LOS COMPONENTES
========================

Los componentes son las PIEZAS BASICAS de React. Son como bloques de LEGO que
puedes combinar para construir tu aplicacion.

ANALOGIA:
Imagina que estas construyendo una casa:
- La casa completa = Tu aplicacion
- Las habitaciones = Paginas (Home, Login, Dashboard)
- Los muebles = Componentes (Boton, Card, Form)
- Los tornillos = Props (datos que pasas entre componentes)


TIPOS DE COMPONENTES
=====================

1. COMPONENTES DE CLASE (Antiguo - NO usamos)
2. COMPONENTES FUNCIONALES (Moderno - Lo que usamos)


COMPONENTE FUNCIONAL BASICO
============================

La forma mas simple:

function Saludo() {
  return <h1>Hola Mundo</h1>
}

export default Saludo


Con arrow function:

const Saludo = () => {
  return <h1>Hola Mundo</h1>
}

export default Saludo


Return implicito (cuando es una sola linea):

const Saludo = () => <h1>Hola Mundo</h1>


ESTRUCTURA DE UN COMPONENTE
============================

// 1. IMPORTS
import React from 'react'
import { useState } from 'react'
import OtroComponente from './OtroComponente'

// 2. INTERFACE DE PROPS (con TypeScript)
interface Props {
  nombre: string
  edad: number
}

// 3. COMPONENTE
function MiComponente({ nombre, edad }: Props) {
  // 4. HOOKS (useState, useEffect, etc)
  const [contador, setContador] = useState(0)
  
  // 5. FUNCIONES INTERNAS
  const handleClick = () => {
    setContador(contador + 1)
  }
  
  // 6. RETURN (JSX)
  return (
    <div>
      <h1>{nombre}</h1>
      <p>Edad: {edad}</p>
      <p>Contador: {contador}</p>
      <button onClick={handleClick}>Incrementar</button>
    </div>
  )
}

// 7. EXPORT
export default MiComponente


PROPS (PROPIEDADES)
===================

Las props son DATOS que pasas de un componente PADRE a un componente HIJO.

Son como ARGUMENTOS de una funcion.


EJEMPLO SIN PROPS:
------------------
function Boton() {
  return <button>Click aqui</button>
}


EJEMPLO CON PROPS:
------------------
function Boton({ texto, color }) {
  return <button style={{ backgroundColor: color }}>{texto}</button>
}

// Usar el componente:
<Boton texto="Guardar" color="blue" />
<Boton texto="Cancelar" color="red" />


CON TYPESCRIPT:
---------------
interface Props {
  texto: string
  color: string
  onClick?: () => void  // Opcional
}

function Boton({ texto, color, onClick }: Props) {
  return (
    <button 
      style={{ backgroundColor: color }}
      onClick={onClick}
    >
      {texto}
    </button>
  )
}


DESTRUCTURING DE PROPS
=======================

FORMA 1 - Sin destructuring (NO recomendado):
----------------------------------------------
function Usuario(props) {
  return (
    <div>
      <h2>{props.nombre}</h2>
      <p>{props.email}</p>
      <p>{props.edad}</p>
    </div>
  )
}


FORMA 2 - Con destructuring (RECOMENDADO):
-------------------------------------------
function Usuario({ nombre, email, edad }) {
  return (
    <div>
      <h2>{nombre}</h2>
      <p>{email}</p>
      <p>{edad}</p>
    </div>
  )
}


FORMA 3 - Con valores por defecto:
-----------------------------------
function Usuario({ nombre, email, edad = 18, activo = true }) {
  return (
    <div>
      <h2>{nombre}</h2>
      <p>{email}</p>
      <p>{edad}</p>
      <p>{activo ? "Activo" : "Inactivo"}</p>
    </div>
  )
}


CHILDREN PROP
=============

children es una prop ESPECIAL que contiene el contenido entre las etiquetas.

function Card({ children }) {
  return (
    <div className="card">
      {children}
    </div>
  )
}

// Uso:
<Card>
  <h2>Titulo</h2>
  <p>Contenido aqui</p>
</Card>


Con TypeScript:
interface Props {
  children: React.ReactNode
  titulo?: string
}

function Card({ children, titulo }: Props) {
  return (
    <div className="card">
      {titulo && <h2>{titulo}</h2>}
      {children}
    </div>
  )
}


COMPOSICION DE COMPONENTES
===========================

Los componentes se pueden combinar para crear componentes mas complejos.

// Componente basico: Boton
function Boton({ texto, onClick }) {
  return (
    <button onClick={onClick}>
      {texto}
    </button>
  )
}

// Componente basico: Input
function Input({ valor, onChange, placeholder }) {
  return (
    <input 
      value={valor}
      onChange={onChange}
      placeholder={placeholder}
    />
  )
}

// Componente compuesto: Formulario
function Formulario() {
  const [nombre, setNombre] = useState('')
  
  const handleSubmit = () => {
    console.log('Nombre:', nombre)
  }
  
  return (
    <div>
      <Input 
        valor={nombre}
        onChange={(e) => setNombre(e.target.value)}
        placeholder="Ingresa tu nombre"
      />
      <Boton texto="Enviar" onClick={handleSubmit} />
    </div>
  )
}


RENDERIZADO CONDICIONAL
========================

Mostrar u ocultar elementos segun condiciones.


FORMA 1 - IF/ELSE TRADICIONAL:
-------------------------------
function Saludo({ usuarioLogueado }) {
  if (usuarioLogueado) {
    return <h1>Bienvenido de vuelta</h1>
  } else {
    return <h1>Por favor inicia sesion</h1>
  }
}


FORMA 2 - OPERADOR TERNARIO (mas comun):
-----------------------------------------
function Saludo({ usuarioLogueado }) {
  return (
    <h1>
      {usuarioLogueado ? "Bienvenido de vuelta" : "Por favor inicia sesion"}
    </h1>
  )
}


FORMA 3 - OPERADOR && (cuando solo quieres mostrar si es true):
----------------------------------------------------------------
function MensajeError({ error }) {
  return (
    <div>
      {error && <p className="error">{error}</p>}
    </div>
  )
}

// Si error es null o "", no se muestra nada
// Si error tiene valor, se muestra el <p>


FORMA 4 - EARLY RETURN:
------------------------
function Perfil({ usuario }) {
  if (!usuario) {
    return <p>Cargando...</p>
  }
  
  return (
    <div>
      <h1>{usuario.nombre}</h1>
      <p>{usuario.email}</p>
    </div>
  )
}


RENDERIZADO DE LISTAS
======================

Usar .map() para renderizar arrays.

function ListaUsuarios({ usuarios }) {
  return (
    <ul>
      {usuarios.map(usuario => (
        <li key={usuario.id}>
          {usuario.nombre}
        </li>
      ))}
    </ul>
  )
}

IMPORTANTE: La prop KEY es OBLIGATORIA y debe ser UNICA.

MAL - Usando index como key (NO hacer esto si la lista puede cambiar):
{usuarios.map((usuario, index) => (
  <li key={index}>{usuario.nombre}</li>
))}

BIEN - Usando ID unico:
{usuarios.map(usuario => (
  <li key={usuario.id}>{usuario.nombre}</li>
))}


EJEMPLO COMPLETO CON COMPONENTE DE LISTA:
------------------------------------------
interface Usuario {
  id: number
  nombre: string
  email: string
}

interface Props {
  usuarios: Usuario[]
}

function TablaUsuarios({ usuarios }: Props) {
  if (usuarios.length === 0) {
    return <p>No hay usuarios</p>
  }
  
  return (
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Nombre</th>
          <th>Email</th>
        </tr>
      </thead>
      <tbody>
        {usuarios.map(usuario => (
          <tr key={usuario.id}>
            <td>{usuario.id}</td>
            <td>{usuario.nombre}</td>
            <td>{usuario.email}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}


EVENTOS
=======

Manejar clicks, cambios de input, envios de formularios, etc.

EVENTOS COMUNES:
----------------
onClick       - Click en boton, div, etc
onChange      - Cambio en input, select, textarea
onSubmit      - Envio de formulario
onMouseEnter  - Mouse entra en elemento
onMouseLeave  - Mouse sale de elemento
onFocus       - Input recibe focus
onBlur        - Input pierde focus
onKeyDown     - Tecla presionada
onKeyUp       - Tecla soltada


EJEMPLO BASICO:
---------------
function Boton() {
  const handleClick = () => {
    console.log('Click!')
  }
  
  return <button onClick={handleClick}>Click aqui</button>
}


CON PARAMETROS:
---------------
function ListaBotones({ items }) {
  const handleClick = (id) => {
    console.log('Click en item:', id)
  }
  
  return (
    <div>
      {items.map(item => (
        <button 
          key={item.id}
          onClick={() => handleClick(item.id)}
        >
          {item.nombre}
        </button>
      ))}
    </div>
  )
}


EVENT OBJECT:
-------------
function Input() {
  const handleChange = (e) => {
    console.log('Valor actual:', e.target.value)
  }
  
  return <input onChange={handleChange} />
}


CON TYPESCRIPT:
---------------
function Formulario() {
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    console.log('Formulario enviado')
  }
  
  const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    console.log('Click en boton')
  }
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    console.log(e.target.value)
  }
  
  return (
    <form onSubmit={handleSubmit}>
      <input onChange={handleChange} />
      <button onClick={handleClick}>Enviar</button>
    </form>
  )
}


FRAGMENTS
=========

Cuando necesitas retornar multiples elementos sin agregar un div extra.

MAL - Agrega div innecesario:
function Componente() {
  return (
    <div>
      <h1>Titulo</h1>
      <p>Parrafo</p>
    </div>
  )
}


BIEN - Usa Fragment:
function Componente() {
  return (
    <>
      <h1>Titulo</h1>
      <p>Parrafo</p>
    </>
  )
}

O con sintaxis completa (cuando necesitas key):
function Lista({ items }) {
  return (
    <>
      {items.map(item => (
        <React.Fragment key={item.id}>
          <h2>{item.titulo}</h2>
          <p>{item.descripcion}</p>
        </React.Fragment>
      ))}
    </>
  )
}


COMPONENTES PUROS
=================

Un componente puro es un componente que:
- Para las mismas props, retorna el mismo resultado
- No tiene efectos secundarios
- No modifica variables externas

function ComponentePuro({ nombre }) {
  return <h1>Hola {nombre}</h1>
}

// Siempre que le pases nombre="Juan", mostrara lo mismo


COMPONENTES IMPUROS (tienen efectos secundarios):
--------------------------------------------------
function ComponenteImpuro({ nombre }) {
  console.log(nombre)  // Efecto secundario
  fetch('/api/users')  // Efecto secundario
  return <h1>Hola {nombre}</h1>
}


ORGANIZACION DE COMPONENTES
============================

ESTRUCTURA DE CARPETAS EN BABYCASH:
------------------------------------
src/
├── components/           Componentes reutilizables
│   ├── common/           Componentes genericos
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Card.tsx
│   │   └── Modal.tsx
│   ├── layout/           Componentes de layout
│   │   ├── Header.tsx
│   │   ├── Footer.tsx
│   │   ├── Sidebar.tsx
│   │   └── Navbar.tsx
│   └── products/         Componentes especificos
│       ├── ProductCard.tsx
│       ├── ProductList.tsx
│       └── ProductFilter.tsx
├── pages/                Paginas completas
│   ├── Home.tsx
│   ├── Login.tsx
│   ├── ProductDetail.tsx
│   └── Dashboard.tsx
└── App.tsx


COMPONENTES COMUNES EN BABYCASH
================================

1. BUTTON (Boton reutilizable)
-------------------------------
interface ButtonProps {
  children: React.ReactNode
  onClick?: () => void
  variant?: 'primary' | 'secondary' | 'danger'
  disabled?: boolean
  type?: 'button' | 'submit' | 'reset'
}

function Button({ 
  children, 
  onClick, 
  variant = 'primary',
  disabled = false,
  type = 'button'
}: ButtonProps) {
  const baseClasses = 'px-4 py-2 rounded font-semibold'
  
  const variantClasses = {
    primary: 'bg-blue-500 text-white hover:bg-blue-600',
    secondary: 'bg-gray-500 text-white hover:bg-gray-600',
    danger: 'bg-red-500 text-white hover:bg-red-600'
  }
  
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={`${baseClasses} ${variantClasses[variant]}`}
    >
      {children}
    </button>
  )
}

// Uso:
<Button variant="primary" onClick={handleSave}>Guardar</Button>
<Button variant="danger" onClick={handleDelete}>Eliminar</Button>


2. INPUT (Campo de entrada)
----------------------------
interface InputProps {
  label: string
  value: string
  onChange: (value: string) => void
  type?: 'text' | 'email' | 'password' | 'number'
  placeholder?: string
  required?: boolean
  error?: string
}

function Input({
  label,
  value,
  onChange,
  type = 'text',
  placeholder,
  required = false,
  error
}: InputProps) {
  return (
    <div className="mb-4">
      <label className="block text-sm font-medium mb-2">
        {label}
        {required && <span className="text-red-500">*</span>}
      </label>
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        required={required}
        className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2"
      />
      {error && <p className="text-red-500 text-sm mt-1">{error}</p>}
    </div>
  )
}


3. CARD (Tarjeta)
-----------------
interface CardProps {
  children: React.ReactNode
  title?: string
  className?: string
}

function Card({ children, title, className = '' }: CardProps) {
  return (
    <div className={`bg-white rounded-lg shadow-md p-6 ${className}`}>
      {title && <h3 className="text-xl font-bold mb-4">{title}</h3>}
      {children}
    </div>
  )
}


4. PRODUCT CARD (Tarjeta de Producto)
--------------------------------------
interface Product {
  id: number
  name: string
  description: string
  price: number
  imageUrl: string
  stock: number
}

interface ProductCardProps {
  product: Product
  onAddToCart: (product: Product) => void
}

function ProductCard({ product, onAddToCart }: ProductCardProps) {
  const handleAddToCart = () => {
    if (product.stock > 0) {
      onAddToCart(product)
    }
  }
  
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      <img 
        src={product.imageUrl} 
        alt={product.name}
        className="w-full h-48 object-cover"
      />
      <div className="p-4">
        <h3 className="text-lg font-bold">{product.name}</h3>
        <p className="text-gray-600 text-sm mt-2">{product.description}</p>
        
        <div className="flex justify-between items-center mt-4">
          <span className="text-2xl font-bold text-green-600">
            ${product.price.toLocaleString()}
          </span>
          
          {product.stock > 0 ? (
            <button
              onClick={handleAddToCart}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Agregar
            </button>
          ) : (
            <span className="text-red-500 font-semibold">Agotado</span>
          )}
        </div>
        
        <p className="text-sm text-gray-500 mt-2">
          Stock: {product.stock} unidades
        </p>
      </div>
    </div>
  )
}


5. MODAL (Ventana emergente)
-----------------------------
interface ModalProps {
  isOpen: boolean
  onClose: () => void
  title: string
  children: React.ReactNode
}

function Modal({ isOpen, onClose, title, children }: ModalProps) {
  if (!isOpen) return null
  
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-md w-full">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-bold">{title}</h2>
          <button 
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 text-2xl"
          >
            ×
          </button>
        </div>
        <div>{children}</div>
      </div>
    </div>
  )
}

// Uso:
function App() {
  const [isOpen, setIsOpen] = useState(false)
  
  return (
    <>
      <button onClick={() => setIsOpen(true)}>Abrir Modal</button>
      
      <Modal 
        isOpen={isOpen} 
        onClose={() => setIsOpen(false)}
        title="Confirmar accion"
      >
        <p>¿Estas seguro de eliminar este elemento?</p>
        <div className="flex gap-2 mt-4">
          <Button variant="danger" onClick={handleDelete}>Eliminar</Button>
          <Button variant="secondary" onClick={() => setIsOpen(false)}>Cancelar</Button>
        </div>
      </Modal>
    </>
  )
}


6. LOADING SPINNER (Indicador de carga)
----------------------------------------
function LoadingSpinner() {
  return (
    <div className="flex justify-center items-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
    </div>
  )
}

// Con mensaje:
function LoadingSpinner({ message = "Cargando..." }: { message?: string }) {
  return (
    <div className="flex flex-col items-center gap-2">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      <p className="text-gray-600">{message}</p>
    </div>
  )
}


PATRONES COMUNES
================

1. CONTAINER / PRESENTATIONAL
------------------------------
// Container: Maneja logica y estado
function ProductListContainer() {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  
  useEffect(() => {
    fetchProducts().then(data => {
      setProducts(data)
      setLoading(false)
    })
  }, [])
  
  if (loading) return <LoadingSpinner />
  
  return <ProductList products={products} />
}

// Presentational: Solo muestra datos
function ProductList({ products }) {
  return (
    <div className="grid grid-cols-3 gap-4">
      {products.map(product => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  )
}


2. COMPOUND COMPONENTS
-----------------------
// Componentes que trabajan juntos
function Tabs({ children }) {
  const [activeTab, setActiveTab] = useState(0)
  return <div>{children}</div>
}

Tabs.Tab = function Tab({ label, children }) {
  return <div>{children}</div>
}

// Uso:
<Tabs>
  <Tabs.Tab label="Tab 1">Contenido 1</Tabs.Tab>
  <Tabs.Tab label="Tab 2">Contenido 2</Tabs.Tab>
</Tabs>


BUENAS PRACTICAS
================

1. UN COMPONENTE = UNA RESPONSABILIDAD
   - Si un componente hace muchas cosas, dividelo

2. NOMBRES DESCRIPTIVOS
   ✓ ProductCard, UserProfile, LoginForm
   ✗ Card, Component1, Thing

3. PROPS DESCRIPTIVAS
   ✓ onAddToCart, onDeleteUser, isLoading
   ✗ onClick, handleClick, flag

4. USAR TYPESCRIPT
   - Define interfaces para props
   - El editor te ayudara con autocompletado

5. EXTRAER LOGICA COMPLEJA
   - Si tienes mucha logica, usa custom hooks
   - Mantén los componentes simples

6. EVITAR PROPS DRILLING
   - Si pasas props por muchos niveles, usa Context

7. COMPONENTES PEQUEÑOS
   - Maximo 200-300 lineas
   - Si es mas grande, dividelo

8. USAR KEY EN LISTAS
   - Siempre usa un id unico como key
   - No uses index si la lista puede cambiar


ERRORES COMUNES
===============

1. OLVIDAR KEY EN LISTAS
   ✗ {items.map(item => <div>{item.name}</div>)}
   ✓ {items.map(item => <div key={item.id}>{item.name}</div>)}

2. MODIFICAR PROPS
   ✗ props.nombre = "Nuevo nombre"
   ✓ Las props son inmutables

3. USAR INDEX COMO KEY
   ✗ {items.map((item, index) => <div key={index}>{item}</div>)}
   ✓ {items.map(item => <div key={item.id}>{item}</div>)}

4. NO USAR TYPESCRIPT
   - Define interfaces para todas las props

5. COMPONENTES MUY GRANDES
   - Dividir en componentes mas pequeños

6. LOGICA EN EL RENDER
   ✗ return <div>{usuarios.filter(u => u.activo).map(...)}</div>
   ✓ const usuariosActivos = usuarios.filter(u => u.activo)
      return <div>{usuariosActivos.map(...)}</div>


RESUMEN
=======

Componentes React:
✓ Son piezas reutilizables de UI
✓ Reciben props (datos) del padre
✓ Pueden tener estado interno (useState)
✓ Se combinan para crear aplicaciones completas

Conceptos clave:
- Props: Datos que se pasan de padre a hijo
- Children: Contenido entre etiquetas del componente
- Key: Identificador unico en listas
- Events: onClick, onChange, onSubmit, etc
- Conditional rendering: if, ternario, &&
- Lists: .map() con key

En BabyCash tenemos:
✓ Componentes comunes: Button, Input, Card, Modal
✓ Componentes de layout: Header, Footer, Sidebar
✓ Componentes especificos: ProductCard, CartItem, OrderSummary

================================================================================
