# AUTH COMPONENTS - COMPONENTES DE AUTENTICACIÓN

## 🎯 Visión General

Los **Auth Components** manejan autenticación:
- **LoginForm**: Formulario de inicio de sesión
- **RegisterForm**: Formulario de registro
- Validación de campos
- Manejo de errores
- Loading states
- Toggle de visibilidad de contraseña

---

## 📁 Ubicación

```
frontend/src/components/auth/LoginForm.tsx
frontend/src/components/auth/RegisterForm.tsx
```

---

## 🔐 LoginForm

```tsx
import { useState } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import Button from '../common/Button';
import Input from '../common/Input';
import ErrorMessage from '../common/ErrorMessage';

export default function LoginForm() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // ✅ Manejar cambios en inputs
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    // Limpiar error al escribir
    if (error) setError(null);
  };
  
  // ✅ Manejar submit
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validaciones
    if (!formData.email || !formData.password) {
      setError('Por favor completa todos los campos');
      return;
    }
    
    if (!formData.email.includes('@')) {
      setError('Email inválido');
      return;
    }
    
    setLoading(true);
    setError(null);
    
    try {
      await login(formData.email, formData.password);
      
      // Redirigir
      const redirect = searchParams.get('redirect') || '/';
      navigate(redirect);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al iniciar sesión');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="max-w-md mx-auto bg-white rounded-lg shadow-md p-8">
      <h2 className="text-3xl font-bold text-center mb-6">Iniciar Sesión</h2>
      
      {/* Error global */}
      {error && <ErrorMessage message={error} />}
      
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Email */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Email
          </label>
          <Input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="tu@email.com"
            required
          />
        </div>
        
        {/* Password */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Contraseña
          </label>
          <div className="relative">
            <Input
              type={showPassword ? 'text' : 'password'}
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
            >
              {showPassword ? '👁️' : '👁️‍🗨️'}
            </button>
          </div>
        </div>
        
        {/* Olvidé contraseña */}
        <div className="text-right">
          <Link
            to="/forgot-password"
            className="text-sm text-baby-pink hover:underline"
          >
            ¿Olvidaste tu contraseña?
          </Link>
        </div>
        
        {/* Submit */}
        <Button type="submit" disabled={loading} className="w-full">
          {loading ? '⏳ Iniciando sesión...' : 'Iniciar Sesión'}
        </Button>
      </form>
      
      {/* Registrarse */}
      <p className="text-center mt-6 text-gray-600">
        ¿No tienes cuenta?{' '}
        <Link to="/register" className="text-baby-pink font-semibold hover:underline">
          Regístrate aquí
        </Link>
      </p>
    </div>
  );
}
```

---

## 📝 RegisterForm

```tsx
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import Button from '../common/Button';
import Input from '../common/Input';
import ErrorMessage from '../common/ErrorMessage';
import { validateEmail, validatePassword } from '../../utils/validators';

export default function RegisterForm() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [showPasswords, setShowPasswords] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  
  // ✅ Validar campo individual
  const validateField = (name: string, value: string): string | null => {
    switch (name) {
      case 'name':
        return value.length < 3 ? 'Nombre debe tener al menos 3 caracteres' : null;
      case 'email':
        return !validateEmail(value) ? 'Email inválido' : null;
      case 'password':
        const passwordError = validatePassword(value);
        return passwordError;
      case 'confirmPassword':
        return value !== formData.password ? 'Las contraseñas no coinciden' : null;
      default:
        return null;
    }
  };
  
  // ✅ Manejar cambios
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    
    setFormData({
      ...formData,
      [name]: value,
    });
    
    // Validar y actualizar errores
    const error = validateField(name, value);
    setErrors(prev => ({
      ...prev,
      [name]: error || '',
    }));
  };
  
  // ✅ Manejar submit
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validar todos los campos
    const newErrors: Record<string, string> = {};
    Object.keys(formData).forEach(key => {
      const error = validateField(key, formData[key as keyof typeof formData]);
      if (error) newErrors[key] = error;
    });
    
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }
    
    setLoading(true);
    
    try {
      await register(formData.name, formData.email, formData.password);
      
      alert('¡Registro exitoso! Bienvenido a Baby Cash 🎉');
      navigate('/');
    } catch (err: any) {
      setErrors({
        global: err.response?.data?.message || 'Error al registrarse',
      });
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="max-w-md mx-auto bg-white rounded-lg shadow-md p-8">
      <h2 className="text-3xl font-bold text-center mb-6">Crear Cuenta</h2>
      
      {/* Error global */}
      {errors.global && <ErrorMessage message={errors.global} />}
      
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Nombre */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Nombre Completo
          </label>
          <Input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="Juan Pérez"
            required
          />
          {errors.name && (
            <p className="text-red-500 text-sm mt-1">{errors.name}</p>
          )}
        </div>
        
        {/* Email */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Email
          </label>
          <Input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="tu@email.com"
            required
          />
          {errors.email && (
            <p className="text-red-500 text-sm mt-1">{errors.email}</p>
          )}
        </div>
        
        {/* Password */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Contraseña
          </label>
          <div className="relative">
            <Input
              type={showPasswords ? 'text' : 'password'}
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              required
            />
            <button
              type="button"
              onClick={() => setShowPasswords(!showPasswords)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
            >
              {showPasswords ? '👁️' : '👁️‍🗨️'}
            </button>
          </div>
          {errors.password && (
            <p className="text-red-500 text-sm mt-1">{errors.password}</p>
          )}
          <p className="text-xs text-gray-500 mt-1">
            Mínimo 8 caracteres, incluir mayúscula, minúscula y número
          </p>
        </div>
        
        {/* Confirm Password */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Confirmar Contraseña
          </label>
          <Input
            type={showPasswords ? 'text' : 'password'}
            name="confirmPassword"
            value={formData.confirmPassword}
            onChange={handleChange}
            placeholder="••••••••"
            required
          />
          {errors.confirmPassword && (
            <p className="text-red-500 text-sm mt-1">{errors.confirmPassword}</p>
          )}
        </div>
        
        {/* Submit */}
        <Button type="submit" disabled={loading} className="w-full">
          {loading ? '⏳ Creando cuenta...' : 'Crear Cuenta'}
        </Button>
      </form>
      
      {/* Login */}
      <p className="text-center mt-6 text-gray-600">
        ¿Ya tienes cuenta?{' '}
        <Link to="/login" className="text-baby-pink font-semibold hover:underline">
          Inicia sesión aquí
        </Link>
      </p>
    </div>
  );
}
```

