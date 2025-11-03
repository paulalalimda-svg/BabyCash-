# TYPESCRIPT PATTERNS - BABY CASH

## 🎯 ¿Por Qué TypeScript?

**JavaScript:**
```javascript
function addToCart(product, quantity) {
  // ¿Qué es product? ¿Qué propiedades tiene?
  // ¿quantity es number o string?
  return cart.add(product, quantity); // Puede fallar en runtime
}
```

**TypeScript:**
```typescript
function addToCart(product: Product, quantity: number): void {
  // product DEBE ser Product (con id, name, price...)
  // quantity DEBE ser number
  // TypeScript previene errores en tiempo de compilación
  cart.add(product, quantity);
}
```

**Beneficios:**
- ✅ Autocompletado inteligente (IntelliSense)
- ✅ Errores en tiempo de compilación (no en runtime)
- ✅ Refactoring seguro
- ✅ Documentación automática
- ✅ Menos bugs en producción

---

## 📦 Types vs Interfaces

### Cuándo Usar Cada Uno

**Type:**
- Uniones (`string | number`)
- Intersecciones (`TypeA & TypeB`)
- Primitivos (`type ID = string | number`)
- Tuplas (`[string, number]`)

**Interface:**
- Objetos
- Clases
- Extensión de tipos existentes (declaration merging)

### Ejemplos en Baby Cash

```typescript
// ✅ INTERFACE: Para objetos y contratos
interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'USER' | 'ADMIN' | 'MODERATOR';
}

// ✅ TYPE: Para uniones y aliases
type UserRole = 'USER' | 'ADMIN' | 'MODERATOR';

type ID = string | number;

type Product = {
  id: number;
  name: string;
  price: number;
};

// ✅ TYPE: Para intersecciones
type AdminUser = User & {
  permissions: string[];
  lastLogin: Date;
};

// ✅ INTERFACE: Para extensión
interface BaseEntity {
  id: number;
  createdAt: string;
  updatedAt: string;
}

interface Product extends BaseEntity {
  name: string;
  price: number;
  category: string;
}

interface Order extends BaseEntity {
  userId: number;
  total: number;
  status: OrderStatus;
}
```

---

## 🔧 Utility Types

### 1️⃣ Partial<T> - Todas las propiedades opcionales

```typescript
interface Product {
  id: number;
  name: string;
  price: number;
  category: string;
  stock: number;
}

// Actualizar producto (no todos los campos requeridos)
function updateProduct(id: number, updates: Partial<Product>) {
  // updates puede tener solo { name: 'Nuevo nombre' }
  // o { price: 50000, stock: 10 }
  // NO requiere todos los campos
}

// Uso
updateProduct(1, { name: 'Producto Actualizado' }); // ✅
updateProduct(1, { price: 45000, stock: 15 }); // ✅
```

---

### 2️⃣ Required<T> - Todas las propiedades requeridas

```typescript
interface UserProfile {
  email?: string;
  phone?: string;
  address?: string;
}

// Admin debe completar todos los campos
type AdminProfile = Required<UserProfile>;
// {
//   email: string;    // Ya no es opcional
//   phone: string;
//   address: string;
// }
```

---

### 3️⃣ Pick<T, K> - Seleccionar propiedades

```typescript
interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  category: string;
  imageUrl: string;
}

// Solo necesito id y name para lista
type ProductSummary = Pick<Product, 'id' | 'name' | 'price'>;
// {
//   id: number;
//   name: string;
//   price: number;
// }

// Uso
const renderProductCard = (product: ProductSummary) => {
  return <div>{product.name} - ${product.price}</div>;
};
```

---

### 4️⃣ Omit<T, K> - Excluir propiedades

```typescript
interface User {
  id: number;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

// Usuario sin password (para frontend)
type SafeUser = Omit<User, 'password'>;
// {
//   id: number;
//   email: string;
//   firstName: string;
//   lastName: string;
// }

// Crear usuario (sin id, se genera en backend)
type CreateUserDTO = Omit<User, 'id'>;
// {
//   email: string;
//   password: string;
//   firstName: string;
//   lastName: string;
// }
```

---

### 5️⃣ Record<K, T> - Objeto con claves de tipo K y valores de tipo T

