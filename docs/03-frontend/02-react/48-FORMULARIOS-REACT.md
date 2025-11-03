================================================================================
ARCHIVO 48: FORMULARIOS EN REACT
================================================================================

QUE SON LOS FORMULARIOS EN REACT
=================================

Los formularios son la forma principal de capturar datos del usuario.
En React, manejamos los formularios de manera diferente que en HTML tradicional.

ANALOGIA:
Un formulario es como una hoja de inscripcion.
React necesita saber en tiempo real que esta escribiendo el usuario en cada campo.


FORMULARIOS CONTROLADOS VS NO CONTROLADOS
==========================================

FORMULARIO NO CONTROLADO (tradicional HTML):
El DOM maneja el estado del formulario.

<form>
  <input type="text" name="nombre" />
</form>

El valor esta en el DOM, no en React.


FORMULARIO CONTROLADO (React way):
React maneja el estado del formulario.

function Formulario() {
  const [nombre, setNombre] = useState('')
  
  return (
    <form>
      <input 
        type="text"
        value={nombre}
        onChange={(e) => setNombre(e.target.value)}
      />
    </form>
  )
}

El valor esta en el estado de React.


POR QUE USAR CONTROLADOS:
- React tiene control total del valor
- Facil validar mientras escribes
- Facil manipular el valor (uppercase, trim, etc)
- Single source of truth (el estado de React)


FORMULARIO BASICO
==================

import { useState } from 'react'

function FormularioBasico() {
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  
  const handleSubmit = (e) => {
    e.preventDefault()  // Evita recargar la pagina
    
    console.log('Datos:', { nombre, email })
    
    // Aqui llamarias a la API
    // await api.post('/usuarios', { nombre, email })
  }
  
  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Nombre:</label>
        <input 
          type="text"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
        />
      </div>
      
      <div>
        <label>Email:</label>
        <input 
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
      </div>
      
      <button type="submit">Enviar</button>
    </form>
  )
}


FORMULARIO COMPLETO CON VALIDACION
===================================

