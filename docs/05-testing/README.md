# 📂 Testing - Índice

## Estructura

```
05-testing/
├── TESTING-OVERVIEW.md          # Estrategia general de testing
└── COMANDOS-PROYECTO.md         # Comandos para ejecutar todo
```

## 📖 Contenido

### TESTING-OVERVIEW.md
- **Pirámide de Testing** - Unit (70%), Integration (20%), E2E (10%)
- **Herramientas** - JUnit, Vitest, Playwright, MSW, Mockito
- **Coverage** - Objetivos y reportes
- **Best Practices** - AAA pattern, naming conventions
- **Qué testear y qué no**

### COMANDOS-PROYECTO.md
Todos los comandos necesarios para:
- 🔧 Instalación inicial
- 🗄️ Base de datos (PostgreSQL, Docker)
- 💻 Desarrollo (Backend, Frontend)
- 🧪 Testing (Unit, Integration, E2E, Coverage)
- 🏗️ Build para producción
- 🐳 Docker y Docker Compose
- 🔍 Debugging
- 📊 Monitoring y logs
- 🚀 Despliegue (Railway, Heroku)

---

## 🚀 Quick Start

```bash
# Backend
cd backend
./mvnw spring-boot:run

# Frontend
cd frontend
npm run dev

# Tests
./mvnw test              # Backend
npm test                 # Frontend
```

---

**Tiempo estimado:** 4-5 horas para completar

**Siguiente:** Lee `TESTING-OVERVIEW.md` para entender la estrategia de testing
