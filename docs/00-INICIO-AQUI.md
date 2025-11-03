# 📚 Baby Cash - Documentación Organizada

## ✨ Reorganización Completa

La documentación de Baby Cash ha sido completamente reorganizada para facilitar el aprendizaje y la navegación.

---

## 🗂️ Estructura Nueva

```
📦 Babycash/
│
├── 📄 README.md                      ← 👈 EMPIEZA AQUÍ (Instalación rápida)
│
├── 📂 docs/                          ← Documentación completa
│   │
│   ├── 📄 README.md                  ← Índice general de docs
│   ├── 📄 ESTRUCTURA.md              ← Visual de la estructura
│   │
│   ├── 📂 01-introduccion/           ← Conceptos básicos
│   │   ├── 00-ROADMAP.md            ← 🗺️ Guía de lectura completa
│   │   ├── HTTP-REST-BASICS.md
│   │   ├── SPRING-BOOT-BASICS.md
│   │   ├── REACT-BASICS.md
│   │   └── SQL-BASICS.md
│   │
│   ├── 📂 02-backend/                ← Spring Boot
│   │   ├── 📄 README.md
│   │   ├── 01-fundamentos/          (Java básico)
│   │   ├── 02-spring-boot/          (Spring inicial)
│   │   ├── 03-arquitectura/         (MVC, capas)
│   │   ├── 04-seguridad/            (JWT, Security)
│   │   └── 05-avanzado/             (Clean Code, SOLID)
│   │
│   ├── 📂 03-frontend/               ← React & TypeScript
│   │   ├── 📄 README.md
│   │   ├── 01-fundamentos/          (Estructura)
│   │   ├── 02-react/                (React core)
│   │   └── 03-avanzado/             (Performance, Security)
│   │
│   ├── 📂 04-base-de-datos/          ← PostgreSQL
│   │   ├── 📄 README.md
│   │   ├── 01-fundamentos/          (Conceptos BD)
│   │   ├── 02-postgresql/           (PostgreSQL)
│   │   └── 03-avanzado/             (Optimización)
│   │
│   └── 📂 05-testing/                ← Testing & Comandos
│       ├── 📄 README.md
│       ├── TESTING-OVERVIEW.md
│       └── COMANDOS-PROYECTO.md     ← ⚡ Todos los comandos
│
├── 📂 backend/                       ← Código Spring Boot
└── 📂 frontend/                      ← Código React
```

---

## 🎯 Cómo Empezar

### 1️⃣ Quiero instalar el proyecto
👉 Lee: [`/README.md`](../README.md)

### 2️⃣ Quiero aprender todo
👉 Lee: [`docs/01-introduccion/00-ROADMAP.md`](01-introduccion/00-ROADMAP.md)

### 3️⃣ Tengo experiencia, quiero ir directo al código
👉 Ve a:
- Backend: [`docs/02-backend/03-arquitectura/`](02-backend/03-arquitectura/)
- Frontend: [`docs/03-frontend/03-avanzado/`](03-frontend/03-avanzado/)

### 4️⃣ Solo necesito los comandos
👉 Lee: [`docs/05-testing/COMANDOS-PROYECTO.md`](05-testing/COMANDOS-PROYECTO.md)

---

## 🚀 Quick Start

```bash
# 1. Clonar
git clone https://github.com/tu-usuario/babycash.git
cd babycash

# 2. Base de datos
docker run -d --name babycash-db \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=babycash \
  -p 5432:5432 postgres:14

# 3. Backend
cd backend
./mvnw spring-boot:run

# 4. Frontend (nueva terminal)
cd frontend
npm install
npm run dev
```

**URLs:**
- Frontend: http://localhost:5173
- Backend: http://localhost:8080/api

---

## 📖 Rutas de Aprendizaje

### 🌱 Principiante (7 semanas, 67 horas)
```
Semana 1: Conceptos básicos (HTTP, SQL, React, Spring Boot)
Semanas 2-3: Backend completo
Semanas 4-5: Frontend completo
Semana 6: Base de datos
Semana 7: Testing
```

### 🚀 Con Experiencia (2-3 días, 15 horas)
```
Día 1: Backend (arquitectura + seguridad)
Día 2: Frontend (React + avanzado)
Día 3: Database + Testing
```

### 🎓 Preparación SENA (1 semana, 20 horas)
```
Lunes: Conceptos básicos
Martes-Miércoles: Backend
Jueves: Frontend
Viernes: Database + Testing
Sábado: Repaso
```

---

## 📊 Contenido

| Módulo | Archivos | Líneas | Tiempo |
|--------|----------|--------|--------|
| Introducción | 5 | 15,000 | 8-10h |
| Backend | 35+ | 87,000 | 25-30h |
| Frontend | 20+ | 78,000 | 19-22h |
| Base de Datos | 15+ | 30,000 | 10-12h |
| Testing | 3 | 6,000 | 4-5h |
| **TOTAL** | **78+** | **~216,000** | **66-79h** |

