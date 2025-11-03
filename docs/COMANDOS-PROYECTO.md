# COMANDOS DEL PROYECTO - BABY CASH

## 🚀 Guía Completa de Comandos

Esta guía contiene todos los comandos necesarios para ejecutar, desarrollar, testear y desplegar Baby Cash.

---

## 📦 Instalación Inicial

### Prerrequisitos

```bash
# Verificar versiones instaladas
java --version          # Java 17+
node --version          # Node 18+
npm --version           # npm 9+
psql --version          # PostgreSQL 14+
docker --version        # Docker (opcional)
```

### Backend Setup

```bash
# Navegar a directorio backend
cd backend

# Instalar dependencias (Maven descarga automáticamente)
./mvnw clean install

# O en Windows
mvnw.cmd clean install
```

### Frontend Setup

```bash
# Navegar a directorio frontend
cd frontend

# Instalar dependencias
npm install

# O con pnpm (más rápido)
pnpm install
```

---

## 🗄️ Base de Datos

### PostgreSQL Local

```bash
# Iniciar PostgreSQL (Linux/Mac)
sudo systemctl start postgresql
# o
brew services start postgresql

# Iniciar PostgreSQL (Windows)
# Iniciar desde Servicios de Windows

# Conectar a PostgreSQL
psql -U postgres

# Crear base de datos
CREATE DATABASE babycash;

# Conectar a la base de datos
\c babycash

# Listar tablas
\dt

# Ver estructura de una tabla
\d products

# Salir
\q
```

### PostgreSQL con Docker

```bash
# Iniciar contenedor PostgreSQL
docker run --name babycash-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=babycash \
  -p 5432:5432 \
  -d postgres:14

# Ver logs
docker logs babycash-db

# Detener contenedor
docker stop babycash-db

# Iniciar contenedor existente
docker start babycash-db

# Eliminar contenedor
docker rm babycash-db
```

### Migrations (Flyway/Liquibase)

```bash
# Ejecutar migrations manualmente
./mvnw flyway:migrate

# Limpiar base de datos
./mvnw flyway:clean

# Ver estado de migrations
./mvnw flyway:info
```

---

## 🔧 Desarrollo

### Backend (Spring Boot)

```bash
# Ejecutar en modo desarrollo
./mvnw spring-boot:run

# Con perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Con hot reload (DevTools)
./mvnw spring-boot:run -Dspring-boot.devtools.restart.enabled=true

# Puerto personalizado
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=9090

# Ver logs en tiempo real
tail -f logs/spring.log
```

**Backend estará en:** http://localhost:8080

### Frontend (Vite + React)

```bash
# Ejecutar en modo desarrollo
npm run dev

# Puerto personalizado
npm run dev -- --port 3000

# Abrir automáticamente en navegador
npm run dev -- --open

# Host personalizado (acceso desde red local)
npm run dev -- --host
```

**Frontend estará en:** http://localhost:5173

### Desarrollo Completo (Backend + Frontend)

```bash
# Terminal 1: Backend
cd backend && ./mvnw spring-boot:run

# Terminal 2: Frontend
cd frontend && npm run dev

# O usar Docker Compose (ver sección Docker)
docker-compose up
```

---

## 🧪 Testing

### Backend Tests

```bash
# Ejecutar todos los tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=ProductServiceTest
./mvnw test -Dtest=ProductControllerTest

# Tests con coverage
./mvnw test jacoco:report

# Ver reporte de coverage
open target/site/jacoco/index.html

# Tests de integración
./mvnw verify

# Skip tests (no recomendado)
./mvnw install -DskipTests
```

### Frontend Tests

```bash
# Ejecutar todos los tests
npm test

# Watch mode (re-ejecutar al cambiar código)
npm test -- --watch

# Tests específicos
npm test -- ProductCard

# Coverage
npm run test:coverage

# Ver reporte HTML
open coverage/index.html

# E2E tests (Playwright)
npm run test:e2e

# E2E en modo UI
npm run test:e2e -- --ui

# E2E solo Chrome
npm run test:e2e -- --project=chromium
```

---

## 🏗️ Build (Producción)

### Backend Build

```bash
# Compilar proyecto
./mvnw clean package

# Skip tests (más rápido, no recomendado)
./mvnw clean package -DskipTests

# JAR generado en:
ls -lh target/*.jar
# baby-cash-backend-0.0.1-SNAPSHOT.jar

# Ejecutar JAR
java -jar target/baby-cash-backend-0.0.1-SNAPSHOT.jar

# Con perfil de producción
java -jar target/baby-cash-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Frontend Build

```bash
# Build para producción
npm run build

# Archivos generados en:
ls -lh dist/

# Preview build localmente
npm run preview

# Build con análisis de bundle
npm run build -- --mode=analyze
```

---

## 🐳 Docker

### Docker Compose (Recomendado)

```bash
# Iniciar todos los servicios
docker-compose up

# En background (detached)
docker-compose up -d

# Ver logs
docker-compose logs -f

