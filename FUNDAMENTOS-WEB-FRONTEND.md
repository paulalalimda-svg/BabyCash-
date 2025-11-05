# 📚 Fundamentos Web y Frontend - Completo

Guía exhaustiva de HTML, CSS, JavaScript, TypeScript, React y desarrollo web moderno.

---

## 📋 Tabla de Contenidos

1. [Fundamentos de la Web](#fundamentos-de-la-web)
2. [HTML - Estructura](#html---estructura)
3. [CSS - Estilos](#css---estilos)
4. [JavaScript - Programación](#javascript---programación)
5. [TypeScript - Tipado Estático](#typescript---tipado-estático)
6. [React - Librer

ía UI](#react---librería-ui)
7. [HTTP y APIs](#http-y-apis)
8. [Herramientas Modernas](#herramientas-modernas)

---

## 🌐 Fundamentos de la Web

### Cómo Funciona la Web

```
Cliente (Navegador)          Servidor
     │                          │
     │──── HTTP Request ───────→│
     │    (GET /products)        │
     │                           │
     │←─── HTTP Response ────────│
     │    (HTML, JSON, etc.)     │
```

### Componentes Principales

1. **Cliente (Frontend)**
   - Navegador web (Chrome, Firefox, Safari)
   - Renderiza HTML, CSS, JavaScript
   - Interactúa con el usuario

2. **Servidor (Backend)**
   - Procesa peticiones
   - Accede a base de datos
   - Devuelve respuestas

3. **Protocolo HTTP/HTTPS**
   - Protocolo de comunicación
   - Peticiones y respuestas
   - Estados y métodos

---

## 📄 HTML - Estructura

### ¿Qué es HTML?

**HTML (HyperText Markup Language)** es el lenguaje de marcado que define la **estructura** del contenido web.

### Anatomía de un Documento HTML

```html
<!DOCTYPE html>
<html lang="es">
  <head>
    <!-- Metadatos -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Baby Cash - E-commerce</title>
    <link rel="stylesheet" href="styles.css">
  </head>
  <body>
    <!-- Contenido visible -->
    <header>
      <h1>Baby Cash</h1>
      <nav>
        <a href="/">Inicio</a>
        <a href="/products">Productos</a>
      </nav>
    </header>
    
    <main>
      <h2>Productos Destacados</h2>
      <div class="products">
        <!-- Contenido dinámico -->
      </div>
    </main>
    
    <footer>
      <p>&copy; 2025 Baby Cash</p>
    </footer>
    
    <script src="app.js"></script>
  </body>
</html>
```

### Etiquetas HTML Esenciales

#### Estructura

```html
<!-- Encabezados (h1 más importante, h6 menos) -->
<h1>Título Principal</h1>
<h2>Subtítulo</h2>
<h3>Sección</h3>

<!-- Párrafos -->
<p>Este es un párrafo de texto.</p>

<!-- Divisores (contenedores genéricos) -->
<div class="container">
  <span>Elemento en línea</span>
</div>

<!-- Secciones semánticas (HTML5) -->
<header>Encabezado del sitio</header>
<nav>Navegación</nav>
<main>Contenido principal</main>
<article>Artículo independiente</article>
<section>Sección temática</section>
<aside>Contenido lateral</aside>
<footer>Pie de página</footer>
```

#### Texto

```html
<!-- Énfasis -->
<strong>Texto importante (negrita)</strong>
<em>Texto con énfasis (cursiva)</em>
<mark>Texto resaltado</mark>

<!-- Saltos y líneas -->
<br>  <!-- Salto de línea -->
<hr>  <!-- Línea horizontal -->

<!-- Citas -->
<blockquote>Cita en bloque</blockquote>
<q>Cita corta</q>
```

#### Listas

```html
<!-- Lista no ordenada -->
<ul>
  <li>Elemento 1</li>
  <li>Elemento 2</li>
  <li>Elemento 3</li>
</ul>

<!-- Lista ordenada -->
<ol>
  <li>Primer paso</li>
  <li>Segundo paso</li>
  <li>Tercer paso</li>
</ol>

<!-- Lista de definiciones -->
<dl>
  <dt>HTML</dt>
  <dd>Lenguaje de marcado</dd>
  <dt>CSS</dt>
  <dd>Lenguaje de estilos</dd>
</dl>
```

#### Enlaces e Imágenes

```html
<!-- Enlaces -->
<a href="https://babycash.com">Ir a Baby Cash</a>
<a href="/products">Productos</a>
<a href="#seccion">Ir a sección</a>
<a href="mailto:info@babycash.com">Contacto</a>

<!-- Imágenes -->
<img src="product.jpg" alt="Descripción del producto">
<img src="logo.png" alt="Logo" width="200" height="100">
```

#### Formularios

```html
<form action="/api/products" method="POST">
  <!-- Input de texto -->
  <label for="name">Nombre:</label>
  <input type="text" id="name" name="name" required>
  
  <!-- Input de email -->
  <label for="email">Email:</label>
  <input type="email" id="email" name="email" required>
  
  <!-- Input de contraseña -->
  <label for="password">Contraseña:</label>
  <input type="password" id="password" name="password" required>
  
  <!-- Input de número -->
  <label for="age">Edad:</label>
  <input type="number" id="age" name="age" min="18" max="100">
  
  <!-- Checkbox -->
  <label>
    <input type="checkbox" name="terms" required>
    Acepto términos y condiciones
  </label>
  
  <!-- Radio buttons -->
  <fieldset>
    <legend>Género:</legend>
    <label><input type="radio" name="gender" value="M"> Masculino</label>
    <label><input type="radio" name="gender" value="F"> Femenino</label>
  </fieldset>
  
  <!-- Select (desplegable) -->
  <label for="category">Categoría:</label>
  <select id="category" name="category">
    <option value="">Seleccione...</option>
    <option value="1">Pañales</option>
    <option value="2">Ropa</option>
    <option value="3">Juguetes</option>
  </select>
  
  <!-- Textarea -->
  <label for="description">Descripción:</label>
  <textarea id="description" name="description" rows="4"></textarea>
  
  <!-- Botones -->
  <button type="submit">Enviar</button>
  <button type="reset">Limpiar</button>
</form>
```

#### Tablas

```html
<table>
  <thead>
    <tr>
      <th>Producto</th>
      <th>Precio</th>
      <th>Stock</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Pañales Baby Cash</td>
      <td>$29.99</td>
      <td>50</td>
    </tr>
    <tr>
      <td>Biberón</td>
      <td>$9.99</td>
      <td>30</td>
    </tr>
  </tbody>
  <tfoot>
    <tr>
      <td colspan="2">Total productos:</td>
      <td>80</td>
    </tr>
  </tfoot>
</table>
```

---

## 🎨 CSS - Estilos

### ¿Qué es CSS?

**CSS (Cascading Style Sheets)** controla el **aspecto visual** de los elementos HTML.

### Formas de Incluir CSS

```html
<!-- 1. Inline (directamente en el elemento) -->
<p style="color: blue; font-size: 16px;">Texto azul</p>

<!-- 2. Interno (en el <head>) -->
<head>
  <style>
    p {
      color: blue;
      font-size: 16px;
    }
  </style>
</head>

<!-- 3. Externo (archivo separado) - RECOMENDADO -->
<head>
  <link rel="stylesheet" href="styles.css">
</head>
```

### Selectores CSS

```css
/* Selector de elemento */
p {
  color: blue;
}

/* Selector de clase */
.destacado {
  font-weight: bold;
}

/* Selector de ID */
#header {
  background-color: #333;
}

/* Selector descendente */
div p {
  margin: 10px;
}

/* Selector hijo directo */
ul > li {
  list-style: none;
}

/* Selector de atributo */
input[type="text"] {
  border: 1px solid #ccc;
}

/* Pseudo-clases */
a:hover {
  color: red;
}

button:active {
  transform: scale(0.95);
}

input:focus {
  outline: 2px solid blue;
}

/* Pseudo-elementos */
p::first-line {
  font-weight: bold;
}

p::before {
  content: "→ ";
}
```

### Propiedades Esenciales

#### Colores y Fondos

```css
.elemento {
  /* Colores */
  color: #333;                    /* Hexadecimal */
  color: rgb(51, 51, 51);         /* RGB */
  color: rgba(51, 51, 51, 0.5);   /* RGB con transparencia */
  color: hsl(0, 0%, 20%);         /* HSL */
  
  /* Fondos */
  background-color: #f0f0f0;
  background-image: url('bg.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}
```

#### Texto

```css
.texto {
  font-family: 'Arial', sans-serif;
  font-size: 16px;
  font-weight: bold;              /* normal, bold, 100-900 */
  font-style: italic;
  line-height: 1.5;
  text-align: center;             /* left, right, center, justify */
  text-decoration: underline;
  text-transform: uppercase;      /* lowercase, capitalize */
  letter-spacing: 2px;
  word-spacing: 5px;
}
```

#### Caja (Box Model)

```css
.caja {
  /* Dimensiones */
  width: 300px;
  height: 200px;
  max-width: 100%;
  min-height: 100px;
  
  /* Espaciado interno */
  padding: 20px;                  /* Todos los lados */
  padding: 10px 20px;             /* Vertical | Horizontal */
  padding: 10px 20px 15px 25px;   /* Top | Right | Bottom | Left */
  
  /* Margen externo */
  margin: 20px;
  margin: 0 auto;                 /* Centrar horizontalmente */
  
  /* Borde */
  border: 2px solid #333;
  border-radius: 8px;
  
  /* Sombra */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
```

**Box Model Visual**:
```
┌─────── margin ───────┐
│ ┌───── border ─────┐ │
│ │ ┌─── padding ──┐ │ │
│ │ │   content    │ │ │
│ │ └──────────────┘ │ │
│ └──────────────────┘ │
└──────────────────────┘
```

#### Flexbox (Layout Flexible)

```css
.container {
  display: flex;
  
  /* Dirección */
  flex-direction: row;            /* row, column, row-reverse, column-reverse */
  
  /* Alineación horizontal */
  justify-content: center;        /* flex-start, flex-end, center, space-between, space-around */
  
  /* Alineación vertical */
  align-items: center;            /* flex-start, flex-end, center, stretch, baseline */
  
  /* Envolver elementos */
  flex-wrap: wrap;                /* nowrap, wrap, wrap-reverse */
  
  /* Espacio entre elementos */
  gap: 20px;
}

.item {
  flex: 1;                        /* Grow | Shrink | Basis */
  flex-grow: 1;                   /* Crecer para llenar espacio */
  flex-shrink: 0;                 /* No encoger */
  flex-basis: 200px;              /* Tamaño base */
}
```

**Ejemplo Práctico**:
```html
<div class="nav">
  <div class="logo">Logo</div>
  <div class="links">
    <a href="/">Inicio</a>
    <a href="/products">Productos</a>
  </div>
</div>

<style>
.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #333;
  color: white;
}

.links {
  display: flex;
  gap: 20px;
}
</style>
```

#### Grid (Layout de Cuadrícula)

```css
.grid-container {
  display: grid;
  
  /* Columnas */
  grid-template-columns: 1fr 1fr 1fr;     /* 3 columnas iguales */
  grid-template-columns: 200px auto 200px; /* Fija | Auto | Fija */
  grid-template-columns: repeat(4, 1fr);   /* 4 columnas iguales */
  
  /* Filas */
  grid-template-rows: 100px auto;
  
  /* Espacio entre celdas */
  gap: 20px;
  grid-gap: 20px;                          /* Deprecated */
  
  /* Áreas nombradas */
  grid-template-areas:
    "header header header"
    "sidebar main main"
    "footer footer footer";
}

.header { grid-area: header; }
.sidebar { grid-area: sidebar; }
.main { grid-area: main; }
.footer { grid-area: footer; }
```

#### Posicionamiento

```css
.elemento {
  position: static;               /* Por defecto */
  position: relative;             /* Relativo a su posición original */
  position: absolute;             /* Relativo al padre posicionado */
  position: fixed;                /* Relativo al viewport */
  position: sticky;               /* Híbrido relativo/fijo */
  
  top: 10px;
  right: 20px;
  bottom: 10px;
  left: 20px;
  
  z-index: 100;                   /* Orden de apilamiento */
}
```

### Responsive Design (Diseño Adaptable)

```css
/* Mobile first */
.container {
  width: 100%;
  padding: 10px;
}

/* Tablet (768px y más) */
@media (min-width: 768px) {
  .container {
    width: 750px;
    padding: 20px;
  }
}

/* Desktop (1024px y más) */
@media (min-width: 1024px) {
  .container {
    width: 1000px;
    padding: 30px;
  }
}

/* Breakpoints comunes */
/* 
  Mobile: < 768px
  Tablet: 768px - 1024px
  Desktop: > 1024px
*/
```

---

## 💻 JavaScript - Programación

### ¿Qué es JavaScript?

**JavaScript** es el lenguaje de programación que añade **interactividad** a las páginas web.

### Sintaxis Básica

```javascript
// Variables
let nombre = "Juan";           // Variable que puede cambiar
const PI = 3.14159;            // Constante (no cambia)
var antigua = "evitar";        // Forma antigua (no usar)

// Tipos de datos
let numero = 42;               // Number
let texto = "Hola";            // String
let booleano = true;           // Boolean
let nulo = null;               // Null
let indefinido = undefined;    // Undefined
let objeto = { nombre: "Juan" }; // Object
let array = [1, 2, 3];         // Array
```

### Funciones

```javascript
// Función tradicional
function sumar(a, b) {
  return a + b;
}

// Arrow function (ES6+)
const sumar = (a, b) => a + b;

// Arrow function con bloque
const calcular = (a, b) => {
  const resultado = a + b;
  return resultado;
};

// Función como parámetro (callback)
const numeros = [1, 2, 3, 4, 5];
const dobles = numeros.map(n => n * 2);  // [2, 4, 6, 8, 10]
```

### Manipulación del DOM

```javascript
// Seleccionar elementos
const elemento = document.getElementById('miId');
const elementos = document.getElementsByClassName('miClase');
const primero = document.querySelector('.miClase');
const todos = document.querySelectorAll('.miClase');

// Modificar contenido
elemento.textContent = "Nuevo texto";
elemento.innerHTML = "<strong>HTML</strong>";

// Modificar atributos
elemento.setAttribute('class', 'nueva-clase');
elemento.classList.add('activo');
elemento.classList.remove('inactivo');
elemento.classList.toggle('visible');

// Modificar estilos
elemento.style.color = 'blue';
elemento.style.backgroundColor = '#f0f0f0';

// Crear elementos
const nuevoDiv = document.createElement('div');
nuevoDiv.textContent = "Nuevo elemento";
document.body.appendChild(nuevoDiv);

// Eliminar elementos
elemento.remove();
```

### Eventos

```javascript
// Event listeners
const boton = document.querySelector('#miBoton');

boton.addEventListener('click', function() {
  console.log('Click!');
});

// Arrow function
boton.addEventListener('click', () => {
  console.log('Click con arrow function');
});

// Evento con parámetro
boton.addEventListener('click', (event) => {
  event.preventDefault();  // Prevenir comportamiento por defecto
  console.log('Target:', event.target);
});

// Eventos comunes
element.addEventListener('click', handler);
element.addEventListener('dblclick', handler);
element.addEventListener('mouseenter', handler);
element.addEventListener('mouseleave', handler);
input.addEventListener('change', handler);
input.addEventListener('input', handler);  // En tiempo real
input.addEventListener('focus', handler);
input.addEventListener('blur', handler);
form.addEventListener('submit', handler);
```

### Async/Await y Promesas

```javascript
// Promise (promesa)
function obtenerDatos() {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve({ nombre: "Juan", edad: 25 });
    }, 1000);
  });
}

// .then()
obtenerDatos()
  .then(datos => console.log(datos))
  .catch(error => console.error(error));

// async/await (RECOMENDADO)
async function cargarUsuario() {
  try {
    const respuesta = await fetch('/api/user/1');
    const datos = await respuesta.json();
    console.log(datos);
  } catch (error) {
    console.error('Error:', error);
  }
}
```

### Fetch API (Peticiones HTTP)

```javascript
// GET
async function obtenerProductos() {
  const respuesta = await fetch('http://localhost:8080/api/products');
  const productos = await respuesta.json();
  return productos;
}

// POST
async function crearProducto(producto) {
  const respuesta = await fetch('http://localhost:8080/api/products', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(producto)
  });
  const nuevoProducto = await respuesta.json();
  return nuevoProducto;
}

// PUT
async function actualizarProducto(id, producto) {
  const respuesta = await fetch(`http://localhost:8080/api/products/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(producto)
  });
  return await respuesta.json();
}

// DELETE
async function eliminarProducto(id) {
  await fetch(`http://localhost:8080/api/products/${id}`, {
    method: 'DELETE'
  });
}
```

### ES6+ Features

```javascript
// Destructuring
const persona = { nombre: "Juan", edad: 25 };
const { nombre, edad } = persona;

const numeros = [1, 2, 3];
const [primero, segundo] = numeros;

// Spread operator
const arr1 = [1, 2, 3];
const arr2 = [...arr1, 4, 5];  // [1, 2, 3, 4, 5]

const obj1 = { a: 1, b: 2 };
const obj2 = { ...obj1, c: 3 };  // { a: 1, b: 2, c: 3 }

// Template literals
const nombre = "Juan";
const saludo = `Hola, ${nombre}!`;  // "Hola, Juan!"

// Optional chaining
const usuario = { perfil: { nombre: "Juan" } };
const nombre = usuario?.perfil?.nombre;  // "Juan"
const apellido = usuario?.perfil?.apellido;  // undefined (no error)

// Nullish coalescing
const valor = null ?? "default";  // "default"
const valor2 = 0 ?? "default";    // 0 (solo null/undefined)
```

---

## 📘 TypeScript - Tipado Estático

### ¿Qué es TypeScript?

**TypeScript** es un superconjunto de JavaScript que añade **tipos estáticos**.

### Tipos Básicos

```typescript
// Primitivos
let nombre: string = "Juan";
let edad: number = 25;
let activo: boolean = true;

// Arrays
let numeros: number[] = [1, 2, 3];
let nombres: Array<string> = ["Ana", "Bob"];

// Tuplas
let persona: [string, number] = ["Juan", 25];

// Enum
enum Color {
  Rojo,
  Verde,
  Azul
}
let color: Color = Color.Rojo;

// Any (evitar)
let cualquierCosa: any = "texto";
cualquierCosa = 42;

// Unknown (mejor que any)
let valor: unknown = "texto";
if (typeof valor === "string") {
  console.log(valor.toUpperCase());
}

// Never (nunca retorna)
function error(mensaje: string): never {
  throw new Error(mensaje);
}
```

### Interfaces

```typescript
// Interface de objeto
interface Product {
  id: number;
  name: string;
  price: number;
  description?: string;  // Opcional
  readonly createdAt: Date;  // Solo lectura
}

const producto: Product = {
  id: 1,
  name: "Pañales",
  price: 29.99,
  createdAt: new Date()
};

// Interface de función
interface CalcularDescuento {
  (precio: number, descuento: number): number;
}

const calcular: CalcularDescuento = (precio, descuento) => {
  return precio * (1 - descuento);
};
```

### Types

```typescript
// Type alias
type ID = number | string;
type Status = 'pending' | 'shipped' | 'delivered';

// Union types
let id: ID = 123;
id = "abc-123";

// Intersection types
type Usuario = {
  nombre: string;
  email: string;
};

type Admin = Usuario & {
  permisos: string[];
};

const admin: Admin = {
  nombre: "Juan",
  email: "juan@example.com",
  permisos: ["read", "write"]
};
```

### Generics

```typescript
// Función genérica
function primero<T>(array: T[]): T {
  return array[0];
}

const num = primero([1, 2, 3]);      // number
const str = primero(["a", "b", "c"]); // string

// Interface genérica
interface ApiResponse<T> {
  data: T;
  status: number;
  message: string;
}

const response: ApiResponse<Product> = {
  data: { id: 1, name: "Producto", price: 29.99 },
  status: 200,
  message: "Success"
};
```

---

## ⚛️ React - Librería UI

### Componentes Funcionales

```tsx
import React from 'react';

// Componente simple
function Saludo() {
  return <h1>Hola Mundo</h1>;
}

// Con props
interface SaludoProps {
  nombre: string;
}

function Saludo({ nombre }: SaludoProps) {
  return <h1>Hola, {nombre}!</h1>;
}

// Uso
<Saludo nombre="Juan" />
```

### useState (Estado)

```tsx
import { useState } from 'react';

function Contador() {
  const [contador, setContador] = useState(0);
  
  const incrementar = () => {
    setContador(contador + 1);
  };
  
  return (
    <div>
      <p>Contador: {contador}</p>
      <button onClick={incrementar}>Incrementar</button>
    </div>
  );
}
```

### useEffect (Efectos Secundarios)

```tsx
import { useState, useEffect } from 'react';

function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    // Se ejecuta después del render
    async function fetchProducts() {
      const response = await fetch('/api/products');
      const data = await response.json();
      setProducts(data);
      setLoading(false);
    }
    
    fetchProducts();
  }, []); // [] = solo una vez al montar
  
  if (loading) return <p>Cargando...</p>;
  
  return (
    <ul>
      {products.map(product => (
        <li key={product.id}>{product.name}</li>
      ))}
    </ul>
  );
}
```

### Ejemplo Completo: Formulario

```tsx
import { useState } from 'react';

interface Product {
  name: string;
  price: number;
  description: string;
}

function ProductForm() {
  const [product, setProduct] = useState<Product>({
    name: '',
    price: 0,
    description: ''
  });
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setProduct(prev => ({
      ...prev,
      [name]: name === 'price' ? parseFloat(value) : value
    }));
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const response = await fetch('/api/products', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(product)
    });
    
    if (response.ok) {
      alert('Producto creado!');
      setProduct({ name: '', price: 0, description: '' });
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        name="name"
        value={product.name}
        onChange={handleChange}
        placeholder="Nombre"
        required
      />
      
      <input
        type="number"
        name="price"
        value={product.price}
        onChange={handleChange}
        placeholder="Precio"
        required
      />
      
      <input
        type="text"
        name="description"
        value={product.description}
        onChange={handleChange}
        placeholder="Descripción"
      />
      
      <button type="submit">Crear Producto</button>
    </form>
  );
}
```

---

## 🌐 HTTP y APIs

### Métodos HTTP

| Método | Propósito | Ejemplo |
|--------|-----------|---------|
| GET | Obtener datos | `GET /api/products` |
| POST | Crear nuevo | `POST /api/products` |
| PUT | Actualizar completo | `PUT /api/products/1` |
| PATCH | Actualizar parcial | `PATCH /api/products/1` |
| DELETE | Eliminar | `DELETE /api/products/1` |

### Códigos de Estado

| Código | Significado | Descripción |
|--------|-------------|-------------|
| 200 | OK | Petición exitosa |
| 201 | Created | Recurso creado |
| 204 | No Content | Éxito sin contenido |
| 400 | Bad Request | Petición mal formada |
| 401 | Unauthorized | No autenticado |
| 403 | Forbidden | Sin permisos |
| 404 | Not Found | Recurso no encontrado |
| 500 | Internal Server Error | Error del servidor |

### REST API Example

```typescript
// Service para productos
class ProductService {
  private baseURL = 'http://localhost:8080/api/products';
  
  async getAll(): Promise<Product[]> {
    const response = await fetch(this.baseURL);
    return await response.json();
  }
  
  async getById(id: number): Promise<Product> {
    const response = await fetch(`${this.baseURL}/${id}`);
    if (!response.ok) throw new Error('Producto no encontrado');
    return await response.json();
  }
  
  async create(product: Omit<Product, 'id'>): Promise<Product> {
    const response = await fetch(this.baseURL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(product)
    });
    return await response.json();
  }
  
  async update(id: number, product: Partial<Product>): Promise<Product> {
    const response = await fetch(`${this.baseURL}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(product)
    });
    return await response.json();
  }
  
  async delete(id: number): Promise<void> {
    await fetch(`${this.baseURL}/${id}`, {
      method: 'DELETE'
    });
  }
}
```

---

## 🛠️ Herramientas Modernas

### NPM (Node Package Manager)

```bash
# Inicializar proyecto
npm init -y

# Instalar dependencias
npm install react react-dom
npm install -D typescript @types/react

# Desinstalar
npm uninstall react

# Actualizar
npm update

# Scripts en package.json
npm run dev
npm run build
npm test
```

### Vite (Build Tool)

```bash
# Crear proyecto
npm create vite@latest my-app -- --template react-ts

# Comandos
npm run dev      # Desarrollo
npm run build    # Producción
npm run preview  # Preview de build
```

### Git (Control de Versiones)

```bash
# Inicializar
git init

# Ver cambios
git status
git diff

# Agregar cambios
git add .
git add archivo.ts

# Commit
git commit -m "mensaje descriptivo"

# Push
git push origin master

# Pull
git pull origin master

# Branches
git checkout -b feature/nueva-funcionalidad
git checkout master
git merge feature/nueva-funcionalidad
```

---

## ✅ Resumen y Mejores Prácticas

### Frontend Checklist

- [ ] HTML semántico
- [ ] CSS responsive (mobile-first)
- [ ] JavaScript moderno (ES6+)
- [ ] TypeScript para type safety
- [ ] Componentes reutilizables (React)
- [ ] Estado manejado correctamente
- [ ] Peticiones async con error handling
- [ ] Validación de formularios
- [ ] Accesibilidad (a11y)
- [ ] Performance optimizations

### Rendimiento

- ✅ Lazy loading de imágenes
- ✅ Code splitting
- ✅ Minimizar re-renders
- ✅ Usar React.memo para componentes
- ✅ Optimizar imágenes
- ✅ Caché de API calls

### Seguridad

- ✅ Sanitizar inputs
- ✅ HTTPS
- ✅ CORS configurado
- ✅ Validar en backend
- ✅ Tokens seguros (JWT)
- ✅ No exponer secrets en frontend

---

**Documento creado**: 4 de Noviembre de 2025  
**Propósito**: Fundamentos completos de desarrollo web y frontend  
**Proyecto**: Baby Cash - SENA
