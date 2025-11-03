# 🐘 ¿QUÉ ES POSTGRESQL?

## 🎯 Definición Simple

PostgreSQL es un **sistema de gestión de bases de datos relacional** (RDBMS). Es como un **Excel súper poderoso** que guarda y organiza información en tablas conectadas.

## 🔧 Definición Técnica

PostgreSQL es un RDBMS open-source, ACID-compliant, con soporte avanzado para tipos de datos, índices, transacciones y concurrencia.

---

## 📊 Base de Datos Relacional

### ¿Qué es?

Los datos se organizan en **tablas** (como hojas de Excel) que se **relacionan** entre sí.

### Ejemplo

```
Tabla: users
+----+-----------------+----------+
| id | email           | name     |
+----+-----------------+----------+
| 1  | maria@gmail.com | María    |
| 2  | juan@gmail.com  | Juan     |
+----+-----------------+----------+

Tabla: orders (relacionada con users)
+----+---------+--------+
| id | user_id | total  |
+----+---------+--------+
| 1  | 1       | 150000 |
| 2  | 1       | 80000  |
| 3  | 2       | 200000 |
+----+---------+--------+
         │
         └─ Referencia a users.id
```

---

## 🆚 SQL vs NoSQL

### SQL (Relacional)

**Ejemplos:** PostgreSQL, MySQL, SQL Server, Oracle

**Características:**
- ✅ Tablas con filas y columnas
- ✅ Relaciones entre tablas (Foreign Keys)
- ✅ Esquema fijo (debes definir columnas)
- ✅ ACID (transacciones seguras)
- ✅ Consultas con SQL

**Ejemplo:**
```sql
SELECT users.name, orders.total
FROM users
JOIN orders ON users.id = orders.user_id
WHERE users.id = 1;
```

### NoSQL (No Relacional)

**Ejemplos:** MongoDB, Redis, Cassandra, DynamoDB

**Características:**
- ✅ Documentos JSON, key-value, grafos
- ✅ Sin relaciones estrictas
- ✅ Esquema flexible (sin estructura fija)
- ✅ Escalabilidad horizontal
- ✅ Rápido para lectura/escritura masiva

**Ejemplo (MongoDB):**
```javascript
{
  "_id": "123",
  "name": "María",
  "email": "maria@gmail.com",
  "orders": [
    { "total": 150000, "date": "2025-10-20" },
    { "total": 80000, "date": "2025-10-25" }
  ]
}
```

### Comparación

| Característica | SQL (PostgreSQL) | NoSQL (MongoDB) |
|----------------|------------------|-----------------|
| **Estructura** | Tablas | Documentos JSON |
| **Esquema** | Fijo | Flexible |
| **Relaciones** | Sí (Foreign Keys) | No (embebido o referencia manual) |
| **Transacciones** | ACID completo | Eventual consistency |
| **Escalabilidad** | Vertical | Horizontal |
| **Consultas** | SQL | Queries específicos del motor |
| **Uso ideal** | E-commerce, finanzas, CRMs | Logs, redes sociales, analytics |

---

## ✅ ¿Por Qué PostgreSQL?

### 1. ACID Compliant

**¿Qué es ACID?**
- **Atomicity** (Atomicidad): Todo o nada
- **Consistency** (Consistencia): Datos siempre válidos
- **Isolation** (Aislamiento): Transacciones no se afectan
- **Durability** (Durabilidad): Los datos no se pierden

**Ejemplo:**
```sql
BEGIN TRANSACTION;
  -- 1. Reducir stock
  UPDATE products SET stock = stock - 2 WHERE id = 5;
  
  -- 2. Crear orden
  INSERT INTO orders (user_id, total) VALUES (1, 90000);
  
  -- Si algo falla aquí, TODO se revierte
COMMIT;
```

Si el paso 2 falla, el paso 1 se **revierte automáticamente**. El stock NO se reduce.

**¿Por qué es importante para e-commerce?**
- ✅ Garantiza que no se venda sin stock
- ✅ Garantiza que los pagos se registren correctamente
- ✅ Previene inconsistencias

### 2. Gratuito y Open Source

```
PostgreSQL: $0
MySQL: $0
SQL Server: $3,717 USD/año (Standard)
Oracle: $47,500 USD/año (Enterprise)
```

### 3. Features Avanzados

```sql
-- JSON nativo
SELECT info->>'name' FROM products WHERE info->>'category' = 'baby';

-- Arrays
SELECT * FROM users WHERE tags @> ARRAY['premium'];

-- Full-text search
SELECT * FROM products WHERE to_tsvector(name) @@ to_tsquery('pañales');

-- Window functions
SELECT name, price, AVG(price) OVER (PARTITION BY category) FROM products;
```

### 4. Performance

```
Benchmark (1 millón de registros):
PostgreSQL:  0.8s
MySQL:       1.2s
MongoDB:     0.5s (pero sin garantías ACID)
```

PostgreSQL es **más rápido** que MySQL en queries complejos.

### 5. Extensiones

```sql
-- PostGIS: Geolocalización
SELECT * FROM stores WHERE location <-> point(lat, lon) < 5000;

-- pg_trgm: Búsqueda fuzzy
SELECT * FROM products WHERE name % 'pañal';  -- Encuentra "pañales", "panal"
```