---

## 🔧 Utilidades de Validación

```tsx
// utils/validators.ts

export function validateEmail(email: string): boolean {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
}

export function validatePassword(password: string): string | null {
  if (password.length < 8) {
    return 'Contraseña debe tener al menos 8 caracteres';
  }
  
  if (!/[A-Z]/.test(password)) {
    return 'Contraseña debe tener al menos una mayúscula';
  }
  
  if (!/[a-z]/.test(password)) {
    return 'Contraseña debe tener al menos una minúscula';
  }
  
  if (!/[0-9]/.test(password)) {
    return 'Contraseña debe tener al menos un número';
  }
  
  return null;
}
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Cómo funciona el toggle de visibilidad de contraseña?"**

> "Uso estado `showPassword` para cambiar tipo de input:
> ```tsx
> const [showPassword, setShowPassword] = useState(false);
> 
> <input type={showPassword ? 'text' : 'password'} />
> <button onClick={() => setShowPassword(!showPassword)}>
>   {showPassword ? '👁️' : '👁️‍🗨️'}
> </button>
> ```
> - Si `showPassword` es `false`, input es tipo 'password' (••••)
> - Al hacer clic, cambia a `true`, input es tipo 'text' (muestra contraseña)
> - Mejora UX (usuario puede verificar que escribió correctamente)"

---

**2. "¿Cómo validas en tiempo real?"**

> "Valido en `handleChange` al escribir:
> ```tsx
> const handleChange = (e) => {
>   // Actualizar valor
>   setFormData({ ...formData, [name]: value });
>   
>   // Validar y mostrar error
>   const error = validateField(name, value);
>   setErrors({ ...errors, [name]: error });
> };
> ```
> - Usuario escribe en campo
> - `handleChange` se ejecuta
> - Valida campo específico
> - Muestra/oculta error inmediatamente
> - Feedback instantáneo mejora UX"

---

**3. "¿Qué validaciones hay para password?"**

> "Función `validatePassword` verifica:
> 1. **Mínimo 8 caracteres**: `/^.{8,}$/`
> 2. **Al menos una mayúscula**: `/[A-Z]/`
> 3. **Al menos una minúscula**: `/[a-z]/`
> 4. **Al menos un número**: `/[0-9]/`
> 
> Si falta alguno, retorna mensaje específico. Esto cumple con mejores prácticas de seguridad."

---

**4. "¿Cómo manejas redirect después de login?"**

> "Uso query param `redirect`:
> ```tsx
> const [searchParams] = useSearchParams();
> 
> // Después de login exitoso
> const redirect = searchParams.get('redirect') || '/';
> navigate(redirect);
> ```
> - Si usuario intentó acceder a `/carrito` sin login
> - `ProtectedRoute` redirige a `/login?redirect=/carrito`
> - Después de login, vuelve a `/carrito`
> - Mejora UX (no pierde contexto)"

---

## 📝 Checklist de Auth Components

```
✅ LoginForm con email/password
✅ RegisterForm con name/email/password/confirmPassword
✅ Toggle visibilidad de contraseña
✅ Validación en tiempo real
✅ Validación al submit
✅ Mensajes de error específicos
✅ Loading states
✅ Manejo de errores del backend
✅ Links entre Login/Register
✅ Redirect después de login
✅ Validaciones robustas (email, password)
```

---

## 🚀 Conclusión

**Auth Components:**
- ✅ Forms completos con validación
- ✅ UX fluida (errores en tiempo real)
- ✅ Seguridad (validaciones robustas)
- ✅ Integración con useAuth hook

**Son componentes críticos para acceso.**

---

**Ahora lee:** `ADMIN-CRUD-COMPONENTS.md` para componentes de admin. 🚀
