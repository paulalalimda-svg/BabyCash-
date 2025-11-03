# ÍNDICES Y OPTIMIZACIÓN - BABY CASH

## 🎯 ¿Qué es un Índice?

Un **índice** es una estructura de datos que mejora la velocidad de búsqueda en una tabla.

### Analogía Simple
> **Imagina un libro de 1000 páginas:**
> 
> **Sin índice:**
> - Para encontrar "PostgreSQL", lees página por página
> - Puedes tardar 30 minutos
> 
> **Con índice (índice alfabético al final):**
> - Buscas "PostgreSQL" en índice
> - Dice: "Página 347"
> - Vas directo a página 347
> - Tardas 30 segundos
> 
> En base de datos:
> - **Sin índice**: PostgreSQL lee TODAS las filas (FULL TABLE SCAN)
> - **Con índice**: PostgreSQL va directo a las filas relevantes

---

## 💡 ¿Cuándo Usar Índices?

### ✅ Crear índice cuando:
- Columna se usa frecuentemente en WHERE
- Columna se usa en JOIN
- Columna se usa en ORDER BY
- Tabla es grande (miles de filas)
- Columna tiene muchos valores distintos (alta cardinalidad)

### ❌ NO crear índice cuando:
- Tabla es pequeña (< 1000 filas)
- Columna tiene pocos valores distintos (ej: género: M/F)
- Columna se modifica frecuentemente (índice se reconstruye)
- Pocos valores distintos (baja cardinalidad)

---

## 📊 Tipos de Índices en PostgreSQL

### 1️⃣ B-Tree (Default)
**Mejor para:** Comparaciones (=, <, >, <=, >=, BETWEEN), ordenamiento

```sql
CREATE INDEX idx_products_price ON products(price);
```

### 2️⃣ Hash
**Mejor para:** Solo igualdad (=)

```sql
CREATE INDEX idx_users_email_hash ON users USING HASH(email);
```

### 3️⃣ GIN (Generalized Inverted Index)
**Mejor para:** Arrays, JSONB, búsqueda de texto completo

```sql
CREATE INDEX idx_product_tags ON products USING GIN(tags);
```

### 4️⃣ GiST (Generalized Search Tree)
**Mejor para:** Datos geométricos, búsqueda de texto

---

## 🚀 Índices en Baby Cash

### Índice 1: Búsqueda de Usuarios por Email

**Problema:** Login verifica email (query muy frecuente).

**Query sin índice:**
```sql
EXPLAIN ANALYZE
SELECT * FROM users WHERE email = 'juan@example.com';

-- Resultado:
-- Seq Scan on users  (cost=0.00..1.04 rows=1 width=556) (actual time=0.015..0.016 rows=1 loops=1)
--   Filter: (email = 'juan@example.com'::text)
-- Planning Time: 0.078 ms
-- Execution Time: 0.032 ms
```

**Solución:**
```sql
CREATE UNIQUE INDEX idx_users_email ON users(email);
```

**Query con índice:**
```sql
EXPLAIN ANALYZE
SELECT * FROM users WHERE email = 'juan@example.com';

-- Resultado:
-- Index Scan using idx_users_email on users  (cost=0.15..8.17 rows=1 width=556) (actual time=0.008..0.009 rows=1 loops=1)
--   Index Cond: (email = 'juan@example.com'::text)
-- Planning Time: 0.082 ms
-- Execution Time: 0.023 ms  ← Más rápido!
```

**Beneficios:**
- ✅ Login más rápido
- ✅ UNIQUE previene emails duplicados
- ✅ PostgreSQL usa índice automáticamente

---

### Índice 2: Filtrar Productos por Categoría

**Problema:** Mostrar productos de una categoría es query muy común.

**Análisis:**
```sql
-- Sin índice
EXPLAIN ANALYZE
SELECT * FROM products WHERE category_id = 2;

-- Seq Scan on products (lee todas las filas)
```

