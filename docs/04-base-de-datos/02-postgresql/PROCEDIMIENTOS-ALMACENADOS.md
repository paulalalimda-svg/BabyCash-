# PROCEDIMIENTOS ALMACENADOS - BABY CASH

## 🎯 ¿Qué es un Procedimiento Almacenado?

Un **procedimiento almacenado** (stored procedure) es un conjunto de instrucciones SQL guardadas en la base de datos que se ejecutan como una unidad.

### Analogía Simple
> **Imagina una receta de cocina:**
> - Tiene ingredientes (parámetros de entrada)
> - Tiene pasos ordenados (instrucciones SQL)
> - Produce un resultado (salida o efecto)
> - La guardas y la reutilizas cuando necesites
> 
> En base de datos:
> - Los procedimientos tienen **parámetros**
> - Contienen **lógica compleja** (IF, LOOP, transacciones)
> - Se ejecutan con `CALL nombre_procedimiento(params)`
> - Se guardan en la base de datos, no en el código

---

## 💡 ¿Por Qué Usar Procedimientos?

### 1️⃣ **Lógica Compleja en un Solo Lugar**
Operaciones multi-paso (crear orden + insertar items + reducir stock) en una llamada.

### 2️⃣ **Transacciones Atómicas**
Todo se hace o nada se hace (COMMIT o ROLLBACK).

### 3️⃣ **Performance**
Ejecuta en el servidor de BD (menos tráfico red).

### 4️⃣ **Reutilización**
Define una vez, úsalo desde múltiples aplicaciones.

### 5️⃣ **Seguridad**
Usuario ejecuta procedimiento sin acceso directo a tablas.

---

## 📊 Procedimientos en Baby Cash

### Procedimiento 1: Crear Orden Completa

**Problema:** Crear orden requiere:
1. Insertar en `orders`
2. Insertar múltiples `order_items`
3. Validar stock
4. Reducir stock
5. Calcular total

Si falla algún paso, **debe revertirse TODO**.

**Solución:** Procedimiento con transacción.

```sql
CREATE OR REPLACE PROCEDURE sp_create_order(
    p_user_id INTEGER,
    p_items JSONB,  -- [{"product_id": 1, "quantity": 2}, {...}]
    OUT p_order_id INTEGER,
    OUT p_message TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_item JSONB;
    v_product_id INTEGER;
    v_quantity INTEGER;
    v_price DECIMAL(10, 2);
    v_stock INTEGER;
    v_total DECIMAL(10, 2) := 0;
BEGIN
    -- Iniciar transacción explícita
    BEGIN
        -- 1. Crear orden con total inicial 0
        INSERT INTO orders (user_id, total, status, created_at, updated_at)
        VALUES (p_user_id, 0, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        RETURNING id INTO p_order_id;
        
        -- 2. Procesar cada item
        FOR v_item IN SELECT * FROM jsonb_array_elements(p_items)
        LOOP
            v_product_id := (v_item->>'product_id')::INTEGER;
            v_quantity := (v_item->>'quantity')::INTEGER;
            
            -- 3. Validar stock disponible
            SELECT stock, price INTO v_stock, v_price
            FROM products
            WHERE id = v_product_id AND active = TRUE;
            
            IF NOT FOUND THEN
                RAISE EXCEPTION 'Producto ID % no existe o no está activo', v_product_id;
            END IF;
            
            IF v_stock < v_quantity THEN
                RAISE EXCEPTION 'Stock insuficiente para producto ID %. Disponible: %, Solicitado: %',
                    v_product_id, v_stock, v_quantity;
            END IF;
            
            -- 4. Insertar order_item
            INSERT INTO order_items (order_id, product_id, quantity, price)
            VALUES (p_order_id, v_product_id, v_quantity, v_price);
            
            -- 5. Reducir stock
            UPDATE products
            SET stock = stock - v_quantity
            WHERE id = v_product_id;
            
            -- 6. Acumular total
            v_total := v_total + (v_price * v_quantity);
        END LOOP;
        
        -- 7. Actualizar total de la orden
        UPDATE orders
        SET total = v_total
        WHERE id = p_order_id;
        
        -- 8. Commit automático al salir
        p_message := 'Orden creada exitosamente';
        
    EXCEPTION
        WHEN OTHERS THEN
            -- Rollback automático al hacer RAISE
            RAISE;
    END;
END;
$$;
```

