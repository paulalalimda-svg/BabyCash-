# TRANSACCIONES Y ACID - BABY CASH

## 🎯 ¿Qué es una Transacción?

Una **transacción** es un conjunto de operaciones SQL que se ejecutan como una **unidad atómica**: todas se completan o ninguna se ejecuta.

### Analogía Simple
> **Imagina transferir dinero entre cuentas bancarias:**
> 
> **Operación 1:** Restar $100 de cuenta A  
> **Operación 2:** Sumar $100 a cuenta B
> 
> **Sin transacción:**
> - Si operación 1 funciona pero operación 2 falla
> - Resultado: ¡$100 desaparecieron! ❌
> 
> **Con transacción:**
> - Ambas operaciones se hacen juntas
> - Si cualquiera falla, AMBAS se revierten
> - Resultado: Dinero seguro ✅

---

## 💡 Propiedades ACID

Las transacciones garantizan 4 propiedades (ACID):

### A - Atomicity (Atomicidad)
**"Todo o nada"**
- Todas las operaciones se completan o ninguna
- No hay estados intermedios

```sql
BEGIN;
    UPDATE cuentas SET saldo = saldo - 100 WHERE id = 1;
    UPDATE cuentas SET saldo = saldo + 100 WHERE id = 2;
COMMIT;  -- Ambas se guardan

-- Si falla alguna:
ROLLBACK;  -- Ninguna se guarda
```

---

### C - Consistency (Consistencia)
**"De estado válido a estado válido"**
- Base de datos siempre está en estado consistente
- Se respetan constraints (CHECK, FOREIGN KEY)

```sql
-- Ejemplo: No se puede tener stock negativo
ALTER TABLE products ADD CONSTRAINT check_stock CHECK (stock >= 0);

BEGIN;
    UPDATE products SET stock = -5 WHERE id = 1;  -- Falla
ROLLBACK;  -- Vuelve a estado consistente
```

---

### I - Isolation (Aislamiento)
**"Transacciones concurrentes no interfieren"**
- Cada transacción opera aislada
- No ve cambios incompletos de otras transacciones

```sql
-- Usuario 1:
BEGIN;
    UPDATE products SET stock = 0 WHERE id = 1;
    -- NO commit aún

-- Usuario 2:
SELECT stock FROM products WHERE id = 1;
-- Ve el valor ANTES del UPDATE de Usuario 1
```

---

### D - Durability (Durabilidad)
**"Una vez COMMIT, permanente"**
- Después de COMMIT, datos persisten
- Incluso si servidor se apaga

```sql
BEGIN;
    INSERT INTO orders (...) VALUES (...);
COMMIT;  -- Guardado permanente
-- Incluso si servidor se reinicia, orden existe
```

---

## 📊 Transacciones en Baby Cash

### Ejemplo 1: Crear Orden Completa

**Problema:** Crear orden requiere múltiples operaciones.

**Sin transacción (MALO):**
```sql
-- Paso 1: Crear orden
INSERT INTO orders (user_id, total, status) 
VALUES (2, 0, 'PENDING') 
RETURNING id;  -- Supongamos que retorna 10

-- Paso 2: Insertar item 1
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES (10, 1, 2, 45000);

-- Paso 3: Insertar item 2 (FALLA - producto no existe)
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES (10, 999, 1, 50000);  -- ERROR

-- PROBLEMA: Orden 10 existe pero sin items completos ❌
```

**Con transacción (BUENO):**
```sql
BEGIN;

-- Paso 1: Crear orden
INSERT INTO orders (user_id, total, status) 
VALUES (2, 0, 'PENDING') 
RETURNING id INTO v_order_id;

-- Paso 2: Insertar items
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES 
    (v_order_id, 1, 2, 45000),
    (v_order_id, 3, 1, 60000);

-- Paso 3: Reducir stock
UPDATE products SET stock = stock - 2 WHERE id = 1;
UPDATE products SET stock = stock - 1 WHERE id = 3;

-- Paso 4: Actualizar total
UPDATE orders SET total = 150000 WHERE id = v_order_id;

COMMIT;  -- Todo se guarda junto

-- Si cualquier paso falla, NADA se guarda ✅
```

---

### Ejemplo 2: Cancelar Orden y Restaurar Stock

**Problema:** Cancelar orden debe revertir múltiples cambios.

```sql
BEGIN;

-- 1. Cambiar status
UPDATE orders SET status = 'CANCELLED' WHERE id = 10;

-- 2. Restaurar stock de todos los items
UPDATE products p
SET stock = stock + oi.quantity
FROM order_items oi
WHERE oi.order_id = 10 AND oi.product_id = p.id;

-- 3. Registrar en log
INSERT INTO order_cancellations (order_id, reason, cancelled_at)
VALUES (10, 'Cliente solicitó cancelación', CURRENT_TIMESTAMP);

COMMIT;  -- Todo se guarda atómicamente
```