```typescript
// Mapeo de categorías a colores
type CategoryColors = Record<string, string>;

const categoryColors: CategoryColors = {
  ropa: '#FF6B6B',
  juguetes: '#4ECDC4',
  hogar: '#45B7D1',
};

// Estadísticas por status
type OrderStatusStats = Record<OrderStatus, number>;

const stats: OrderStatusStats = {
  PENDING: 15,
  PROCESSING: 8,
  SHIPPED: 23,
  DELIVERED: 142,
  CANCELLED: 7,
};
```

---

### 6️⃣ Readonly<T> - Propiedades de solo lectura

```typescript
interface Config {
  apiUrl: string;
  timeout: number;
  retries: number;
}

const config: Readonly<Config> = {
  apiUrl: 'http://localhost:8080/api',
  timeout: 5000,
  retries: 3,
};

config.apiUrl = 'http://otro.com'; // ❌ Error: Cannot assign to 'apiUrl'
```

---

### 7️⃣ ReturnType<T> - Tipo de retorno de función

```typescript
function getUser() {
  return {
    id: 1,
    name: 'Juan',
    email: 'juan@example.com',
  };
}

type UserType = ReturnType<typeof getUser>;
// {
//   id: number;
//   name: string;
//   email: string;
// }
```

---

### 8️⃣ Parameters<T> - Parámetros de función como tupla

```typescript
function createOrder(userId: number, items: CartItem[], notes?: string) {
  // ...
}

type CreateOrderParams = Parameters<typeof createOrder>;
// [userId: number, items: CartItem[], notes?: string]

const params: CreateOrderParams = [1, [item1, item2], 'Entrega rápida'];
createOrder(...params);
```

---

## 🎨 Discriminated Unions (Tagged Unions)

### Problema: Type Union sin discriminador

```typescript
type ApiResponse = 
  | { data: Product[] }
  | { error: string };

function handleResponse(response: ApiResponse) {
  if (response.data) { // ❌ Error: Property 'data' does not exist
    return response.data;
  }
}
```

### Solución: Discriminated Union

```typescript
// Con discriminador 'type'
type SuccessResponse = {
  type: 'success';
  data: Product[];
};

type ErrorResponse = {
  type: 'error';
  error: string;
  code: number;
};

type ApiResponse = SuccessResponse | ErrorResponse;

function handleResponse(response: ApiResponse) {
  if (response.type === 'success') {
    // TypeScript sabe que es SuccessResponse
    return response.data; // ✅ OK
  } else {
    // TypeScript sabe que es ErrorResponse
    console.error(response.error); // ✅ OK
  }
}
```

### Ejemplo Real: Order Status

```typescript
type PendingOrder = {
  status: 'PENDING';
  paymentMethod: 'credit_card' | 'cash';
};

type ProcessingOrder = {
  status: 'PROCESSING';
  estimatedDelivery: Date;
};

type ShippedOrder = {
  status: 'SHIPPED';
  trackingNumber: string;
  carrier: string;
};

type DeliveredOrder = {
  status: 'DELIVERED';
  deliveredAt: Date;
  signature: string;
};

type CancelledOrder = {
  status: 'CANCELLED';
  reason: string;
  refundAmount: number;
};

type Order = PendingOrder | ProcessingOrder | ShippedOrder | DeliveredOrder | CancelledOrder;

function renderOrderStatus(order: Order) {
  switch (order.status) {
    case 'PENDING':
      return <div>Pendiente - Método: {order.paymentMethod}</div>;
    
    case 'PROCESSING':
      return <div>Procesando - Entrega estimada: {order.estimatedDelivery}</div>;
    
    case 'SHIPPED':
      return <div>Enviado - Tracking: {order.trackingNumber}</div>;
    
    case 'DELIVERED':
      return <div>Entregado el {order.deliveredAt}</div>;
    
    case 'CANCELLED':
      return <div>Cancelado - Razón: {order.reason}</div>;
    
    default:
      // Exhaustiveness check: TypeScript verifica que cubrimos todos los casos
      const _exhaustive: never = order;
      return _exhaustive;
  }
}
```

---

## 🛡️ Type Guards

### typeof Type Guard

```typescript
function formatValue(value: string | number) {
  if (typeof value === 'string') {
    // TypeScript sabe que value es string
    return value.toUpperCase();
  } else {
    // TypeScript sabe que value es number
    return value.toFixed(2);
  }
}
```

### instanceof Type Guard

```typescript
class ValidationError extends Error {
  constructor(public field: string, message: string) {
    super(message);
  }
}

function handleError(error: unknown) {
  if (error instanceof ValidationError) {
    console.log(`Error en campo: ${error.field}`);
  } else if (error instanceof Error) {
    console.log(error.message);
  } else {
    console.log('Error desconocido');
  }
}
```

