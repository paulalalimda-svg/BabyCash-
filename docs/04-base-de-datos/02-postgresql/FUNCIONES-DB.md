# FUNCIONES DE BASE DE DATOS - BABY CASH

## 🎯 ¿Qué es una Función?

Una **función** (function) es un bloque de código que recibe parámetros, realiza cálculos o transformaciones, y **retorna un valor**.

### Analogía Simple
> **Imagina una calculadora:**
> - Le das números (parámetros)
> - Hace un cálculo
> - Te devuelve el resultado
> 
> En base de datos:
> - Función recibe valores
> - Ejecuta lógica SQL
> - **Retorna UN valor** (número, texto, tabla, etc.)
> - Se usa dentro de queries: `SELECT funcion(x) FROM tabla`

---

## 💡 Diferencia: Función vs Procedimiento

| Característica | Función | Procedimiento |
|----------------|---------|---------------|
| **Ejecución** | `SELECT funcion()` | `CALL procedimiento()` |
| **Retorno** | `RETURNS` un valor | Múltiples `OUT` params |
| **Uso en queries** | Sí (WHERE, SELECT) | No |
| **Transacciones** | No puede COMMIT | Sí puede COMMIT |
| **Propósito** | Cálculos, transformaciones | Operaciones complejas |

---

## 📊 Funciones en Baby Cash

### Función 1: Calcular Precio con Descuento

**Problema:** Necesitamos calcular precio final con descuento en múltiples lugares.

**Solución:**
```sql
CREATE OR REPLACE FUNCTION calculate_discounted_price(
    p_price DECIMAL(10, 2),
    p_discount DECIMAL(5, 2)
)
RETURNS DECIMAL(10, 2)
LANGUAGE plpgsql
IMMUTABLE  -- Mismo input = mismo output (permite optimización)
AS $$
BEGIN
    IF p_discount IS NULL OR p_discount = 0 THEN
        RETURN p_price;
    END IF;
    
    RETURN ROUND(p_price * (1 - p_discount / 100.0), 2);
END;
$$;
```

**Uso:**
```sql
-- En SELECT
SELECT 
    name,
    price as precio_original,
    discount as descuento,
    calculate_discounted_price(price, discount) as precio_final
FROM products;

-- En WHERE
SELECT * FROM products
WHERE calculate_discounted_price(price, discount) < 50000;

-- En INSERT
INSERT INTO order_items (order_id, product_id, quantity, price)
SELECT 
    1,  -- order_id
    id,
    2,  -- quantity
    calculate_discounted_price(price, discount)
FROM products
WHERE id = 5;
```

---

### Función 2: Estado de Stock

**Problema:** Determinar si stock es bajo, medio o alto.

**Solución:**
```sql
CREATE OR REPLACE FUNCTION get_stock_status(p_stock INTEGER)
RETURNS VARCHAR(20)
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    IF p_stock = 0 THEN
        RETURN 'Sin stock';
    ELSIF p_stock <= 5 THEN
        RETURN 'Stock bajo';
    ELSIF p_stock <= 20 THEN
        RETURN 'Stock medio';
    ELSE
        RETURN 'Stock alto';
    END IF;
END;
$$;
```

**Uso:**
```sql
-- Dashboard de inventario
SELECT 
    name,
    stock,
    get_stock_status(stock) as estado
FROM products
ORDER BY stock;

-- Filtrar productos con stock bajo
SELECT * FROM products
WHERE get_stock_status(stock) = 'Stock bajo';

-- Agrupar por estado
SELECT 
    get_stock_status(stock) as estado,
    COUNT(*) as cantidad_productos
FROM products
GROUP BY get_stock_status(stock);
```

---

### Función 3: Formatear Precio en Pesos Colombianos

**Problema:** Mostrar precios con formato COP.

**Solución:**
```sql
CREATE OR REPLACE FUNCTION format_currency(p_amount DECIMAL(10, 2))
RETURNS TEXT
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    RETURN '$' || TO_CHAR(p_amount, 'FM999,999,999');
END;
$$;
```

**Uso:**
```sql
-- Productos con precio formateado
SELECT 
    name,
    format_currency(price) as precio,
    format_currency(calculate_discounted_price(price, discount)) as precio_final
FROM products;

-- Resultado:
-- name          | precio      | precio_final
-- Camiseta Azul | $45,000     | $36,000
-- Juguete Robot | $120,000    | $96,000
```

---

### Función 4: Contar Items en Orden

**Problema:** Saber cuántos items tiene una orden.

