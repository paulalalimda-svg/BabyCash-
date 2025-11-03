# 📁 Estructura de Carpetas - Baby Cash Docs

## 🎯 Nueva Estructura Organizada

```
docs/
│
├── 📄 README.md                    # Índice principal
│
├── 📂 01-introduccion/             # EMPIEZA AQUÍ
│   ├── 00-ROADMAP.md              # 👈 Guía de lectura completa
│   ├── HTTP-REST-BASICS.md
│   ├── SPRING-BOOT-BASICS.md
│   ├── REACT-BASICS.md
│   └── SQL-BASICS.md
│
├── 📂 02-backend/                  # Spring Boot
│   ├── README.md                   # Índice backend
│   │
│   ├── 📁 01-fundamentos/          # Java básico
│   │   ├── VARIABLES-Y-CONTROL.md
│   │   ├── CLASES-Y-OBJETOS.md
│   │   ├── METODOS-Y-FUNCIONES.md
│   │   ├── ANOTACIONES-JAVA.md
│   │   ├── MODIFICADORES-ACCESO.md
│   │   └── PAQUETES-E-IMPORTS.md
│   │
│   ├── 📁 02-spring-boot/          # Spring Boot inicial
│   │   ├── SPRING-BOOT-INICIAL.md
│   │   ├── ESTRUCTURA-PROYECTO.md
│   │   ├── MAVEN-DEPENDENCIES.md
│   │   └── APPLICATION-PROPERTIES.md
│   │
│   ├── 📁 03-arquitectura/         # MVC y capas
│   │   ├── ARQUITECTURA-MVC.md
│   │   ├── CAPA-CONTROLLER.md
│   │   ├── CAPA-SERVICE.md
│   │   ├── CAPA-REPOSITORY.md
│   │   ├── DTOS-DATA-TRANSFER-OBJECTS.md
│   │   └── FLUJO-REQUEST-RESPONSE.md
│   │
│   ├── 📁 04-seguridad/            # JWT y Security
│   │   ├── JWT-SPRING-SECURITY.md
│   │   ├── AUTHENTICATION.md
│   │   ├── AUTHORIZATION.md
│   │   └── PASSWORD-ENCODING.md
│   │
│   └── 📁 05-avanzado/             # Clean Code, SOLID, Patterns
│       ├── CLEAN-CODE/
│       │   ├── NOMBRES-SIGNIFICATIVOS.md
│       │   ├── FUNCIONES-METODOS-LIMPIOS.md
│       │   └── ...
│       ├── SOLID/
│       │   ├── SINGLE-RESPONSIBILITY.md
│       │   ├── OPEN-CLOSED.md
│       │   └── ...
│       └── PATTERNS/
│           ├── SINGLETON.md
│           ├── FACTORY.md
│           └── ...
│
├── 📂 03-frontend/                 # React & TypeScript
│   ├── README.md                   # Índice frontend
│   │
│   ├── 📁 01-fundamentos/          # Estructura proyecto
│   │   ├── ESTRUCTURA-PROYECTO.md
│   │   ├── PACKAGE-JSON.md
│   │   └── VITE-CONFIG.md
│   │
│   ├── 📁 02-react/                # React core
│   │   ├── COMPONENTS/
│   │   │   ├── NAVBAR.md
│   │   │   ├── PRODUCT-CARD.md
│   │   │   └── ...
│   │   ├── PAGES/
│   │   │   ├── HOME.md
│   │   │   ├── PRODUCTOS.md
│   │   │   └── ...
│   │   ├── REACT-ROUTER.md
│   │   ├── CONTEXT-API.md
│   │   ├── FORMS.md
│   │   └── AXIOS-API.md
│   │
│   └── 📁 03-avanzado/             # Temas avanzados
│       ├── CONTEXT-API-STATE-MANAGEMENT.md  ⭐
│       ├── PERFORMANCE-OPTIMIZACION.md       ⭐
│       ├── MANEJO-ERRORES.md                 ⭐
│       ├── TYPESCRIPT-PATTERNS.md            ⭐
│       ├── TESTING-STRATEGIES.md             ⭐
│       └── SECURITY.md                       ⭐
│
├── 📂 04-base-de-datos/            # PostgreSQL
│   ├── README.md                   # Índice database
│   │
│   ├── 📁 01-fundamentos/          # Conceptos BD
│   │   ├── BASES-DATOS-RELACIONALES.md
│   │   ├── NORMALIZACION.md
│   │   └── TIPOS-RELACIONES.md
│   │
│   ├── 📁 02-postgresql/           # PostgreSQL básico
│   │   ├── POSTGRESQL-SETUP.md
│   │   ├── CREAR-TABLAS.md
│   │   ├── QUERIES-BASICAS.md
│   │   ├── JOINS.md
│   │   └── CONSTRAINTS.md
│   │
│   └── 📁 03-avanzado/             # Optimización
│       ├── INDICES-PERFORMANCE.md
│       ├── TRIGGERS-FUNCIONES.md
│       ├── VISTAS.md
│       ├── MIGRATIONS.md
│       ├── BACKUP-RESTORE.md
│       └── SEGURIDAD-BD.md
│
└── 📂 05-testing/                  # Testing & Comandos
    ├── README.md                   # Índice testing
    ├── TESTING-OVERVIEW.md         # ⭐ Estrategia de testing
    └── COMANDOS-PROYECTO.md        # ⭐ Todos los comandos
```