---

## 🌟 Archivos Destacados

### Must Read
- 📍 [`README.md`](../README.md) - Instalación rápida
- 📍 [`docs/01-introduccion/00-ROADMAP.md`](01-introduccion/00-ROADMAP.md) - Guía completa

### Backend
- ⭐ [`02-backend/03-arquitectura/ARQUITECTURA-MVC.md`](02-backend/03-arquitectura/)
- ⭐ [`02-backend/04-seguridad/JWT-SPRING-SECURITY.md`](02-backend/04-seguridad/)

### Frontend
- ⭐ [`03-frontend/03-avanzado/CONTEXT-API-STATE-MANAGEMENT.md`](03-frontend/03-avanzado/)
- ⭐ [`03-frontend/03-avanzado/PERFORMANCE-OPTIMIZACION.md`](03-frontend/03-avanzado/)
- ⭐ [`03-frontend/03-avanzado/SECURITY.md`](03-frontend/03-avanzado/)

### Testing
- ⭐ [`05-testing/TESTING-OVERVIEW.md`](05-testing/)
- ⭐ [`05-testing/COMANDOS-PROYECTO.md`](05-testing/)

---

## 💡 Ventajas de la Nueva Estructura

✅ **Numeración clara** - 01, 02, 03, 04, 05
✅ **Nombres descriptivos** - Sabes qué hay en cada carpeta
✅ **Orden lógico** - Fundamentos → Avanzado
✅ **READMEs en cada nivel** - Siempre sabes dónde estás
✅ **Roadmap incluido** - Guía de estudio completa
✅ **Fácil navegación** - Estructura intuitiva
✅ **Profesional** - Lista para SENA y producción

---

## 🔍 Búsqueda Rápida

### Por Concepto

**API REST:**
- [`01-introduccion/HTTP-REST-BASICS.md`](01-introduccion/HTTP-REST-BASICS.md)
- [`02-backend/03-arquitectura/`](02-backend/03-arquitectura/)

**Autenticación:**
- [`02-backend/04-seguridad/`](02-backend/04-seguridad/)
- [`03-frontend/03-avanzado/SECURITY.md`](03-frontend/03-avanzado/)

**Estado Global:**
- [`03-frontend/02-react/CONTEXT-API.md`](03-frontend/02-react/)
- [`03-frontend/03-avanzado/CONTEXT-API-STATE-MANAGEMENT.md`](03-frontend/03-avanzado/)

**Performance:**
- [`03-frontend/03-avanzado/PERFORMANCE-OPTIMIZACION.md`](03-frontend/03-avanzado/)
- [`04-base-de-datos/03-avanzado/`](04-base-de-datos/03-avanzado/)

**Testing:**
- [`05-testing/TESTING-OVERVIEW.md`](05-testing/)
- [`03-frontend/03-avanzado/TESTING-STRATEGIES.md`](03-frontend/03-avanzado/)

---

## 🎓 Para Evaluación SENA

### Temas Cubiertos

**Backend:**
- ✅ API REST completa
- ✅ Spring Boot (Controller, Service, Repository)
- ✅ JWT Authentication
- ✅ Validación de datos
- ✅ Testing JUnit

**Frontend:**
- ✅ React + TypeScript
- ✅ Context API
- ✅ React Router
- ✅ Performance
- ✅ Security

**Base de Datos:**
- ✅ PostgreSQL
- ✅ Relaciones
- ✅ Optimización
- ✅ Migrations

**Testing:**
- ✅ Unit Tests
- ✅ Integration Tests
- ✅ E2E Tests
- ✅ Coverage > 80%

---

## 📞 Soporte

**Tienes dudas?**
1. Lee el [Roadmap](01-introduccion/00-ROADMAP.md)
2. Revisa [Conceptos Básicos](01-introduccion/)
3. Consulta [Comandos](05-testing/COMANDOS-PROYECTO.md)
4. Revisa [Estructura](ESTRUCTURA.md)

---

## 🤝 Contribución

Este proyecto fue desarrollado como parte del programa de formación del SENA.

**Documentación creada:** Octubre 2025

---

## ⭐ Características

- 📚 78+ archivos de documentación
- 📝 ~216,000 líneas de contenido
- 🎯 Roadmap completo
- 📊 Guías por nivel
- 🔍 Búsqueda rápida
- ⏱️ Estimaciones de tiempo
- ✅ Lista para evaluación SENA

---

**¡Comienza tu aprendizaje ahora!** 🚀

👉 **Siguiente paso:** Lee el [README principal](../README.md) para instalar el proyecto o el [Roadmap](01-introduccion/00-ROADMAP.md) para empezar a aprender.

---

**Desarrollado con ❤️ para el SENA**
