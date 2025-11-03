================================================================================
ARCHIVO 45: TAILWIND CSS
================================================================================

QUE ES TAILWIND CSS
====================

Tailwind es un framework de CSS que usa CLASES UTILITARIAS.

En vez de escribir CSS tradicional, usas clases predefinidas directamente
en tus elementos HTML/JSX.

ANALOGIA:
CSS tradicional es como cocinar desde cero (cortar, mezclar, cocinar).
Tailwind es como usar ingredientes pre-procesados (listo para usar).


CSS TRADICIONAL VS TAILWIND
============================

CSS TRADICIONAL:
----------------
<button class="mi-boton">Click</button>

<style>
.mi-boton {
  background-color: blue;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  font-weight: 600;
}

.mi-boton:hover {
  background-color: darkblue;
}
</style>


TAILWIND:
---------
<button class="bg-blue-500 text-white px-4 py-2 rounded font-semibold hover:bg-blue-600">
  Click
</button>

No necesitas CSS separado. Todo esta en las clases.


VENTAJAS DE TAILWIND
====================

1. RAPIDO DE DESARROLLAR
   - No tienes que pensar nombres de clases
   - No tienes que cambiar entre archivos
   
2. CONSISTENTE
   - Usa una escala predefinida de colores, espaciados, tamaños
   - Todo se ve uniforme
   
3. RESPONSIVE POR DEFECTO
   - Facil hacer diseños para movil, tablet, desktop
   
4. NO CRECE EL CSS
   - Solo se incluyen las clases que usas
   - Archivos CSS pequeños
   
5. FACIL DE MANTENER
   - Cambias las clases y ya
   - No hay CSS huerfano


INSTALACION EN BABYCASH
========================

Ya esta instalado en el proyecto. Ver en:
- package.json (dependencias)
- tailwind.config.js (configuracion)
- index.css (imports de Tailwind)


CLASES BASICAS DE TAILWIND
===========================

1. COLORES
----------

TEXTO:
text-black      - Negro
text-white      - Blanco
text-gray-500   - Gris (100-900)
text-red-500    - Rojo
text-blue-500   - Azul
text-green-500  - Verde
text-yellow-500 - Amarillo

FONDO:
bg-black
bg-white
bg-gray-100
bg-blue-500
bg-red-500

BORDES:
border-gray-300
border-blue-500


Escala de colores (50 mas claro, 900 mas oscuro):
text-blue-50
text-blue-100
text-blue-200
...
text-blue-900


2. ESPACIADO (PADDING Y MARGIN)
--------------------------------

PADDING:
p-4         - padding: 1rem (16px) en todos los lados
px-4        - padding horizontal (left y right)
py-4        - padding vertical (top y bottom)
pt-4        - padding-top
pr-4        - padding-right
pb-4        - padding-bottom
pl-4        - padding-left

MARGIN:
m-4         - margin en todos los lados
mx-4        - margin horizontal
my-4        - margin vertical
mt-4        - margin-top
mr-4        - margin-right
mb-4        - margin-bottom
ml-4        - margin-left

ESCALA:
0  = 0px
1  = 0.25rem = 4px
2  = 0.5rem  = 8px
3  = 0.75rem = 12px
4  = 1rem    = 16px
5  = 1.25rem = 20px
6  = 1.5rem  = 24px
8  = 2rem    = 32px
10 = 2.5rem  = 40px
12 = 3rem    = 48px


Ejemplo:
<div class="p-4 m-2">             padding 16px, margin 8px
<div class="px-6 py-3">           padding horizontal 24px, vertical 12px
<div class="mt-8 mb-4">           margin-top 32px, margin-bottom 16px


3. TAMAÑO (WIDTH Y HEIGHT)
---------------------------

WIDTH:
w-full      - width: 100%
w-1/2       - width: 50%
w-1/3       - width: 33.33%
w-1/4       - width: 25%
w-screen    - width: 100vw
w-auto      - width: auto
w-12        - width: 3rem (48px)

HEIGHT:
h-full      - height: 100%
h-screen    - height: 100vh
h-12        - height: 3rem
h-auto      - height: auto

MAX/MIN:
max-w-md    - max-width: 28rem
max-w-lg    - max-width: 32rem
max-w-xl    - max-width: 36rem
max-w-full  - max-width: 100%
min-h-screen - min-height: 100vh


4. FLEXBOX
----------

