================================================================================
ARCHIVO 42: TYPESCRIPT BASICO
================================================================================

QUE ES TYPESCRIPT
=================

TypeScript es JavaScript con TIPOS. Es JavaScript con superpoderes.

ANALOGIA SIMPLE:
Imagina que JavaScript es como una caja donde puedes meter cualquier cosa.
TypeScript es como una caja etiquetada: "Solo manzanas".

Si intentas meter una naranja, TypeScript te avisa ANTES de que causes problemas.


JAVASCRIPT VS TYPESCRIPT
=========================

JAVASCRIPT (sin tipos):
-----------------------
let nombre = "Juan"
nombre = 123        // No hay error, pero puede causar problemas despues

function sumar(a, b) {
  return a + b
}

sumar(5, "3")       // Retorna "53" (string) en vez de 8
sumar(5)            // Retorna NaN (Not a Number)


TYPESCRIPT (con tipos):
-----------------------
let nombre: string = "Juan"
nombre = 123        // ERROR: Type 'number' is not assignable to type 'string'

function sumar(a: number, b: number): number {
  return a + b
}

sumar(5, "3")       // ERROR: Argument of type 'string' is not assignable
sumar(5)            // ERROR: Expected 2 arguments, but got 1


POR QUE USAR TYPESCRIPT
========================

VENTAJAS:
---------
1. DETECTA ERRORES ANTES DE EJECUTAR
   - El editor te marca los errores en rojo
   - No tienes que esperar a que la app falle
   
2. AUTOCOMPLETADO INTELIGENTE
   - El editor sabe que propiedades tiene cada objeto
   - Te sugiere opciones mientras escribes
   
3. DOCUMENTACION AUTOMATICA
   - Los tipos sirven como documentacion
   - Sabes que espera cada funcion sin leer comentarios
   
4. REFACTORING SEGURO
   - Si cambias algo, TypeScript te dice donde mas debes cambiar
   - No dejas cosas rotas por olvido
   
5. MEJOR PARA EQUIPOS
   - Todos entienden que tipo de datos maneja cada parte
   - Menos bugs, menos confusiones

DESVENTAJAS:
------------
1. MAS CODIGO QUE ESCRIBIR
   - Tienes que declarar tipos
   - Pero a la larga te ahorra tiempo
   
2. CURVA DE APRENDIZAJE
   - Al principio puede ser confuso
   - Pero los conceptos basicos son faciles


TIPOS BASICOS
=============

1. STRING (Texto)
-----------------
let nombre: string = "Maria"
let apellido: string = 'Gomez'
let mensaje: string = `Hola ${nombre}`  // Template literal


2. NUMBER (Numeros)
-------------------
let edad: number = 25
let precio: number = 19.99
let cantidad: number = -10


3. BOOLEAN (Verdadero o Falso)
-------------------------------
let estaActivo: boolean = true
let tieneDescuento: boolean = false


4. ARRAY (Listas)
-----------------
Forma 1:
let numeros: number[] = [1, 2, 3, 4, 5]
let nombres: string[] = ["Juan", "Maria", "Pedro"]

Forma 2 (generics):
let numeros: Array<number> = [1, 2, 3]
let nombres: Array<string> = ["Juan", "Maria"]


5. OBJECT (Objetos)
-------------------
// Forma basica
let usuario: object = { nombre: "Juan", edad: 25 }

// Forma especifica (mejor)
let usuario: { nombre: string; edad: number } = {
  nombre: "Juan",
  edad: 25
}


6. ANY (Cualquier tipo - NO RECOMENDADO)
-----------------------------------------
let variable: any = "Hola"
variable = 123          // OK
variable = true         // OK
variable = []           // OK

// Evitar any porque pierde el beneficio de TypeScript


7. UNKNOWN (Cualquier tipo pero mas seguro)
--------------------------------------------
let valor: unknown = "Hola"

// Tienes que verificar el tipo antes de usarlo
if (typeof valor === "string") {
  console.log(valor.toUpperCase())  // OK
}


8. NULL Y UNDEFINED
-------------------
let vacio: null = null
let noDefinido: undefined = undefined


9. VOID (Sin retorno)
---------------------
function saludar(): void {
  console.log("Hola")
  // No retorna nada
}


10. NEVER (Nunca retorna)
-------------------------
function error(mensaje: string): never {
  throw new Error(mensaje)
  // Esta funcion nunca termina normalmente
}


INTERFACES
==========

Las interfaces definen la estructura de un objeto.