### Custom Type Guard

```typescript
interface User {
  id: number;
  name: string;
  role: 'USER';
}

interface Admin {
  id: number;
  name: string;
  role: 'ADMIN';
  permissions: string[];
}

// Type guard personalizado
function isAdmin(user: User | Admin): user is Admin {
  return user.role === 'ADMIN';
}

function renderUserActions(user: User | Admin) {
  if (isAdmin(user)) {
    // TypeScript sabe que user es Admin
    return user.permissions.map(p => <button>{p}</button>);
  } else {
    // TypeScript sabe que user es User
    return <div>Usuario regular</div>;
  }
}
```

### in Operator Type Guard

```typescript
interface Car {
  drive: () => void;
}

interface Boat {
  sail: () => void;
}

function operate(vehicle: Car | Boat) {
  if ('drive' in vehicle) {
    // TypeScript sabe que vehicle es Car
    vehicle.drive();
  } else {
    // TypeScript sabe que vehicle es Boat
    vehicle.sail();
  }
}
```

---

## 🎁 Generics

### Función Genérica Simple

```typescript
// Sin generic (repetitivo)
function getFirstString(arr: string[]): string | undefined {
  return arr[0];
}

function getFirstNumber(arr: number[]): number | undefined {
  return arr[0];
}

// Con generic (reutilizable)
function getFirst<T>(arr: T[]): T | undefined {
  return arr[0];
}

// Uso
const firstStr = getFirst<string>(['a', 'b', 'c']); // string | undefined
const firstNum = getFirst<number>([1, 2, 3]); // number | undefined
const firstProduct = getFirst<Product>(products); // Product | undefined
```

### API Response Genérico

```typescript
interface ApiResponse<T> {
  data: T;
  status: number;
  message: string;
}

// Diferentes tipos de respuestas
type ProductResponse = ApiResponse<Product>;
type UserResponse = ApiResponse<User>;
type OrdersResponse = ApiResponse<Order[]>;

async function fetchData<T>(url: string): Promise<ApiResponse<T>> {
  const response = await fetch(url);
  return response.json();
}

// Uso
const products = await fetchData<Product[]>('/api/products');
console.log(products.data); // Product[]

const user = await fetchData<User>('/api/user/1');
console.log(user.data); // User
```

### Custom Hook Genérico

```typescript
// src/hooks/useFetch.ts
import { useState, useEffect } from 'react';

interface UseFetchResult<T> {
  data: T | null;
  loading: boolean;
  error: Error | null;
  refetch: () => void;
}

export function useFetch<T>(url: string): UseFetchResult<T> {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      const response = await fetch(url);
      const json = await response.json();
      setData(json);
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [url]);

  return { data, loading, error, refetch: fetchData };
}

// Uso
const ProductList = () => {
  const { data: products, loading } = useFetch<Product[]>('/api/products');
  // products es Product[] | null (TypeScript lo infiere)
  
  if (loading) return <div>Loading...</div>;
  
  return (
    <div>
      {products?.map(p => <ProductCard key={p.id} product={p} />)}
    </div>
  );
};
```

### Constraints en Generics