---

## 🎯 Cómo Usar Esta Estructura

### 1️⃣ Principiantes
**Ruta:** `01-introduccion` → `02-backend` → `03-frontend` → `04-base-de-datos` → `05-testing`

**Comienza aquí:** `01-introduccion/00-ROADMAP.md`

### 2️⃣ Con Experiencia
**Acceso directo a carpetas específicas:**
- Backend: `02-backend/03-arquitectura/`
- Frontend: `03-frontend/03-avanzado/`
- Testing: `05-testing/`

### 3️⃣ Evaluación SENA
**Archivos clave:**
- Conceptos básicos: `01-introduccion/` (todos)
- Arquitectura: `02-backend/03-arquitectura/`
- React: `03-frontend/02-react/`
- Testing: `05-testing/TESTING-OVERVIEW.md`

---

## 📊 Resumen

| Carpeta | Subcarpetas | Archivos | Tiempo |
|---------|-------------|----------|--------|
| 01-introduccion | - | 5 | 8-10h |
| 02-backend | 5 | 35+ | 25-30h |
| 03-frontend | 3 | 20+ | 19-22h |
| 04-base-de-datos | 3 | 15+ | 10-12h |
| 05-testing | - | 3 | 4-5h |
| **TOTAL** | **11** | **78+** | **66-79h** |

---

## 🌟 Archivos Destacados

### Must Read (Imprescindibles)
- 📍 `01-introduccion/00-ROADMAP.md` - Tu guía completa
- 📍 `05-testing/COMANDOS-PROYECTO.md` - Comandos esenciales

### Backend
- ⭐ `02-backend/03-arquitectura/ARQUITECTURA-MVC.md`
- ⭐ `02-backend/04-seguridad/JWT-SPRING-SECURITY.md`

### Frontend
- ⭐ `03-frontend/03-avanzado/CONTEXT-API-STATE-MANAGEMENT.md`
- ⭐ `03-frontend/03-avanzado/PERFORMANCE-OPTIMIZACION.md`
- ⭐ `03-frontend/03-avanzado/SECURITY.md`

### Testing
- ⭐ `05-testing/TESTING-OVERVIEW.md`

---

## 🚀 Quick Navigation

```bash
# Ver estructura
cd docs
ls -la

# Leer roadmap
cat 01-introduccion/00-ROADMAP.md

# Comandos del proyecto
cat 05-testing/COMANDOS-PROYECTO.md

# Backend MVC
cat 02-backend/03-arquitectura/ARQUITECTURA-MVC.md

# Frontend Context API
cat 03-frontend/03-avanzado/CONTEXT-API-STATE-MANAGEMENT.md
```

---

## 💡 Ventajas de Esta Estructura

✅ **Numeración clara** - Orden natural de lectura (01, 02, 03...)
✅ **Nombres descriptivos** - Sabes qué hay en cada carpeta
✅ **Subcarpetas organizadas** - Fundamentos → Avanzado
✅ **READMEs en cada nivel** - Guías de navegación
✅ **Roadmap central** - Planificación de estudio
✅ **Fácil búsqueda** - Estructura lógica

---

**¡Estructura profesional y fácil de navegar!** 📚✨