EJEMPLO BASICO:
---------------
interface Usuario {
  id: number
  nombre: string
  email: string
  edad: number
}

const usuario: Usuario = {
  id: 1,
  nombre: "Juan",
  email: "juan@example.com",
  edad: 25
}


PROPIEDADES OPCIONALES:
-----------------------
interface Producto {
  id: number
  nombre: string
  precio: number
  descripcion?: string      // ? significa opcional
  imagen?: string
}

const producto1: Producto = {
  id: 1,
  nombre: "Pañales",
  precio: 25000
  // descripcion e imagen son opcionales
}


PROPIEDADES DE SOLO LECTURA:
-----------------------------
interface Config {
  readonly apiUrl: string
  readonly timeout: number
}

const config: Config = {
  apiUrl: "https://api.example.com",
  timeout: 5000
}

config.apiUrl = "otra-url"  // ERROR: Cannot assign to 'apiUrl' because it is read-only


INTERFACES EN BABYCASH
======================

// Usuario del sistema
interface User {
  id: number
  name: string
  email: string
  role: string
  createdAt: string
}

// Producto
interface Product {
  id: number
  name: string
  description: string
  price: number
  stock: number
  imageUrl: string
  categoryId: number
  createdAt: string
  updatedAt: string
}

// Item del carrito
interface CartItem {
  id: number
  productId: number
  product: Product
  quantity: number
  price: number
}

// Orden
interface Order {
  id: number
  userId: number
  totalAmount: number
  status: string
  orderItems: OrderItem[]
  createdAt: string
}


TYPE VS INTERFACE
=================

Son muy similares, pero hay diferencias sutiles.

INTERFACE:
----------
interface Usuario {
  nombre: string
  edad: number
}

// Se puede extender
interface Admin extends Usuario {
  permisos: string[]
}

// Se puede redeclarar (merge)
interface Usuario {
  email: string
}
// Ahora Usuario tiene: nombre, edad, email


TYPE:
-----
type Usuario = {
  nombre: string
  edad: number
}

// Se puede combinar con &
type Admin = Usuario & {
  permisos: string[]
}

// Type puede ser para cualquier cosa
type ID = number | string
type Callback = (data: string) => void


CUANDO USAR CADA UNO:
---------------------
✓ Usa INTERFACE para objetos y clases
✓ Usa TYPE para uniones, intersecciones, y tipos complejos

En BabyCash usamos mayormente INTERFACES para modelos de datos.


UNION TYPES (Tipos de Union)
=============================

Una variable puede ser de varios tipos:

type ID = number | string

let usuarioId: ID = 123        // OK
usuarioId = "abc-123"          // OK
usuarioId = true               // ERROR


Ejemplo practico:
type Estado = "pendiente" | "completado" | "cancelado"

let ordenEstado: Estado = "pendiente"      // OK
ordenEstado = "completado"                  // OK
ordenEstado = "en proceso"                  // ERROR


LITERAL TYPES
=============

Valores especificos como tipos:

let direccion: "izquierda" | "derecha" | "arriba" | "abajo"
direccion = "izquierda"    // OK
direccion = "diagonal"     // ERROR


type Rol = "ADMIN" | "USER" | "MODERATOR"

interface Usuario {
  nombre: string
  rol: Rol
}


TIPOS PARA FUNCIONES
====================

DECLARAR TIPOS DE PARAMETROS Y RETORNO:
----------------------------------------
function sumar(a: number, b: number): number {
  return a + b
}

function saludar(nombre: string): string {
  return `Hola ${nombre}`
}

function mostrarMensaje(mensaje: string): void {
  console.log(mensaje)
}


FUNCIONES COMO TIPO:
--------------------
type Callback = (mensaje: string) => void

function ejecutar(callback: Callback) {
  callback("Hola")
}

ejecutar((msg) => console.log(msg))


PARAMETROS OPCIONALES:
----------------------
function saludar(nombre: string, apellido?: string): string {
  if (apellido) {
    return `Hola ${nombre} ${apellido}`
  }
  return `Hola ${nombre}`
}

saludar("Juan")                    // OK
saludar("Juan", "Gomez")          // OK


PARAMETROS CON VALOR POR DEFECTO:
----------------------------------
function crear(nombre: string, activo: boolean = true) {
  // si no se pasa activo, sera true
}

crear("Producto")              // activo = true
crear("Producto", false)       // activo = false


GENERICS (Tipos Genericos)
==========================

