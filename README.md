# 🍼 Baby Cash - E-commerce de Productos para Bebés

> **Estado**: ✅ Proyecto funcional y listo para despliegue
> **Última actualización**: 8 de Noviembre de 2025

## 📋 Resumen del Proyecto

E-commerce de productos para bebés desarrollado con Spring Boot y React.

---

## 🚀 Tecnologías

### Backend

- **Java 17**
- **Spring Boot 3.2**
- **PostgreSQL 14**
- **Maven**
- **JWT Authentication**

### Frontend

- **React 18.3**
- **TypeScript 5.3**
- **Vite 5.0**
- **Tailwind CSS**
- **React Router**

---

## 📋 Prerrequisitos

```bash
java --version      # Java 17+
node --version      # Node 18+
psql --version      # PostgreSQL 14+
```

---

## ⚡ Instalación

### 1. Clonar repositorio

```bash
git clone https://github.com/tu-usuario/babycash.git
cd babycash
```

### 2. Base de datos

```bash
# Crear base de datos
psql -U postgres
CREATE DATABASE babycash;
\q

# O con Docker
docker run --name babycash-db \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=babycash \
  -p 5432:5432 \
  -d postgres:14
```

### 3. Backend

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

**Backend estará en:** http://localhost:8080

### 4. Frontend

```bash
cd frontend
npm install
npm run dev
```

**Frontend estará en:** http://localhost:5173

---

## 🧪 Testing

```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm test

# Test coverage
./mvnw test jacoco:report    # Backend
npm run test:coverage         # Frontend
```

---

## 🏗️ Build Producción

```bash
# Backend
cd backend
./mvnw clean package
java -jar target/baby-cash-backend-0.0.1-SNAPSHOT.jar

# Frontend
cd frontend
npm run build
npm run preview
```

---

## 📂 Estructura del Proyecto

```
babycash/
├── backend/                 # Spring Boot API
│   ├── src/main/java/
│   ├── src/main/resources/
│   └── pom.xml
├── frontend/                # React App
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
└── docs/                    # Documentación
    ├── 01-introduccion/
    ├── 02-backend/
    ├── 03-frontend/
    ├── 04-base-de-datos/
    └── 05-testing/
```

---

## 📚 Documentación

### 📖 Guías de Aprendizaje (NUEVO ✨)

Documentación exhaustiva para aprender desde cero:

- **[FUNDAMENTOS-PROGRAMACION.md](FUNDAMENTOS-PROGRAMACION.md)** (~1000 líneas)

  - Variables, tipos de datos, operadores
  - Estructuras de control (if, loops, switch)
  - Funciones, métodos, recursión
  - POO completa (encapsulación, herencia, polimorfismo, abstracción)
  - Manejo de errores y excepciones
  - Algoritmos básicos

- **[FUNDAMENTOS-JAVA-SPRING.md](FUNDAMENTOS-JAVA-SPRING.md)** (~1200 líneas)

  - Estructura y sintaxis de Java
  - Java avanzado (interfaces, generics, lambdas, streams, Optional)
  - Maven (pom.xml, ciclo de vida, dependencias)
  - Spring Framework (IoC, DI, estereotipos)
  - Spring Boot (arquitectura, CRUD completo)
  - Testing (JUnit 5, Mockito, integration tests)
  - Clean Code y mejores prácticas
  - SOLID (todos los principios con ejemplos)
  - Patrones de diseño (Singleton, Factory, Strategy, Observer)

- **[FUNDAMENTOS-WEB-FRONTEND.md](FUNDAMENTOS-WEB-FRONTEND.md)** (~1000 líneas)

  - HTML (estructura, semántica, formularios)
  - CSS (selectores, Flexbox, Grid, responsive)
  - JavaScript ES6+ (async/await, fetch, DOM)
  - TypeScript (tipos, interfaces, generics)
  - React (componentes, hooks, state, lifecycle)
  - HTTP y REST APIs
  - Herramientas (NPM, Vite, Git)

- **[CONCEPTOS-TECNICOS-FUNDAMENTALES.md](CONCEPTOS-TECNICOS-FUNDAMENTALES.md)**
  - Overview de todas las tecnologías del proyecto
  - Java, Spring Boot, React, TypeScript, PostgreSQL

### 🔧 Guías Técnicas

- **[INTEGRACION-FRONTEND-BACKEND.md](INTEGRACION-FRONTEND-BACKEND.md)** (NUEVO ✨)

  - Cómo integrar el frontend React en el backend Spring Boot
  - Arquitectura MVC híbrida
  - Script de integración automática (`integrate-frontend.sh`)
  - Troubleshooting completo

- **[INSTRUCCIONES-RAPIDAS.md](INSTRUCCIONES-RAPIDAS.md)** (NUEVO ✨)

  - Guía rápida de comandos
  - Problemas resueltos y verificación
  - FAQ y troubleshooting
  - Checklist diario

