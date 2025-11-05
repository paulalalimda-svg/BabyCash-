# 🔒 Reglas Críticas de Linting - Baby Cash

Configuración de reglas críticas para seguridad y mantenibilidad del proyecto.

---

## 📋 Índice

1. [Frontend - ESLint](#-frontend---eslint)
2. [Backend - Checkstyle](#-backend---checkstyle)
3. [Niveles de Severidad](#-niveles-de-severidad)
4. [Verificación de Reglas](#-verificación-de-reglas)

---

## 🎨 Frontend - ESLint

### 🔒 Reglas Críticas de Seguridad (ERROR)

Estas reglas **bloquean el build** y deben corregirse inmediatamente.

#### 1. Prevención de Código Inseguro

```javascript
// ❌ ERROR - No permitido
eval('code');
new Function('code');
innerHTML = userInput;

// ✅ Correcto
// No usar eval, usar alternativas seguras
// Sanitizar HTML antes de insertar
```

**Reglas**:
- `no-eval`: Prevenir eval()
- `no-implied-eval`: Prevenir setTimeout/setInterval con strings
- `no-new-func`: Prevenir new Function()

#### 2. Comparaciones Seguras

```javascript
// ❌ ERROR - Comparación débil
if (value == null) { }
if (count == 0) { }

// ✅ Correcto - Comparación estricta
if (value === null) { }
if (count === 0) { }
```

**Regla**: `eqeqeq: 'always'`

#### 3. Variables Modernas

```javascript
// ❌ ERROR - var obsoleto
var count = 0;

// ✅ Correcto - let/const
const MAX_COUNT = 10;
let count = 0;
```

**Regla**: `no-var: 'error'`

#### 4. Seguridad Web - Target Blank

```jsx
// ❌ ERROR - Vulnerable a tabnabbing
<a href="https://external.com" target="_blank">Link</a>

// ✅ Correcto - Seguro
<a href="https://external.com" target="_blank" rel="noopener noreferrer">
  Link
</a>
```

**Regla**: `react/jsx-no-target-blank: 'error'`

#### 5. Prevención de XSS

```jsx
// ⚠️ WARNING - Usar con precaución
<div dangerouslySetInnerHTML={{ __html: userInput }} />

// ✅ Mejor - Evitar dangerouslySetInnerHTML
<div>{sanitizedContent}</div>
```

**Regla**: `react/no-danger: 'warn'`

---

### 🔧 Reglas Críticas de Mantenibilidad (ERROR)

#### 1. TypeScript - No usar `any`

```typescript
// ❌ ERROR - any sin justificación
function processData(data: any) { }

// ✅ Correcto - Tipos específicos
function processData(data: UserData) { }
function processData<T>(data: T) { }
```

**Regla**: `@typescript-eslint/no-explicit-any: 'error'`

#### 2. Variables No Usadas

```typescript
// ❌ ERROR - Variable no usada
const unused = 10;
function test(unusedParam) { }

// ✅ Correcto - Prefijo _ para ignorar
const _willUse = 10;
function test(_unusedParam) { }
```

**Regla**: `@typescript-eslint/no-unused-vars: 'error'`

#### 3. Non-null Assertion

```typescript
// ❌ ERROR - ! sin validación
const user = users.find(u => u.id === id)!;

// ✅ Correcto - Validar primero
const user = users.find(u => u.id === id);
if (!user) throw new Error('User not found');
```

**Regla**: `@typescript-eslint/no-non-null-assertion: 'error'`

#### 4. React Hooks - Dependencias

```jsx
// ❌ ERROR - Falta dependencia
useEffect(() => {
  fetchData(userId);
}, []); // userId faltante

// ✅ Correcto - Todas las dependencias
useEffect(() => {
  fetchData(userId);
}, [userId]);
```

**Reglas**:
- `react-hooks/rules-of-hooks: 'error'`
- `react-hooks/exhaustive-deps: 'error'`

#### 5. Keys en Listas

```jsx
// ❌ ERROR - Sin key
items.map(item => <Item name={item} />)

// ❌ ERROR - Índice como key (antipatrón)
items.map((item, i) => <Item key={i} name={item} />)

// ✅ Correcto - ID único
items.map(item => <Item key={item.id} name={item} />)
```

**Reglas**:
- `react/jsx-key: 'error'`
- `react/no-array-index-key: 'warn'`

---

### ♿ Reglas Críticas de Accesibilidad (ERROR)

#### 1. Imágenes con Alt

```jsx
// ❌ ERROR - Sin alt
<img src="photo.jpg" />

// ✅ Correcto
<img src="photo.jpg" alt="Descripción de la foto" />
```

**Regla**: `jsx-a11y/alt-text: 'error'`

#### 2. Elementos Interactivos

```jsx
// ❌ ERROR - onClick sin teclado
<div onClick={handleClick}>Clickeable</div>

// ✅ Correcto - Accesible
<button onClick={handleClick}>Clickeable</button>

// O con teclado
<div onClick={handleClick} onKeyDown={handleKeyDown} role="button" tabIndex={0}>
  Clickeable
</div>
```

**Reglas**:
- `jsx-a11y/click-events-have-key-events: 'error'`
- `jsx-a11y/no-static-element-interactions: 'error'`

---

### 📏 Reglas de Complejidad (WARNING)

```javascript
// ⚠️ WARNING - Complejidad alta
function complexFunction() {
  if (a) {
    if (b) {
      if (c) {
        if (d) {
          if (e) {
            // Demasiado anidado
          }
        }
      }
    }
  }
}

// ✅ Mejor - Extraer funciones
function simpleFunction() {
  if (!isValid()) return;
  processData();
  updateUI();
}
```

**Reglas**:
- `complexity: ['warn', { max: 15 }]`
- `max-params: ['warn', { max: 5 }]`
- `max-lines-per-function: ['warn', { max: 150 }]`

---

## 🔧 Backend - Checkstyle

### 🔒 Reglas Críticas de Seguridad (ERROR)

#### 1. Prevención de SQL Injection

```java
// ❌ ERROR - Vulnerable a SQL Injection
Statement stmt = connection.createStatement();
stmt.execute("SELECT * FROM users WHERE id = " + userId);

// ✅ Correcto - PreparedStatement
PreparedStatement pstmt = connection.prepareStatement(
    "SELECT * FROM users WHERE id = ?"
);
pstmt.setLong(1, userId);
```

**Regla**: `IllegalType` - Prohibir `java.sql.Statement`

#### 2. Random Inseguro

```java
// ⚠️ WARNING - No usar para criptografía
Random random = new Random();
byte[] token = new byte[16];
random.nextBytes(token);

// ✅ Correcto - SecureRandom
SecureRandom secureRandom = new SecureRandom();
byte[] token = new byte[16];
secureRandom.nextBytes(token);
```

**Regla**: `Regexp` - Detectar `new Random()`

#### 3. Imports Ilegales

```java
// ❌ ERROR - Imports prohibidos
import sun.misc.Unsafe;
import com.sun.internal.*;
import org.junit.Assert; // Usar AssertJ

// ✅ Correcto
import java.security.SecureRandom;
import static org.assertj.core.api.Assertions.*;
```

**Regla**: `IllegalImport`

---

### 🔧 Reglas Críticas de Mantenibilidad (ERROR)

#### 1. Imports (CRÍTICO)

```java
// ❌ ERROR - Star imports
import java.util.*;
import com.babycash.backend.entity.*;

// ✅ Correcto - Imports específicos
import java.util.List;
import java.util.Optional;
import com.babycash.backend.entity.User;
import com.babycash.backend.entity.Product;
```

**Reglas**:
- `AvoidStarImport: 'error'`
- `UnusedImports: 'error'`
- `RedundantImport: 'error'`

#### 2. Orden de Imports

```java
// ❌ ERROR - Orden incorrecto
import com.babycash.backend.service.UserService;
import java.util.List;
import org.springframework.stereotype.Service;

// ✅ Correcto - Orden alfabético por grupo
import java.util.List;

import jakarta.persistence.Entity;

import org.springframework.stereotype.Service;

import com.babycash.backend.service.UserService;
```

**Regla**: `ImportOrder` - Grupos: `java,javax,jakarta,org,com`

#### 3. Nomenclatura (ERROR)

```java
// ❌ ERROR - Nomenclatura incorrecta
class user_service { }              // PascalCase requerido
void Get_User() { }                 // camelCase requerido
private String USER_NAME;           // camelCase para variables
final int max_count = 10;           // UPPER_CASE para constantes

// ✅ Correcto
class UserService { }
void getUser() { }
private String userName;
private static final int MAX_COUNT = 10;
```

**Reglas**:
- `TypeName: 'error'` - Clases en PascalCase
- `MethodName: 'error'` - Métodos en camelCase
- `ConstantName: 'error'` - Constantes en UPPER_CASE
- `MemberName: 'error'` - Variables en camelCase

#### 4. Tamaño de Métodos (ERROR)

```java
// ❌ ERROR - Método muy largo (>150 líneas)
public void processOrder() {
    // ... 200 líneas de código
}

// ✅ Correcto - Extraer submétodos
public void processOrder() {
    validateOrder();
    calculateTotal();
    applyDiscounts();
    saveOrder();
    sendNotification();
}
```

**Regla**: `MethodLength: max=150 lines`

#### 5. Número de Parámetros (ERROR)

```java
// ❌ ERROR - Demasiados parámetros (>7)
public void createUser(String name, String email, String phone, 
                       String address, String city, String country, 
                       String postalCode, int age) { }

// ✅ Correcto - Usar DTO
public void createUser(UserRegistrationDTO userDTO) { }
```

**Regla**: `ParameterNumber: max=7`

#### 6. Complejidad Ciclomática (ERROR)

```java
// ❌ ERROR - Complejidad > 15
public void processPayment(Order order) {
    if (order.isValid()) {
        if (order.hasDiscount()) {
            if (order.isVip()) {
                if (order.amount > 1000) {
                    // ... muchos if anidados
                }
            }
        }
    }
}

// ✅ Correcto - Simplificar
public void processPayment(Order order) {
    if (!order.isValid()) return;
    
    PaymentStrategy strategy = getPaymentStrategy(order);
    strategy.process(order);
}
```

**Regla**: `CyclomaticComplexity: max=15`

---

### ✅ Buenas Prácticas (ERROR)

#### 1. equals() y hashCode()

```java
// ❌ ERROR - Solo implementa equals()
@Override
public boolean equals(Object obj) {
    // ...
}
// Falta hashCode()

// ✅ Correcto - Ambos implementados
@Override
public boolean equals(Object obj) {
    // ...
}

@Override
public int hashCode() {
    return Objects.hash(id, name);
}
```

**Regla**: `EqualsHashCode: 'error'`

#### 2. Comparación de Strings

```java
// ❌ ERROR - Usar ==
if (name == "Admin") { }

// ✅ Correcto - Usar equals()
if ("Admin".equals(name)) { }
```

**Regla**: `StringLiteralEquality: 'error'`

#### 3. No Modificar Parámetros

```java
// ❌ ERROR - Modificar parámetro
public void updateUser(User user) {
    user = new User(); // Reasignación
}

// ✅ Correcto - Variable local
public void updateUser(User user) {
    User newUser = new User();
    // Usar newUser
}
```

**Regla**: `ParameterAssignment: 'error'`

#### 4. No usar System.out

```java
// ⚠️ WARNING - No usar System.out
System.out.println("Debug message");
System.err.println("Error");

// ✅ Correcto - Usar logger
log.info("Debug message");
log.error("Error occurred");
```

**Regla**: `Regexp` - Detectar `System.(out|err).print`

#### 5. No Catch Genérico

```java
// ⚠️ WARNING - Catch muy genérico
try {
    riskyOperation();
} catch (Exception e) { // Muy amplio
}

// ✅ Correcto - Catch específico
try {
    riskyOperation();
} catch (IOException e) {
    log.error("IO error", e);
} catch (ValidationException e) {
    log.warn("Validation failed", e);
}
```

**Regla**: `IllegalCatch`

---

## 📊 Niveles de Severidad

### ERROR (🔴)
- **Bloquea el build/merge**
- Debe corregirse inmediatamente
- Representa problemas de seguridad o bugs potenciales

### WARNING (⚠️)
- **Debe corregirse antes de merge**
- No bloquea el build pero se recomienda corregir
- Problemas de mantenibilidad o estilo

### INFO (ℹ️)
- **Sugerencia de mejora**
- No requiere corrección inmediata
- Optimizaciones o mejores prácticas

---

## ✅ Verificación de Reglas

### Frontend

```bash
cd frontend

# Ver todas las reglas activas
npm run lint

# Ver solo errores críticos
npm run lint -- --quiet

# Corregir automáticamente
npm run lint:fix
```

### Backend

```bash
cd backend

# Verificar todas las reglas
./mvnw checkstyle:check

# Generar reporte HTML detallado
./mvnw checkstyle:checkstyle
open target/site/checkstyle.html

# Ver solo errores (filtrar warnings)
./mvnw checkstyle:check | grep "\[ERROR\]"
```

---

## 🚀 Integración en Flujo de Trabajo

### 1. Pre-commit (Local)

```bash
# Antes de cada commit
./check-code.sh
```

### 2. Pre-push (Local)

```bash
# Antes de push
cd frontend && npm run check:fix
cd backend && ./mvnw checkstyle:check
```

### 3. CI/CD (Automatizado)

GitHub Actions verifica automáticamente:
- ESLint sin errores
- Checkstyle sin errores de severidad ERROR
- Prettier formateado
- TypeScript sin errores de tipo

---

## 📚 Referencias

### Frontend
- [ESLint Rules](https://eslint.org/docs/latest/rules/)
- [TypeScript ESLint](https://typescript-eslint.io/rules/)
- [React Hooks Rules](https://react.dev/reference/react/hooks#rules-of-hooks)
- [JSX A11y](https://github.com/jsx-eslint/eslint-plugin-jsx-a11y)

### Backend
- [Checkstyle Checks](https://checkstyle.sourceforge.io/checks.html)
- [Google Java Style](https://google.github.io/styleguide/javaguide.html)
- [OWASP Secure Coding](https://owasp.org/www-project-secure-coding-practices-quick-reference-guide/)

---

## 🎯 Resumen

**Frontend - 35+ reglas críticas configuradas**:
- 10 reglas de seguridad (ERROR)
- 15 reglas de mantenibilidad (ERROR)
- 5 reglas de accesibilidad (ERROR)
- 5+ reglas de complejidad (WARNING)

**Backend - 40+ reglas críticas configuradas**:
- 5 reglas de seguridad (ERROR)
- 20 reglas de mantenibilidad (ERROR)
- 15 reglas de buenas prácticas (ERROR/WARNING)

**Total: 75+ reglas críticas activas** 🎉

---

**¡Código más seguro, mantenible y de calidad profesional!** ✨
