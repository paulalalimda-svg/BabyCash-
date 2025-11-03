# 📂 Base de Datos - Índice

## Estructura

```
04-base-de-datos/
├── 01-fundamentos/          # Conceptos de BD relacionales
├── 02-postgresql/           # PostgreSQL, tablas, queries
└── 03-avanzado/             # Índices, triggers, backup
```

## 📖 Orden de Lectura Recomendado

### Nivel Principiante
Primero lee: `../../01-introduccion/SQL-BASICS.md`

Luego:
1. **Fundamentos** → Conceptos de bases de datos relacionales
2. **PostgreSQL** → Instalación, tablas, relaciones, queries
3. **Avanzado** → Optimización, triggers, migrations

### Nivel Intermedio
Puedes empezar directamente en **PostgreSQL** si ya conoces SQL.

---

## 📚 Contenido por Carpeta

### 01-fundamentos/
- ¿Qué es una base de datos relacional?
- Tablas, filas, columnas
- Primary Keys y Foreign Keys
- Normalización (1NF, 2NF, 3NF)
- Tipos de relaciones (1-N, N-M)

### 02-postgresql/
- Instalación y configuración PostgreSQL
- Crear base de datos y tablas
- CRUD operations (SELECT, INSERT, UPDATE, DELETE)
- JOIN entre tablas
- Constraints (UNIQUE, NOT NULL, CHECK)
- Diagrama ER de Baby Cash

### 03-avanzado/
- **Índices** - Performance, tipos de índices, cuándo usar
- **Triggers** - Automatización, auditoría
- **Funciones** - Stored procedures
- **Vistas** - Simplificar queries complejas
- **Migrations** - Flyway/Liquibase
- **Backup & Restore** - pg_dump, pg_restore
- **Seguridad** - Usuarios, roles, permisos

---

**Tiempo estimado:** 10-12 horas para completar

**Siguiente:** Empieza con `02-postgresql/POSTGRESQL-SETUP.md`
