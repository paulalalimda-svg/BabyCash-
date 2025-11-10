# 🔒 Configuración de Git Hooks y Automatización - Baby Cash

Documentación completa de la configuración de pre-commit hooks, integración con IDE y flujo de trabajo automatizado.

## ✅ Estado: FUNCIONANDO CORRECTAMENTE

**Fecha de Verificación**: 4 de Noviembre de 2025  
**Resultado de Pruebas**: [Ver RESULTADO-PRUEBAS-HOOKS.md](./RESULTADO-PRUEBAS-HOOKS.md)

- ✅ Pre-commit hook bloqueando commits con errores
- ✅ Formato automático al guardar en VS Code
- ✅ Integración completa con ESLint y Prettier

---

## 📋 Tabla de Contenidos

1. [Integración en el IDE (Formato Automático)](#-integración-en-el-ide-formato-automático)
2. [Configuración del Pre-commit Hook](#-configuración-del-pre-commit-hook)
3. [Tabla de Automatización](#-tabla-de-automatización)
4. [Prueba de Fuego](#-prueba-de-fuego)
5. [Solución de Problemas](#-solución-de-problemas)

---

## 🎨 Integración en el IDE (Formato Automático)

### Visual Studio Code

#### **Paso 1: Instalar Extensiones Necesarias**

```bash
# Abrir VS Code
code .

# Instalar extensiones (se instalarán automáticamente desde .vscode/extensions.json)
# O instalar manualmente:
```

**Extensiones Frontend**:
- ✅ **ESLint** (`dbaeumer.vscode-eslint`)
- ✅ **Prettier** (`esbenp.prettier-vscode`)
- ✅ **Tailwind CSS IntelliSense** (`bradlc.vscode-tailwindcss`)

**Extensiones Backend**:
- ✅ **Language Support for Java** (`vscjava.vscode-java-pack`)
- ✅ **Checkstyle** (`shengchen.vscode-checkstyle`)

**Extensiones Generales**:
- ✅ **EditorConfig** (`editorconfig.editorconfig`)
- ✅ **GitLens** (`eamodio.gitlens`)

#### **Paso 2: Configuración Activada en settings.json**

El archivo `.vscode/settings.json` ya está configurado con:

```jsonc
{
  // 🔥 FORMATO AUTOMÁTICO AL GUARDAR
  "editor.formatOnSave": true,
  
  // 🔧 ACCIONES AUTOMÁTICAS AL GUARDAR
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": "explicit",      // Corrige errores ESLint
    "source.organizeImports": "explicit"     // Organiza imports
  },
  
  // 🧹 LIMPIEZA AUTOMÁTICA
  "files.trimTrailingWhitespace": true,      // Elimina espacios al final
  "files.insertFinalNewline": true,          // Agrega línea final
  "files.trimFinalNewlines": true,           // Elimina líneas vacías finales
  
  // 💅 PRETTIER COMO FORMATEADOR
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[typescriptreact]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  
  // ☕ JAVA FORMATTING
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.formatOnSave": true
  }
}
```

#### **Paso 3: Verificar que Funciona**

1. Abre cualquier archivo `.ts`, `.tsx` o `.java`
2. Escribe código mal formateado:
   ```typescript
   const x=1;const y=2;
   ```
3. Presiona `Ctrl+S` (Guardar)
4. ✅ El código debe formatearse automáticamente:
   ```typescript
   const x = 1;
   const y = 2;
   ```

---

## 🔒 Configuración del Pre-commit Hook

### Instalación de Herramientas

#### **1. Hook de Pre-commit (Instalación)**

```bash
cd frontend

# Instalar Husky y lint-staged
npm install -D husky lint-staged

# Inicializar Husky
npx husky init
```

**Propósito**: Habilitar la intercepción del comando `git commit` para ejecutar verificaciones automáticas.

**¿Qué hace Husky?**
- Instala git hooks en `.git/hooks/`
- Permite ejecutar scripts antes de cada commit
- Bloquea el commit si los scripts fallan

**¿Qué hace lint-staged?**
- Ejecuta linters/formatters SOLO en archivos modificados (staged)
- Mejora el rendimiento (no verifica todo el proyecto)
- Revierte cambios si hay errores

---

#### **2. Ejecución del Formato**

**Configuración en `package.json`**:

```json
{
  "lint-staged": {
    "*.{ts,tsx}": [
      "eslint --fix",
      "prettier --write"
    ],
    "*.{json,css,md}": [
      "prettier --write"
    ]
  }
}
```

**Comando exacto**: `npx lint-staged`

**Propósito**: 
- ✅ Arreglar automáticamente el estilo de los archivos antes de la validación
- ✅ Formatear código con Prettier
- ✅ Corregir errores de ESLint que puedan arreglarse automáticamente

**Flujo**:
1. Git detecta archivos en staging (`git add`)
2. lint-staged identifica archivos modificados por tipo
3. Ejecuta `eslint --fix` en archivos TypeScript
4. Ejecuta `prettier --write` para formatear
5. Si todo OK, continúa al siguiente paso
6. Si hay errores, **bloquea el commit**

---

#### **3. Ejecución de la Validación Lógica**

**Configuración en `.husky/pre-commit`**:

```bash
#!/usr/bin/env sh
. "$(dirname -- "$0")/_/husky.sh"

echo "🔍 Ejecutando verificación de código..."
npx lint-staged

if [ $? -ne 0 ]; then
  echo "❌ Commit bloqueado: Se encontraron errores"
  exit 1
fi

echo "✅ Código verificado exitosamente"
```

**Comando exacto**: `npx lint-staged` (ejecutado por Husky)

**Propósito**:
- ✅ Detener el commit si hay errores fatales definidos en ESLint
- ✅ Validar que el código cumple con las reglas críticas
- ✅ Prevenir que código con errores llegue al repositorio

**Errores fatales bloqueantes** (configurados como `"error"` en ESLint):
- `no-unused-vars` - Variables no usadas
- `no-undef` - Variables no definidas
- `@typescript-eslint/no-explicit-any` - Uso de `any`
- `react-hooks/rules-of-hooks` - Mal uso de hooks
- `react-hooks/exhaustive-deps` - Dependencias faltantes

---

## 📊 Tabla de Automatización

| Etapa del Commit | Comando/Script | Propósito de la Automatización |
|------------------|----------------|--------------------------------|
| **1. Hook de Pre-commit (Instalación)** | `npm install -D husky lint-staged` <br> `npx husky init` | Habilitar la intercepción del comando `git commit` para ejecutar verificaciones antes de confirmar cambios. |
| **2. Ejecución del Formato** | `npx lint-staged` ejecuta: <br> - `eslint --fix` <br> - `prettier --write` | Arreglar automáticamente el estilo de los archivos antes de la validación. Corrige indentación, espacios, comillas, etc. |
| **3. Ejecución de la Validación Lógica** | `npx lint-staged` ejecuta: <br> - `eslint` (sin --fix) | Detener el commit si hay errores fatales como variables no usadas, uso de `any`, imports faltantes, reglas de hooks incorrectas. |
| **4. Bloqueo del Commit** | `exit 1` en pre-commit hook | Si alguna validación falla, el commit se cancela automáticamente y se muestra mensaje de error al desarrollador. |
| **5. Commit Exitoso** | `git commit` continúa | Si todas las validaciones pasan, el commit se registra en el historial de Git con código limpio y verificado. |

---

## 🔥 Prueba de Fuego

### Prueba 1: Variable No Usada (Error Fatal)

1. **Crear archivo con error**:
   ```bash
   cd frontend/src
   ```

2. **Crear `test-error.ts`**:
   ```typescript
   // Variable no usada - debe bloquear el commit
   const unusedVariable = 'esto no se usa';
   
   export const testFunction = () => {
     console.log('test');
   };
   ```

3. **Intentar commit**:
   ```bash
   git add test-error.ts
   git commit -m "test: agregar variable no usada"
   ```

4. **Resultado esperado**:
   ```
   🔍 Ejecutando verificación de código...
   
   /path/to/test-error.ts
     2:7  error  'unusedVariable' is assigned a value but never used  @typescript-eslint/no-unused-vars
   
   ✖ 1 problem (1 error, 0 warnings)
   
   ❌ Commit bloqueado: Se encontraron errores de linting o formato
   💡 Revisa los errores arriba y corrígelos antes de hacer commit
   ```

5. **Commit BLOQUEADO** ✅

---

### Prueba 2: Uso de `any` (Error Fatal)

1. **Crear `test-any.ts`**:
   ```typescript
   // Uso de any - debe bloquear el commit
   export const processData = (data: any) => {
     return data.someProperty;
   };
   ```

2. **Intentar commit**:
   ```bash
   git add test-any.ts
   git commit -m "test: usar tipo any"
   ```

3. **Resultado esperado**:
   ```
   /path/to/test-any.ts
     2:36  error  Unexpected any. Specify a different type  @typescript-eslint/no-explicit-any
   
   ❌ Commit bloqueado
   ```

4. **Commit BLOQUEADO** ✅

---

### Prueba 3: Código Correcto (Commit Exitoso)

1. **Crear `test-success.ts`**:
   ```typescript
   export const validFunction = (name: string): string => {
     return `Hello, ${name}!`;
   };
   ```

2. **Intentar commit**:
   ```bash
   git add test-success.ts
   git commit -m "feat: agregar función válida"
   ```

3. **Resultado esperado**:
   ```
   🔍 Ejecutando verificación de código...
   ✅ Código verificado exitosamente
   
   [main abc123] feat: agregar función válida
    1 file changed, 3 insertions(+)
   ```

4. **Commit EXITOSO** ✅

---

### Prueba 4: Formato Automático

1. **Crear archivo mal formateado**:
   ```typescript
   // Mal formateado
   const x=1;const y=2;
   function test(){return x+y;}
   ```

2. **Hacer commit**:
   ```bash
   git add mal-formateado.ts
   git commit -m "test: formato"
   ```

3. **Resultado esperado**:
   ```
   🔍 Ejecutando verificación de código...
   
   ✔ Preparing lint-staged...
   ⚠ Running tasks for staged files...
     ✔ *.{ts,tsx} — 2 files
       ✔ eslint --fix
       ✔ prettier --write
   ✔ Applying modifications from tasks...
   ✔ Cleaning up temporary files...
   
   ✅ Código verificado exitosamente
   ```

4. **El archivo se formatea automáticamente antes del commit** ✅

---

## 🛠️ Comandos Útiles

### Verificar Estado de Husky

```bash
cd frontend

# Ver hooks instalados
ls -la .husky/

# Ver contenido del pre-commit
cat .husky/pre-commit

# Probar lint-staged manualmente
npx lint-staged
```

### Bypass del Pre-commit (NO RECOMENDADO)

```bash
# Solo usar en emergencias
git commit --no-verify -m "mensaje"
```

### Reinstalar Husky

```bash
cd frontend

# Eliminar hooks
rm -rf .husky

# Reinstalar
npx husky init

# Reconfigurar pre-commit (copiar contenido del paso 3)
```

---

## 🔍 Verificar Configuración

### Frontend

```bash
cd frontend

# 1. Verificar que Husky está instalado
ls .husky/pre-commit

# 2. Verificar que lint-staged está configurado
cat package.json | grep -A 10 "lint-staged"

# 3. Probar lint-staged manualmente
npx lint-staged

# 4. Ver extensiones VS Code instaladas
code --list-extensions | grep -E "(eslint|prettier|java)"
```

### Backend

```bash
cd backend

# Verificar Checkstyle
./mvnw checkstyle:check

# Verificar configuración VS Code
cat ../.vscode/settings.json | grep -A 5 "java"
```

---

## 📚 Archivos de Configuración

### Frontend

```
frontend/
├── .husky/
│   └── pre-commit          # Hook de pre-commit
├── package.json            # Configuración lint-staged
├── .prettierrc             # Reglas Prettier
├── eslint.config.js        # Reglas ESLint
└── tsconfig.json           # Configuración TypeScript
```

### VS Code

```
.vscode/
├── settings.json           # Format on save + code actions
└── extensions.json         # Extensiones recomendadas
```

---

## ❓ Solución de Problemas

### Problema: Pre-commit no se ejecuta

**Solución**:
```bash
cd frontend

# Reinstalar hooks
rm -rf .git/hooks
npx husky init

# Verificar permisos
chmod +x .husky/pre-commit

# Verificar que prepare está en package.json
cat package.json | grep "prepare"
```

### Problema: Formato no se aplica automáticamente en VS Code

**Solución**:
1. Verificar que Prettier está instalado: `code --list-extensions | grep prettier`
2. Verificar settings.json: `"editor.formatOnSave": true`
3. Recargar VS Code: `Ctrl+Shift+P` → "Reload Window"
4. Verificar que .prettierrc existe en la raíz del frontend

### Problema: ESLint no muestra errores en VS Code

**Solución**:
1. Abrir Output: `Ctrl+Shift+U`
2. Seleccionar "ESLint" en el dropdown
3. Ver errores de configuración
4. Reiniciar ESLint Server: `Ctrl+Shift+P` → "ESLint: Restart ESLint Server"

### Problema: Commit muy lento

**Solución**:
```bash
# lint-staged solo verifica archivos modificados
# Si sigue lento, verificar:

# 1. Excluir node_modules
cat .gitignore | grep node_modules

# 2. Limpiar caché
rm -rf node_modules/.cache

# 3. Ver qué archivos se están verificando
npx lint-staged --debug
```

---

## 🎯 Reglas Críticas Configuradas

### Errores que BLOQUEAN el commit (severity: "error")

#### Frontend (ESLint)

**Seguridad**:
- ❌ `no-eval` - No usar `eval()`
- ❌ `no-implied-eval` - No usar eval implícito
- ❌ `no-new-func` - No crear funciones con `new Function()`

**Calidad de Código**:
- ❌ `@typescript-eslint/no-unused-vars` - Variables no usadas
- ❌ `@typescript-eslint/no-explicit-any` - Uso de `any`
- ❌ `no-var` - Usar `const`/`let` en lugar de `var`

**React**:
- ❌ `react-hooks/rules-of-hooks` - Reglas de hooks
- ❌ `react-hooks/exhaustive-deps` - Dependencias de hooks

### Warnings que NO bloquean (severity: "warn")

- ⚠️ `no-console` - Console.log en producción
- ⚠️ `tailwindcss/classnames-order` - Orden de clases Tailwind
- ⚠️ Otros warnings no críticos

---

## ✅ Checklist de Configuración Completa

- [x] Husky instalado (`npm install -D husky`)
- [x] lint-staged instalado (`npm install -D lint-staged`)
- [x] Pre-commit hook configurado (`.husky/pre-commit`)
- [x] lint-staged configurado (`package.json`)
- [x] VS Code extensions instaladas
- [x] VS Code settings.json configurado
- [x] Format on save activado
- [x] Code actions on save activadas
- [x] ESLint reglas críticas configuradas
- [x] Prettier configurado
- [x] Checkstyle configurado (backend)
- [x] Pruebas de bloqueo exitosas

---

**🎉 ¡Configuración completa! El código sucio ya no puede pasar al repositorio.**
