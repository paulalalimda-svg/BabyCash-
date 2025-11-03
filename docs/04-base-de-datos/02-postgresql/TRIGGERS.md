# TRIGGERS - BASE DE DATOS BABY CASH

## 🎯 ¿Qué es un Trigger?

Un **trigger** (o disparador) es un procedimiento almacenado que se ejecuta **automáticamente** cuando ocurre un evento específico en la base de datos.

### Analogía Simple
> **Imagina un timbre automático en una tienda:**
> - Cuando alguien abre la puerta (EVENTO)
> - El timbre suena automáticamente (TRIGGER)
> - Sin que nadie presione un botón manualmente
> 
> En base de datos:
> - Cuando insertas/actualizas/eliminas un registro (EVENTO)
> - El trigger ejecuta código automáticamente (ACCIÓN)

---

## 📋 Tipos de Triggers

### 1️⃣ **BEFORE Triggers**
Se ejecutan **antes** de que ocurra el evento.

**Usos comunes:**
- Validar datos antes de insertar
- Modificar valores antes de guardar
- Prevenir operaciones no permitidas

---

### 2️⃣ **AFTER Triggers**
Se ejecutan **después** de que ocurra el evento.

**Usos comunes:**
- Registrar auditoría
- Actualizar tablas relacionadas
- Enviar notificaciones

---

### 3️⃣ **INSTEAD OF Triggers**
Reemplazan completamente la operación original.

**Usos comunes:**
- Triggers en vistas
- Lógica compleja de inserción

---

## 🛠️ Triggers en Baby Cash

### Trigger 1: Actualizar `updated_at` Automáticamente

**Problema:** Necesitamos actualizar `updated_at` cada vez que modificamos un registro.

**Solución:** Trigger que actualiza automáticamente antes de UPDATE.

```sql
-- Función reutilizable para actualizar updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger en tabla users
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Trigger en tabla products
CREATE TRIGGER update_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Trigger en tabla orders
CREATE TRIGGER update_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

**Uso:**
```sql
-- Antes (sin trigger)
UPDATE users SET name = 'Juan Carlos', updated_at = CURRENT_TIMESTAMP WHERE id = 1;

-- Ahora (con trigger)
UPDATE users SET name = 'Juan Carlos' WHERE id = 1;
-- updated_at se actualiza automáticamente ✅
```

**Explicación:**
- `BEFORE UPDATE`: Se ejecuta antes del UPDATE
- `FOR EACH ROW`: Se ejecuta por cada fila afectada
- `NEW.updated_at`: Modifica el valor que se va a guardar
- `RETURN NEW`: Devuelve la fila modificada

---

### Trigger 2: Validar Stock Antes de Venta

**Problema:** Prevenir ventas si no hay stock suficiente.

**Solución:** Trigger que valida stock antes de crear order_item.

```sql
CREATE OR REPLACE FUNCTION validate_product_stock()
RETURNS TRIGGER AS $$
DECLARE
    available_stock INTEGER;
BEGIN
    -- Obtener stock actual del producto
    SELECT stock INTO available_stock
    FROM products
    WHERE id = NEW.product_id;
    
    -- Validar que hay stock suficiente
    IF available_stock < NEW.quantity THEN
        RAISE EXCEPTION 'Stock insuficiente para producto ID %. Disponible: %, Requerido: %',
            NEW.product_id, available_stock, NEW.quantity;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_stock_before_order_item
    BEFORE INSERT ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION validate_product_stock();
```

**Comportamiento:**
```sql
-- Producto tiene stock = 5

-- Intento 1: Comprar 3 unidades
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES (1, 10, 3, 45000);
-- ✅ ÉXITO: Stock suficiente

-- Intento 2: Comprar 10 unidades
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES (1, 10, 10, 45000);
-- ❌ ERROR: Stock insuficiente para producto ID 10. Disponible: 5, Requerido: 10
```

---

### Trigger 3: Actualizar Stock al Crear Orden

**Problema:** Reducir stock automáticamente al crear order_item.

**Solución:** Trigger que actualiza stock después de INSERT.

```sql
CREATE OR REPLACE FUNCTION decrease_product_stock()
RETURNS TRIGGER AS $$
BEGIN
    -- Reducir stock del producto
    UPDATE products
    SET stock = stock - NEW.quantity
    WHERE id = NEW.product_id;
    
    -- Verificar que stock no quedó negativo (seguridad adicional)
    IF (SELECT stock FROM products WHERE id = NEW.product_id) < 0 THEN
        RAISE EXCEPTION 'Error: Stock resultante es negativo para producto ID %', NEW.product_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_stock_after_order_item
    AFTER INSERT ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION decrease_product_stock();