---

### Ejemplo 3: Transferir Stock Entre Productos

**Escenario:** Promoción "2x1" - reducir stock de A, aumentar stock de B.

```sql
BEGIN;

-- Verificar stock disponible
SELECT stock INTO v_stock FROM products WHERE id = 1 FOR UPDATE;
-- FOR UPDATE bloquea fila (evita race condition)

IF v_stock < 10 THEN
    RAISE EXCEPTION 'Stock insuficiente';
END IF;

-- Reducir stock producto A
UPDATE products SET stock = stock - 10 WHERE id = 1;

-- Aumentar stock producto B
UPDATE products SET stock = stock + 10 WHERE id = 2;

COMMIT;
```

---

## 🔒 Niveles de Aislamiento

PostgreSQL ofrece 4 niveles de aislamiento:

### 1️⃣ READ UNCOMMITTED
**"Lee cambios no confirmados"**
- ⚠️ NO soportado en PostgreSQL (se comporta como READ COMMITTED)
- Puede leer datos de transacciones no completadas (dirty read)

---

### 2️⃣ READ COMMITTED (Default)
**"Solo lee cambios confirmados"**
- Ve datos de transacciones con COMMIT
- No ve cambios de transacciones activas
- **Problema:** Non-repeatable reads (datos cambian entre lecturas)

```sql
-- Transacción 1:
BEGIN;
SELECT stock FROM products WHERE id = 1;  -- Retorna 10
-- ... espera ...
SELECT stock FROM products WHERE id = 1;  -- Retorna 5 (otra transacción lo modificó)
COMMIT;
```

---

### 3️⃣ REPEATABLE READ
**"Lecturas consistentes dentro de transacción"**
- Ve snapshot de datos al inicio de transacción
- Misma query retorna mismos datos
- **Problema:** Phantom reads (nuevas filas aparecen)

```sql
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
BEGIN;

SELECT COUNT(*) FROM products;  -- Retorna 100

-- Otra transacción inserta producto

SELECT COUNT(*) FROM products;  -- Sigue retornando 100 (snapshot)
COMMIT;
```

---

### 4️⃣ SERIALIZABLE (Más estricto)
**"Como si transacciones se ejecutaran una por una"**
- Máximo aislamiento
- Evita anomalías
- **Problema:** Performance (más bloqueos)

```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
BEGIN;
    -- operaciones
COMMIT;
```

---

## 🎯 Uso en Baby Cash

### Configurar Nivel de Aislamiento
```sql
-- Por transacción
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;
    -- operaciones
COMMIT;

-- Por sesión
SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL REPEATABLE READ;
```

### Recomendaciones Baby Cash

**READ COMMITTED (default):** Mayoría de operaciones
```sql
BEGIN;
    INSERT INTO orders (...) VALUES (...);
COMMIT;
```

**REPEATABLE READ:** Reportes financieros
```sql
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;
    -- Generar reporte de ventas
    SELECT SUM(total) FROM orders WHERE status = 'COMPLETED';
    SELECT COUNT(*) FROM order_items;
COMMIT;
```

**SERIALIZABLE:** Operaciones críticas (stock limitado)
```sql
BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    -- Comprar último item disponible
    SELECT stock FROM products WHERE id = 1;  -- stock = 1
    UPDATE products SET stock = stock - 1 WHERE id = 1;
COMMIT;
```

---

## 🔧 Comandos de Transacción

### Iniciar Transacción
```sql
BEGIN;
-- o
START TRANSACTION;
-- o
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;
```

### Confirmar Cambios
```sql
COMMIT;
```

### Revertir Cambios
```sql
ROLLBACK;
```

### Savepoint (Punto de Guardado)
```sql
BEGIN;
    INSERT INTO orders (...) VALUES (...);
    
    SAVEPOINT before_items;
    
    INSERT INTO order_items (...) VALUES (...);
    -- Falla
    
    ROLLBACK TO SAVEPOINT before_items;  -- Solo revierte items, orden permanece
    
COMMIT;
```

---

## 🚨 Problemas Comunes

### Problema 1: Deadlock (Bloqueo Mutuo)

**Escenario:**
```sql
-- Transacción 1:
BEGIN;
UPDATE products SET stock = stock - 1 WHERE id = 1;  -- Bloquea producto 1
-- espera...
UPDATE products SET stock = stock - 1 WHERE id = 2;  -- Espera por producto 2

-- Transacción 2:
BEGIN;
UPDATE products SET stock = stock - 1 WHERE id = 2;  -- Bloquea producto 2
-- espera...
UPDATE products SET stock = stock - 1 WHERE id = 1;  -- Espera por producto 1

-- ¡DEADLOCK! Cada una espera por la otra
```