CONTENEDOR FLEX:
flex            - display: flex
flex-row        - flex-direction: row (horizontal)
flex-col        - flex-direction: column (vertical)
flex-wrap       - flex-wrap: wrap

JUSTIFICAR (horizontal en row, vertical en col):
justify-start   - al inicio
justify-center  - centrado
justify-end     - al final
justify-between - espacio entre elementos
justify-around  - espacio alrededor de elementos

ALINEAR (vertical en row, horizontal en col):
items-start     - al inicio
items-center    - centrado
items-end       - al final
items-stretch   - estirar

GAP (espacio entre elementos):
gap-2           - gap: 0.5rem
gap-4           - gap: 1rem
gap-6           - gap: 1.5rem


Ejemplos:
<div class="flex justify-center items-center">
  Centrado horizontal y vertical
</div>

<div class="flex flex-col gap-4">
  <div>Item 1</div>
  <div>Item 2</div>
  <div>Item 3</div>
</div>


5. GRID
-------

grid                - display: grid
grid-cols-2         - 2 columnas
grid-cols-3         - 3 columnas
grid-cols-4         - 4 columnas
grid-rows-2         - 2 filas
gap-4               - gap entre elementos

col-span-2          - ocupa 2 columnas
row-span-2          - ocupa 2 filas


Ejemplo:
<div class="grid grid-cols-3 gap-4">
  <div>1</div>
  <div>2</div>
  <div>3</div>
  <div>4</div>
  <div>5</div>
  <div>6</div>
</div>


6. TIPOGRAFIA
-------------

TAMAÑO:
text-xs     - 0.75rem (12px)
text-sm     - 0.875rem (14px)
text-base   - 1rem (16px)
text-lg     - 1.125rem (18px)
text-xl     - 1.25rem (20px)
text-2xl    - 1.5rem (24px)
text-3xl    - 1.875rem (30px)
text-4xl    - 2.25rem (36px)

PESO:
font-thin       - 100
font-light      - 300
font-normal     - 400
font-medium     - 500
font-semibold   - 600
font-bold       - 700
font-extrabold  - 800

ALINEACION:
text-left
text-center
text-right
text-justify

OTROS:
uppercase       - TEXTO EN MAYUSCULAS
lowercase       - texto en minusculas
capitalize      - Cada Palabra Con Mayuscula
underline       - subrayado
line-through    - tachado


7. BORDES
---------

ANCHO:
border          - 1px
border-2        - 2px
border-4        - 4px
border-0        - sin borde

LADOS:
border-t        - top
border-r        - right
border-b        - bottom
border-l        - left

RADIUS (redondeo):
rounded-none    - sin redondeo
rounded-sm      - pequeno
rounded         - normal
rounded-md      - mediano
rounded-lg      - grande
rounded-full    - completamente redondo (circulo/pill)

rounded-t       - redondeo arriba
rounded-b       - redondeo abajo
rounded-l       - redondeo izquierda
rounded-r       - redondeo derecha


8. SOMBRAS
----------

shadow-none     - sin sombra
shadow-sm       - sombra pequena
shadow          - sombra normal
shadow-md       - sombra mediana
shadow-lg       - sombra grande
shadow-xl       - sombra extra grande
shadow-2xl      - sombra enorme


9. DISPLAY
----------

block           - display: block
inline          - display: inline
inline-block    - display: inline-block
flex            - display: flex
grid            - display: grid
hidden          - display: none


10. POSICION
------------

static          - position: static
fixed           - position: fixed
absolute        - position: absolute
relative        - position: relative
sticky          - position: sticky

top-0           - top: 0
right-0         - right: 0
bottom-0        - bottom: 0
left-0          - left: 0

inset-0         - top, right, bottom, left: 0


RESPONSIVE DESIGN
=================

Tailwind usa breakpoints para diferentes tamaños de pantalla:

sm:     640px   (tablets pequenas)
md:     768px   (tablets)
lg:     1024px  (laptops)
xl:     1280px  (desktops)
2xl:    1536px  (desktops grandes)


MOBILE FIRST:
Por defecto las clases son para movil, luego las cambias para pantallas mas grandes.

Ejemplo:
<div class="text-sm md:text-base lg:text-lg">
  Texto pequeno en movil
  Texto normal en tablets
  Texto grande en laptops
</div>

<div class="w-full md:w-1/2 lg:w-1/3">
  Ancho completo en movil
  Mitad en tablets
  Un tercio en laptops
</div>

