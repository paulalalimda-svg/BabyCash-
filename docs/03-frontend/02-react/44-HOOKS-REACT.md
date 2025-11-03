================================================================================
ARCHIVO 44: HOOKS REACT
================================================================================

QUE SON LOS HOOKS
=================

Los hooks son FUNCIONES ESPECIALES de React que te permiten usar estado y
otras caracteristicas de React en componentes funcionales.

ANALOGIA:
Los hooks son como "ganchos" que conectan tu componente con funcionalidades
especiales de React.

REGLAS DE LOS HOOKS:
1. Solo llamar hooks en el NIVEL SUPERIOR (no dentro de if, loops, o funciones anidadas)
2. Solo llamar hooks en COMPONENTES FUNCIONALES o CUSTOM HOOKS
3. Los nombres de hooks SIEMPRE empiezan con "use"


HOOKS PRINCIPALES
=================

1. useState    - Manejo de estado
2. useEffect   - Efectos secundarios (API calls, timers, etc)
3. useContext  - Acceder a Context
4. useRef      - Referencias a elementos DOM
5. useMemo     - Memoizacion de valores
6. useCallback - Memoizacion de funciones
7. useReducer  - Estado complejo (alternativa a useState)
8. Custom Hooks - Hooks personalizados


================================================================================
1. USESTATE - MANEJO DE ESTADO
================================================================================

El estado es INFORMACION que puede CAMBIAR en el componente.

SINTAXIS:
const [valor, setValor] = useState(valorInicial)

- valor: El valor actual del estado
- setValor: Funcion para actualizar el estado
- valorInicial: Valor con el que inicia el estado


EJEMPLO BASICO - CONTADOR:
---------------------------
import { useState } from 'react'

function Contador() {
  const [contador, setContador] = useState(0)
  
  const incrementar = () => {
    setContador(contador + 1)
  }
  
  const decrementar = () => {
    setContador(contador - 1)
  }
  
  return (
    <div>
      <h2>Contador: {contador}</h2>
      <button onClick={incrementar}>+</button>
      <button onClick={decrementar}>-</button>
    </div>
  )
}


EJEMPLO - INPUT DE TEXTO:
--------------------------
function Formulario() {
  const [nombre, setNombre] = useState('')
  
  return (
    <div>
      <input 
        value={nombre}
        onChange={(e) => setNombre(e.target.value)}
        placeholder="Ingresa tu nombre"
      />
      <p>Tu nombre es: {nombre}</p>
    </div>
  )
}


EJEMPLO - CHECKBOX:
-------------------
function FormularioRegistro() {
  const [aceptoTerminos, setAceptoTerminos] = useState(false)
  
  return (
    <div>
      <label>
        <input 
          type="checkbox"
          checked={aceptoTerminos}
          onChange={(e) => setAceptoTerminos(e.target.checked)}
        />
        Acepto los terminos y condiciones
      </label>
      
      <button disabled={!aceptoTerminos}>
        Registrarse
      </button>
    </div>
  )
}


ESTADO CON OBJETOS:
-------------------
function FormularioUsuario() {
  const [usuario, setUsuario] = useState({
    nombre: '',
    email: '',
    edad: 0
  })
  
  const handleChange = (campo, valor) => {
    setUsuario({
      ...usuario,      // Mantener valores anteriores
      [campo]: valor   // Actualizar solo este campo
    })
  }
  
  return (
    <div>
      <input 
        value={usuario.nombre}
        onChange={(e) => handleChange('nombre', e.target.value)}
        placeholder="Nombre"
      />
      <input 
        value={usuario.email}
        onChange={(e) => handleChange('email', e.target.value)}
        placeholder="Email"
      />
      <input 
        type="number"
        value={usuario.edad}
        onChange={(e) => handleChange('edad', Number(e.target.value))}
        placeholder="Edad"
      />
    </div>
  )
}