**PostgreSQL detecta y aborta una transacción:**
```
ERROR: deadlock detected
```

**Solución:**
- Ordenar operaciones consistentemente (siempre producto 1 antes que 2)
- Reducir duración de transacciones
- Usar `SELECT FOR UPDATE NOWAIT` (falla inmediatamente si bloqueado)

```sql
BEGIN;
    SELECT * FROM products WHERE id = 1 FOR UPDATE NOWAIT;
    -- Si bloqueado, falla inmediatamente (no espera)
COMMIT;
```

---

### Problema 2: Long-Running Transactions (Transacciones Largas)

**Malo:**
```sql
BEGIN;
    SELECT * FROM orders;  -- Lee 1 millón de filas
    -- Procesar en aplicación (tarda 10 minutos)
    UPDATE orders SET status = 'PROCESSED' WHERE id = 1;
COMMIT;
```

**Consecuencias:**
- Bloquea filas por mucho tiempo
- Otros usuarios esperan
- Puede causar timeouts

**Solución:**
- Mantener transacciones cortas
- Procesar en lotes (batches)

```sql
-- Procesar en lotes de 1000
FOR i IN 1..10 LOOP
    BEGIN;
        UPDATE orders SET status = 'PROCESSED' 
        WHERE id BETWEEN (i-1)*1000+1 AND i*1000;
    COMMIT;
END LOOP;
```

---

### Problema 3: Lost Updates (Actualizaciones Perdidas)

**Escenario:**
```sql
-- Usuario 1:
BEGIN;
    SELECT stock FROM products WHERE id = 1;  -- stock = 10
    -- Calcula: nuevo_stock = 10 - 2 = 8

-- Usuario 2:
BEGIN;
    SELECT stock FROM products WHERE id = 1;  -- stock = 10
    -- Calcula: nuevo_stock = 10 - 3 = 7

-- Usuario 1:
    UPDATE products SET stock = 8 WHERE id = 1;
COMMIT;

-- Usuario 2:
    UPDATE products SET stock = 7 WHERE id = 1;  -- ¡Sobrescribe cambio de Usuario 1!
COMMIT;

-- Resultado: stock = 7 (debería ser 5) ❌
```

**Solución 1: Optimistic Locking (version column)**
```sql
ALTER TABLE products ADD COLUMN version INTEGER DEFAULT 0;

-- Usuario 1:
BEGIN;
    SELECT stock, version FROM products WHERE id = 1;  -- stock=10, version=0
    UPDATE products 
    SET stock = 8, version = version + 1 
    WHERE id = 1 AND version = 0;
    -- Actualiza 1 fila
COMMIT;

-- Usuario 2:
BEGIN;
    SELECT stock, version FROM products WHERE id = 1;  -- stock=10, version=0
    UPDATE products 
    SET stock = 7, version = version + 1 
    WHERE id = 1 AND version = 0;
    -- Actualiza 0 filas (version ya cambió a 1)
    -- Lanzar error: "Datos desactualizados, reintentar"
ROLLBACK;
```

**Solución 2: SELECT FOR UPDATE**
```sql
-- Usuario 1:
BEGIN;
    SELECT stock FROM products WHERE id = 1 FOR UPDATE;  -- Bloquea fila
    UPDATE products SET stock = stock - 2 WHERE id = 1;
COMMIT;

-- Usuario 2:
BEGIN;
    SELECT stock FROM products WHERE id = 1 FOR UPDATE;  -- ESPERA hasta que Usuario 1 haga COMMIT
    UPDATE products SET stock = stock - 3 WHERE id = 1;
COMMIT;

-- Resultado: stock = 5 ✅
```

---

## 🎓 Para la Evaluación del SENA

### Preguntas Frecuentes

**1. "¿Qué es una transacción y por qué es importante?"**

> "Una transacción es un conjunto de operaciones que se ejecutan como UNA unidad:
> - **Atomicidad:** Todo se hace o nada se hace
> - **Ejemplo:** Crear orden
>   1. INSERT en orders
>   2. INSERT en order_items
>   3. UPDATE stock
>   
>   Si paso 3 falla, pasos 1 y 2 se revierten (ROLLBACK).
> 
> **Importancia:**
> - Evita inconsistencias
> - Garantiza integridad de datos
> - Como transferencia bancaria: todo o nada"

---

**2. "¿Qué es ACID?"**