<div class="flex flex-col md:flex-row">
  Columna en movil
  Fila en tablets y mas grande
</div>


ESTADOS (HOVER, FOCUS, ACTIVE)
===============================

hover:          - cuando el mouse esta encima
focus:          - cuando tiene focus (inputs)
active:         - cuando esta siendo clickeado
disabled:       - cuando esta deshabilitado
group-hover:    - hover en el elemento padre


Ejemplos:
<button class="bg-blue-500 hover:bg-blue-600">
  Cambia color al pasar mouse
</button>

<input class="border focus:border-blue-500 focus:ring-2">
  Cambia borde al hacer focus
</input>

<a class="text-blue-500 hover:underline">
  Se subraya al pasar mouse
</a>

<div class="group">
  <p class="group-hover:text-blue-500">
    Cambia color cuando pasas mouse por el div padre
  </p>
</div>


EJEMPLOS COMPLETOS EN BABYCASH
===============================

1. BOTON PRIMARIO:
------------------
<button class="bg-blue-500 text-white px-6 py-2 rounded-lg font-semibold hover:bg-blue-600 active:scale-95 transition">
  Agregar al carrito
</button>


2. TARJETA DE PRODUCTO:
------------------------
<div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition">
  <img src="..." class="w-full h-48 object-cover" />
  <div class="p-4">
    <h3 class="text-lg font-bold text-gray-800">Pañales Huggies</h3>
    <p class="text-gray-600 text-sm mt-2">Descripcion del producto...</p>
    <div class="flex justify-between items-center mt-4">
      <span class="text-2xl font-bold text-green-600">$25.000</span>
      <button class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
        Agregar
      </button>
    </div>
  </div>
</div>


3. FORMULARIO:
--------------
<form class="max-w-md mx-auto bg-white p-6 rounded-lg shadow-md">
  <h2 class="text-2xl font-bold mb-6">Iniciar Sesion</h2>
  
  <div class="mb-4">
    <label class="block text-sm font-medium text-gray-700 mb-2">
      Email
    </label>
    <input 
      type="email"
      class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
      placeholder="tu@email.com"
    />
  </div>
  
  <div class="mb-6">
    <label class="block text-sm font-medium text-gray-700 mb-2">
      Contraseña
    </label>
    <input 
      type="password"
      class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
    />
  </div>
  
  <button class="w-full bg-blue-500 text-white py-2 rounded font-semibold hover:bg-blue-600">
    Entrar
  </button>
</form>


4. NAVBAR:
----------
<nav class="bg-white shadow-md">
  <div class="max-w-7xl mx-auto px-4">
    <div class="flex justify-between items-center h-16">
      <div class="flex items-center gap-8">
        <img src="/logo.png" class="h-10" />
        <div class="hidden md:flex gap-6">
          <a href="/" class="text-gray-700 hover:text-blue-500">Inicio</a>
          <a href="/productos" class="text-gray-700 hover:text-blue-500">Productos</a>
          <a href="/blog" class="text-gray-700 hover:text-blue-500">Blog</a>
        </div>
      </div>
      <div class="flex items-center gap-4">
        <button class="text-gray-700 hover:text-blue-500">
          Carrito (0)
        </button>
        <button class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
          Login
        </button>
      </div>
    </div>
  </div>
</nav>


5. GRID DE PRODUCTOS:
----------------------
<div class="container mx-auto px-4 py-8">
  <h1 class="text-3xl font-bold mb-8">Nuestros Productos</h1>
  
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
    {productos.map(producto => (
      <ProductCard key={producto.id} producto={producto} />
    ))}
  </div>
</div>


6. MODAL:
---------
<div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
  <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-bold">Confirmar</h2>
      <button class="text-gray-500 hover:text-gray-700 text-2xl">×</button>
    </div>
    <p class="text-gray-700 mb-6">¿Estas seguro de eliminar este producto?</p>
    <div class="flex gap-2 justify-end">
      <button class="px-4 py-2 border border-gray-300 rounded hover:bg-gray-100">
        Cancelar
      </button>
      <button class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">
        Eliminar
      </button>
    </div>
  </div>
</div>


PERSONALIZACION EN TAILWIND.CONFIG.JS
======================================

Puedes personalizar colores, fuentes, espaciados, etc.

// tailwind.config.js
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#3B82F6',
        secondary: '#10B981',
        danger: '#EF4444'
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif']
      }
    }
  },
  plugins: []
}