**Solución:**
```sql
CREATE INDEX idx_products_category ON products(category_id);
```

**Resultado:**
```sql
-- Con índice
EXPLAIN ANALYZE
SELECT * FROM products WHERE category_id = 2;

-- Index Scan using idx_products_category on products
-- Mucho más rápido en tablas grandes
```

---

### Índice 3: Búsqueda de Productos por Nombre

**Problema:** Barra de búsqueda usa LIKE '%texto%'.

**Índice básico (no ayuda con LIKE '%texto%'):**
```sql
CREATE INDEX idx_products_name ON products(name);
-- No funciona con '%texto%' porque % al inicio impide usar índice
```

**Solución 1: GIN con pg_trgm (trigram similarity)**
```sql
-- Habilitar extensión
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Crear índice
CREATE INDEX idx_products_name_gin ON products USING GIN(name gin_trgm_ops);

-- Ahora LIKE '%texto%' usa índice
SELECT * FROM products WHERE name ILIKE '%robot%';
```

**Solución 2: Índice de texto completo (mejor para búsquedas complejas)**
```sql
-- Agregar columna tsvector
ALTER TABLE products ADD COLUMN name_tsv tsvector;

-- Actualizar columna
UPDATE products SET name_tsv = to_tsvector('spanish', name);

-- Crear índice GIN
CREATE INDEX idx_products_name_fts ON products USING GIN(name_tsv);

-- Trigger para mantener actualizado
CREATE TRIGGER products_name_tsv_update
BEFORE INSERT OR UPDATE ON products
FOR EACH ROW EXECUTE FUNCTION
tsvector_update_trigger(name_tsv, 'pg_catalog.spanish', name);

-- Búsqueda
SELECT * FROM products 
WHERE name_tsv @@ to_tsquery('spanish', 'robot');
```

---

### Índice 4: Órdenes de un Usuario

**Problema:** Historial de órdenes por usuario.

**Solución:**
```sql
CREATE INDEX idx_orders_user ON orders(user_id);

-- Query optimizado
SELECT * FROM orders WHERE user_id = 2 ORDER BY created_at DESC;
```

---

### Índice 5: Filtrar Órdenes por Status y Fecha

**Problema:** Admin filtra órdenes pendientes recientes.

**Índice compuesto:**
```sql
CREATE INDEX idx_orders_status_date ON orders(status, created_at DESC);

-- Query optimizado
SELECT * FROM orders 
WHERE status = 'PENDING' 
ORDER BY created_at DESC;
```

**Orden de columnas importa:**
- Primera columna: más selectiva (status en este caso)
- Segunda columna: ordenamiento

---

### Índice 6: Order Items por Orden

**Problema:** Ver detalles de una orden (order_items).

**Solución:**
```sql
CREATE INDEX idx_order_items_order ON order_items(order_id);

-- Query optimizado
SELECT * FROM order_items WHERE order_id = 10;
```

---

### Índice 7: Búsqueda de Productos por Rango de Precio

**Problema:** Filtro de precio: "Entre $50,000 y $100,000".

**Solución:**
```sql
CREATE INDEX idx_products_price ON products(price);

-- Query optimizado
SELECT * FROM products 
WHERE price BETWEEN 50000 AND 100000
ORDER BY price;
```

---

### Índice 8: Productos Activos con Stock

**Problema:** Mostrar solo productos disponibles.

**Índice parcial (solo filas que cumplen condición):**
```sql
CREATE INDEX idx_products_active_stock 
ON products(stock) 
WHERE active = TRUE AND stock > 0;

-- Query optimizado
SELECT * FROM products 
WHERE active = TRUE AND stock > 0
ORDER BY stock DESC;
```

**Ventaja:** Índice más pequeño (solo productos activos con stock).

---

## 🔍 EXPLAIN ANALYZE - Analizar Queries

### Comando
```sql
EXPLAIN ANALYZE <query>;
```

