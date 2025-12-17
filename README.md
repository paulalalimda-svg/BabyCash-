# 🍼 Baby Cash - E-commerce de Productos para Bebés

> **Estado**: ✅ Proyecto funcional y listo para despliegue
> **Última actualización**: 8 de Noviembre de 2025

## 📋 Resumen del Proyecto

E-commerce de productos para bebés desarrollado con Spring Boot y React.

---

Explicación completa de la Base de Datos:
📌 1. ¿Qué es una Base de Datos y por qué usamos PostgreSQL?

Una base de datos es un sistema que permite almacenar, organizar y consultar información de forma estructurada. En una aplicación como BabyCash, la base de datos guarda cosas como:

✔ datos de usuarios
✔ productos
✔ roles y permisos
✔ carritos y compras
✔ etc.

PostgreSQL es el sistema de gestión de base de datos (DBMS) que usamos. Es muy potente, compatible con el estándar SQL, permite transacciones, procedimientos almacenados, vistas y triggers, y mantiene la integridad de los datos de forma robusta. 
ionos.es
+1

🔹 ¿Por qué elegimos PostgreSQL y no MySQL?

Aunque MySQL también es popular, PostgreSQL ofrece ventajas importantes:

✔ mejor cumplimiento del estándar SQL
✔ soporte completo para transacciones ACID (atomicidad, consistencia, aislamiento y durabilidad)
✔ permite triggers y procedimientos complejos
✔ tipos de datos avanzados y funciones poderosas para consultas complejas 
repositorio.upct.es
+1

Esto lo hace ideal para aplicaciones con lógica de negocio compleja, como BabyCash.

📊 2. Estructura de la Base de Datos

En tu proyecto, la base de datos contiene varias tablas que representan las entidades principales de la app.

📌 Ejemplos de tablas típicas:
Tabla	Descripción
users	Guarda la información de usuario (correo, contraseña, rol)
products	Información de productos (nombre, precio, stock)
orders	Pedidos hechos por usuarios
roles	Roles de usuario (cliente, admin)
order_items	Productos dentro de cada pedido
🧱 3. Tipos de Objetos en la Base de Datos
🔹 Tablas

Son las estructuras principales donde se almacenan filas de datos.

Ejemplo de creación de tabla:

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

🔹 Vistas (Views)

Una vista es como una “tabla virtual” derivada de una consulta. No guarda datos por sí misma, sino que muestra información combinada de varias tablas.

Ejemplo:

CREATE VIEW view_products_stock AS
SELECT p.id, p.name, p.stock
FROM products p
WHERE p.stock > 0;

🔹 Procedimientos almacenados / Funciones

Son bloques de código SQL que se almacenan en la base de datos para realizar operaciones que se usan varias veces.

Ejemplo de una función en PostgreSQL:

CREATE OR REPLACE FUNCTION add_stock(product_id INT, amount INT)
RETURNS VOID AS $$
BEGIN
  UPDATE products
  SET stock = stock + amount
  WHERE id = product_id;
END;
$$ LANGUAGE plpgsql;


Las funciones ayudan a:
✔ encapsular lógica compleja
✔ evitar repetir consultas
✔ mejorar rendimiento del sistema 
bbdd.codeandcoke.com

🔹 Triggers (Disparadores)

Los triggers son código SQL que se ejecuta automáticamente cuando ocurre un evento (INSERT, UPDATE, DELETE) en una tabla. 
Wikipedia

Ejemplo:

CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_timestamp
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE PROCEDURE actualizar_fecha_modificacion();


Esto permite, por ejemplo, actualizar automáticamente la fecha de modificación de un registro cuando se edita.

🛠 4. Comandos SQL Esenciales por Categoría
🧱 DDL – Definición de Datos
CREATE DATABASE babycash;
CREATE TABLE ...
ALTER TABLE ...
DROP TABLE ...

📥 DML – Manipulación de Datos
INSERT INTO users ...
UPDATE products SET ...
DELETE FROM orders WHERE ...

📊 DQL – Consultas
SELECT * FROM products;
SELECT name, price FROM products WHERE stock > 0;

🔐 DCL – Control de Datos
GRANT SELECT ON products TO public;
REVOKE UPDATE ON orders FROM guest;

🚀 Otras operaciones útiles
CREATE INDEX idx_products_name ON products(name);

🔍 5. Consultas Útiles para Documentar
✨ Ver todas las tablas
SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname = 'public';

✨ Ver estructura de una tabla
\d users

✨ Ver funciones existentes
SELECT proname FROM pg_proc;

✨ Ver triggers existentes
SELECT tgname FROM pg_trigger WHERE tgrelid = 'users'::regclass;

🧾 6. Cómo se Conecta con el Backend (Spring Boot)

📌 En Spring Boot, la conexión con PostgreSQL está configurada en application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/babycash
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña


Spring Boot usa estas propiedades para abrir la conexión automáticamente cuando la app inicia.

Spring emplea Spring Data JPA o JDBC para consultar y actualizar datos sin tener que escribir código de conexión nativo cada vez.

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