**Solución:**
```sql
CREATE OR REPLACE FUNCTION get_order_item_count(p_order_id INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
STABLE  -- Resultado puede cambiar entre transacciones
AS $$
DECLARE
    v_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM order_items
    WHERE order_id = p_order_id;
    
    RETURN COALESCE(v_count, 0);
END;
$$;
```

**Uso:**
```sql
-- Órdenes con cantidad de items
SELECT 
    id,
    user_id,
    total,
    get_order_item_count(id) as cantidad_items
FROM orders
ORDER BY id DESC;

-- Filtrar órdenes grandes
SELECT * FROM orders
WHERE get_order_item_count(id) > 5;
```

---

### Función 5: Calcular Subtotal de Orden

**Problema:** Calcular total sumando todos los order_items.

**Solución:**
```sql
CREATE OR REPLACE FUNCTION calculate_order_subtotal(p_order_id INTEGER)
RETURNS DECIMAL(10, 2)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_subtotal DECIMAL(10, 2);
BEGIN
    SELECT COALESCE(SUM(quantity * price), 0)
    INTO v_subtotal
    FROM order_items
    WHERE order_id = p_order_id;
    
    RETURN v_subtotal;
END;
$$;
```

**Uso:**
```sql
-- Verificar consistencia
SELECT 
    id,
    total as total_guardado,
    calculate_order_subtotal(id) as total_calculado,
    CASE 
        WHEN total = calculate_order_subtotal(id) THEN '✓ OK'
        ELSE '✗ Inconsistente'
    END as validacion
FROM orders;

-- Corregir totales inconsistentes
UPDATE orders
SET total = calculate_order_subtotal(id)
WHERE total != calculate_order_subtotal(id);
```

---

### Función 6: Validar Email

**Problema:** Verificar si email es válido.

**Solución:**
```sql
CREATE OR REPLACE FUNCTION is_valid_email(p_email TEXT)
RETURNS BOOLEAN
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    -- Validación básica: contiene @, tiene texto antes y después
    RETURN p_email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$';
END;
$$;
```

**Uso:**
```sql
-- Validar en INSERT/UPDATE (mejor usar CHECK constraint o trigger)
SELECT is_valid_email('juan@example.com');  -- TRUE
SELECT is_valid_email('invalid-email');     -- FALSE

-- Encontrar emails inválidos
SELECT * FROM users
WHERE NOT is_valid_email(email);

-- Usar en CHECK constraint
ALTER TABLE users
ADD CONSTRAINT check_email_format
CHECK (is_valid_email(email));
```

---

### Función 7: Generar Slug desde Texto

**Problema:** Crear slug URL-friendly desde nombre.

**Solución:**
```sql
CREATE OR REPLACE FUNCTION generate_slug(p_text TEXT)
RETURNS TEXT
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
    RETURN LOWER(
        REGEXP_REPLACE(
            REGEXP_REPLACE(
                TRIM(p_text),
                '[^a-zA-Z0-9\s-]',  -- Remover caracteres especiales
                '',
                'g'
            ),
            '\s+',  -- Reemplazar espacios con guiones
            '-',
            'g'
        )
    );
END;
$$;
```

**Uso:**
```sql
-- Generar slugs
SELECT generate_slug('Camiseta Azul Talla M');  -- camiseta-azul-talla-m
SELECT generate_slug('Juguete (Niños) 3+');     -- juguete-nios-3

-- Actualizar slugs existentes
UPDATE categories
SET slug = generate_slug(name)
WHERE slug IS NULL OR slug = '';

-- Usar en trigger
CREATE TRIGGER set_category_slug
    BEFORE INSERT OR UPDATE ON categories
    FOR EACH ROW
    WHEN (NEW.slug IS NULL OR NEW.slug = '')
    EXECUTE FUNCTION set_slug_from_name();

CREATE OR REPLACE FUNCTION set_slug_from_name()
RETURNS TRIGGER AS $$
BEGIN
    NEW.slug := generate_slug(NEW.name);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

---

### Función 8: Obtener Productos de una Categoría (Table Function)

**Problema:** Retornar conjunto de filas.

**Solución:**
```sql
CREATE OR REPLACE FUNCTION get_category_products(p_category_slug TEXT)
RETURNS TABLE(
    product_id INTEGER,
    product_name VARCHAR(200),
    price DECIMAL(10, 2),
    discount DECIMAL(5, 2),
    final_price DECIMAL(10, 2),
    stock INTEGER,
    stock_status TEXT
)
LANGUAGE plpgsql
STABLE
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id,
        p.name,
        p.price,
        p.discount,
        calculate_discounted_price(p.price, p.discount),
        p.stock,
        get_stock_status(p.stock)
    FROM products p
    INNER JOIN categories c ON p.category_id = c.id
    WHERE c.slug = p_category_slug
        AND p.active = TRUE
    ORDER BY p.name;