- **[GIT-HOOKS-SETUP.md](GIT-HOOKS-SETUP.md)**

  - Configuración de Husky y lint-staged
  - Pre-commit hooks automáticos
  - Linters y formatters

- **[LINTERS-FORMATTERS.md](LINTERS-FORMATTERS.md)**
  - ESLint, Prettier, Checkstyle
  - Reglas configuradas
  - Comandos de verificación

### 📂 Documentación del Proyecto

La documentación completa está en la carpeta `docs/`:

- **[Roadmap](docs/01-introduccion/00-ROADMAP.md)** - Guía de lectura
- **[Backend](docs/02-backend/)** - Spring Boot, API REST
- **[Frontend](docs/03-frontend/)** - React, TypeScript
- **[Base de Datos](docs/04-base-de-datos/)** - PostgreSQL
- **[Testing](docs/05-testing/)** - Tests y comandos

### 🎯 Quick Links

- [HTTP y REST Básico](docs/01-introduccion/HTTP-REST-BASICS.md)
- [React Básico](docs/01-introduccion/REACT-BASICS.md)
- [SQL Básico](docs/01-introduccion/SQL-BASICS.md)
- [Comandos del Proyecto](docs/05-testing/COMANDOS-PROYECTO.md)

---

## 🔑 Usuarios de Prueba

```
Admin:
  Email: admin@babycash.com
  Password: admin123

Usuario:
  Email: user@babycash.com
  Password: user123
```

---

## 🐳 Docker (Opcional)

```bash
# Levantar todo con Docker Compose
docker-compose up

# Detener
docker-compose down
```

---

## 📝 Variables de Entorno

### Backend (`application.properties`)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash
spring.datasource.username=postgres
spring.datasource.password=postgres
jwt.secret=tu-secret-key
```

### Frontend (`.env`)

```env
VITE_API_URL=http://localhost:8080/api
```

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crea tu rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Add: nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

---

## �️ Code Quality & Git Hooks

Este proyecto tiene configurado un sistema de **linters y formatters** que actúan como guardianes del código limpio.

### Formato Automático

- **Al guardar** (Ctrl+S): ESLint y Prettier formatean automáticamente
- **VS Code**: Configuración en `.vscode/settings.json`

### Pre-commit Hooks

- **Antes de cada commit**: Husky ejecuta validación automática
- **Código con errores**: El commit es bloqueado automáticamente
- **Código limpio**: El commit se permite

### Herramientas

| Tool            | Propósito                   | Estado    |
| --------------- | --------------------------- | --------- |
| **ESLint**      | Linting de TypeScript/React | ✅ Activo |
| **Prettier**    | Formateo de código          | ✅ Activo |
| **Checkstyle**  | Linting de Java             | ✅ Activo |
| **Husky**       | Git hooks manager           | ✅ Activo |
| **lint-staged** | Validación de staged files  | ✅ Activo |

### Comandos de Verificación

```bash
# Frontend - Linting
cd frontend
npm run lint          # Ver errores
npm run lint:fix      # Auto-corregir
npm run format        # Formatear todo

# Backend - Checkstyle
cd backend
./mvnw checkstyle:check

# Verificar versiones instaladas
./check-versions.sh

# Verificar código completo (frontend + backend)
./check-code.sh
```

### Documentación de Linters

- **[LINTERS-FORMATTERS.md](LINTERS-FORMATTERS.md)** - Guía completa
- **[REGLAS-CRITICAS.md](REGLAS-CRITICAS.md)** - Reglas configuradas
- **[GIT-HOOKS-AUTOMATIZACION.md](GIT-HOOKS-AUTOMATIZACION.md)** - Configuración de hooks
- **[RESULTADO-PRUEBAS-HOOKS.md](RESULTADO-PRUEBAS-HOOKS.md)** - Pruebas realizadas

### Ejemplo de Uso

```bash
# 1. Escribir código con errores
echo "const x: any = 1;" > frontend/src/test.tsx

# 2. Intentar commit
git add frontend/src/test.tsx
git commit -m "test"

# Resultado:
# ❌ Commit bloqueado: Errores de linting encontrados
# Error: Unexpected any. Specify a different type

# 3. Corregir error
echo "const x: number = 1;" > frontend/src/test.tsx

# 4. Reintentar commit
git add frontend/src/test.tsx
git commit -m "test"

# Resultado:
# ✅ Código verificado exitosamente
# [master abc1234] test
```

---

## �📄 Licencia

Este proyecto fue desarrollado como parte del programa de formación del SENA.

---

## 📞 Contacto

- **Proyecto:** Baby Cash
- **Documentación:** Ver carpeta `docs/`
- **Issues:** GitHub Issues

---

## ⭐ Features

- ✅ Autenticación JWT
- ✅ Gestión de productos
- ✅ Carrito de compras
- ✅ Proceso de checkout
- ✅ Panel de administración
- ✅ Roles y permisos
- ✅ Responsive design
- ✅ Testing completo (Unit, Integration, E2E)

---
