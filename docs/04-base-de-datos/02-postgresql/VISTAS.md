# VISTAS - BASE DE DATOS BABY CASH

## 🎯 ¿Qué es una Vista?

Una **vista** (VIEW) es una "tabla virtual" que muestra el resultado de una query SELECT. No almacena datos, sino que ejecuta la query cada vez que se consulta.

### Analogía Simple
> **Imagina una ventana con un marco especial:**
> - La ventana no contiene la vista, solo la MUESTRA
> - Cada vez que miras, ves el paisaje ACTUAL (no una foto vieja)
> - El marco (la vista) determina qué parte del paisaje ves
> 
> En base de datos:
> - La vista no guarda datos, solo MUESTRA resultados
> - Cada consulta ejecuta la query y muestra datos ACTUALES
> - La vista define QUÉ datos y CÓMO se muestran

---

## 💡 ¿Por Qué Usar Vistas?

### 1️⃣ **Simplificar Queries Complejos**
En lugar de escribir JOIN largo cada vez, usas `SELECT * FROM vista_simple`.

### 2️⃣ **Reutilización**
Defines la query una vez, úsala en múltiples lugares.

### 3️⃣ **Seguridad**
Oculta columnas sensibles (como passwords) o filas no autorizadas.

### 4️⃣ **Abstracción**
Oculta complejidad de estructura de tablas al frontend.

### 5️⃣ **Consistencia**
Todos usan la misma lógica, no hay discrepancias.

---

## 📊 Vistas en Baby Cash

### Vista 1: Productos con Información Completa

**Problema:** Siempre necesitamos JOIN entre products y categories.

**Solución:** Vista que ya incluye el JOIN.

```sql
CREATE OR REPLACE VIEW v_products_full AS
SELECT 
    p.id,
    p.name,
    p.description,
    p.price,
    p.stock,
    p.image_url,
    p.discount,
    p.active,
    p.created_at,
    p.updated_at,
    -- Información de categoría
    c.id as category_id,
    c.name as category_name,
    c.slug as category_slug,
    c.icon as category_icon,
    -- Cálculos
    ROUND(p.price * (1 - p.discount / 100.0), 2) as final_price,
    CASE 
        WHEN p.stock = 0 THEN 'Sin stock'
        WHEN p.stock <= 5 THEN 'Stock bajo'
        ELSE 'Disponible'
    END as stock_status
FROM products p
INNER JOIN categories c ON p.category_id = c.id;
```

**Uso:**
```sql
-- Sin vista (query complejo)
SELECT p.*, c.name as category_name, 
       ROUND(p.price * (1 - p.discount / 100.0), 2) as final_price
FROM products p
INNER JOIN categories c ON p.category_id = c.id
WHERE p.active = TRUE;

-- Con vista (simple)
SELECT * FROM v_products_full WHERE active = TRUE;

-- Filtrar por categoría
SELECT * FROM v_products_full WHERE category_slug = 'ropa';

-- Productos con descuento
SELECT * FROM v_products_full WHERE discount > 0 ORDER BY discount DESC;
```

---

### Vista 2: Órdenes con Detalles Completos

**Problema:** Consultar órdenes requiere JOIN con users y order_items.

**Solución:** Vista que consolida toda la información.

```sql
CREATE OR REPLACE VIEW v_orders_full AS
SELECT 
    o.id as order_id,
    o.total as order_total,
    o.status as order_status,
    o.created_at as order_date,
    -- Información del usuario
    u.id as user_id,
    u.name as customer_name,
    u.email as customer_email,
    -- Estadísticas de la orden
    COUNT(DISTINCT oi.id) as total_items,
    SUM(oi.quantity) as total_products
FROM orders o
INNER JOIN users u ON o.user_id = u.id
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id, o.total, o.status, o.created_at, u.id, u.name, u.email;
```

**Uso:**
```sql
-- Órdenes de un usuario
SELECT * FROM v_orders_full 
WHERE user_id = 2 
ORDER BY order_date DESC;

-- Órdenes pendientes
SELECT order_id, customer_name, order_total, total_items
FROM v_orders_full
WHERE order_status = 'PENDING'
ORDER BY order_date;

-- Estadísticas rápidas
SELECT 
    order_status,
    COUNT(*) as total,
    SUM(order_total) as total_sales
FROM v_orders_full
GROUP BY order_status;
```

---

### Vista 3: Detalle de Order Items con Productos

**Problema:** Ver items de orden con nombre de producto requiere múltiples JOINs.

**Solución:** Vista que muestra todo junto.