```

**Flujo completo:**
```sql
-- Estado inicial: Producto 10 tiene stock = 50

-- Se crea order_item
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES (1, 10, 5, 45000);

-- Triggers se ejecutan en orden:
-- 1. BEFORE INSERT: check_stock_before_order_item valida (50 >= 5) ✅
-- 2. INSERT: Se inserta el order_item
-- 3. AFTER INSERT: update_stock_after_order_item reduce stock
--    UPDATE products SET stock = 50 - 5 WHERE id = 10;

-- Estado final: Producto 10 tiene stock = 45
```

---

### Trigger 4: Restaurar Stock al Cancelar Orden

**Problema:** Devolver stock si se cancela una orden.

**Solución:** Trigger que restaura stock al cambiar status a CANCELLED.

```sql
CREATE OR REPLACE FUNCTION restore_stock_on_cancel()
RETURNS TRIGGER AS $$
BEGIN
    -- Solo si el status cambió a CANCELLED
    IF OLD.status != 'CANCELLED' AND NEW.status = 'CANCELLED' THEN
        -- Restaurar stock de todos los items de la orden
        UPDATE products p
        SET stock = stock + oi.quantity
        FROM order_items oi
        WHERE oi.order_id = NEW.id
          AND oi.product_id = p.id;
        
        RAISE NOTICE 'Stock restaurado para orden %', NEW.id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER restore_stock_when_order_cancelled
    AFTER UPDATE ON orders
    FOR EACH ROW
    WHEN (NEW.status = 'CANCELLED')
    EXECUTE FUNCTION restore_stock_on_cancel();
```

**Ejemplo:**
```sql
-- Orden 1 tiene:
-- - Item 1: product_id=10, quantity=5 (stock actual: 45)
-- - Item 2: product_id=11, quantity=3 (stock actual: 20)

-- Cancelar orden
UPDATE orders SET status = 'CANCELLED' WHERE id = 1;