END;
$$;
```

**Uso:**
```sql
-- Usar como tabla
SELECT * FROM get_category_products('ropa');

-- Resultado:
-- product_id | product_name     | price   | discount | final_price | stock | stock_status
-- 1          | Camiseta Azul    | 45000   | 20       | 36000       | 15    | Stock medio
-- 5          | Pantalón Negro   | 80000   | 0        | 80000       | 8     | Stock medio

-- Usar en JOIN
SELECT 
    o.id as order_id,
    gcp.product_name,
    gcp.final_price
FROM orders o
CROSS JOIN get_category_products('juguetes') gcp
WHERE o.user_id = 2;
```

---

### Función 9: Calcular Días desde Última Orden

**Problema:** ¿Cuántos días han pasado desde la última compra del usuario?

**Solución:**
```sql
CREATE OR REPLACE FUNCTION days_since_last_order(p_user_id INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_last_order_date TIMESTAMP;
BEGIN
    SELECT MAX(created_at) INTO v_last_order_date
    FROM orders
    WHERE user_id = p_user_id;
    
    IF v_last_order_date IS NULL THEN
        RETURN NULL;  -- Usuario nunca ha comprado
    END IF;
    
    RETURN EXTRACT(DAY FROM CURRENT_TIMESTAMP - v_last_order_date)::INTEGER;
END;
$$;
```

**Uso:**
```sql
-- Usuarios inactivos
SELECT 
    id,
    name,
    email,
    days_since_last_order(id) as dias_sin_comprar
FROM users
WHERE days_since_last_order(id) > 90
ORDER BY days_since_last_order(id) DESC;

-- Segmentar usuarios
SELECT 
    CASE 
        WHEN days_since_last_order(id) IS NULL THEN 'Nunca ha comprado'
        WHEN days_since_last_order(id) <= 7 THEN 'Activo (última semana)'
        WHEN days_since_last_order(id) <= 30 THEN 'Activo (último mes)'
        WHEN days_since_last_order(id) <= 90 THEN 'Moderado'
        ELSE 'Inactivo'
    END as segmento,
    COUNT(*) as cantidad_usuarios
FROM users
GROUP BY segmento;
```

---

### Función 10: Obtener Total Gastado por Usuario

**Problema:** Total de dinero gastado por un usuario.

**Solución:**
```sql
CREATE OR REPLACE FUNCTION get_user_total_spent(p_user_id INTEGER)
RETURNS DECIMAL(10, 2)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_total DECIMAL(10, 2);
BEGIN
    SELECT COALESCE(SUM(total), 0) INTO v_total
    FROM orders
    WHERE user_id = p_user_id
        AND status = 'COMPLETED';
    
    RETURN v_total;
END;
$$;
```

**Uso:**
```sql
-- Top clientes por gasto
SELECT 
    id,
    name,
    email,
    format_currency(get_user_total_spent(id)) as total_gastado
FROM users
WHERE role = 'USER'
ORDER BY get_user_total_spent(id) DESC
LIMIT 10;

-- Clasificar clientes
SELECT 
    id,
    name,
    CASE 
        WHEN get_user_total_spent(id) >= 1000000 THEN 'VIP'
        WHEN get_user_total_spent(id) >= 500000 THEN 'Premium'
        WHEN get_user_total_spent(id) >= 100000 THEN 'Regular'
        ELSE 'Nuevo'
    END as categoria_cliente
FROM users
WHERE role = 'USER';
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es una función de base de datos?"**

> "Una función es un bloque de código que:
> - Recibe parámetros
> - Ejecuta cálculos o transformaciones
> - **Retorna UN valor** (número, texto, tabla)
> - Se usa en queries: `SELECT funcion(x) FROM tabla`
> 
> Ejemplo:
> ```sql
> CREATE FUNCTION calculate_discount(price DECIMAL, discount DECIMAL)
> RETURNS DECIMAL AS $$
> BEGIN
>     RETURN price * (1 - discount / 100.0);
> END;
> $$ LANGUAGE plpgsql;
> 
> -- Usar:
> SELECT name, calculate_discount(price, 20) FROM products;
> ```"

---

**2. "Diferencia entre función y procedimiento"**

> "**Función:**
> - `SELECT funcion()` o en WHERE
> - RETURNS un valor
> - No puede COMMIT/ROLLBACK
> - Para cálculos
> 
> **Procedimiento:**
> - `CALL procedimiento()`
> - OUT params (múltiples)
> - Puede COMMIT/ROLLBACK
> - Para operaciones complejas
> 
> Baby Cash:
> - Función: `calculate_discounted_price()` - calcular precio
> - Procedimiento: `sp_create_order()` - crear orden completa"

---

**3. "¿Qué significa IMMUTABLE, STABLE, VOLATILE?"**

> "**IMMUTABLE:** (inmutable)
> - Mismos parámetros = mismo resultado SIEMPRE
> - No consulta tablas
> - Ejemplo: `calculate_discount(100, 10)` siempre retorna 90
> - PostgreSQL puede cachear resultado
> 
> **STABLE:** (estable)
> - Mismo resultado dentro de UNA transacción
> - Puede consultar tablas
> - Ejemplo: `get_order_total(1)` retorna igual durante la transacción
> 
> **VOLATILE:** (volátil) - default
> - Resultado puede cambiar en cada llamada
> - Ejemplo: `CURRENT_TIMESTAMP` cambia cada vez
> 
> Beneficio: IMMUTABLE/STABLE permiten optimizaciones."

---

**4. "¿Cuándo usar función vs hacer cálculo en el backend?"**

> "Usa **Función** cuando:
> - Cálculo se usa en múltiples queries
> - Lógica depende solo de datos (no servicios externos)
> - Necesitas usar en WHERE, ORDER BY, GROUP BY
> - Performance es crítico (ejecuta en DB)
> 
> Usa **Backend** cuando:
> - Lógica cambia frecuentemente
> - Interactúa con APIs externas
> - Cálculo muy complejo (mejor en Java/Python)
> - Necesitas librerías externas
> 
> Baby Cash:
> - Función: `calculate_discounted_price()` - simple, reutilizable
> - Backend: Calcular impuestos regionales (depende de API externa)"

---

## 📝 Resumen de Funciones en Baby Cash

| Función | Retorna | Propósito | Uso Común |
|---------|---------|-----------|-----------|
| `calculate_discounted_price` | DECIMAL | Calcular precio final | SELECT, WHERE |
| `get_stock_status` | VARCHAR | Estado de stock | Dashboard, filtros |
| `format_currency` | TEXT | Formatear COP | Reportes, UI |
| `get_order_item_count` | INTEGER | Contar items | Estadísticas |
| `calculate_order_subtotal` | DECIMAL | Sumar items | Validación |
| `is_valid_email` | BOOLEAN | Validar email | CHECK constraint |
| `generate_slug` | TEXT | Crear slug | Auto-generación |
| `get_category_products` | TABLE | Productos de categoría | Consultas complejas |
| `days_since_last_order` | INTEGER | Días inactividad | Segmentación |
| `get_user_total_spent` | DECIMAL | Total gastado | Clasificación clientes |

---

## 🔧 Comandos Útiles

### Crear Función Escalar
```sql
CREATE OR REPLACE FUNCTION nombre(params)
RETURNS tipo_retorno
LANGUAGE plpgsql
[IMMUTABLE | STABLE | VOLATILE]
AS $$
BEGIN
    RETURN valor;
END;
$$;
```

### Crear Función Tabla
```sql
CREATE OR REPLACE FUNCTION nombre(params)
RETURNS TABLE(col1 tipo, col2 tipo)
AS $$
BEGIN
    RETURN QUERY SELECT ...;
END;
$$;
```

### Ver Funciones
```sql
SELECT proname, pg_get_functiondef(oid)
FROM pg_proc
WHERE prokind = 'f'  -- 'f' = function
    AND pronamespace = 'public'::regnamespace;
```

### Eliminar Función
```sql
DROP FUNCTION IF EXISTS nombre_funcion(tipos_parametros);
```

---

## 🚀 Conclusión

**Funciones en Baby Cash:**
- ✅ Simplifican cálculos repetitivos
- ✅ Se usan directamente en queries
- ✅ Mejoran consistencia
- ✅ Optimizan performance (IMMUTABLE/STABLE)

**Son las "herramientas" que usas en tus queries.**

---

**Ahora lee:** `INDICES-OPTIMIZACION.md` para acelerar consultas. 🚀