```sql
CREATE OR REPLACE VIEW v_order_items_detail AS
SELECT 
    oi.id as item_id,
    oi.order_id,
    oi.product_id,
    oi.quantity,
    oi.price as unit_price,
    (oi.quantity * oi.price) as subtotal,
    -- Información del producto
    p.name as product_name,
    p.image_url as product_image,
    p.stock as current_stock,
    c.name as category_name,
    -- Información de la orden
    o.status as order_status,
    o.created_at as order_date,
    u.name as customer_name
FROM order_items oi
INNER JOIN products p ON oi.product_id = p.id
INNER JOIN categories c ON p.category_id = c.id
INNER JOIN orders o ON oi.order_id = o.id
INNER JOIN users u ON o.user_id = u.id;
```

**Uso:**
```sql
-- Items de una orden específica
SELECT 
    product_name,
    quantity,
    unit_price,
    subtotal
FROM v_order_items_detail
WHERE order_id = 1;

-- Productos más vendidos
SELECT 
    product_id,
    product_name,
    category_name,
    SUM(quantity) as total_sold,
    SUM(subtotal) as total_revenue
FROM v_order_items_detail
WHERE order_status = 'COMPLETED'
GROUP BY product_id, product_name, category_name
ORDER BY total_sold DESC
LIMIT 10;

-- Compras de un cliente
SELECT 
    order_id,
    order_date,
    product_name,
    quantity,
    subtotal
FROM v_order_items_detail
WHERE customer_name = 'María González'
ORDER BY order_date DESC;
```

---

### Vista 4: Productos Más Vendidos

**Problema:** Calcular productos más vendidos es query complejo que se usa frecuentemente.

**Solución:** Vista materializada (se actualiza periódicamente).

```sql
CREATE MATERIALIZED VIEW v_top_products AS
SELECT 
    p.id,
    p.name,
    p.price,
    p.image_url,
    c.name as category,
    COUNT(DISTINCT oi.order_id) as times_ordered,
    SUM(oi.quantity) as total_sold,
    SUM(oi.quantity * oi.price) as total_revenue
FROM products p
INNER JOIN categories c ON p.category_id = c.id
INNER JOIN order_items oi ON p.id = oi.product_id
INNER JOIN orders o ON oi.order_id = o.id
WHERE o.status = 'COMPLETED'
GROUP BY p.id, p.name, p.price, p.image_url, c.name
ORDER BY total_sold DESC;

-- Crear índice para búsquedas rápidas
CREATE INDEX idx_v_top_products_sold ON v_top_products(total_sold DESC);
```

**Uso:**
```sql
-- Top 10 productos
SELECT * FROM v_top_products LIMIT 10;

-- Productos de categoría específica más vendidos
SELECT * FROM v_top_products WHERE category = 'Juguetes' LIMIT 5;

-- Refrescar vista (hacer diariamente con cron job)
REFRESH MATERIALIZED VIEW v_top_products;
```

**Diferencia con Vista Normal:**
- **Vista normal**: Ejecuta query cada vez (lenta si es compleja)
- **Vista materializada**: Guarda resultados (rápida), se actualiza manualmente

---

### Vista 5: Inventario con Valoración

**Problema:** Calcular valor del inventario actual.

**Solución:** Vista que suma precio × stock por categoría.

```sql
CREATE OR REPLACE VIEW v_inventory_valuation AS
SELECT 
    c.id as category_id,
    c.name as category_name,
    COUNT(p.id) as total_products,
    SUM(p.stock) as total_stock,
    SUM(p.stock * p.price) as inventory_value,
    AVG(p.price) as average_price
FROM categories c
LEFT JOIN products p ON c.id = p.category_id AND p.active = TRUE
GROUP BY c.id, c.name
ORDER BY inventory_value DESC;
```

**Uso:**
```sql
-- Valor total del inventario
SELECT 
    SUM(inventory_value) as total_inventory_value,
    SUM(total_stock) as total_items
FROM v_inventory_valuation;

-- Inventario por categoría
SELECT 
    category_name,
    total_products,
    total_stock,
    TO_CHAR(inventory_value, 'L999,999,999') as value_formatted
FROM v_inventory_valuation;

-- Categorías con bajo inventario
SELECT * FROM v_inventory_valuation 
WHERE total_stock < 100
ORDER BY total_stock;
```

---

### Vista 6: Dashboard de Administrador

**Problema:** Panel admin necesita múltiples estadísticas.

**Solución:** Vista que consolida métricas clave.

