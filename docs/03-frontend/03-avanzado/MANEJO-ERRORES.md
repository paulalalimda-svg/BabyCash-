# MANEJO DE ERRORES - BABY CASH

## 🎯 ¿Por Qué es Crítico el Manejo de Errores?

**Errores mal manejados:**
- ❌ App crashea (pantalla blanca)
- ❌ Usuario no sabe qué pasó
- ❌ Datos inconsistentes
- ❌ Mala experiencia de usuario

**Errores bien manejados:**
- ✅ App sigue funcionando
- ✅ Usuario ve mensaje claro
- ✅ Datos se recuperan/revierten
- ✅ Logs para debugging

---

## 🛡️ Error Boundary

### ¿Qué es Error Boundary?

**Error Boundary** captura errores en componentes hijos y muestra UI de fallback en lugar de crash.

### Problema sin Error Boundary

```tsx
// Home.tsx
const Home = () => {
  const products = null; // Error: data no llegó del backend
  
  return (
    <div>
      {products.map(p => <ProductCard key={p.id} product={p} />)}
      {/* CRASH: Cannot read property 'map' of null */}
    </div>
  );
};

// Resultado: Pantalla blanca en toda la app ❌
```

### Solución: Error Boundary

```tsx
// src/components/ErrorBoundary.tsx

import { Component, type ReactNode, type ErrorInfo } from 'react';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
    };
  }

  // Método estático: actualizar state cuando hay error
  static getDerivedStateFromError(error: Error): State {
    return {
      hasError: true,
      error,
    };
  }

  // Método lifecycle: enviar error a servicio de logging
  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Error capturado por ErrorBoundary:', error);
    console.error('Component stack:', errorInfo.componentStack);
    
    // Enviar a servicio de logging (Sentry, LogRocket, etc.)
    // logErrorToService(error, errorInfo);
  }

  // Resetear error
  handleReset = () => {
    this.setState({ hasError: false, error: null });
  };

  render() {
    if (this.state.hasError) {
      // UI de fallback personalizada
      if (this.props.fallback) {
        return this.props.fallback;
      }

      // UI de fallback default
      return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
          <div className="max-w-md w-full bg-white shadow-lg rounded-lg p-6">
            <div className="flex items-center justify-center w-12 h-12 mx-auto bg-red-100 rounded-full">
              <svg
                className="w-6 h-6 text-red-600"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                />
              </svg>
            </div>
            
            <h1 className="mt-4 text-xl font-semibold text-center text-gray-900">
              ¡Ups! Algo salió mal
            </h1>
            
            <p className="mt-2 text-sm text-center text-gray-600">
              Lo sentimos, ocurrió un error inesperado. Por favor, intenta recargar la página.
            </p>
            
            {/* Mostrar error en desarrollo */}
            {process.env.NODE_ENV === 'development' && this.state.error && (
              <details className="mt-4 p-3 bg-gray-100 rounded text-xs">
                <summary className="cursor-pointer font-medium">
                  Detalles del error (solo en desarrollo)
                </summary>
                <pre className="mt-2 whitespace-pre-wrap break-words">
                  {this.state.error.toString()}
                </pre>
              </details>
            )}
            
            <div className="mt-6 flex gap-3">
              <button
                onClick={this.handleReset}
                className="flex-1 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
              >
                Intentar de nuevo
              </button>
              
              <button
                onClick={() => window.location.href = '/'}
                className="flex-1 px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300 transition"
              >
                Ir al inicio
              </button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
```

### Uso en App

```tsx
// App.tsx
import ErrorBoundary from './components/ErrorBoundary';

function App() {
  return (
    <ErrorBoundary>
      <AuthProvider>
        <CartProvider>
          <AppRouter />
        </CartProvider>
      </AuthProvider>
    </ErrorBoundary>
  );
}
```

### Error Boundaries Granulares

```tsx
// Proteger secciones específicas
const ProductList = () => {
  return (
    <div>
      <h1>Productos</h1>
      
      {/* Si productos fallan, solo esta sección muestra error */}
      <ErrorBoundary fallback={<ProductsErrorFallback />}>
        <Products />
      </ErrorBoundary>
      
      {/* Recomendaciones siguen funcionando */}
      <ErrorBoundary fallback={<RecommendationsErrorFallback />}>
        <Recommendations />
      </ErrorBoundary>
    </div>
  );
};
```

**Limitación de Error Boundary:**
- ❌ NO captura errores en:
  - Event handlers (onClick, onChange)
  - Código asíncrono (setTimeout, promises)
  - Server-side rendering
  - Errores en el propio Error Boundary