ESTADO CON ARRAYS:
------------------
function ListaTareas() {
  const [tareas, setTareas] = useState([])
  const [nuevaTarea, setNuevaTarea] = useState('')
  
  const agregarTarea = () => {
    setTareas([...tareas, nuevaTarea])
    setNuevaTarea('')
  }
  
  const eliminarTarea = (index) => {
    setTareas(tareas.filter((_, i) => i !== index))
  }
  
  return (
    <div>
      <input 
        value={nuevaTarea}
        onChange={(e) => setNuevaTarea(e.target.value)}
      />
      <button onClick={agregarTarea}>Agregar</button>
      
      <ul>
        {tareas.map((tarea, index) => (
          <li key={index}>
            {tarea}
            <button onClick={() => eliminarTarea(index)}>Eliminar</button>
          </li>
        ))}
      </ul>
    </div>
  )
}


ACTUALIZACION FUNCIONAL:
-------------------------
Usar cuando el nuevo estado depende del anterior.

MAL:
const incrementar = () => {
  setContador(contador + 1)
  setContador(contador + 1)  // No funciona como esperas
}

BIEN:
const incrementar = () => {
  setContador(prev => prev + 1)
  setContador(prev => prev + 1)  // Funciona correctamente
}


================================================================================
2. USEEFFECT - EFECTOS SECUNDARIOS
================================================================================

useEffect se usa para ejecutar codigo que tiene "efectos secundarios":
- Llamadas a API
- Suscripciones
- Timers
- Manipulacion del DOM
- localStorage


SINTAXIS:
useEffect(() => {
  // Codigo que se ejecuta
  
  return () => {
    // Cleanup (limpieza) - opcional
  }
}, [dependencias])


CASO 1 - EJECUTAR UNA VEZ AL MONTAR:
-------------------------------------
useEffect(() => {
  console.log('Componente montado')
  // Llamar API, inicializar datos, etc
}, [])  // Array vacio = solo al montar


CASO 2 - EJECUTAR EN CADA RENDER:
----------------------------------
useEffect(() => {
  console.log('Componente renderizado')
})  // Sin array de dependencias


CASO 3 - EJECUTAR CUANDO CAMBIA UNA VARIABLE:
----------------------------------------------
useEffect(() => {
  console.log('El contador cambio:', contador)
}, [contador])  // Se ejecuta cuando contador cambia


CASO 4 - CLEANUP (LIMPIEZA):
-----------------------------
useEffect(() => {
  const timer = setInterval(() => {
    console.log('Tick')
  }, 1000)
  
  // Cleanup: se ejecuta al desmontar o antes de re-ejecutar
  return () => {
    clearInterval(timer)
  }
}, [])


EJEMPLO COMPLETO - LLAMADA A API:
----------------------------------
function ListaUsuarios() {
  const [usuarios, setUsuarios] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  
  useEffect(() => {
    // Llamar API cuando el componente se monta
    fetch('https://api.example.com/usuarios')
      .then(response => response.json())
      .then(data => {
        setUsuarios(data)
        setLoading(false)
      })
      .catch(error => {
        setError(error.message)
        setLoading(false)
      })
  }, [])  // [] = solo una vez al montar
  
  if (loading) return <p>Cargando...</p>
  if (error) return <p>Error: {error}</p>
  
  return (
    <ul>
      {usuarios.map(usuario => (
        <li key={usuario.id}>{usuario.nombre}</li>
      ))}
    </ul>
  )
}


EJEMPLO - SINCRONIZAR CON LOCALSTORAGE:
----------------------------------------
function Formulario() {
  const [nombre, setNombre] = useState(() => {
    // Inicializar desde localStorage
    return localStorage.getItem('nombre') || ''
  })
  
  useEffect(() => {
    // Guardar en localStorage cada vez que cambia
    localStorage.setItem('nombre', nombre)
  }, [nombre])
  
  return (
    <input 
      value={nombre}
      onChange={(e) => setNombre(e.target.value)}
    />
  )
}