```sql
CREATE OR REPLACE VIEW v_admin_dashboard AS
SELECT 
    -- Productos
    (SELECT COUNT(*) FROM products WHERE active = TRUE) as total_products,
    (SELECT COUNT(*) FROM products WHERE stock = 0) as out_of_stock_products,
    (SELECT COUNT(*) FROM products WHERE stock > 0 AND stock <= 5) as low_stock_products,
    
    -- Órdenes
    (SELECT COUNT(*) FROM orders) as total_orders,
    (SELECT COUNT(*) FROM orders WHERE status = 'PENDING') as pending_orders,
    (SELECT COUNT(*) FROM orders WHERE status = 'COMPLETED') as completed_orders,
    (SELECT COUNT(*) FROM orders WHERE DATE(created_at) = CURRENT_DATE) as orders_today,
    
    -- Usuarios
    (SELECT COUNT(*) FROM users WHERE role = 'USER') as total_customers,
    (SELECT COUNT(*) FROM users WHERE DATE(created_at) = CURRENT_DATE) as new_users_today,
    
    -- Ventas
    (SELECT COALESCE(SUM(total), 0) FROM orders WHERE status = 'COMPLETED') as total_revenue,
    (SELECT COALESCE(SUM(total), 0) FROM orders WHERE status = 'COMPLETED' AND DATE(created_at) = CURRENT_DATE) as revenue_today,
    (SELECT COALESCE(AVG(total), 0) FROM orders WHERE status = 'COMPLETED') as average_order_value;
```

**Uso:**
```sql
-- Obtener todas las métricas con una sola query
SELECT * FROM v_admin_dashboard;

-- Resultado:
-- total_products | out_of_stock_products | low_stock_products | total_orders | pending_orders | ...
-- 68             | 3                     | 7                  | 342          | 15             | ...
```

---

### Vista 7: Usuarios con Estadísticas de Compra

**Problema:** Ver usuarios con su historial de compras.

**Solución:** Vista que agrega estadísticas por usuario.

```sql
CREATE OR REPLACE VIEW v_users_with_stats AS
SELECT 
    u.id,
    u.name,
    u.email,
    u.role,
    u.created_at as registered_at,
    COUNT(o.id) as total_orders,
    COUNT(CASE WHEN o.status = 'COMPLETED' THEN 1 END) as completed_orders,
    COUNT(CASE WHEN o.status = 'PENDING' THEN 1 END) as pending_orders,
    COALESCE(SUM(CASE WHEN o.status = 'COMPLETED' THEN o.total END), 0) as total_spent,
    COALESCE(AVG(CASE WHEN o.status = 'COMPLETED' THEN o.total END), 0) as average_order,
    MAX(o.created_at) as last_order_date
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
WHERE u.role = 'USER'
GROUP BY u.id, u.name, u.email, u.role, u.created_at;
```

**Uso:**
```sql
-- Clientes más valiosos
SELECT name, email, total_spent, total_orders
FROM v_users_with_stats
ORDER BY total_spent DESC
LIMIT 10;

-- Clientes sin compras
SELECT name, email, registered_at
FROM v_users_with_stats
WHERE total_orders = 0
ORDER BY registered_at DESC;

-- Clientes activos recientes
SELECT name, email, last_order_date, total_orders
FROM v_users_with_stats
WHERE last_order_date >= CURRENT_DATE - INTERVAL '30 days'
ORDER BY last_order_date DESC;
```

---

### Vista 8: Reporte de Ventas Mensual

**Problema:** Generar reportes mensuales requiere query complejo.

**Solución:** Vista que agrupa por mes.

```sql
CREATE OR REPLACE VIEW v_monthly_sales_report AS
SELECT 
    DATE_TRUNC('month', o.created_at) as month,
    TO_CHAR(o.created_at, 'YYYY-MM') as month_label,
    COUNT(o.id) as total_orders,
    COUNT(CASE WHEN o.status = 'COMPLETED' THEN 1 END) as completed_orders,
    COUNT(CASE WHEN o.status = 'PENDING' THEN 1 END) as pending_orders,
    COUNT(CASE WHEN o.status = 'CANCELLED' THEN 1 END) as cancelled_orders,
    COALESCE(SUM(CASE WHEN o.status = 'COMPLETED' THEN o.total END), 0) as total_revenue,
    COALESCE(AVG(CASE WHEN o.status = 'COMPLETED' THEN o.total END), 0) as average_order_value,
    COUNT(DISTINCT o.user_id) as unique_customers
FROM orders o
GROUP BY DATE_TRUNC('month', o.created_at), TO_CHAR(o.created_at, 'YYYY-MM')
ORDER BY month DESC;
```