---

## 🔄 Retry Strategies

### Retry Manual con Axios

```tsx
// src/services/api.ts
import axios from 'axios';
import axiosRetry from 'axios-retry';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
});

// Configurar retry automático
axiosRetry(api, {
  retries: 3,                    // Reintentar 3 veces
  retryDelay: axiosRetry.exponentialDelay,  // 1s, 2s, 4s
  retryCondition: (error) => {
    // Solo reintentar en errores de red o 5xx
    return (
      axiosRetry.isNetworkOrIdempotentRequestError(error) ||
      (error.response?.status ?? 0) >= 500
    );
  },
  onRetry: (retryCount, error) => {
    console.log(`Reintento ${retryCount} debido a:`, error.message);
  },
});

export default api;
```

### Custom Hook useRetry

```tsx
// src/hooks/useRetry.ts
import { useState, useCallback } from 'react';

interface UseRetryOptions {
  maxRetries?: number;
  retryDelay?: number;
  onError?: (error: Error) => void;
}

export const useRetry = <T,>(
  asyncFn: () => Promise<T>,
  options: UseRetryOptions = {}
) => {
  const { maxRetries = 3, retryDelay = 1000, onError } = options;
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const [retryCount, setRetryCount] = useState(0);

  const execute = useCallback(async (): Promise<T | null> => {
    setLoading(true);
    setError(null);
    
    for (let attempt = 0; attempt <= maxRetries; attempt++) {
      try {
        const result = await asyncFn();
        setLoading(false);
        setRetryCount(0);
        return result;
      } catch (err) {
        const error = err as Error;
        
        if (attempt === maxRetries) {
          // Último intento falló
          setError(error);
          setLoading(false);
          onError?.(error);
          return null;
        }
        
        // Esperar antes de reintentar
        setRetryCount(attempt + 1);
        await new Promise(resolve => 
          setTimeout(resolve, retryDelay * Math.pow(2, attempt))
        );
      }
    }
    
    return null;
  }, [asyncFn, maxRetries, retryDelay, onError]);

  return { execute, loading, error, retryCount };
};
```

**Uso:**

```tsx
// ProductList.tsx
import { useRetry } from '../hooks/useRetry';

const ProductList = () => {
  const [products, setProducts] = useState<Product[]>([]);
  
  const { execute, loading, error, retryCount } = useRetry(
    () => api.getProducts(),
    {
      maxRetries: 3,
      onError: (error) => {
        toast.error('Error al cargar productos');
      },
    }
  );

  useEffect(() => {
    execute().then(data => {
      if (data) setProducts(data);
    });
  }, []);

  if (loading) {
    return (
      <div>
        Cargando productos...
        {retryCount > 0 && ` (Reintento ${retryCount}/3)`}
      </div>
    );
  }

  if (error) {
    return (
      <div>
        <p>Error al cargar productos</p>
        <button onClick={execute}>Reintentar</button>
      </div>
    );
  }

  return <div>{/* Productos */}</div>;
};
```

---

## 🎯 User Feedback

### Toast Notifications (React Hot Toast)

```tsx
// Ya configurado en Baby Cash
import toast from 'react-hot-toast';

// Success
toast.success('Producto agregado al carrito');

// Error
toast.error('Error al procesar pago');

// Loading
const toastId = toast.loading('Procesando...');
// ... operación
toast.success('Completado', { id: toastId });
// o
toast.error('Error', { id: toastId });

// Custom
toast.custom((t) => (
  <div className={`${t.visible ? 'animate-enter' : 'animate-leave'} bg-white shadow-lg rounded-lg p-4`}>
    <p className="font-semibold">Orden confirmada</p>
    <p className="text-sm text-gray-600">Tu orden #12345 está en camino</p>
  </div>
));

// Promise (automático)
toast.promise(
  api.createOrder(orderData),
  {
    loading: 'Creando orden...',
    success: 'Orden creada exitosamente',
    error: 'Error al crear orden',
  }
);
```

### Error Messages Amigables

