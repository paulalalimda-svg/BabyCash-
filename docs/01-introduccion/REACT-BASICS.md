# REACT - CONCEPTOS BÁSICOS

## ⚛️ ¿Qué es React?

**Librería de JavaScript para construir interfaces de usuario (UI).**

### Analogía: LEGO 🧱

```
UI = Componentes (piezas LEGO)

Navbar + ProductCard + Footer = Página completa
```

---

## 🎯 ¿Por Qué React?

### Sin React (Vanilla JS):
```javascript
// Actualizar UI manualmente
document.getElementById('count').innerText = count;
document.getElementById('title').innerText = title;
// ... ❌ Tedioso y propenso a errores
```

### Con React:
```typescript
// Actualizar estado, React actualiza UI automáticamente
setCount(5);
setTitle('Nuevo título');
// ✅ React se encarga del DOM
```

---

## 🧩 Componentes

### Componente Funcional

```typescript
// Componente simple
function Welcome() {
  return <h1>¡Hola, Baby Cash!</h1>;
}

// Con Props
function Welcome({ name }: { name: string }) {
  return <h1>¡Hola, {name}!</h1>;
}

// Uso
<Welcome name="Juan" />
```

### JSX (JavaScript XML)

```typescript
// JSX - Parece HTML pero es JavaScript
const element = <h1>Hola Mundo</h1>;

// Se compila a:
const element = React.createElement('h1', null, 'Hola Mundo');

// Expresiones JavaScript en JSX
const name = 'Baby Cash';
const element = <h1>Bienvenido a {name}</h1>;

// Atributos
const element = <img src={imageUrl} alt="Producto" />;

// Múltiples elementos (necesitan un padre)
return (
  <div>
    <h1>Título</h1>
    <p>Párrafo</p>
  </div>
);

// O usar Fragment
return (
  <>
    <h1>Título</h1>
    <p>Párrafo</p>
  </>
);
```

---

## 📦 Props (Propiedades)

**Pasar datos de componente padre a hijo.**

```typescript
// Padre
function ProductList() {
  return (
    <div>
      <ProductCard
        name="Pañales"
        price={45000}
        image="/pañales.jpg"
      />
      <ProductCard
        name="Biberón"
        price={25000}
        image="/biberon.jpg"
      />
    </div>
  );
}

// Hijo
interface ProductCardProps {
  name: string;
  price: number;
  image: string;
}

function ProductCard({ name, price, image }: ProductCardProps) {
  return (
    <div className="card">
      <img src={image} alt={name} />
      <h3>{name}</h3>
      <p>${price.toLocaleString()}</p>
    </div>
  );
}
```

**Props son inmutables (no se pueden modificar en el hijo).**

---

## 🔄 State (Estado)

**Datos que cambian en el componente.**

### useState Hook

```typescript
import { useState } from 'react';

function Counter() {
  // Estado: count
  // Función para actualizar: setCount
  const [count, setCount] = useState(0);

  return (
    <div>
      <p>Contador: {count}</p>
      <button onClick={() => setCount(count + 1)}>
        Incrementar
      </button>
      <button onClick={() => setCount(count - 1)}>
        Decrementar
      </button>
      <button onClick={() => setCount(0)}>
        Resetear
      </button>
    </div>
  );
}
```

### Múltiples Estados

```typescript
function ProductForm() {
  const [name, setName] = useState('');
  const [price, setPrice] = useState(0);
  const [inStock, setInStock] = useState(true);

  return (
    <form>
      <input
        value={name}
        onChange={(e) => setName(e.target.value)}
      />
      <input
        type="number"
        value={price}
        onChange={(e) => setPrice(Number(e.target.value))}
      />
      <label>
        <input
          type="checkbox"
          checked={inStock}
          onChange={(e) => setInStock(e.target.checked)}
        />
        En stock
      </label>
    </form>
  );
}
```

### Estado de Objetos

```typescript
function ProductForm() {
  const [product, setProduct] = useState({
    name: '',
    price: 0,
    stock: 0
  });

  // Actualizar una propiedad (spread operator)
  const handleNameChange = (newName: string) => {
    setProduct({
      ...product, // Copiar propiedades existentes
      name: newName // Actualizar name
    });
  };

  return (
    <input
      value={product.name}
      onChange={(e) => handleNameChange(e.target.value)}
    />
  );
}
```