# Ver logs de un servicio
docker-compose logs -f backend
docker-compose logs -f frontend

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Rebuild imágenes
docker-compose up --build
```

### Docker Individual

```bash
# Backend
docker build -t babycash-backend ./backend
docker run -p 8080:8080 babycash-backend

# Frontend
docker build -t babycash-frontend ./frontend
docker run -p 5173:80 babycash-frontend
```

---

## 🔍 Debugging

### Backend Debug

```bash
# Ejecutar con debug habilitado
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Conectar debugger en IntelliJ/Eclipse al puerto 5005
```

### Frontend Debug

```bash
# Chrome DevTools
npm run dev
# Abrir http://localhost:5173
# F12 para abrir DevTools

# React DevTools
# Instalar extensión: React Developer Tools
```

---

## 📊 Monitoring y Logs

### Backend Logs

```bash
# Ver logs en tiempo real
tail -f logs/spring.log

# Buscar errores
grep ERROR logs/spring.log

# Ver últimas 100 líneas
tail -n 100 logs/spring.log

# Ver logs por fecha
grep "2024-01-15" logs/spring.log
```

### Spring Boot Actuator

```bash
# Health check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Info
curl http://localhost:8080/actuator/info

# Todas las metricas disponibles
curl http://localhost:8080/actuator
```

---

## 🧹 Limpieza

### Backend

```bash
# Limpiar target/
./mvnw clean

# Limpiar cache de Maven
rm -rf ~/.m2/repository

# Limpiar dependencias y redownload
./mvnw dependency:purge-local-repository
```

### Frontend

```bash
# Limpiar node_modules
rm -rf node_modules

# Limpiar cache de npm
npm cache clean --force

# Limpiar dist/
rm -rf dist

# Reinstalar dependencias
npm install
```

---

## 🔄 Git

### Commits

```bash
# Ver cambios
git status

# Agregar archivos
git add .

# Commit
git commit -m "feat: agregar filtro de productos"

# Push
git push origin main
```

### Branches

```bash
# Crear branch
git checkout -b feature/nuevo-filtro

# Cambiar branch
git checkout main

# Ver branches
git branch

# Merge
git checkout main
git merge feature/nuevo-filtro
```

---

## 🚀 Despliegue

### Railway (Recomendado)

```bash
# Instalar CLI
npm install -g @railway/cli

# Login
railway login

# Inicializar proyecto
railway init

# Deploy
railway up

# Ver logs
railway logs

# Open en navegador
railway open
```

### Heroku

```bash
# Login
heroku login

# Crear app
heroku create babycash-app

# Deploy
git push heroku main

# Ver logs
heroku logs --tail

# Abrir app
heroku open
```

---

## 📋 Cheat Sheet

### Backend (Spring Boot)

| Comando | Descripción |
|---------|-------------|
| `./mvnw spring-boot:run` | Ejecutar aplicación |
| `./mvnw test` | Ejecutar tests |
| `./mvnw clean package` | Build JAR |
| `java -jar target/*.jar` | Ejecutar JAR |

### Frontend (React + Vite)

| Comando | Descripción |
|---------|-------------|
| `npm run dev` | Desarrollo |
| `npm test` | Tests |
| `npm run build` | Build producción |
| `npm run preview` | Preview build |

### Base de Datos

| Comando | Descripción |
|---------|-------------|
| `psql -U postgres` | Conectar PostgreSQL |
| `CREATE DATABASE babycash;` | Crear DB |
| `\dt` | Listar tablas |
| `\q` | Salir |

### Docker

| Comando | Descripción |
|---------|-------------|
| `docker-compose up` | Iniciar servicios |
| `docker-compose down` | Detener servicios |
| `docker-compose logs -f` | Ver logs |
| `docker ps` | Ver contenedores |

---

## 🎓 Para la Evaluación del SENA

**1. "¿Cómo ejecutar el proyecto?"**

> **Backend:** `./mvnw spring-boot:run` (http://localhost:8080)
> 
> **Frontend:** `npm run dev` (http://localhost:5173)
> 
> **Database:** `docker-compose up` o PostgreSQL local

**2. "¿Cómo ejecutar tests?"**

> **Backend:** `./mvnw test`
> 
> **Frontend:** `npm test`
> 
> **Coverage:** `./mvnw test jacoco:report` y `npm run test:coverage`

**3. "¿Cómo hacer build de producción?"**

> **Backend:** `./mvnw clean package` → JAR en `target/`
> 
> **Frontend:** `npm run build` → archivos en `dist/`
> 
> **Ejecutar:** `java -jar target/*.jar`

---

## 🔗 URLs Importantes

- **Frontend Dev:** http://localhost:5173
- **Backend API:** http://localhost:8080/api
- **API Docs (Swagger):** http://localhost:8080/swagger-ui.html
- **Actuator:** http://localhost:8080/actuator
- **PostgreSQL:** localhost:5432

---

**¡Listo para desarrollar en Baby Cash!** 🚀