EJEMPLO - TIMER:
----------------
function Reloj() {
  const [hora, setHora] = useState(new Date())
  
  useEffect(() => {
    const timer = setInterval(() => {
      setHora(new Date())
    }, 1000)
    
    // Limpiar el timer al desmontar
    return () => clearInterval(timer)
  }, [])
  
  return <h1>{hora.toLocaleTimeString()}</h1>
}


EJEMPLO - SCROLL LISTENER:
---------------------------
function ScrollDetector() {
  const [scrollY, setScrollY] = useState(0)
  
  useEffect(() => {
    const handleScroll = () => {
      setScrollY(window.scrollY)
    }
    
    window.addEventListener('scroll', handleScroll)
    
    // Remover listener al desmontar
    return () => {
      window.removeEventListener('scroll', handleScroll)
    }
  }, [])
  
  return <p>Scroll: {scrollY}px</p>
}


================================================================================
3. USECONTEXT - COMPARTIR DATOS GLOBALMENTE
================================================================================

useContext permite acceder a datos sin tener que pasar props por muchos niveles.

ANALOGIA:
En vez de pasar un paquete de mano en mano por toda una cadena de personas,
pones el paquete en un lugar central donde todos pueden acceder.


PASO 1 - CREAR EL CONTEXT:
---------------------------
// AuthContext.tsx
import { createContext, useContext, useState } from 'react'

interface AuthContextType {
  usuario: User | null
  login: (usuario: User) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)


PASO 2 - CREAR EL PROVIDER:
----------------------------
export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [usuario, setUsuario] = useState<User | null>(null)
  
  const login = (user: User) => {
    setUsuario(user)
    localStorage.setItem('token', user.token)
  }
  
  const logout = () => {
    setUsuario(null)
    localStorage.removeItem('token')
  }
  
  return (
    <AuthContext.Provider value={{ usuario, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}


PASO 3 - CREAR HOOK PERSONALIZADO:
-----------------------------------
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider')
  }
  return context
}


PASO 4 - ENVOLVER LA APP:
--------------------------
// App.tsx
function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
      </Router>
    </AuthProvider>
  )
}


PASO 5 - USAR EN CUALQUIER COMPONENTE:
---------------------------------------
function Header() {
  const { usuario, logout } = useAuth()
  
  return (
    <header>
      {usuario ? (
        <>
          <span>Hola, {usuario.nombre}</span>
          <button onClick={logout}>Cerrar sesion</button>
        </>
      ) : (
        <a href="/login">Iniciar sesion</a>
      )}
    </header>
  )
}


================================================================================
4. USEREF - REFERENCIAS
================================================================================

useRef crea una referencia que:
1. Persiste entre renders
2. NO causa re-render cuando cambia
3. Se usa para acceder a elementos DOM

SINTAXIS:
const ref = useRef(valorInicial)


CASO 1 - ACCEDER A ELEMENTO DOM:
---------------------------------
function InputFocus() {
  const inputRef = useRef<HTMLInputElement>(null)
  
  const enfocarInput = () => {
    inputRef.current?.focus()
  }
  
  return (
    <>
      <input ref={inputRef} />
      <button onClick={enfocarInput}>Enfocar</button>
    </>
  )
}


CASO 2 - GUARDAR VALOR SIN CAUSAR RE-RENDER:
---------------------------------------------
function Contador() {
  const [contador, setContador] = useState(0)
  const renderCount = useRef(0)
  
  useEffect(() => {
    renderCount.current += 1
  })
  
  return (
    <div>
      <p>Contador: {contador}</p>
      <p>Renders: {renderCount.current}</p>
      <button onClick={() => setContador(c => c + 1)}>Incrementar</button>
    </div>
  )
}


CASO 3 - GUARDAR VALOR ANTERIOR:
---------------------------------
function usePrevious(value) {
  const ref = useRef()
  
  useEffect(() => {
    ref.current = value
  }, [value])
  
  return ref.current
}