**Uso desde SQL:**
```sql
-- Llamar procedimiento
DO $$
DECLARE
    v_order_id INTEGER;
    v_message TEXT;
BEGIN
    CALL sp_create_order(
        p_user_id := 2,
        p_items := '[
            {"product_id": 1, "quantity": 2},
            {"product_id": 3, "quantity": 1}
        ]'::JSONB,
        p_order_id := v_order_id,
        p_message := v_message
    );
    
    RAISE NOTICE 'Order ID: %, Message: %', v_order_id, v_message;
END;
$$;
```

**Uso desde Java (Spring Boot):**
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Procedure(name = "sp_create_order")
    Map<String, Object> createOrder(
        @Param("p_user_id") Long userId,
        @Param("p_items") String itemsJson
    );
}

// Service
public OrderResponse createOrder(Long userId, List<OrderItemRequest> items) {
    String itemsJson = objectMapper.writeValueAsString(items);
    Map<String, Object> result = orderRepository.createOrder(userId, itemsJson);
    
    Long orderId = (Long) result.get("p_order_id");
    String message = (String) result.get("p_message");
    
    return new OrderResponse(orderId, message);
}
```

---

### Procedimiento 2: Cancelar Orden y Restaurar Stock

**Problema:** Al cancelar orden, se debe:
1. Cambiar status a CANCELLED
2. Restaurar stock de todos los productos
3. Registrar en log de auditoría

**Solución:**
```sql
CREATE OR REPLACE PROCEDURE sp_cancel_order(
    p_order_id INTEGER,
    p_reason TEXT DEFAULT NULL,
    OUT p_success BOOLEAN,
    OUT p_message TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_current_status VARCHAR(20);
    v_item RECORD;
BEGIN
    -- Verificar que orden existe
    SELECT status INTO v_current_status
    FROM orders
    WHERE id = p_order_id;
    
    IF NOT FOUND THEN
        p_success := FALSE;
        p_message := 'Orden no encontrada';
        RETURN;
    END IF;
    
    -- Verificar que puede ser cancelada
    IF v_current_status = 'CANCELLED' THEN
        p_success := FALSE;
        p_message := 'Orden ya está cancelada';
        RETURN;
    END IF;
    
    IF v_current_status = 'COMPLETED' THEN
        p_success := FALSE;
        p_message := 'No se puede cancelar orden completada';
        RETURN;
    END IF;
    
    BEGIN
        -- 1. Actualizar status
        UPDATE orders
        SET status = 'CANCELLED', updated_at = CURRENT_TIMESTAMP
        WHERE id = p_order_id;
        
        -- 2. Restaurar stock de cada producto
        FOR v_item IN 
            SELECT product_id, quantity 
            FROM order_items 
            WHERE order_id = p_order_id
        LOOP
            UPDATE products
            SET stock = stock + v_item.quantity
            WHERE id = v_item.product_id;
        END LOOP;
        
        -- 3. Registrar en log (asumiendo tabla order_cancellations)
        INSERT INTO order_cancellations (order_id, reason, cancelled_at)
        VALUES (p_order_id, COALESCE(p_reason, 'Sin razón especificada'), CURRENT_TIMESTAMP);
        
        p_success := TRUE;
        p_message := 'Orden cancelada y stock restaurado exitosamente';
        
    EXCEPTION
        WHEN OTHERS THEN
            p_success := FALSE;
            p_message := 'Error al cancelar orden: ' || SQLERRM;
            RAISE;
    END;
END;
$$;
```

**Uso:**
```sql
DO $$
DECLARE
    v_success BOOLEAN;
    v_message TEXT;
BEGIN
    CALL sp_cancel_order(
        p_order_id := 5,
        p_reason := 'Cliente solicitó cancelación',
        p_success := v_success,
        p_message := v_message
    );
    
    RAISE NOTICE 'Success: %, Message: %', v_success, v_message;
END;
$$;
```

---

### Procedimiento 3: Aplicar Descuento Masivo

**Problema:** Promociones requieren aplicar descuento a múltiples productos.

**Solución:**
```sql
CREATE OR REPLACE PROCEDURE sp_apply_bulk_discount(
    p_category_id INTEGER DEFAULT NULL,
    p_product_ids INTEGER[] DEFAULT NULL,
    p_discount_percentage DECIMAL(5, 2),
    OUT p_affected_count INTEGER,
    OUT p_message TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Validar descuento
    IF p_discount_percentage < 0 OR p_discount_percentage > 100 THEN
        RAISE EXCEPTION 'Descuento debe estar entre 0 y 100';
    END IF;
    
    BEGIN
        -- Aplicar por categoría
        IF p_category_id IS NOT NULL THEN
            UPDATE products
            SET discount = p_discount_percentage,
                updated_at = CURRENT_TIMESTAMP
            WHERE category_id = p_category_id AND active = TRUE;
            
            GET DIAGNOSTICS p_affected_count = ROW_COUNT;
            p_message := 'Descuento aplicado a ' || p_affected_count || ' productos de categoría ' || p_category_id;
        
        -- Aplicar por lista de productos
        ELSIF p_product_ids IS NOT NULL THEN
            UPDATE products
            SET discount = p_discount_percentage,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ANY(p_product_ids) AND active = TRUE;
            
            GET DIAGNOSTICS p_affected_count = ROW_COUNT;
            p_message := 'Descuento aplicado a ' || p_affected_count || ' productos específicos';
        
        ELSE
            RAISE EXCEPTION 'Debe especificar category_id o product_ids';
        END IF;
        
    EXCEPTION
        WHEN OTHERS THEN
            p_affected_count := 0;
            p_message := 'Error: ' || SQLERRM;
            RAISE;
    END;
END;
$$;
```

**Uso:**
```sql
-- Descuento a categoría
DO $$
DECLARE
    v_count INTEGER;
    v_msg TEXT;
BEGIN
    CALL sp_apply_bulk_discount(
        p_category_id := 2,  -- Juguetes
        p_discount_percentage := 15.0,
        p_affected_count := v_count,
        p_message := v_msg
    );
    RAISE NOTICE '%', v_msg;
END;
$$;

-- Descuento a productos específicos
DO $$
DECLARE
    v_count INTEGER;
    v_msg TEXT;
BEGIN
    CALL sp_apply_bulk_discount(
        p_product_ids := ARRAY[1, 5, 8, 12],
        p_discount_percentage := 20.0,
        p_affected_count := v_count,
        p_message := v_msg
    );
    RAISE NOTICE '%', v_msg;
END;
$$;
```

---

### Procedimiento 4: Generar Reporte de Ventas

**Problema:** Generar reportes complejos con múltiples agregaciones.

**Solución:**
```sql
CREATE OR REPLACE PROCEDURE sp_generate_sales_report(
    p_start_date DATE,
    p_end_date DATE,
    OUT p_total_orders INTEGER,
    OUT p_total_revenue DECIMAL(10, 2),
    OUT p_average_order DECIMAL(10, 2),
    OUT p_top_product_id INTEGER,
    OUT p_top_product_name VARCHAR(200),
    OUT p_top_customer_id INTEGER,
    OUT p_top_customer_name VARCHAR(100)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_top_product RECORD;
    v_top_customer RECORD;
BEGIN
    -- Validar fechas
    IF p_start_date > p_end_date THEN
        RAISE EXCEPTION 'Fecha inicio no puede ser mayor que fecha fin';
    END IF;
    
    -- 1. Estadísticas generales
    SELECT 
        COUNT(*),
        COALESCE(SUM(total), 0),
        COALESCE(AVG(total), 0)
    INTO p_total_orders, p_total_revenue, p_average_order
    FROM orders
    WHERE status = 'COMPLETED'
        AND DATE(created_at) BETWEEN p_start_date AND p_end_date;
    
    -- 2. Producto más vendido
    SELECT p.id, p.name, SUM(oi.quantity) as total_sold
    INTO v_top_product
    FROM products p
    INNER JOIN order_items oi ON p.id = oi.product_id
    INNER JOIN orders o ON oi.order_id = o.id
    WHERE o.status = 'COMPLETED'
        AND DATE(o.created_at) BETWEEN p_start_date AND p_end_date
    GROUP BY p.id, p.name
    ORDER BY total_sold DESC
    LIMIT 1;
    
    p_top_product_id := v_top_product.id;
    p_top_product_name := v_top_product.name;
    
    -- 3. Cliente que más gastó
    SELECT u.id, u.name, SUM(o.total) as total_spent
    INTO v_top_customer
    FROM users u
    INNER JOIN orders o ON u.id = o.user_id
    WHERE o.status = 'COMPLETED'
        AND DATE(o.created_at) BETWEEN p_start_date AND p_end_date
    GROUP BY u.id, u.name
    ORDER BY total_spent DESC
    LIMIT 1;
    
    p_top_customer_id := v_top_customer.id;
    p_top_customer_name := v_top_customer.name;
    
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'Error generando reporte: %', SQLERRM;
        RAISE;
END;
$$;
```

**Uso:**
```sql
DO $$
DECLARE
    v_total_orders INTEGER;
    v_revenue DECIMAL(10, 2);
    v_avg DECIMAL(10, 2);
    v_prod_id INTEGER;
    v_prod_name VARCHAR(200);
    v_cust_id INTEGER;
    v_cust_name VARCHAR(100);
BEGIN
    CALL sp_generate_sales_report(
        p_start_date := '2024-01-01',
        p_end_date := '2024-01-31',
        p_total_orders := v_total_orders,
        p_total_revenue := v_revenue,
        p_average_order := v_avg,
        p_top_product_id := v_prod_id,
        p_top_product_name := v_prod_name,
        p_top_customer_id := v_cust_id,
        p_top_customer_name := v_cust_name
    );
    
    RAISE NOTICE '=== REPORTE DE VENTAS ===';
    RAISE NOTICE 'Total órdenes: %', v_total_orders;
    RAISE NOTICE 'Ingresos totales: $%', v_revenue;
    RAISE NOTICE 'Orden promedio: $%', v_avg;
    RAISE NOTICE 'Producto top: % (ID: %)', v_prod_name, v_prod_id;
    RAISE NOTICE 'Cliente top: % (ID: %)', v_cust_name, v_cust_id;
END;
$$;
```

---

### Procedimiento 5: Limpieza de Órdenes Antiguas

**Problema:** Órdenes pendientes muy antiguas deben ser canceladas.

**Solución:**
```sql
CREATE OR REPLACE PROCEDURE sp_cleanup_old_orders(
    p_days_old INTEGER DEFAULT 30,
    OUT p_cancelled_count INTEGER,
    OUT p_message TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_order RECORD;
    v_errors TEXT := '';
BEGIN
    p_cancelled_count := 0;
    
    BEGIN
        -- Buscar órdenes pendientes antiguas
        FOR v_order IN
            SELECT id
            FROM orders
            WHERE status = 'PENDING'
                AND created_at < CURRENT_TIMESTAMP - (p_days_old || ' days')::INTERVAL
        LOOP
            BEGIN
                -- Usar procedimiento de cancelación
                CALL sp_cancel_order(
                    p_order_id := v_order.id,
                    p_reason := 'Cancelación automática por antigüedad'
                );
                
                p_cancelled_count := p_cancelled_count + 1;
                
            EXCEPTION
                WHEN OTHERS THEN
                    v_errors := v_errors || 'Error en orden ' || v_order.id || ': ' || SQLERRM || '; ';
            END;
        END LOOP;
        
        IF p_cancelled_count = 0 THEN
            p_message := 'No hay órdenes antiguas para cancelar';
        ELSE
            p_message := 'Se cancelaron ' || p_cancelled_count || ' órdenes';
            IF v_errors != '' THEN
                p_message := p_message || '. Errores: ' || v_errors;
            END IF;
        END IF;
        
    EXCEPTION
        WHEN OTHERS THEN
            p_message := 'Error en limpieza: ' || SQLERRM;
            RAISE;
    END;
END;
$$;
```

**Uso (ejecutar con cron job):**
```sql
-- Ejecutar diariamente
DO $$
DECLARE
    v_count INTEGER;
    v_msg TEXT;
BEGIN
    CALL sp_cleanup_old_orders(
        p_days_old := 30,
        p_cancelled_count := v_count,
        p_message := v_msg
    );
    RAISE NOTICE '%', v_msg;
END;
$$;
```

---

### Procedimiento 6: Actualizar Precios con Incremento

**Problema:** Ajustar precios por inflación o cambio de proveedor.

**Solución:**
```sql
CREATE OR REPLACE PROCEDURE sp_update_prices(
    p_category_id INTEGER DEFAULT NULL,
    p_percentage_change DECIMAL(5, 2),
    p_round_to_hundreds BOOLEAN DEFAULT TRUE,
    OUT p_affected_count INTEGER,
    OUT p_message TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_new_price DECIMAL(10, 2);
BEGIN
    BEGIN
        IF p_category_id IS NOT NULL THEN
            -- Actualizar por categoría
            IF p_round_to_hundreds THEN
                UPDATE products
                SET price = ROUND((price * (1 + p_percentage_change / 100.0)) / 100) * 100,
                    updated_at = CURRENT_TIMESTAMP
                WHERE category_id = p_category_id AND active = TRUE;
            ELSE
                UPDATE products
                SET price = ROUND(price * (1 + p_percentage_change / 100.0), 2),
                    updated_at = CURRENT_TIMESTAMP
                WHERE category_id = p_category_id AND active = TRUE;
            END IF;
        ELSE
            -- Actualizar todos
            IF p_round_to_hundreds THEN
                UPDATE products
                SET price = ROUND((price * (1 + p_percentage_change / 100.0)) / 100) * 100,
                    updated_at = CURRENT_TIMESTAMP
                WHERE active = TRUE;
            ELSE
                UPDATE products
                SET price = ROUND(price * (1 + p_percentage_change / 100.0), 2),
                    updated_at = CURRENT_TIMESTAMP
                WHERE active = TRUE;
            END IF;
        END IF;
        
        GET DIAGNOSTICS p_affected_count = ROW_COUNT;
        p_message := 'Precios actualizados: ' || p_affected_count || ' productos';
        
    EXCEPTION
        WHEN OTHERS THEN
            p_affected_count := 0;
            p_message := 'Error: ' || SQLERRM;
            RAISE;
    END;
END;
$$;
```

**Uso:**
```sql
-- Incrementar 10% todos los productos
DO $$
DECLARE
    v_count INTEGER;
    v_msg TEXT;
BEGIN
    CALL sp_update_prices(
        p_percentage_change := 10.0,
        p_round_to_hundreds := TRUE,
        p_affected_count := v_count,
        p_message := v_msg
    );
    RAISE NOTICE '%', v_msg;
END;
$$;

-- Ejemplo: Precio $45,678 + 10% = $50,245 → redondeado a $50,200
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es un procedimiento almacenado?"**

> "Un procedimiento almacenado es un conjunto de instrucciones SQL guardadas en la base de datos:
> - Se define una vez y se ejecuta con CALL
> - Puede tener parámetros de entrada (IN) y salida (OUT)
> - Contiene lógica compleja (IF, LOOP, transacciones)
> - Se ejecuta en el servidor de base de datos
> 
> Ejemplo:
> ```sql
> CREATE PROCEDURE sp_create_order(p_user_id INT, p_items JSONB)
> AS $$
> BEGIN
>     -- Crear orden, insertar items, reducir stock
>     -- Todo en una transacción
> END;
> $$ LANGUAGE plpgsql;
> 
> -- Ejecutar:
> CALL sp_create_order(2, '[{...}]');
> ```"

---

**2. "¿Cuál es la diferencia entre procedimiento y función?"**

> "**Procedimiento:**
> - Se ejecuta con `CALL`
> - Puede tener múltiples parámetros OUT
> - Puede hacer COMMIT/ROLLBACK
> - No retorna valor directo (usa OUT params)
> - Usado para operaciones complejas
> 
> **Función:**
> - Se ejecuta con `SELECT`
> - Retorna UN solo valor (RETURNS)
> - No puede hacer COMMIT/ROLLBACK
> - Se puede usar en queries (SELECT, WHERE)
> - Usado para cálculos y transformaciones
> 
> Baby Cash:
> - Procedimiento: `sp_create_order()` - crea orden completa
> - Función: `calculate_discounted_price()` - calcula precio"

---

**3. "¿Por qué usar transacciones en procedimientos?"**

> "Las transacciones garantizan que operaciones múltiples sean **atómicas**:
> 
> Ejemplo: Crear orden
> 1. INSERT en orders
> 2. INSERT en order_items
> 3. UPDATE stock en products
> 
> **Sin transacción:**
> - Si paso 3 falla, orden queda incompleta
> - Stock no se reduce, pero orden existe
> - Datos inconsistentes
> 
> **Con transacción:**
> - Si cualquier paso falla, TODO se revierte (ROLLBACK)
> - O TODO se guarda (COMMIT)
> - Garantiza consistencia
> 
> ```sql
> BEGIN
>     -- operaciones
>     COMMIT;  -- Todo OK
> EXCEPTION
>     WHEN OTHERS THEN
>         ROLLBACK;  -- Revertir todo
> END;
> ```"

---

**4. "¿Cuándo usar procedimiento vs hacer todo en el backend (Java/Node)?"**

> "Usa **Procedimiento** cuando:
> - Operación involucra múltiples tablas
> - Necesitas transacción atómica
> - Lógica se reutiliza en múltiples aplicaciones
> - Performance es crítico (menos tráfico red)
> 
> Usa **Backend** cuando:
> - Lógica es simple (un INSERT)
> - Necesitas flexibilidad (cambios frecuentes)
> - Interactúas con servicios externos (APIs, email)
> - Lógica de negocio compleja (no solo datos)
> 
> **Baby Cash:**
> - Procedimiento: `sp_create_order()` (múltiples tablas, transacción)
> - Backend: Validar tarjeta de crédito (interactúa con API externa)"

---

## 📝 Resumen de Procedimientos en Baby Cash

| Procedimiento | Parámetros IN | Parámetros OUT | Propósito |
|---------------|---------------|----------------|-----------|
| `sp_create_order` | user_id, items | order_id, message | Crear orden completa con items |
| `sp_cancel_order` | order_id, reason | success, message | Cancelar y restaurar stock |
| `sp_apply_bulk_discount` | category/products, discount% | affected_count, message | Aplicar descuentos masivos |
| `sp_generate_sales_report` | start_date, end_date | múltiples estadísticas | Generar reporte de ventas |
| `sp_cleanup_old_orders` | days_old | cancelled_count, message | Cancelar órdenes antiguas |
| `sp_update_prices` | category, percentage, round | affected_count, message | Ajustar precios por inflación |

---

## 🔧 Comandos Útiles

### Crear Procedimiento
```sql
CREATE OR REPLACE PROCEDURE nombre(params)
LANGUAGE plpgsql
AS $$
BEGIN
    -- código
END;
$$;
```

### Ejecutar Procedimiento
```sql
CALL nombre_procedimiento(valor1, valor2);

-- Con parámetros OUT
DO $$
DECLARE
    v_result INTEGER;
BEGIN
    CALL proc(IN param1, OUT v_result);
    RAISE NOTICE 'Result: %', v_result;
END;
$$;
```

### Ver Procedimientos
```sql
SELECT proname, pg_get_functiondef(oid)
FROM pg_proc
WHERE prokind = 'p'  -- 'p' = procedure
    AND pronamespace = 'public'::regnamespace;
```

### Eliminar Procedimiento
```sql
DROP PROCEDURE IF EXISTS nombre_procedimiento;
```

---

## 🚀 Conclusión

**Procedimientos Almacenados en Baby Cash:**
- ✅ Garantizan atomicidad con transacciones
- ✅ Simplifican operaciones complejas
- ✅ Mejoran performance
- ✅ Centralizan lógica reutilizable

**Son la "lógica de negocio" que vive en la base de datos.**

---

**Ahora lee:** `FUNCIONES-DB.md` para cálculos y transformaciones. 🚀