```tsx
// src/utils/errorMessages.ts

export const getErrorMessage = (error: unknown): string => {
  // Error de Axios
  if (axios.isAxiosError(error)) {
    const status = error.response?.status;
    const data = error.response?.data;
    
    // Errores específicos del backend
    if (data?.message) {
      return data.message;
    }
    
    // Errores por código HTTP
    switch (status) {
      case 400:
        return 'Datos inválidos. Por favor verifica la información.';
      case 401:
        return 'Sesión expirada. Por favor inicia sesión nuevamente.';
      case 403:
        return 'No tienes permisos para esta acción.';
      case 404:
        return 'Recurso no encontrado.';
      case 409:
        return 'Este recurso ya existe.';
      case 422:
        return 'Datos no válidos. Verifica los campos.';
      case 500:
        return 'Error del servidor. Intenta más tarde.';
      case 503:
        return 'Servicio no disponible. Intenta más tarde.';
      default:
        return 'Error de conexión. Verifica tu internet.';
    }
  }
  
  // Error de JavaScript
  if (error instanceof Error) {
    return error.message;
  }
  
  // Error desconocido
  return 'Ocurrió un error inesperado.';
};
```

**Uso:**

```tsx
const handleLogin = async () => {
  try {
    await login(email, password);
  } catch (error) {
    const message = getErrorMessage(error);
    toast.error(message);
  }
};
```

---

## 📋 Logging y Monitoreo

### Logger Utility

```tsx
// src/utils/logger.ts

type LogLevel = 'info' | 'warn' | 'error' | 'debug';

class Logger {
  private isDevelopment = import.meta.env.DEV;
  
  private log(level: LogLevel, message: string, data?: unknown) {
    const timestamp = new Date().toISOString();
    const prefix = `[${timestamp}] [${level.toUpperCase()}]`;
    
    if (this.isDevelopment) {
      // En desarrollo: console
      switch (level) {
        case 'error':
          console.error(prefix, message, data);
          break;
        case 'warn':
          console.warn(prefix, message, data);
          break;
        case 'debug':
          console.debug(prefix, message, data);
          break;
        default:
          console.log(prefix, message, data);
      }
    } else {
      // En producción: enviar a servicio externo
      this.sendToLogService(level, message, data);
    }
  }
  
  private sendToLogService(level: LogLevel, message: string, data?: unknown) {
    // Implementar integración con:
    // - Sentry
    // - LogRocket
    // - DataDog
    // - Custom backend endpoint
    
    if (level === 'error') {
      // Ejemplo: enviar a Sentry
      // Sentry.captureException(new Error(message), { extra: data });
    }
  }
  
  info(message: string, data?: unknown) {
    this.log('info', message, data);
  }
  
  warn(message: string, data?: unknown) {
    this.log('warn', message, data);
  }
  
  error(message: string, data?: unknown) {
    this.log('error', message, data);
  }
  
  debug(message: string, data?: unknown) {
    this.log('debug', message, data);
  }
}

export const logger = new Logger();
```

**Uso:**

```tsx
// CartContext.tsx
const addToCart = async (item: CartItem) => {
  logger.info('Agregando item al carrito', { itemId: item.id });
  
  try {
    await cartService.addToCart(item);
    logger.info('Item agregado exitosamente', { itemId: item.id });
  } catch (error) {
    logger.error('Error al agregar item', { 
      itemId: item.id, 
      error: error instanceof Error ? error.message : 'Unknown error' 
    });
    throw error;
  }
};
```

---

## 🔒 Validación de Datos

### Zod para Validación

```bash
npm install zod
```

```tsx
// src/schemas/auth.schema.ts
import { z } from 'zod';

export const loginSchema = z.object({
  email: z
    .string()
    .min(1, 'Email es requerido')
    .email('Email inválido'),
  password: z
    .string()
    .min(6, 'Contraseña debe tener al menos 6 caracteres'),
});

export const registerSchema = z.object({
  firstName: z
    .string()
    .min(2, 'Nombre debe tener al menos 2 caracteres')
    .max(50, 'Nombre muy largo'),
  lastName: z
    .string()
    .min(2, 'Apellido debe tener al menos 2 caracteres')
    .max(50, 'Apellido muy largo'),
  email: z
    .string()
    .min(1, 'Email es requerido')
    .email('Email inválido'),
  password: z
    .string()
    .min(8, 'Contraseña debe tener al menos 8 caracteres')
    .regex(/[A-Z]/, 'Debe contener una mayúscula')
    .regex(/[0-9]/, 'Debe contener un número'),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Las contraseñas no coinciden',
  path: ['confirmPassword'],
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerSchema>;
```

### React Hook Form con Zod

```bash
npm install react-hook-form @hookform/resolvers
```