> "ACID son las 4 propiedades de transacciones:
> 
> **A - Atomicity (Atomicidad):**
> - Todo o nada
> - No hay estados intermedios
> 
> **C - Consistency (Consistencia):**
> - De estado válido a estado válido
> - Se respetan constraints
> 
> **I - Isolation (Aislamiento):**
> - Transacciones no interfieren
> - Cada una ve datos consistentes
> 
> **D - Durability (Durabilidad):**
> - COMMIT = permanente
> - Datos persisten incluso si servidor falla
> 
> Baby Cash: Crear orden es transacción ACID."

---

**3. "¿Qué es un deadlock y cómo se resuelve?"**

> "**Deadlock** (bloqueo mutuo) ocurre cuando:
> - Transacción 1 bloquea recurso A, espera recurso B
> - Transacción 2 bloquea recurso B, espera recurso A
> - Ambas esperan indefinidamente
> 
> **Ejemplo Baby Cash:**
> ```sql
> -- Transacción 1:
> UPDATE products SET stock = stock - 1 WHERE id = 1;  -- Bloquea producto 1
> UPDATE products SET stock = stock - 1 WHERE id = 2;  -- Espera producto 2
> 
> -- Transacción 2:
> UPDATE products SET stock = stock - 1 WHERE id = 2;  -- Bloquea producto 2
> UPDATE products SET stock = stock - 1 WHERE id = 1;  -- Espera producto 1
> ```
> 
> **Solución:**
> - PostgreSQL detecta y aborta una transacción
> - Ordenar operaciones consistentemente (siempre ID 1 antes de ID 2)
> - Reducir tiempo de transacción
> - Usar NOWAIT para fallar inmediatamente"

---

**4. "¿Cuándo usar transacciones explícitas?"**

> "Usa **BEGIN...COMMIT** cuando:
> - Múltiples operaciones relacionadas (crear orden con items)
> - Necesitas atomicidad (todo o nada)
> - Validaciones complejas entre operaciones
> 
> NO necesitas BEGIN...COMMIT cuando:
> - Una sola operación (un INSERT)
> - PostgreSQL usa transacción implícita
> 
> Baby Cash:
> - ✅ `BEGIN; INSERT orden; INSERT items; UPDATE stock; COMMIT;`
> - ❌ `BEGIN; INSERT INTO users (...); COMMIT;` (innecesario, un INSERT basta)"

---

## 📝 Ejemplos Prácticos Baby Cash

### Crear Orden (Transacción Completa)
```sql
BEGIN;

-- Crear orden
INSERT INTO orders (user_id, total, status)
VALUES (2, 0, 'PENDING')
RETURNING id INTO v_order_id;

-- Insertar items
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES 
    (v_order_id, 1, 2, 45000),
    (v_order_id, 3, 1, 60000);

-- Reducir stock
UPDATE products SET stock = stock - 2 WHERE id = 1;
UPDATE products SET stock = stock - 1 WHERE id = 3;

-- Calcular total
UPDATE orders SET total = (
    SELECT SUM(quantity * price) FROM order_items WHERE order_id = v_order_id
) WHERE id = v_order_id;

COMMIT;
```

### Aplicar Descuento Masivo
```sql
BEGIN;

-- Registrar promoción
INSERT INTO promotions (name, discount, start_date, end_date)
VALUES ('Black Friday', 20, '2024-11-25', '2024-11-30')
RETURNING id INTO v_promo_id;

-- Aplicar descuento a categoría
UPDATE products
SET discount = 20
WHERE category_id = 2;  -- Juguetes

-- Auditar
INSERT INTO audit_log (action, table_name, description)
VALUES ('PROMOTION', 'products', 'Black Friday 20% descuento en Juguetes');

COMMIT;
```

### Reporte Consistente
```sql
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- Generar reporte (datos consistentes)
SELECT 
    (SELECT COUNT(*) FROM orders WHERE status = 'COMPLETED') as total_orders,
    (SELECT SUM(total) FROM orders WHERE status = 'COMPLETED') as total_revenue,
    (SELECT COUNT(DISTINCT user_id) FROM orders) as total_customers;

COMMIT;
```

---

## 🚀 Conclusión

**Transacciones en Baby Cash:**
- ✅ Garantizan atomicidad (todo o nada)
- ✅ Mantienen consistencia de datos
- ✅ Aíslan operaciones concurrentes
- ✅ Persisten cambios permanentemente

**ACID = Confiabilidad de la base de datos.**

---

**Has completado la documentación avanzada de base de datos:** ✅
- Triggers → Automatización
- Vistas → Reutilización de queries
- Procedimientos → Lógica compleja
- Funciones → Cálculos
- Índices → Performance
- Transacciones → Consistencia

**¡Felicitaciones! 🎉**