**Uso:**
```sql
-- Últimos 12 meses
SELECT * FROM v_monthly_sales_report LIMIT 12;

-- Comparar año actual vs año anterior
SELECT 
    EXTRACT(MONTH FROM month) as month_number,
    month_label,
    total_revenue,
    LAG(total_revenue) OVER (ORDER BY month) as previous_month_revenue,
    ROUND(((total_revenue - LAG(total_revenue) OVER (ORDER BY month)) / 
           NULLIF(LAG(total_revenue) OVER (ORDER BY month), 0) * 100), 2) as growth_percentage
FROM v_monthly_sales_report
WHERE month >= DATE_TRUNC('year', CURRENT_DATE);
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es una vista y cómo funciona?"**

> "Una vista es una 'tabla virtual' que no guarda datos:
> - Es una query SELECT guardada con un nombre
> - Cada vez que consultas la vista, ejecuta la query
> - Siempre muestra datos actualizados
> - Simplifica queries complejos
> 
> Ejemplo:
> ```sql
> CREATE VIEW v_productos AS 
> SELECT p.*, c.name as categoria FROM products p JOIN categories c ON...;
> 
> -- Ahora solo haces:
> SELECT * FROM v_productos;  -- Mucho más simple
> ```"

---

**2. "¿Cuál es la diferencia entre vista y tabla?"**

> "**Tabla:**
> - Almacena datos físicamente en disco
> - Ocupa espacio
> - Datos persisten
> - INSERT/UPDATE/DELETE directos
> 
> **Vista:**
> - No almacena datos (solo la query)
> - No ocupa espacio (solo guarda definición)
> - Muestra datos de tablas base
> - INSERT/UPDATE/DELETE generalmente no permitidos (excepto vistas simples)
> 
> **Vista Materializada:**
> - Almacena resultados (como tabla)
> - Ocupa espacio
> - Más rápida que vista normal
> - Necesita REFRESH para actualizar"

---

**3. "¿Cuándo usar vista vs query directa?"**

> "Usa **Vista** cuando:
> - Query se usa en múltiples lugares
> - Query es complejo (muchos JOINs)
> - Quieres ocultar complejidad
> - Necesitas seguridad (ocultar columnas)
> - Quieres consistencia en la lógica
> 
> Usa **Query directa** cuando:
> - Query es simple
> - Solo se usa una vez
> - Necesitas parámetros dinámicos complejos
> - Query cambia frecuentemente"

---

**4. "¿Las vistas afectan el performance?"**

> "Depende del tipo:
> 
> **Vista normal:**
> - Ejecuta query cada vez (puede ser lenta si query es compleja)
> - No ocupa espacio
> - Siempre datos actuales
> 
> **Vista materializada:**
> - Muy rápida (datos precalculados)
> - Ocupa espacio
> - Datos pueden estar desactualizados (hasta que hagas REFRESH)
> 
> **Optimización:**
> - Crear índices en vistas materializadas
> - Usar WHERE en consultas a vistas
> - No hacer SELECT * si no necesitas todos los campos"

---

## 📝 Resumen de Vistas en Baby Cash

| Vista | Tipo | Propósito | Uso Común |
|-------|------|-----------|-----------|
| `v_products_full` | Normal | Productos con categoría | Catálogo, búsquedas |
| `v_orders_full` | Normal | Órdenes con cliente | Historial, admin |
| `v_order_items_detail` | Normal | Items con productos | Detalle orden, reportes |
| `v_top_products` | Materializada | Productos más vendidos | Dashboard, home |
| `v_inventory_valuation` | Normal | Valor de inventario | Reportes financieros |
| `v_admin_dashboard` | Normal | Métricas admin | Panel administrador |
| `v_users_with_stats` | Normal | Usuarios con estadísticas | CRM, segmentación |
| `v_monthly_sales_report` | Normal | Ventas por mes | Reportes, análisis |

---

## 🔧 Comandos Útiles

### Crear Vista
```sql
CREATE OR REPLACE VIEW nombre_vista AS
SELECT ...;
```

### Crear Vista Materializada
```sql
CREATE MATERIALIZED VIEW nombre_vista AS
SELECT ...;
```

### Refrescar Vista Materializada
```sql
REFRESH MATERIALIZED VIEW nombre_vista;
```

### Ver Definición de Vista
```sql
SELECT definition 
FROM pg_views 
WHERE viewname = 'v_products_full';
```

### Listar Todas las Vistas
```sql
SELECT schemaname, viewname 
FROM pg_views 
WHERE schemaname = 'public';
```

### Eliminar Vista
```sql
DROP VIEW IF EXISTS nombre_vista;
DROP MATERIALIZED VIEW IF EXISTS nombre_vista;
```

---

## 🚀 Conclusión

**Vistas en Baby Cash:**
- ✅ Simplifican queries complejos
- ✅ Reutilizan lógica común
- ✅ Mejoran seguridad
- ✅ Facilitan mantenimiento

**Son la "capa de abstracción" entre base de datos y aplicación.**

---

**Ahora lee:** `PROCEDIMIENTOS-ALMACENADOS.md` para lógica compleja. 🚀