function Componente() {
  const [contador, setContador] = useState(0)
  const contadorAnterior = usePrevious(contador)
  
  return (
    <div>
      <p>Actual: {contador}</p>
      <p>Anterior: {contadorAnterior}</p>
      <button onClick={() => setContador(c => c + 1)}>+</button>
    </div>
  )
}


================================================================================
5. USEMEMO - MEMOIZACION DE VALORES
================================================================================

useMemo guarda el resultado de un calculo costoso y solo lo recalcula cuando
las dependencias cambian.

CUANDO USAR:
- Calculos costosos
- Crear objetos/arrays que se pasan como props
- Evitar renders innecesarios

SINTAXIS:
const valorMemoizado = useMemo(() => calcularValor(), [dependencias])


EJEMPLO - CALCULO COSTOSO:
---------------------------
function ListaFiltrada({ items, filtro }) {
  // Sin useMemo: se recalcula en cada render
  // const itemsFiltrados = items.filter(item => 
  //   item.nombre.toLowerCase().includes(filtro.toLowerCase())
  // )
  
  // Con useMemo: solo se recalcula cuando items o filtro cambian
  const itemsFiltrados = useMemo(() => {
    console.log('Filtrando items...')
    return items.filter(item => 
      item.nombre.toLowerCase().includes(filtro.toLowerCase())
    )
  }, [items, filtro])
  
  return (
    <ul>
      {itemsFiltrados.map(item => (
        <li key={item.id}>{item.nombre}</li>
      ))}
    </ul>
  )
}


EJEMPLO - CALCULOS MATEMATICOS:
--------------------------------
function CalculadoraCompleja({ numeros }) {
  const suma = useMemo(() => {
    console.log('Calculando suma...')
    return numeros.reduce((a, b) => a + b, 0)
  }, [numeros])
  
  const promedio = useMemo(() => {
    console.log('Calculando promedio...')
    return suma / numeros.length
  }, [suma, numeros.length])
  
  return (
    <div>
      <p>Suma: {suma}</p>
      <p>Promedio: {promedio}</p>
    </div>
  )
}


================================================================================
6. USECALLBACK - MEMOIZACION DE FUNCIONES
================================================================================

useCallback guarda una funcion y solo la recrea cuando las dependencias cambian.

CUANDO USAR:
- Pasar funciones como props a componentes hijos
- Evitar re-renders innecesarios
- Dependencias de useEffect

SINTAXIS:
const funcionMemoizada = useCallback(() => { /* codigo */ }, [dependencias])


EJEMPLO BASICO:
---------------
function Padre() {
  const [contador, setContador] = useState(0)
  const [otro, setOtro] = useState(0)
  
  // Sin useCallback: se crea nueva funcion en cada render
  // const incrementar = () => setContador(c => c + 1)
  
  // Con useCallback: misma funcion mientras contador no cambie
  const incrementar = useCallback(() => {
    setContador(c => c + 1)
  }, [])
  
  return (
    <>
      <Hijo onIncrement={incrementar} />
      <p>Otro: {otro}</p>
      <button onClick={() => setOtro(o => o + 1)}>Cambiar otro</button>
    </>
  )
}


EJEMPLO CON PARAMETROS:
------------------------
function Lista({ items }) {
  const handleDelete = useCallback((id) => {
    // Eliminar item
    fetch(`/api/items/${id}`, { method: 'DELETE' })
  }, [])
  
  return (
    <ul>
      {items.map(item => (
        <Item 
          key={item.id} 
          item={item}
          onDelete={handleDelete}
        />
      ))}
    </ul>
  )
}


================================================================================
7. USEREDUCER - ESTADO COMPLEJO
================================================================================

useReducer es como useState pero para estado mas complejo.

CUANDO USAR:
- Estado con multiples sub-valores
- Logica de actualizacion compleja
- Transiciones de estado