Los generics permiten crear componentes reutilizables con diferentes tipos.

EJEMPLO BASICO:
---------------
function obtenerPrimero<T>(array: T[]): T {
  return array[0]
}

const numeros = [1, 2, 3]
const primero = obtenerPrimero(numeros)  // primero es number

const nombres = ["Juan", "Maria"]
const primerNombre = obtenerPrimero(nombres)  // primerNombre es string


INTERFACES GENERICAS:
---------------------
interface Respuesta<T> {
  success: boolean
  data: T
  message: string
}

// Respuesta con usuario
const respuestaUsuario: Respuesta<Usuario> = {
  success: true,
  data: { id: 1, nombre: "Juan", email: "juan@example.com" },
  message: "Usuario encontrado"
}

// Respuesta con productos
const respuestaProductos: Respuesta<Product[]> = {
  success: true,
  data: [producto1, producto2],
  message: "Productos encontrados"
}


GENERICS EN BABYCASH:
---------------------
// Respuesta generica de la API
interface ApiResponse<T> {
  data: T
  message: string
  success: boolean
}

// Usar con diferentes tipos
type UserResponse = ApiResponse<User>
type ProductsResponse = ApiResponse<Product[]>
type OrderResponse = ApiResponse<Order>


TYPESCRIPT EN REACT
===================

COMPONENTES FUNCIONALES:
------------------------
import React from 'react'

interface Props {
  nombre: string
  edad: number
  activo?: boolean
}

const Usuario: React.FC<Props> = ({ nombre, edad, activo = true }) => {
  return (
    <div>
      <h2>{nombre}</h2>
      <p>Edad: {edad}</p>
      <p>Estado: {activo ? "Activo" : "Inactivo"}</p>
    </div>
  )
}

export default Usuario


O sin React.FC (forma mas moderna):
------------------------------------
interface Props {
  nombre: string
  edad: number
}

function Usuario({ nombre, edad }: Props) {
  return (
    <div>
      <h2>{nombre}</h2>
      <p>Edad: {edad}</p>
    </div>
  )
}


HOOKS CON TYPESCRIPT:
---------------------

1. useState:
const [contador, setContador] = useState<number>(0)
const [nombre, setNombre] = useState<string>("")
const [usuario, setUsuario] = useState<Usuario | null>(null)

2. useEffect:
useEffect(() => {
  // codigo
}, [])

3. Custom hooks:
function useAuth(): { usuario: Usuario | null; login: (email: string, password: string) => void } {
  const [usuario, setUsuario] = useState<Usuario | null>(null)
  
  const login = (email: string, password: string) => {
    // logica
  }
  
  return { usuario, login }
}


EVENTOS EN REACT CON TYPESCRIPT:
---------------------------------
function Formulario() {
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    // codigo
  }
  
  const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    // codigo
  }
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // codigo
  }
  
  return (
    <form onSubmit={handleSubmit}>
      <input onChange={handleChange} />
      <button onClick={handleClick}>Enviar</button>
    </form>
  )
}


TIPOS COMUNES EN REACT:
-----------------------
React.ReactNode          - Cualquier cosa que React puede renderizar
React.ReactElement       - Un elemento React
React.FC                 - Functional Component
React.CSSProperties      - Estilos en linea
React.Ref                - Referencias


UTILITY TYPES (Tipos Utilitarios)
==================================

TypeScript incluye tipos utilitarios para transformar tipos existentes.

1. PARTIAL<T>
   Hace todas las propiedades opcionales
   
   interface Usuario {
     nombre: string
     email: string
     edad: number
   }
   
   type UsuarioParcial = Partial<Usuario>
   // Equivalente a:
   // { nombre?: string; email?: string; edad?: number }


2. REQUIRED<T>
   Hace todas las propiedades obligatorias
   
   interface Producto {
     nombre: string
     descripcion?: string
   }
   
   type ProductoCompleto = Required<Producto>
   // descripcion ya no es opcional


3. READONLY<T>
   Hace todas las propiedades de solo lectura
   
   type UsuarioInmutable = Readonly<Usuario>
   // No se pueden modificar las propiedades


4. PICK<T, Keys>
   Selecciona solo algunas propiedades
   
   type UsuarioBasico = Pick<Usuario, "nombre" | "email">
   // Solo tiene nombre y email


5. OMIT<T, Keys>
   Omite algunas propiedades
   
   type UsuarioSinEmail = Omit<Usuario, "email">
   // Tiene todo excepto email