```tsx
// Login.tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema, type LoginFormData } from '../schemas/auth.schema';

const Login = () => {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data.email, data.password);
      toast.success('¡Bienvenido!');
    } catch (error) {
      toast.error(getErrorMessage(error));
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div>
        <label>Email</label>
        <input
          type="email"
          {...register('email')}
          className={errors.email ? 'border-red-500' : ''}
        />
        {errors.email && (
          <p className="text-red-500 text-sm">{errors.email.message}</p>
        )}
      </div>

      <div>
        <label>Contraseña</label>
        <input
          type="password"
          {...register('password')}
          className={errors.password ? 'border-red-500' : ''}
        />
        {errors.password && (
          <p className="text-red-500 text-sm">{errors.password.message}</p>
        )}
      </div>

      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Iniciando sesión...' : 'Iniciar Sesión'}
      </button>
    </form>
  );
};
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es Error Boundary y para qué sirve?"**

> "Error Boundary captura errores en componentes hijos:
> 
> **Sin Error Boundary:**
> - Un error en ProductCard crashea toda la app
> - Usuario ve pantalla blanca
> 
> **Con Error Boundary:**
> ```tsx
> <ErrorBoundary>
>   <ProductList />
> </ErrorBoundary>
> ```
> - Error en ProductCard → solo esa sección falla
> - Resto de la app sigue funcionando
> - Usuario ve mensaje de error amigable
> 
> **Limitación:** NO captura errores en event handlers o async code."

---

**2. "¿Cómo manejar errores de API?"**

> "Tres capas de manejo:
> 
> **1. Axios Interceptor (global):**
> ```tsx
> axios.interceptors.response.use(
>   response => response,
>   error => {
>     if (error.response?.status === 401) {
>       // Redirigir a login
>     }
>     return Promise.reject(error);
>   }
> );
> ```
> 
> **2. Try-Catch (específico):**
> ```tsx
> try {
>   await api.createOrder();
>   toast.success('Orden creada');
> } catch (error) {
>   toast.error(getErrorMessage(error));
> }
> ```
> 
> **3. Retry Strategy:**
> ```tsx
> axiosRetry(api, { retries: 3 });
> ```
> 
> Baby Cash usa las 3 capas."

---

**3. "¿Qué es retry strategy?"**

> "Reintentar operación automáticamente si falla:
> 
> **Sin retry:**
> - Llamada API falla (red inestable)
> - Usuario ve error
> - Debe recargar página
> 
> **Con retry:**
> ```tsx
> axiosRetry(api, {
>   retries: 3,              // 3 intentos
>   retryDelay: exponentialDelay,  // 1s, 2s, 4s
>   retryCondition: (error) => 
>     error.response?.status >= 500  // Solo errores servidor
> });
> ```
> - Primera llamada falla
> - Espera 1 segundo, reintenta
> - Si falla, espera 2 segundos, reintenta
> - Si falla, espera 4 segundos, último intento
> 
> Baby Cash: Axios Retry configurado para todas las llamadas."

---

**4. "¿Cómo validar formularios?"**

> "React Hook Form + Zod:
> 
> **1. Definir schema:**
> ```tsx
> const loginSchema = z.object({
>   email: z.string().email('Email inválido'),
>   password: z.string().min(6, 'Mínimo 6 caracteres'),
> });
> ```
> 
> **2. Usar en formulario:**
> ```tsx
> const { register, handleSubmit, formState: { errors } } = useForm({
>   resolver: zodResolver(loginSchema)
> });
> ```
> 
> **Beneficios:**
> - Validación automática
> - Mensajes de error claros
> - Type safety con TypeScript
> - Menos código manual
> 
> Baby Cash: Zod en todos los formularios."

---

## 📝 Checklist de Manejo de Errores

- ✅ Error Boundary en App root
- ✅ Error Boundaries granulares en secciones críticas
- ✅ Axios interceptors para errores globales
- ✅ Retry strategy configurado
- ✅ Toast notifications para feedback
- ✅ Mensajes de error amigables (no técnicos)
- ✅ Logging en desarrollo y producción
- ✅ Validación con Zod + React Hook Form
- ✅ Loading states durante operaciones async
- ✅ Botones "Reintentar" en errores recuperables

---

## 🚀 Conclusión

**Manejo de Errores en Baby Cash:**
- ✅ Error Boundary captura crashes
- ✅ Retry strategy automático con Axios
- ✅ Toast notifications para feedback
- ✅ Logger para debugging
- ✅ Validación robusta con Zod
- ✅ Mensajes claros y accionables

**Resultado: App robusta que maneja errores gracefully.**

---

**Ahora lee:** `TYPESCRIPT-PATTERNS.md` para aprovechar TypeScript al máximo. 🚀