// Uso:
<button class="bg-primary text-white">Boton</button>
<p class="font-sans">Texto con fuente Inter</p>


UTILIDADES PERSONALIZADAS
==========================

Puedes crear clases personalizadas en index.css:

@layer components {
  .btn-primary {
    @apply bg-blue-500 text-white px-6 py-2 rounded-lg font-semibold hover:bg-blue-600 transition;
  }
  
  .card {
    @apply bg-white rounded-lg shadow-md p-6;
  }
}

// Uso:
<button class="btn-primary">Guardar</button>
<div class="card">Contenido</div>


DARK MODE
=========

Tailwind soporta dark mode:

<div class="bg-white dark:bg-gray-800 text-black dark:text-white">
  Contenido
</div>

Configurar en tailwind.config.js:
module.exports = {
  darkMode: 'class', // o 'media'
  // ...
}


ANIMACIONES Y TRANSICIONES
===========================

transition              - transicion suave
duration-200            - duracion de 200ms
duration-500            - duracion de 500ms
ease-in                 - ease-in
ease-out                - ease-out
ease-in-out             - ease-in-out

animate-spin            - rotar
animate-ping            - pulsar
animate-bounce          - rebotar
animate-pulse           - pulsar opacidad


Ejemplo:
<button class="bg-blue-500 hover:bg-blue-600 transition duration-300">
  Transicion suave al cambiar color
</button>

<div class="animate-spin">⟳</div>


OPACIDAD
========

opacity-0       - completamente transparente
opacity-25      - 25% opaco
opacity-50      - 50% opaco
opacity-75      - 75% opaco
opacity-100     - completamente opaco


bg-black bg-opacity-50    - fondo negro 50% transparente


Z-INDEX
=======

z-0
z-10
z-20
z-30
z-40
z-50      - para modales generalmente


OVERFLOW
========

overflow-hidden     - ocultar contenido que sobresale
overflow-scroll     - scroll siempre visible
overflow-auto       - scroll cuando sea necesario
overflow-x-auto     - scroll horizontal
overflow-y-auto     - scroll vertical


CURSOR
======

cursor-pointer      - manita (para links/botones)
cursor-not-allowed  - prohibido
cursor-wait         - reloj de espera
cursor-text         - cursor de texto


CONSEJOS PRACTICOS
===================

1. USA EL AUTOCOMPLETADO
   - VS Code con Tailwind CSS IntelliSense extension
   - Te sugiere clases mientras escribes

2. APRENDE LOS PATRONES COMUNES
   - flex justify-center items-center (centrar)
   - w-full max-w-md mx-auto (contenedor centrado)
   - grid grid-cols-3 gap-4 (grid de 3 columnas)

3. MOBILE FIRST
   - Diseña primero para movil
   - Luego ajusta para pantallas mas grandes

4. USA VARIABLES CSS CUANDO SEA NECESARIO
   - Para valores muy especificos
   - Para animaciones complejas

5. COMPONENTES REUTILIZABLES
   - Crea componentes con clases predefinidas
   - No repitas las mismas clases en todos lados


ERRORES COMUNES
===============

1. OLVIDAR MOBILE FIRST
   ✗ <div class="lg:w-1/2 w-full">
   ✓ <div class="w-full lg:w-1/2">

2. USAR VALORES ARBITRARIOS INNECESARIOS
   ✗ <div class="p-[13px]">
   ✓ <div class="p-3"> (usar escala de Tailwind)

3. NO USAR GAP EN FLEX/GRID
   ✗ <div class="flex"><div class="mr-4">...</div></div>
   ✓ <div class="flex gap-4"><div>...</div></div>

4. CLASES DEMASIADO LARGAS
   - Extraer a componentes
   - Usar @apply para crear clases personalizadas


RESUMEN
=======

Tailwind CSS:
✓ Framework de CSS con clases utilitarias
✓ Rapido de desarrollar
✓ Responsive por defecto
✓ Consistente y mantenible

Clases principales:
- Colores: text-*, bg-*, border-*
- Espaciado: p-*, m-*, gap-*
- Layout: flex, grid, w-*, h-*
- Tipografia: text-*, font-*
- Bordes: border, rounded
- Responsive: sm:, md:, lg:, xl:
- Estados: hover:, focus:, active:

En BabyCash:
✓ Todo el styling usa Tailwind
✓ Componentes reutilizables con clases consistentes
✓ Diseño responsive para movil y desktop

================================================================================