6. RECORD<Keys, Type>
   Crea un objeto con keys especificas y un tipo de valor
   
   type Roles = "admin" | "user" | "moderator"
   type Permisos = Record<Roles, boolean>
   
   const permisos: Permisos = {
     admin: true,
     user: false,
     moderator: true
   }


EJEMPLO COMPLETO EN BABYCASH
=============================

// types/index.ts

// Enums
export enum UserRole {
  ADMIN = "ADMIN",
  USER = "USER",
  MODERATOR = "MODERATOR"
}

export enum OrderStatus {
  PENDING = "PENDING",
  COMPLETED = "COMPLETED",
  CANCELLED = "CANCELLED"
}

// Interfaces
export interface User {
  id: number
  name: string
  email: string
  role: UserRole
  createdAt: string
  updatedAt: string
}

export interface Product {
  id: number
  name: string
  description: string
  price: number
  stock: number
  imageUrl: string
  categoryId: number
}

export interface CartItem {
  id: number
  productId: number
  product: Product
  quantity: number
  subtotal: number
}

export interface Order {
  id: number
  userId: number
  user: User
  items: CartItem[]
  totalAmount: number
  status: OrderStatus
  createdAt: string
}

// DTOs (Data Transfer Objects)
export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  name: string
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  user: User
}

// API Response generica
export interface ApiResponse<T> {
  success: boolean
  data: T
  message: string
}

// Props de componentes
export interface ProductCardProps {
  product: Product
  onAddToCart: (product: Product) => void
}

export interface ButtonProps {
  children: React.ReactNode
  onClick?: () => void
  variant?: "primary" | "secondary" | "danger"
  disabled?: boolean
}


ERRORES COMUNES Y SOLUCIONES
=============================

1. ERROR: Property 'X' does not exist on type 'Y'
   SOLUCION: Verifica que la propiedad este definida en la interface

2. ERROR: Type 'null' is not assignable to type 'X'
   SOLUCION: Usa union type: X | null

3. ERROR: Argument of type 'string' is not assignable to parameter of type 'number'
   SOLUCION: Convierte el tipo: Number(string) o parseInt(string)

4. ERROR: Object is possibly 'undefined'
   SOLUCION: Usa optional chaining: objeto?.propiedad
   O verifica antes: if (objeto) { objeto.propiedad }


CONFIGURACION TYPESCRIPT
========================

tsconfig.json - Archivo de configuracion:

{
  "compilerOptions": {
    "target": "ES2020",                    // Version de JavaScript
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "jsx": "react-jsx",                    // Soporte para JSX
    "module": "ESNext",
    "moduleResolution": "bundler",
    "strict": true,                        // Modo estricto
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true
  },
  "include": ["src"],                      // Archivos a incluir
  "exclude": ["node_modules"]              // Archivos a excluir
}


CONSEJOS PRACTICOS
===================

1. EMPIEZA SIMPLE
   - No te compliques con tipos avanzados al principio
   - Usa any temporalmente si estas bloqueado (pero luego arreglalo)

2. USA INTERFACES PARA DATOS
   - Define interfaces para todos los modelos
   - Mantenlas en un archivo types/index.ts

3. DEJA QUE TYPESCRIPT INFIERA
   - No siempre tienes que declarar tipos
   - TypeScript es inteligente y puede inferir muchos tipos
   
   const nombre = "Juan"  // TypeScript sabe que es string
   const edad = 25        // TypeScript sabe que es number

4. USA EL AUTOCOMPLETADO
   - El editor te muestra que propiedades tiene cada objeto
   - Aprovecha esto mientras escribes codigo

5. LEE LOS ERRORES
   - Los mensajes de error de TypeScript son descriptivos
   - Te dicen exactamente que esta mal y donde


RESUMEN
=======

TypeScript es JavaScript con tipos:
✓ Detecta errores antes de ejecutar
✓ Autocompletado inteligente
✓ Codigo mas seguro y mantenible
✓ Mejor para trabajar en equipo

Conceptos clave:
- Tipos basicos: string, number, boolean, array, object
- Interfaces: Definen estructura de objetos
- Types: Para tipos mas complejos
- Generics: Componentes reutilizables con diferentes tipos
- Utility Types: Partial, Required, Pick, Omit, etc

En BabyCash usamos TypeScript para:
✓ Definir modelos de datos (User, Product, Order)
✓ Tipar componentes React
✓ Tipar llamadas a API
✓ Prevenir errores en desarrollo

================================================================================