function FormularioRegistro() {
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    password: '',
    confirmPassword: ''
  })
  
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)
  
  // Manejar cambios en inputs
  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
    
    // Limpiar error del campo cuando el usuario escribe
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }))
    }
  }
  
  // Validar formulario
  const validate = () => {
    const newErrors = {}
    
    if (!formData.nombre) {
      newErrors.nombre = 'El nombre es requerido'
    } else if (formData.nombre.length < 3) {
      newErrors.nombre = 'El nombre debe tener al menos 3 caracteres'
    }
    
    if (!formData.email) {
      newErrors.email = 'El email es requerido'
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email invalido'
    }
    
    if (!formData.password) {
      newErrors.password = 'La contraseña es requerida'
    } else if (formData.password.length < 6) {
      newErrors.password = 'La contraseña debe tener al menos 6 caracteres'
    }
    
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Las contraseñas no coinciden'
    }
    
    return newErrors
  }
  
  // Enviar formulario
  const handleSubmit = async (e) => {
    e.preventDefault()
    
    // Validar
    const newErrors = validate()
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      return
    }
    
    // Enviar datos
    setLoading(true)
    try {
      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nombre: formData.nombre,
          email: formData.email,
          password: formData.password
        })
      })
      
      if (response.ok) {
        alert('Registro exitoso')
        // Redirigir o limpiar formulario
      } else {
        alert('Error en registro')
      }
    } catch (error) {
      alert('Error de conexion')
    } finally {
      setLoading(false)
    }
  }
  
  return (
    <form onSubmit={handleSubmit} className="max-w-md mx-auto p-6">
      <h2 className="text-2xl font-bold mb-6">Registro</h2>
      
      {/* Nombre */}
      <div className="mb-4">
        <label className="block text-sm font-medium mb-2">
          Nombre *
        </label>
        <input 
          type="text"
          name="nombre"
          value={formData.nombre}
          onChange={handleChange}
          className={`w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 ${
            errors.nombre ? 'border-red-500' : 'border-gray-300'
          }`}
        />
        {errors.nombre && (
          <p className="text-red-500 text-sm mt-1">{errors.nombre}</p>
        )}
      </div>
      
      {/* Email */}
      <div className="mb-4">
        <label className="block text-sm font-medium mb-2">
          Email *
        </label>
        <input 
          type="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          className={`w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 ${
            errors.email ? 'border-red-500' : 'border-gray-300'
          }`}
        />
        {errors.email && (
          <p className="text-red-500 text-sm mt-1">{errors.email}</p>
        )}
      </div>
      
      {/* Password */}
      <div className="mb-4">
        <label className="block text-sm font-medium mb-2">
          Contraseña *
        </label>
        <input 
          type="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          className={`w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 ${
            errors.password ? 'border-red-500' : 'border-gray-300'
          }`}
        />
        {errors.password && (
          <p className="text-red-500 text-sm mt-1">{errors.password}</p>
        )}
      </div>
      
      {/* Confirmar Password */}
      <div className="mb-6">
        <label className="block text-sm font-medium mb-2">
          Confirmar Contraseña *
        </label>
        <input 
          type="password"
          name="confirmPassword"
          value={formData.confirmPassword}
          onChange={handleChange}
          className={`w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 ${
            errors.confirmPassword ? 'border-red-500' : 'border-gray-300'
          }`}
        />
        {errors.confirmPassword && (
          <p className="text-red-500 text-sm mt-1">{errors.confirmPassword}</p>
        )}
      </div>
      
      {/* Boton Submit */}
      <button 
        type="submit"
        disabled={loading}
        className="w-full bg-blue-500 text-white py-2 rounded font-semibold hover:bg-blue-600 disabled:bg-gray-400"
      >
        {loading ? 'Registrando...' : 'Registrarse'}
      </button>
    </form>
  )
}


TIPOS DE INPUTS
===============

1. TEXT INPUT:
--------------
<input 
  type="text"
  value={valor}
  onChange={(e) => setValor(e.target.value)}
  placeholder="Ingresa tu nombre"
/>


2. EMAIL INPUT:
---------------
<input 
  type="email"
  value={email}
  onChange={(e) => setEmail(e.target.value)}
  placeholder="tu@email.com"
/>


3. PASSWORD INPUT:
------------------
const [password, setPassword] = useState('')
const [showPassword, setShowPassword] = useState(false)

<div className="relative">
  <input 
    type={showPassword ? "text" : "password"}
    value={password}
    onChange={(e) => setPassword(e.target.value)}
  />
  <button 
    type="button"
    onClick={() => setShowPassword(!showPassword)}
    className="absolute right-3 top-1/2 transform -translate-y-1/2"
  >
    {showPassword ? '👁️' : '👁️‍🗨️'}
  </button>
</div>


4. NUMBER INPUT:
----------------
<input 
  type="number"
  value={cantidad}
  onChange={(e) => setCantidad(Number(e.target.value))}
  min="1"
  max="100"
/>


5. CHECKBOX:
------------
<label className="flex items-center gap-2">
  <input 
    type="checkbox"
    checked={aceptoTerminos}
    onChange={(e) => setAceptoTerminos(e.target.checked)}
  />
  Acepto los terminos y condiciones
</label>


6. RADIO BUTTONS:
-----------------
const [genero, setGenero] = useState('')

<div>
  <label className="flex items-center gap-2">
    <input 
      type="radio"
      value="masculino"
      checked={genero === 'masculino'}
      onChange={(e) => setGenero(e.target.value)}
    />
    Masculino
  </label>
  
  <label className="flex items-center gap-2">
    <input 
      type="radio"
      value="femenino"
      checked={genero === 'femenino'}
      onChange={(e) => setGenero(e.target.value)}
    />
    Femenino
  </label>
</div>


7. SELECT (DROPDOWN):
---------------------
<select 
  value={categoria}
  onChange={(e) => setCategoria(e.target.value)}
  className="w-full px-3 py-2 border rounded"
>
  <option value="">Selecciona una categoria</option>
  <option value="bebes">Bebes</option>
  <option value="niños">Niños</option>
  <option value="juguetes">Juguetes</option>
</select>


8. TEXTAREA:
------------
<textarea 
  value={descripcion}
  onChange={(e) => setDescripcion(e.target.value)}
  rows={5}
  className="w-full px-3 py-2 border rounded"
  placeholder="Escribe una descripcion..."
/>


9. FILE INPUT (IMAGENES):
-------------------------
const [imagen, setImagen] = useState(null)
const [preview, setPreview] = useState(null)

const handleFileChange = (e) => {
  const file = e.target.files[0]
  setImagen(file)
  
  // Crear preview
  const reader = new FileReader()
  reader.onloadend = () => {
    setPreview(reader.result)
  }
  reader.readAsDataURL(file)
}

<div>
  <input 
    type="file"
    onChange={handleFileChange}
    accept="image/*"
  />
  {preview && (
    <img src={preview} alt="Preview" className="mt-4 w-32 h-32 object-cover" />
  )}
</div>


10. DATE INPUT:
---------------
<input 
  type="date"
  value={fecha}
  onChange={(e) => setFecha(e.target.value)}
/>


VALIDACIONES
============

VALIDACION EN TIEMPO REAL:
---------------------------
const [email, setEmail] = useState('')
const [emailError, setEmailError] = useState('')

const handleEmailChange = (e) => {
  const value = e.target.value
  setEmail(value)
  
  // Validar mientras escribe
  if (value && !/\S+@\S+\.\S+/.test(value)) {
    setEmailError('Email invalido')
  } else {
    setEmailError('')
  }
}


VALIDACION AL PERDER FOCUS (onBlur):
-------------------------------------
const handleBlur = () => {
  if (!email) {
    setEmailError('El email es requerido')
  } else if (!/\S+@\S+\.\S+/.test(email)) {
    setEmailError('Email invalido')
  }
}

<input 
  value={email}
  onChange={handleEmailChange}
  onBlur={handleBlur}
/>


PATRONES DE VALIDACION COMUNES:
--------------------------------
// Email
const emailRegex = /\S+@\S+\.\S+/

// Solo letras
const soloLetras = /^[a-zA-Z\s]+$/

// Solo numeros
const soloNumeros = /^\d+$/

// Telefono (formato colombiano)
const telefono = /^3\d{9}$/

// Password fuerte (min 8, mayuscula, minuscula, numero)
const passwordFuerte = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/


FORMULARIO DE LOGIN EN BABYCASH
================================

import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [acceptedTerms, setAcceptedTerms] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    
    // Validar
    if (!email || !password) {
      setError('Completa todos los campos')
      return
    }
    
    if (!acceptedTerms) {
      setError('Debes aceptar los terminos')
      return
    }
    
    // Login
    setLoading(true)
    try {
      await login(email, password)
      
      // Redirigir segun rol
      const user = JSON.parse(localStorage.getItem('user'))
      if (user.role === 'ADMIN') {
        navigate('/admin')
      } else {
        navigate('/')
      }
    } catch (err) {
      setError('Credenciales invalidas')
    } finally {
      setLoading(false)
    }
  }
  
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-baby-blue via-baby-pink to-baby-mint px-4">
      <div className="bg-white shadow-xl rounded-2xl w-full max-w-md p-8">
        {/* Logo */}
        <div className="flex justify-center mb-6">
          <img src="/logo.png" alt="Logo" className="w-20 h-20" />
        </div>
        
        <h2 className="text-2xl font-bold text-center mb-6">
          Inicia sesion en <span className="text-baby-blue">Baby Cash</span>
        </h2>
        
        {error && (
          <p className="bg-red-100 text-red-700 p-3 rounded mb-4">{error}</p>
        )}
        
        <form onSubmit={handleSubmit}>
          {/* Email */}
          <div className="mb-4">
            <label className="block text-sm font-medium mb-2">Email</label>
            <input 
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
              placeholder="tu@email.com"
              required
            />
          </div>
          
          {/* Password */}
          <div className="mb-4">
            <label className="block text-sm font-medium mb-2">Contraseña</label>
            <div className="relative">
              <input 
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-baby-blue"
                required
              />
              <button 
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500"
              >
                {showPassword ? '👁️' : '👁️‍🗨️'}
              </button>
            </div>
          </div>
          
          {/* Terminos */}
          <div className="mb-6">
            <label className="flex items-start gap-2 text-sm">
              <input 
                type="checkbox"
                checked={acceptedTerms}
                onChange={(e) => setAcceptedTerms(e.target.checked)}
                className="mt-1"
              />
              <span>
                Acepto los{' '}
                <a href="/terminos" className="text-baby-blue underline">
                  terminos y condiciones
                </a>
              </span>
            </label>
          </div>
          
          {/* Submit */}
          <button 
            type="submit"
            disabled={loading}
            className="w-full bg-baby-blue text-white py-3 rounded-lg font-semibold hover:bg-blue-600 disabled:bg-gray-400 transition"
          >
            {loading ? 'Iniciando sesion...' : 'Iniciar sesion'}
          </button>
        </form>
        
        {/* Links */}
        <div className="mt-6 text-center text-sm">
          <p>
            ¿No tienes cuenta?{' '}
            <a href="/register" className="text-baby-blue font-semibold">
              Registrate
            </a>
          </p>
          <a href="/forgot-password" className="text-gray-600 mt-2 block">
            ¿Olvidaste tu contraseña?
          </a>
        </div>
      </div>
    </div>
  )
}


FORMULARIO DE CONTACTO
=======================

function Contacto() {
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    telefono: '',
    asunto: '',
    mensaje: ''
  })
  
  const [errors, setErrors] = useState({})
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)
  
  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }
  
  const validate = () => {
    const newErrors = {}
    
    if (!formData.nombre) newErrors.nombre = 'El nombre es requerido'
    if (!formData.email) newErrors.email = 'El email es requerido'
    if (!formData.mensaje) newErrors.mensaje = 'El mensaje es requerido'
    
    return newErrors
  }
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    
    const newErrors = validate()
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      return
    }
    
    setLoading(true)
    try {
      await fetch('/api/contacto', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      })
      
      setSuccess(true)
      setFormData({ nombre: '', email: '', telefono: '', asunto: '', mensaje: '' })
      
      setTimeout(() => setSuccess(false), 3000)
    } catch (error) {
      alert('Error al enviar mensaje')
    } finally {
      setLoading(false)
    }
  }
  
  return (
    <div className="max-w-2xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-8">Contactanos</h1>
      
      {success && (
        <div className="bg-green-100 text-green-700 p-4 rounded mb-6">
          ¡Mensaje enviado exitosamente!
        </div>
      )}
      
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">Nombre *</label>
            <input 
              name="nombre"
              value={formData.nombre}
              onChange={handleChange}
              className="w-full px-4 py-2 border rounded"
            />
            {errors.nombre && <p className="text-red-500 text-sm">{errors.nombre}</p>}
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-2">Email *</label>
            <input 
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="w-full px-4 py-2 border rounded"
            />
            {errors.email && <p className="text-red-500 text-sm">{errors.email}</p>}
          </div>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">Telefono</label>
            <input 
              type="tel"
              name="telefono"
              value={formData.telefono}
              onChange={handleChange}
              className="w-full px-4 py-2 border rounded"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-2">Asunto</label>
            <select 
              name="asunto"
              value={formData.asunto}
              onChange={handleChange}
              className="w-full px-4 py-2 border rounded"
            >
              <option value="">Selecciona un asunto</option>
              <option value="consulta">Consulta general</option>
              <option value="pedido">Sobre mi pedido</option>
              <option value="producto">Informacion de producto</option>
              <option value="queja">Queja o reclamo</option>
            </select>
          </div>
        </div>
        
        <div>
          <label className="block text-sm font-medium mb-2">Mensaje *</label>
          <textarea 
            name="mensaje"
            value={formData.mensaje}
            onChange={handleChange}
            rows={6}
            className="w-full px-4 py-2 border rounded"
            placeholder="Escribenos tu mensaje..."
          />
          {errors.mensaje && <p className="text-red-500 text-sm">{errors.mensaje}</p>}
        </div>
        
        <button 
          type="submit"
          disabled={loading}
          className="w-full bg-baby-blue text-white py-3 rounded-lg font-semibold hover:bg-blue-600 disabled:bg-gray-400"
        >
          {loading ? 'Enviando...' : 'Enviar mensaje'}
        </button>
      </form>
    </div>
  )
}


FORMULARIO CON MULTIPLES PASOS
===============================

function FormularioMultiPaso() {
  const [paso, setPaso] = useState(1)
  const [formData, setFormData] = useState({
    // Paso 1
    nombre: '',
    email: '',
    // Paso 2
    direccion: '',
    ciudad: '',
    // Paso 3
    tarjeta: '',
    cvv: ''
  })
  
  const siguiente = () => {
    if (paso < 3) setPaso(paso + 1)
  }
  
  const anterior = () => {
    if (paso > 1) setPaso(paso - 1)
  }
  
  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }
  
  const handleSubmit = (e) => {
    e.preventDefault()
    console.log('Datos completos:', formData)
  }
  
  return (
    <div className="max-w-2xl mx-auto p-6">
      {/* Indicador de pasos */}
      <div className="flex justify-between mb-8">
        <div className={`flex-1 text-center ${paso >= 1 ? 'text-blue-600 font-bold' : 'text-gray-400'}`}>
          Paso 1: Informacion
        </div>
        <div className={`flex-1 text-center ${paso >= 2 ? 'text-blue-600 font-bold' : 'text-gray-400'}`}>
          Paso 2: Direccion
        </div>
        <div className={`flex-1 text-center ${paso >= 3 ? 'text-blue-600 font-bold' : 'text-gray-400'}`}>
          Paso 3: Pago
        </div>
      </div>
      
      <form onSubmit={handleSubmit}>
        {/* Paso 1 */}
        {paso === 1 && (
          <div>
            <h2 className="text-2xl font-bold mb-4">Informacion personal</h2>
            <input 
              name="nombre"
              value={formData.nombre}
              onChange={handleChange}
              placeholder="Nombre"
              className="w-full px-4 py-2 border rounded mb-4"
            />
            <input 
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Email"
              className="w-full px-4 py-2 border rounded"
            />
          </div>
        )}
        
        {/* Paso 2 */}
        {paso === 2 && (
          <div>
            <h2 className="text-2xl font-bold mb-4">Direccion de envio</h2>
            <input 
              name="direccion"
              value={formData.direccion}
              onChange={handleChange}
              placeholder="Direccion"
              className="w-full px-4 py-2 border rounded mb-4"
            />
            <input 
              name="ciudad"
              value={formData.ciudad}
              onChange={handleChange}
              placeholder="Ciudad"
              className="w-full px-4 py-2 border rounded"
            />
          </div>
        )}
        
        {/* Paso 3 */}
        {paso === 3 && (
          <div>
            <h2 className="text-2xl font-bold mb-4">Informacion de pago</h2>
            <input 
              name="tarjeta"
              value={formData.tarjeta}
              onChange={handleChange}
              placeholder="Numero de tarjeta"
              className="w-full px-4 py-2 border rounded mb-4"
            />
            <input 
              name="cvv"
              value={formData.cvv}
              onChange={handleChange}
              placeholder="CVV"
              className="w-full px-4 py-2 border rounded"
            />
          </div>
        )}
        
        {/* Botones */}
        <div className="flex justify-between mt-6">
          <button 
            type="button"
            onClick={anterior}
            disabled={paso === 1}
            className="px-6 py-2 border rounded disabled:opacity-50"
          >
            Anterior
          </button>
          
          {paso < 3 ? (
            <button 
              type="button"
              onClick={siguiente}
              className="px-6 py-2 bg-blue-500 text-white rounded"
            >
              Siguiente
            </button>
          ) : (
            <button 
              type="submit"
              className="px-6 py-2 bg-green-500 text-white rounded"
            >
              Finalizar
            </button>
          )}
        </div>
      </form>
    </div>
  )
}


CONSEJOS Y MEJORES PRACTICAS
=============================

1. SIEMPRE USAR e.preventDefault() en onSubmit
   Evita que la pagina se recargue

2. VALIDAR ANTES DE ENVIAR
   Validar en frontend Y backend

3. DESHABILITAR BOTON MIENTRAS CARGA
   disabled={loading}

4. MOSTRAR FEEDBACK AL USUARIO
   - Mensajes de error claros
   - Spinner mientras carga
   - Mensaje de exito

5. LIMPIAR FORMULARIO DESPUES DE ENVIAR
   setFormData({ campo1: '', campo2: '' })

6. USAR LABELS CON htmlFor
   <label htmlFor="email">Email</label>
   <input id="email" />

7. ACCESIBILIDAD
   - required en campos obligatorios
   - placeholder descriptivos
   - aria-labels cuando sea necesario

8. MANTENER EL ESTADO EN UN SOLO OBJETO
   const [formData, setFormData] = useState({})
   En vez de multiples useState

9. CREAR COMPONENTES REUTILIZABLES
   Input, Select, Textarea personalizados

10. USAR TypeScript PARA TIPOS
    interface FormData { nombre: string; email: string }


ERRORES COMUNES
===============

1. NO USAR e.preventDefault()
   ✗ El formulario recarga la pagina
   ✓ Siempre usar e.preventDefault() en onSubmit

2. OLVIDAR value Y onChange
   ✗ <input onChange={...} /> (sin value)
   ✓ <input value={...} onChange={...} />

3. NO VALIDAR ANTES DE ENVIAR
   ✗ Enviar sin validar
   ✓ Validar y mostrar errores

4. NO MANEJAR LOADING STATE
   ✗ El usuario puede hacer click multiples veces
   ✓ Deshabilitar boton mientras carga

5. NO MOSTRAR ERRORES AL USUARIO
   ✗ console.log(error)
   ✓ Mostrar mensaje de error en UI


RESUMEN
=======

Formularios en React:
✓ Usar formularios controlados (value + onChange)
✓ Estado en React, no en DOM
✓ Validar antes de enviar
✓ Manejar loading, error, success states
✓ e.preventDefault() para evitar reload
✓ Feedback claro al usuario

Validaciones:
✓ En tiempo real (onChange)
✓ Al perder focus (onBlur)
✓ Antes de enviar (onSubmit)
✓ Frontend y Backend

Tipos de inputs:
- text, email, password, number
- checkbox, radio, select
- textarea, file, date

En BabyCash:
✓ Login con email y password
✓ Registro con validaciones
✓ Contacto con asunto y mensaje
✓ Checkout multi-paso

================================================================================