```typescript
// Solo tipos con propiedad 'id'
interface HasId {
  id: number | string;
}

function findById<T extends HasId>(items: T[], id: number | string): T | undefined {
  return items.find(item => item.id === id);
}

// Uso
const product = findById<Product>(products, 1); // ✅ Product tiene id
const user = findById<User>(users, 2); // ✅ User tiene id

// ❌ Error si tipo no tiene id
const result = findById<{ name: string }>([], 1);
// Error: Type '{ name: string }' does not satisfy constraint 'HasId'
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué son los Utility Types?"**

> "Utility Types son tipos auxiliares de TypeScript:
> 
> **Partial<T>:** Hace todas las propiedades opcionales
> ```typescript
> Partial<Product> // Todas las props de Product son opcionales
> ```
> 
> **Pick<T, K>:** Selecciona solo algunas propiedades
> ```typescript
> Pick<Product, 'id' | 'name'> // Solo id y name
> ```
> 
> **Omit<T, K>:** Excluye propiedades
> ```typescript
> Omit<User, 'password'> // User sin password
> ```
> 
> **Record<K, T>:** Objeto con claves K y valores T
> ```typescript
> Record<string, number> // { [key: string]: number }
> ```
> 
> Baby Cash: Usa Partial para updates, Pick para DTOs."

---

**2. "¿Qué son Discriminated Unions?"**

> "Unions con propiedad discriminadora:
> 
> **Sin discriminador:**
> ```typescript
> type Response = { data: Product[] } | { error: string };
> // TypeScript no sabe cuál es cuál
> ```
> 
> **Con discriminador (type):**
> ```typescript
> type SuccessResponse = { type: 'success'; data: Product[] };
> type ErrorResponse = { type: 'error'; error: string };
> type Response = SuccessResponse | ErrorResponse;
> 
> if (response.type === 'success') {
>   console.log(response.data); // TypeScript sabe que existe data
> }
> ```
> 
> **Beneficios:**
> - Type narrowing automático
> - Exhaustiveness checking
> - Código más seguro
> 
> Baby Cash: Order status usa discriminated unions."

---

**3. "¿Qué son Type Guards?"**

> "Funciones que permiten narrowing de tipos:
> 
> **Custom Type Guard:**
> ```typescript
> function isAdmin(user: User | Admin): user is Admin {
>   return user.role === 'ADMIN';
> }
> 
> if (isAdmin(user)) {
>   // TypeScript sabe que user es Admin
>   user.permissions.forEach(...);
> }
> ```
> 
> **Otros Type Guards:**
> - `typeof`: Primitivos (`typeof x === 'string'`)
> - `instanceof`: Clases (`x instanceof Error`)
> - `in`: Propiedades (`'drive' in vehicle`)
> 
> Baby Cash: isAdmin, isAuthenticated usan type guards."

---

**4. "¿Qué son Generics y cuándo usarlos?"**

> "Generics permiten código reutilizable con diferentes tipos:
> 
> **Sin Generics (repetitivo):**
> ```typescript
> function getFirstString(arr: string[]): string { ... }
> function getFirstNumber(arr: number[]): number { ... }
> ```
> 
> **Con Generics (reutilizable):**
> ```typescript
> function getFirst<T>(arr: T[]): T {
>   return arr[0];
> }
> 
> getFirst<string>(['a', 'b']); // string
> getFirst<number>([1, 2]); // number
> ```
> 
> **Cuándo usar:**
> - Funciones que trabajan con múltiples tipos
> - Hooks reutilizables (useFetch<T>)
> - Componentes genéricos (List<T>)
> - API responses con diferentes data
> 
> Baby Cash: useFetch<T>, ApiResponse<T> son genéricos."

---

## 📝 Best Practices TypeScript

### 1️⃣ Evitar `any`

```typescript
// ❌ MAL
function processData(data: any) {
  return data.map(item => item.id); // No type safety
}

// ✅ BIEN
function processData<T extends { id: number }>(data: T[]) {
  return data.map(item => item.id); // Type safe
}
```

### 2️⃣ Usar `unknown` en lugar de `any` para errores

```typescript
// ❌ MAL
catch (error: any) {
  console.log(error.message); // Puede fallar
}

// ✅ BIEN
catch (error: unknown) {
  if (error instanceof Error) {
    console.log(error.message); // Type safe
  }
}
```

### 3️⃣ Strict Mode Enabled

```json
// tsconfig.json
{
  "compilerOptions": {
    "strict": true,               // Activa todos los checks estrictos
    "noImplicitAny": true,        // No permite any implícito
    "strictNullChecks": true,     // null y undefined no son asignables
    "strictFunctionTypes": true   // Checks estrictos en funciones
  }
}
```

### 4️⃣ Inferir tipos cuando sea posible

```typescript
// ❌ Redundante
const name: string = 'Juan';
const age: number = 25;

// ✅ TypeScript infiere
const name = 'Juan'; // string
const age = 25; // number

// ✅ Especificar cuando no es obvio
const users: User[] = []; // Necesario (array vacío)
```

---

## 🚀 Conclusión

**TypeScript en Baby Cash:**
- ✅ Type safety en toda la aplicación
- ✅ Utility Types para transformaciones
- ✅ Discriminated Unions para estados complejos
- ✅ Type Guards para narrowing seguro
- ✅ Generics para código reutilizable
- ✅ Strict mode habilitado

**Resultado: Menos bugs, mejor DX (Developer Experience).**

---

**Ahora lee:** `TESTING-STRATEGIES.md` para garantizar calidad. 🚀