-- Trigger restaura stock:
-- - Producto 10: stock = 45 + 5 = 50
-- - Producto 11: stock = 20 + 3 = 23
```

---

### Trigger 5: Auditoría de Cambios en Productos

**Problema:** Registrar quién y cuándo modificó productos.

**Solución:** Trigger que guarda historial en tabla de auditoría.

```sql
-- Tabla de auditoría
CREATE TABLE products_audit (
    audit_id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    action VARCHAR(10) NOT NULL,  -- INSERT, UPDATE, DELETE
    old_name VARCHAR(200),
    new_name VARCHAR(200),
    old_price DECIMAL(10, 2),
    new_price DECIMAL(10, 2),
    old_stock INTEGER,
    new_stock INTEGER,
    changed_by VARCHAR(150),  -- Email del usuario que hizo el cambio
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Función de auditoría
CREATE OR REPLACE FUNCTION audit_product_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO products_audit (
            product_id, action, new_name, new_price, new_stock
        ) VALUES (
            NEW.id, 'INSERT', NEW.name, NEW.price, NEW.stock
        );
        RETURN NEW;
        
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO products_audit (
            product_id, action,
            old_name, new_name,
            old_price, new_price,
            old_stock, new_stock
        ) VALUES (
            NEW.id, 'UPDATE',
            OLD.name, NEW.name,
            OLD.price, NEW.price,
            OLD.stock, NEW.stock
        );
        RETURN NEW;
        
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO products_audit (
            product_id, action, old_name, old_price, old_stock
        ) VALUES (
            OLD.id, 'DELETE', OLD.name, OLD.price, OLD.stock
        );
        RETURN OLD;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Triggers para INSERT, UPDATE y DELETE
CREATE TRIGGER audit_product_insert
    AFTER INSERT ON products
    FOR EACH ROW
    EXECUTE FUNCTION audit_product_changes();

CREATE TRIGGER audit_product_update
    AFTER UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION audit_product_changes();

CREATE TRIGGER audit_product_delete
    AFTER DELETE ON products
    FOR EACH ROW
    EXECUTE FUNCTION audit_product_changes();
```

**Uso:**
```sql
-- Se crea producto
INSERT INTO products (name, price, stock, category_id)
VALUES ('Biberón Nuevo', 50000, 30, 3);
-- Registro en products_audit: action='INSERT', new_name='Biberón Nuevo'

-- Se actualiza precio
UPDATE products SET price = 45000 WHERE id = 100;
-- Registro en products_audit: action='UPDATE', old_price=50000, new_price=45000

-- Se elimina producto
DELETE FROM products WHERE id = 100;
-- Registro en products_audit: action='DELETE', old_name='Biberón Nuevo'

-- Consultar historial
SELECT * FROM products_audit WHERE product_id = 100 ORDER BY changed_at;
```

---

### Trigger 6: Validar Email Antes de Insertar Usuario

**Problema:** Asegurar que email tiene formato válido.

**Solución:** Trigger que valida formato antes de INSERT/UPDATE.

```sql
CREATE OR REPLACE FUNCTION validate_user_email()
RETURNS TRIGGER AS $$
BEGIN
    -- Validar que email contiene @
    IF NEW.email NOT LIKE '%@%' THEN
        RAISE EXCEPTION 'Email inválido: debe contener @';
    END IF;
    
    -- Validar que email tiene punto después de @
    IF NEW.email NOT LIKE '%@%.%' THEN
        RAISE EXCEPTION 'Email inválido: formato incorrecto';
    END IF;
    
    -- Convertir email a minúsculas
    NEW.email = LOWER(NEW.email);
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER validate_email_before_user_insert
    BEFORE INSERT OR UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION validate_user_email();
```

**Comportamiento:**
```sql
-- Intento 1
INSERT INTO users (name, email, password) VALUES ('Juan', 'juan', 'pass123');
-- ❌ ERROR: Email inválido: debe contener @

-- Intento 2
INSERT INTO users (name, email, password) VALUES ('Juan', 'juan@email', 'pass123');
-- ❌ ERROR: Email inválido: formato incorrecto

-- Intento 3
INSERT INTO users (name, email, password) VALUES ('Juan', 'JUAN@EMAIL.COM', 'pass123');
-- ✅ ÉXITO: Email guardado como 'juan@email.com' (minúsculas automáticamente)
```

---

### Trigger 7: Calcular Total de Orden Automáticamente

**Problema:** Garantizar que order.total sea correcto al agregar/modificar items.

**Solución:** Trigger que recalcula total después de cambios en order_items.

```sql
CREATE OR REPLACE FUNCTION recalculate_order_total()
RETURNS TRIGGER AS $$
DECLARE
    order_id_to_update INTEGER;
    new_total DECIMAL(10, 2);
BEGIN
    -- Determinar qué orden actualizar
    IF TG_OP = 'DELETE' THEN
        order_id_to_update := OLD.order_id;
    ELSE
        order_id_to_update := NEW.order_id;
    END IF;
    
    -- Calcular nuevo total
    SELECT COALESCE(SUM(quantity * price), 0)
    INTO new_total
    FROM order_items
    WHERE order_id = order_id_to_update;
    
    -- Actualizar orden
    UPDATE orders
    SET total = new_total,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = order_id_to_update;
    
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_order_total_on_item_change
    AFTER INSERT OR UPDATE OR DELETE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION recalculate_order_total();
```

**Flujo:**
```sql
-- Orden 1 tiene total = 0

-- Agregar item
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES (1, 10, 2, 45000);
-- Trigger recalcula: total = 2 * 45000 = 90000
-- UPDATE orders SET total = 90000 WHERE id = 1;

-- Agregar otro item
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES (1, 11, 1, 28000);
-- Trigger recalcula: total = 90000 + 28000 = 118000
-- UPDATE orders SET total = 118000 WHERE id = 1;

-- Eliminar item
DELETE FROM order_items WHERE order_id = 1 AND product_id = 10;
-- Trigger recalcula: total = 28000
-- UPDATE orders SET total = 28000 WHERE id = 1;
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es un trigger y para qué sirve?"**

> "Un trigger es código que se ejecuta automáticamente cuando ocurre un evento:
> - **Evento**: INSERT, UPDATE o DELETE en una tabla
> - **Ejecución automática**: No necesitas llamarlo manualmente
> - **Usos**: Validaciones, auditoría, actualizar tablas relacionadas
> 
> Ejemplo: Al insertar order_item, trigger reduce stock automáticamente."

---

**2. "¿Cuál es la diferencia entre BEFORE y AFTER?"**

> "**BEFORE**: Se ejecuta ANTES de guardar los datos
> - Puedes modificar datos antes de guardar (NEW.campo = valor)
> - Puedes cancelar operación (RAISE EXCEPTION)
> - Ejemplo: Validar email antes de INSERT
> 
> **AFTER**: Se ejecuta DESPUÉS de guardar los datos
> - No puedes modificar datos ya guardados
> - Puedes actualizar otras tablas
> - Ejemplo: Registrar auditoría después de UPDATE"

---

**3. "¿Qué es NEW y OLD en triggers?"**

> "Son variables especiales que contienen datos de la fila:
> - **NEW**: Datos NUEVOS (después de INSERT/UPDATE)
> - **OLD**: Datos VIEJOS (antes de UPDATE/DELETE)
> 
> ```sql
> -- En UPDATE
> OLD.price = 45000  -- Precio antes del cambio
> NEW.price = 50000  -- Precio después del cambio
> 
> -- En INSERT
> NEW.name = 'Biberón'  -- Solo NEW existe
> 
> -- En DELETE
> OLD.name = 'Biberón'  -- Solo OLD existe
> ```"

---

**4. "¿Pueden los triggers causar problemas?"**

> "Sí, si no se usan correctamente:
> 
> **Problemas comunes:**
> - **Triggers recursivos**: Trigger A actualiza tabla B, trigger B actualiza tabla A → loop infinito
> - **Performance**: Triggers lentos ralentizan todas las operaciones
> - **Debugging**: Errores en triggers son difíciles de rastrear
> 
> **Buenas prácticas:**
> - Mantener triggers simples y rápidos
> - Evitar lógica compleja
> - Documentar bien qué hace cada trigger
> - Usar RAISE NOTICE para debugging"

---

## 📝 Lista Completa de Triggers en Baby Cash

| Trigger | Tabla | Evento | Tipo | Función |
|---------|-------|--------|------|---------|
| `update_users_updated_at` | users | UPDATE | BEFORE | Actualizar timestamp |
| `update_products_updated_at` | products | UPDATE | BEFORE | Actualizar timestamp |
| `update_orders_updated_at` | orders | UPDATE | BEFORE | Actualizar timestamp |
| `check_stock_before_order_item` | order_items | INSERT | BEFORE | Validar stock disponible |
| `update_stock_after_order_item` | order_items | INSERT | AFTER | Reducir stock |
| `restore_stock_when_order_cancelled` | orders | UPDATE | AFTER | Restaurar stock al cancelar |
| `audit_product_insert` | products | INSERT | AFTER | Registrar auditoría |
| `audit_product_update` | products | UPDATE | AFTER | Registrar auditoría |
| `audit_product_delete` | products | DELETE | AFTER | Registrar auditoría |
| `validate_email_before_user_insert` | users | INSERT/UPDATE | BEFORE | Validar email |
| `update_order_total_on_item_change` | order_items | INSERT/UPDATE/DELETE | AFTER | Recalcular total |

---

## 🔧 Comandos Útiles

### Ver Triggers de una Tabla
```sql
SELECT trigger_name, event_manipulation, action_timing
FROM information_schema.triggers
WHERE event_object_table = 'products';
```

### Desactivar Trigger Temporalmente
```sql
ALTER TABLE products DISABLE TRIGGER audit_product_update;
```

### Reactivar Trigger
```sql
ALTER TABLE products ENABLE TRIGGER audit_product_update;
```

### Eliminar Trigger
```sql
DROP TRIGGER IF EXISTS audit_product_update ON products;
```

---

## 🚀 Conclusión

**Triggers en Baby Cash:**
- ✅ Automatizan tareas repetitivas
- ✅ Garantizan integridad de datos
- ✅ Mantienen auditoría completa
- ✅ Sincronizan tablas relacionadas

**Son el "piloto automático" de la base de datos.**

---

**Ahora lee:** `VISTAS.md` para queries reutilizables. 🚀