ANALOGIA:
useState es como un interruptor simple (on/off).
useReducer es como un control remoto con muchos botones.


SINTAXIS:
const [state, dispatch] = useReducer(reducer, initialState)


EJEMPLO - CONTADOR CON REDUCER:
--------------------------------
// Reducer: funcion que define como actualizar el estado
function reducer(state, action) {
  switch (action.type) {
    case 'incrementar':
      return { contador: state.contador + 1 }
    case 'decrementar':
      return { contador: state.contador - 1 }
    case 'reset':
      return { contador: 0 }
    default:
      return state
  }
}

function Contador() {
  const [state, dispatch] = useReducer(reducer, { contador: 0 })
  
  return (
    <div>
      <p>Contador: {state.contador}</p>
      <button onClick={() => dispatch({ type: 'incrementar' })}>+</button>
      <button onClick={() => dispatch({ type: 'decrementar' })}>-</button>
      <button onClick={() => dispatch({ type: 'reset' })}>Reset</button>
    </div>
  )
}


EJEMPLO - FORMULARIO CON REDUCER:
----------------------------------
interface FormState {
  nombre: string
  email: string
  password: string
  errors: { [key: string]: string }
}

type Action = 
  | { type: 'UPDATE_FIELD'; field: string; value: string }
  | { type: 'SET_ERROR'; field: string; error: string }
  | { type: 'RESET' }

function formReducer(state: FormState, action: Action): FormState {
  switch (action.type) {
    case 'UPDATE_FIELD':
      return {
        ...state,
        [action.field]: action.value,
        errors: { ...state.errors, [action.field]: '' }
      }
    case 'SET_ERROR':
      return {
        ...state,
        errors: { ...state.errors, [action.field]: action.error }
      }
    case 'RESET':
      return initialState
    default:
      return state
  }
}

const initialState: FormState = {
  nombre: '',
  email: '',
  password: '',
  errors: {}
}

function Formulario() {
  const [state, dispatch] = useReducer(formReducer, initialState)
  
  const handleChange = (field: string, value: string) => {
    dispatch({ type: 'UPDATE_FIELD', field, value })
  }
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    
    // Validar
    if (!state.nombre) {
      dispatch({ type: 'SET_ERROR', field: 'nombre', error: 'Nombre requerido' })
      return
    }
    
    // Enviar datos
    console.log(state)
  }
  
  return (
    <form onSubmit={handleSubmit}>
      <input 
        value={state.nombre}
        onChange={(e) => handleChange('nombre', e.target.value)}
      />
      {state.errors.nombre && <p>{state.errors.nombre}</p>}
      
      <input 
        value={state.email}
        onChange={(e) => handleChange('email', e.target.value)}
      />
      
      <button type="submit">Enviar</button>
    </form>
  )
}


================================================================================
8. CUSTOM HOOKS - HOOKS PERSONALIZADOS
================================================================================

Los custom hooks son funciones que usan otros hooks y encapsulan logica reutilizable.

REGLAS:
1. El nombre DEBE empezar con "use"
2. Pueden usar otros hooks
3. Son funciones normales de JavaScript


EJEMPLO 1 - useLocalStorage:
-----------------------------
function useLocalStorage<T>(key: string, initialValue: T) {
  const [value, setValue] = useState<T>(() => {
    const item = localStorage.getItem(key)
    return item ? JSON.parse(item) : initialValue
  })
  
  const setStoredValue = (newValue: T) => {
    setValue(newValue)
    localStorage.setItem(key, JSON.stringify(newValue))
  }
  
  return [value, setStoredValue] as const
}

// Uso:
function App() {
  const [nombre, setNombre] = useLocalStorage('nombre', '')
  
  return (
    <input 
      value={nombre}
      onChange={(e) => setNombre(e.target.value)}
    />
  )
}


EJEMPLO 2 - useFetch:
----------------------
interface FetchState<T> {
  data: T | null
  loading: boolean
  error: string | null
}