---

## 🎣 Hooks Básicos

### useEffect

**Ejecutar código cuando el componente se monta o actualiza.**

```typescript
import { useState, useEffect } from 'react';

function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  // Cargar productos cuando el componente se monta
  useEffect(() => {
    fetch('/api/products')
      .then(res => res.json())
      .then(data => {
        setProducts(data);
        setLoading(false);
      });
  }, []); // [] = solo ejecutar una vez al montar

  if (loading) return <div>Cargando...</div>;

  return (
    <div>
      {products.map(product => (
        <ProductCard key={product.id} {...product} />
      ))}
    </div>
  );
}
```

**Dependency Array:**
```typescript
// [] - Solo al montar
useEffect(() => {
  console.log('Componente montado');
}, []);

// [count] - Al montar y cuando count cambia
useEffect(() => {
  console.log('Count cambió:', count);
}, [count]);

// Sin array - En cada render
useEffect(() => {
  console.log('Cada render');
});
```

### useContext

**Compartir datos entre componentes sin props.**

```typescript
import { createContext, useContext, useState } from 'react';

// Crear contexto
const AuthContext = createContext(null);

// Provider
function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  const login = (email, password) => {
    // Lógica de login
    setUser({ email, name: 'Juan' });
  };

  return (
    <AuthContext.Provider value={{ user, login }}>
      {children}
    </AuthContext.Provider>
  );
}

// Consumir contexto
function Profile() {
  const { user } = useContext(AuthContext);

  if (!user) return <div>No autenticado</div>;

  return <div>Hola, {user.name}</div>;
}

// App
function App() {
  return (
    <AuthProvider>
      <Profile />
    </AuthProvider>
  );
}
```

---

## 🎨 Conditional Rendering

```typescript
function ProductCard({ product, inStock }) {
  // If simple
  if (!inStock) {
    return <div>Producto agotado</div>;
  }

  // Ternario
  return (
    <div>
      <h3>{product.name}</h3>
      {inStock ? (
        <button>Agregar al Carrito</button>
      ) : (
        <p>Agotado</p>
      )}
    </div>
  );
}

// && operator
function Notification({ message }) {
  return (
    <div>
      {message && <div className="notification">{message}</div>}
    </div>
  );
}
```

---

## 🔁 Listas y Keys

```typescript
function ProductList({ products }) {
  return (
    <div>
      {products.map(product => (
        <ProductCard
          key={product.id} // ⚠️ Key único y estable
          product={product}
        />
      ))}
    </div>
  );
}

// ❌ No usar index como key
products.map((product, index) => (
  <ProductCard key={index} product={product} />
));

// ✅ Usar ID único
products.map(product => (
  <ProductCard key={product.id} product={product} />
));
```

---

## 📝 Forms

```typescript
function ProductForm() {
  const [formData, setFormData] = useState({
    name: '',
    price: 0,
    description: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault(); // Prevenir reload
    console.log('Enviando:', formData);
    // Enviar a API
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        name="name"
        value={formData.name}
        onChange={handleChange}
        placeholder="Nombre"
      />
      <input
        name="price"
        type="number"
        value={formData.price}
        onChange={handleChange}
        placeholder="Precio"
      />
      <textarea
        name="description"
        value={formData.description}
        onChange={handleChange}
        placeholder="Descripción"
      />
      <button type="submit">Guardar</button>
    </form>
  );
}
```

---

## 🎓 Para la Evaluación del SENA

**1. "¿Qué es React?"**

> "Librería de JavaScript para construir interfaces de usuario. Usa componentes reutilizables como piezas LEGO."

**2. "¿Qué son Props?"**

> "Propiedades que se pasan de componente padre a hijo. Son inmutables. Ejemplo: `<ProductCard name='Pañales' price={45000} />`"

**3. "¿Qué es State?"**

> "Datos que cambian en el componente. Se usa useState. Ejemplo: `const [count, setCount] = useState(0)`"

**4. "¿Qué es useEffect?"**

> "Hook para efectos secundarios (fetch, timers). Se ejecuta cuando el componente se monta o actualiza. Ejemplo: Cargar productos de la API."

---

**Siguiente:** `VIRTUAL-DOM.md` 🚀