### Ejemplo
```sql
EXPLAIN ANALYZE
SELECT p.*, c.name as category_name
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE p.price > 50000;
```

### Leer Resultado

**Seq Scan (Sequential Scan):**
```
Seq Scan on products  (cost=0.00..15.50 rows=100 width=200) (actual time=0.010..0.250 rows=45 loops=1)
  Filter: (price > 50000)
```
- ❌ **Malo:** Lee TODAS las filas
- **Solución:** Crear índice

**Index Scan:**
```
Index Scan using idx_products_price on products  (cost=0.15..8.20 rows=45 width=200) (actual time=0.005..0.050 rows=45 loops=1)
  Index Cond: (price > 50000)
```
- ✅ **Bueno:** Usa índice
- Mucho más rápido

**Métricas Clave:**
- **cost:** Estimación de costo (menor = mejor)
- **rows:** Filas procesadas
- **actual time:** Tiempo real
- **loops:** Veces que se ejecutó

---

## 🛠️ Estrategias de Optimización

### 1️⃣ Crear Índices Selectivos

**Malo (índice en columna con pocos valores):**
```sql
-- Columna 'active' solo tiene TRUE/FALSE (baja cardinalidad)
CREATE INDEX idx_products_active ON products(active);  -- Poco útil
```

**Bueno (índice parcial):**
```sql
-- Solo indexar productos activos
CREATE INDEX idx_products_active_partial ON products(id) WHERE active = TRUE;
```

---

### 2️⃣ Índices Compuestos vs Múltiples Índices

**Escenario:** Filtrar por category_id Y active.

**Opción 1: Dos índices separados**
```sql
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_active ON products(active);
```

**Opción 2: Índice compuesto**
```sql
CREATE INDEX idx_products_category_active ON products(category_id, active);
```

**Mejor:** Opción 2 (índice compuesto) si siempre filtras ambas columnas.

---

### 3️⃣ Cubriendo Índices (Covering Index)

**Problema:** Query necesita columnas adicionales.

```sql
-- Query
SELECT name, price FROM products WHERE category_id = 2;

-- Índice básico
CREATE INDEX idx_products_category ON products(category_id);
-- PostgreSQL usa índice pero debe ir a la tabla para leer name, price
```

**Solución: Índice que incluye columnas extra**
```sql
CREATE INDEX idx_products_category_covering 
ON products(category_id) 
INCLUDE (name, price);

-- Ahora PostgreSQL lee TODO del índice (más rápido)
```

---

### 4️⃣ Orden de Columnas en Índice Compuesto

**Regla:** Columna más selectiva PRIMERO.

```sql
-- Malo (active tiene solo 2 valores: TRUE/FALSE)
CREATE INDEX idx_bad ON products(active, category_id);

-- Bueno (category_id es más selectivo)
CREATE INDEX idx_good ON products(category_id, active);
```

---

## 📈 Monitoreo de Índices

### Ver Índices de una Tabla
```sql
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'products';
```

### Ver Uso de Índices
```sql
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan as index_scans,
    idx_tup_read as tuples_read,
    idx_tup_fetch as tuples_fetched
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;
```

**Interpretación:**
- **idx_scan = 0:** Índice NO se usa (considera eliminarlo)
- **idx_scan alto:** Índice es útil

### Índices No Usados (Candidatos a Eliminar)
```sql
SELECT 
    schemaname,
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) as index_size
FROM pg_stat_user_indexes
WHERE idx_scan = 0
    AND indexrelname NOT LIKE '%_pkey'  -- Excluir primary keys
ORDER BY pg_relation_size(indexrelid) DESC;
```

### Tamaño de Índices
```sql
SELECT 
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) as index_size
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY pg_relation_size(indexrelid) DESC;
```

---

## 🔧 Mantenimiento de Índices

### Reconstruir Índice (fragmentado)
```sql
REINDEX INDEX idx_products_name;
REINDEX TABLE products;  -- Todos los índices de la tabla
```