function useFetch<T>(url: string) {
  const [state, setState] = useState<FetchState<T>>({
    data: null,
    loading: true,
    error: null
  })
  
  useEffect(() => {
    let cancelled = false
    
    fetch(url)
      .then(res => res.json())
      .then(data => {
        if (!cancelled) {
          setState({ data, loading: false, error: null })
        }
      })
      .catch(error => {
        if (!cancelled) {
          setState({ data: null, loading: false, error: error.message })
        }
      })
    
    return () => {
      cancelled = true
    }
  }, [url])
  
  return state
}

// Uso:
function Usuarios() {
  const { data, loading, error } = useFetch<User[]>('/api/usuarios')
  
  if (loading) return <p>Cargando...</p>
  if (error) return <p>Error: {error}</p>
  
  return (
    <ul>
      {data?.map(user => (
        <li key={user.id}>{user.nombre}</li>
      ))}
    </ul>
  )
}


EJEMPLO 3 - useToggle:
-----------------------
function useToggle(initialValue = false) {
  const [value, setValue] = useState(initialValue)
  
  const toggle = () => setValue(v => !v)
  const setTrue = () => setValue(true)
  const setFalse = () => setValue(false)
  
  return { value, toggle, setTrue, setFalse }
}

// Uso:
function Modal() {
  const { value: isOpen, toggle, setFalse } = useToggle()
  
  return (
    <>
      <button onClick={toggle}>Abrir Modal</button>
      {isOpen && (
        <div className="modal">
          <button onClick={setFalse}>Cerrar</button>
          <p>Contenido del modal</p>
        </div>
      )}
    </>
  )
}


EJEMPLO 4 - useDebounce:
-------------------------
function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value)
  
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedValue(value)
    }, delay)
    
    return () => clearTimeout(timer)
  }, [value, delay])
  
  return debouncedValue
}

// Uso:
function BuscadorProductos() {
  const [busqueda, setBusqueda] = useState('')
  const busquedaDebounced = useDebounce(busqueda, 500)
  
  useEffect(() => {
    if (busquedaDebounced) {
      // Llamar API solo despues de 500ms sin escribir
      fetch(`/api/productos?search=${busquedaDebounced}`)
        .then(res => res.json())
        .then(data => console.log(data))
    }
  }, [busquedaDebounced])
  
  return (
    <input 
      value={busqueda}
      onChange={(e) => setBusqueda(e.target.value)}
      placeholder="Buscar productos..."
    />
  )
}


CUSTOM HOOK EN BABYCASH - useAuth:
-----------------------------------
// hooks/useAuth.ts
import { useContext } from 'react'
import { AuthContext } from '../contexts/AuthContext'

export function useAuth() {
  const context = useContext(AuthContext)
  
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider')
  }
  
  return context
}

// Uso en componentes:
function Header() {
  const { user, logout } = useAuth()
  
  return (
    <header>
      {user && (
        <>
          <span>Hola, {user.name}</span>
          <button onClick={logout}>Salir</button>
        </>
      )}
    </header>
  )
}


RESUMEN DE HOOKS
================

useState     - Manejo de estado local
useEffect    - Efectos secundarios (API, timers, etc)
useContext   - Acceder a datos globales
useRef       - Referencias a DOM y valores persistentes
useMemo      - Memoizar valores costosos
useCallback  - Memoizar funciones
useReducer   - Estado complejo con reducer
Custom Hooks - Reutilizar logica entre componentes

REGLAS:
✓ Llamar hooks solo en el nivel superior
✓ Llamar hooks solo en componentes funcionales o custom hooks
✓ Nombres de hooks empiezan con "use"

ERRORES COMUNES:
✗ Llamar hooks dentro de if/loops
✗ Olvidar dependencias en useEffect
✗ Modificar directamente el estado (usar siempre setState)
✗ Usar index como key en listas

================================================================================
