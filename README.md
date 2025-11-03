# 🍼 Baby Cash

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

## 📄 Licencia

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