### Vacuum (limpiar espacio)
```sql
VACUUM ANALYZE products;  -- Actualizar estadísticas
```

### Eliminar Índice
```sql
DROP INDEX IF EXISTS idx_nombre;
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es un índice y para qué sirve?"**

> "Un índice es una estructura de datos que acelera búsquedas:
> - Como el índice de un libro
> - Permite encontrar filas rápidamente
> - Mejora queries con WHERE, JOIN, ORDER BY
> 
> **Sin índice:**
> ```sql
> SELECT * FROM products WHERE name = 'Camiseta';
> -- Lee 10,000 filas → lento
> ```
> 
> **Con índice:**
> ```sql
> CREATE INDEX idx_products_name ON products(name);
> -- Va directo a 'Camiseta' → rápido
> ```"

---

**2. "¿Cuándo usar índice?"**

> "Usa índice cuando:
> - Columna se usa en WHERE frecuentemente
> - Tabla es grande (miles de filas)
> - Columna tiene muchos valores distintos
> 
> NO uses índice cuando:
> - Tabla es pequeña (< 1000 filas)
> - Columna se modifica frecuentemente
> - Columna tiene pocos valores (ej: active: TRUE/FALSE)
> 
> Baby Cash:
> - ✅ Índice en `users.email` (búsqueda en login)
> - ✅ Índice en `products.category_id` (filtro común)
> - ❌ No índice en `products.active` (solo 2 valores)"

---

**3. "¿Qué es un índice compuesto?"**

> "Índice en MÚLTIPLES columnas:
> ```sql
> CREATE INDEX idx_orders_user_status ON orders(user_id, status);
> ```
> 
> **Útil para queries con múltiples filtros:**
> ```sql
> SELECT * FROM orders WHERE user_id = 2 AND status = 'PENDING';
> -- Usa idx_orders_user_status
> ```
> 
> **Orden de columnas importa:**
> - Primera columna: más selectiva
> - Ejemplo: user_id (muchos valores) antes que status (pocos valores)"

---

**4. "¿Cómo saber si query usa índice?"**

> "Usa `EXPLAIN ANALYZE`:
> ```sql
> EXPLAIN ANALYZE SELECT * FROM products WHERE price > 50000;
> ```
> 
> **Sin índice:**
> ```
> Seq Scan on products  ← Malo (lee todas las filas)
> ```
> 
> **Con índice:**
> ```
> Index Scan using idx_products_price  ← Bueno (usa índice)
> ```
> 
> **Métricas:**
> - `actual time`: Tiempo real
> - `rows`: Filas procesadas
> - Menor = mejor"

---

## 📝 Resumen de Índices en Baby Cash

| Índice | Columnas | Tipo | Propósito |
|--------|----------|------|-----------|
| `idx_users_email` | email | UNIQUE | Login rápido |
| `idx_products_category` | category_id | B-tree | Filtrar por categoría |
| `idx_products_name_gin` | name | GIN | Búsqueda con LIKE |
| `idx_products_price` | price | B-tree | Filtro de rango |
| `idx_orders_user` | user_id | B-tree | Historial usuario |
| `idx_orders_status_date` | status, created_at | Compuesto | Filtro admin |
| `idx_order_items_order` | order_id | B-tree | Detalles orden |
| `idx_products_active_stock` | stock (WHERE active) | Parcial | Solo productos disponibles |

---

## 🚀 Conclusión

**Índices en Baby Cash:**
- ✅ Aceleran queries frecuentes (login, filtros, búsquedas)
- ✅ Mejoran performance en tablas grandes
- ✅ Se usan automáticamente por PostgreSQL
- ⚠️ Ocupan espacio
- ⚠️ Ralentizan INSERT/UPDATE (se actualizan)

**Balance:** Crear índices en columnas críticas, monitorear uso.

---

**Ahora lee:** `TRANSACCIONES.md` para garantizar consistencia. 🚀