### 6. Confiabilidad

**Usado por:**
- Instagram (maneja millones de usuarios)
- Spotify (recomendaciones musicales)
- Reddit (posts y comentarios)
- Apple (servicios internos)
- Uber (geolocalización)

---

## 🔄 PostgreSQL en BabyCash

### Tablas Principales

```
users            → Usuarios registrados
products         → Catálogo de productos
carts            → Carritos de compras
cart_items       → Items en carritos
orders           → Órdenes de compra
order_items      → Productos en órdenes
payments         → Pagos procesados
blog_posts       → Artículos del blog
testimonials     → Reseñas de clientes
loyalty_points   → Puntos de lealtad
refresh_tokens   → Tokens JWT
```

### ¿Por Qué PostgreSQL y NO MongoDB?

**E-commerce necesita:**
1. ✅ **Transacciones ACID**: Orden + Pago + Reducir Stock = TODO o NADA
2. ✅ **Relaciones estrictas**: User → Orders → OrderItems → Products
3. ✅ **Consistencia**: Stock SIEMPRE correcto
4. ✅ **Integridad referencial**: No puede haber orden sin usuario

MongoDB sería bueno para:
- ❌ Blogs (sin transacciones críticas)
- ❌ Logs (escritura masiva, sin relaciones)
- ❌ Analytics (esquema flexible)

Pero para **e-commerce**, PostgreSQL es **superior**.

---

## 🛠️ Instalación

### Linux (Arch)
```bash
sudo pacman -S postgresql
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### Ubuntu/Debian
```bash
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### macOS
```bash
brew install postgresql
brew services start postgresql
```

### Windows
Descargar instalador desde: https://www.postgresql.org/download/windows/

---

## 🔧 Configuración Inicial

### Crear Base de Datos

```bash
# Conectar como usuario postgres
sudo -u postgres psql

# Crear base de datos
CREATE DATABASE babycash;

# Crear usuario
CREATE USER babycash_user WITH PASSWORD 'tu_password_seguro';

# Dar permisos
GRANT ALL PRIVILEGES ON DATABASE babycash TO babycash_user;

# Salir
\q
```

### Conectar desde Spring Boot

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/babycash
spring.datasource.username=babycash_user
spring.datasource.password=tu_password_seguro
```

---

## 📊 Herramientas

### Cliente CLI: psql

```bash
# Conectar
psql -U babycash_user -d babycash

# Listar tablas
\dt

# Describir tabla
\d users

# Ver datos
SELECT * FROM users;
```

### GUI (Interfaz Gráfica)

**pgAdmin 4** (Oficial)
- Gratis
- Potente
- Complejo para principiantes

**DBeaver** (Recomendado)
- Gratis
- Fácil de usar
- Soporta múltiples BD

**DataGrip** (JetBrains)
- Pago ($89/año estudiantes gratis)
- Mejor autocompletado
- Integración con IntelliJ IDEA

---

## 🔐 Seguridad

### Buenas Prácticas

```sql
-- ❌ MAL - Usuario con todos los permisos
GRANT ALL ON DATABASE babycash TO app_user;

-- ✅ BIEN - Solo permisos necesarios
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
```

### Encriptación

```sql
-- Encriptar datos sensibles
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (email, password) 
VALUES ('user@example.com', crypt('password123', gen_salt('bf')));

-- Verificar password
SELECT * FROM users 
WHERE email = 'user@example.com' 
AND password = crypt('password123', password);
```

(Pero en Spring Boot usamos BCrypt en Java, no en SQL)

---

## 📈 Ventajas vs Desventajas

### Ventajas

1. ✅ **ACID completo**: Transacciones seguras
2. ✅ **Gratuito**: $0 de licencia
3. ✅ **Potente**: JSON, arrays, full-text search
4. ✅ **Estable**: No pierde datos
5. ✅ **Comunidad**: Documentación extensa
6. ✅ **Standards**: SQL estándar (portabilidad)

### Desventajas

1. ❌ **Escalabilidad horizontal**: Difícil (vs MongoDB)
2. ❌ **Setup**: Más complejo que SQLite
3. ❌ **Memoria**: Usa más RAM que MySQL
4. ❌ **Curva de aprendizaje**: SQL es complejo al inicio

---

## 🎯 Casos de Uso

### ✅ Usa PostgreSQL para:

- E-commerce (BabyCash)
- Sistemas financieros
- CRMs
- ERPs
- Aplicaciones con transacciones críticas
- Reportes complejos

### ❌ NO uses PostgreSQL para:

- Apps de lectura masiva (mejor Redis)
- Logs (mejor Elasticsearch)
- Datos sin estructura (mejor MongoDB)
- Prototipado rápido (mejor SQLite)

---

## 📋 Resumen

| Característica | Valor |
|----------------|-------|
| **Tipo** | Base de datos relacional |
| **Licencia** | Open source (PostgreSQL License) |
| **Versión usada** | 14+ |
| **Puerto** | 5432 |
| **Lenguaje** | SQL |
| **Transacciones** | ACID completo |
| **Casos de uso** | E-commerce, finanzas, CRMs |

---

**Última actualización**: Octubre 2025
