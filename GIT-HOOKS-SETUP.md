# 🛡️ Configuración de Git Hooks y Format on Save

Documentación completa para configurar el guardián de código limpio en Baby Cash.

---

## 📋 Tabla de Contenidos

1. [Integración en el IDE (Formato Automático)](#-integración-en-el-ide-formato-automático)
2. [Configuración del Pre-commit Hook](#-configuración-del-pre-commit-hook)
3. [Prueba de Fuego](#-prueba-de-fuego)
4. [Troubleshooting](#-troubleshooting)

---

## 🎨 Integración en el IDE (Formato Automático)

### Paso 1: Instalar Extensiones Requeridas

#### VS Code (Recomendado)

**Extensiones Frontend:**
1. **ESLint** - `dbaeumer.vscode-eslint`
   - Ctrl+P → `ext install dbaeumer.vscode-eslint`
   
2. **Prettier** - `esbenp.prettier-vscode`
   - Ctrl+P → `ext install esbenp.prettier-vscode`
   
3. **Tailwind CSS IntelliSense** - `bradlc.vscode-tailwindcss`
   - Ctrl+P → `ext install bradlc.vscode-tailwindcss`

**Extensiones Backend:**
1. **Extension Pack for Java** - `vscjava.vscode-java-pack`
   - Ctrl+P → `ext install vscjava.vscode-java-pack`
   
2. **Checkstyle for Java** - `shengchen.vscode-checkstyle`
   - Ctrl+P → `ext install shengchen.vscode-checkstyle`

**Extensiones Opcionales:**
- **Error Lens** - `usernamehw.errorlens` (muestra errores inline)
- **GitLens** - `eamodio.gitlens` (mejor integración Git)

#### IntelliJ IDEA (Alternativa)

**Para Frontend:**
1. Settings → Plugins → Buscar "Prettier"
2. Settings → Plugins → Buscar "ESLint"

**Para Backend:**
1. Settings → Plugins → Buscar "Checkstyle-IDEA"

---

### Paso 2: Configurar Format on Save

#### VS Code - Configuración Automática ✅

Ya está configurado en `.vscode/settings.json`:

```json
{
  // Formato automático al guardar
  "editor.formatOnSave": true,
  
  // Ejecutar ESLint y organizar imports al guardar
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": "explicit",
    "source.organizeImports": "explicit"
  },
  
  // Prettier como formateador por defecto
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  
  // Configuración específica por lenguaje
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "[typescriptreact]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.formatOnSave": true
  }
}
```

**¿Qué hace esta configuración?**
- ✅ Formatea código automáticamente al presionar `Ctrl+S`
- ✅ Ejecuta ESLint fix automáticamente
- ✅ Organiza imports automáticamente
- ✅ Elimina espacios en blanco al final de líneas
- ✅ Añade nueva línea al final de archivos

#### IntelliJ IDEA - Pasos Manuales

1. **File** → **Settings** (o `Ctrl+Alt+S`)
2. **Tools** → **Actions on Save**
3. Marcar las siguientes opciones:
   - ☑️ **Reformat code**
   - ☑️ **Optimize imports**
   - ☑️ **Run code cleanup**
4. Click **Apply** → **OK**

---

### Paso 3: Verificar que Funciona

#### Test en VS Code

1. Abre cualquier archivo `.tsx` o `.java`
2. Rompe el formato (ej: añade espacios extras, líneas vacías)
3. Presiona `Ctrl+S` (guardar)
4. **Resultado esperado**: El archivo se formatea automáticamente

#### Test Manual de Prettier

```bash
cd frontend

# Ver qué archivos necesitan formato
npm run format:check

# Formatear todos
npm run format
```

#### Test Manual de ESLint

```bash
cd frontend

# Ver errores
npm run lint

# Corregir automáticamente
npm run lint:fix
```

---

## 🔒 Configuración del Pre-commit Hook

### Tabla de Configuración

| **Etapa del Commit** | **Comando/Script** | **Propósito de la Automatización** |
|----------------------|--------------------|------------------------------------|
| **1. Instalación de Hooks** | `npm install -D husky lint-staged` | Habilitar la intercepción del comando `git commit` |
| **2. Inicialización** | `npx husky init` | Crear estructura de carpetas `.husky/` |
| **3. Configurar lint-staged** | Ver `package.json` → `lint-staged` | Definir qué comandos ejecutar en archivos staged |
| **4. Hook Pre-commit** | `.husky/pre-commit` → `npx lint-staged` | Ejecutar validaciones antes del commit |
| **5. Ejecución del Formato** | `prettier --write` (via lint-staged) | Arreglar el estilo de los archivos antes de la validación |
| **6. Validación Lógica** | `eslint --fix` (via lint-staged) | Detener el commit si hay errores fatales |

---

### Paso 1: Instalación del Gestor de Hooks

```bash
cd frontend

# Instalar Husky y lint-staged
npm install -D husky lint-staged

# Inicializar Husky
npx husky init
```

**¿Qué hace esto?**
- ✅ Instala Husky (gestor de Git hooks)
- ✅ Instala lint-staged (ejecuta comandos solo en archivos modificados)
- ✅ Crea carpeta `.husky/` con hooks
- ✅ Añade script `"prepare": "husky"` a package.json

---

### Paso 2: Configurar lint-staged en package.json

Archivo: `frontend/package.json`

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

**¿Qué hace esta configuración?**
- ✅ Para archivos `.ts` y `.tsx`:
  1. Ejecuta `eslint --fix` (corrige errores)
  2. Ejecuta `prettier --write` (formatea)
- ✅ Para archivos `.json`, `.css`, `.md`:
  1. Solo ejecuta `prettier --write`

---

### Paso 3: Configurar el Hook de Pre-commit

Archivo: `frontend/.husky/pre-commit`

```bash
#!/usr/bin/env sh
. "$(dirname -- "$0")/_/husky.sh"

# 🔍 Pre-commit Hook - Baby Cash Frontend
echo "🔍 Ejecutando validaciones pre-commit..."

# Ejecutar lint-staged (ESLint + Prettier en archivos staged)
npx lint-staged

# Capturar el código de salida
EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
  echo ""
  echo "❌ Pre-commit falló: Se encontraron errores que deben corregirse"
  echo ""
  echo "💡 Opciones:"
  echo "   1. Corrige los errores manualmente"
  echo "   2. Ejecuta: npm run lint:fix && npm run format"
  echo "   3. Luego intenta el commit nuevamente"
  echo ""
  exit 1
fi

echo "✅ Validaciones pre-commit exitosas"
exit 0
```

**Flujo de ejecución:**
1. Usuario ejecuta `git commit`
2. Husky intercepta el comando
3. Ejecuta `.husky/pre-commit`
4. Ejecuta `npx lint-staged`
5. lint-staged ejecuta ESLint + Prettier en archivos staged
6. Si hay errores → **BLOQUEA** el commit
7. Si todo está bien → **PERMITE** el commit

---

### Paso 4: Dar Permisos de Ejecución

```bash
chmod +x frontend/.husky/pre-commit
```

---

## 🔥 Prueba de Fuego

### Prueba 1: Commit con Variable No Usada

#### Crear archivo con error:

```bash
cd frontend/src
```

Crear `TestError.tsx`:

```typescript
// ❌ ERROR: Variable no usada
const unusedVariable = 'esto causará error';

const TestComponent = () => {
  return <div>Test</div>;
};

export default TestComponent;
```

#### Intentar commit:

```bash
git add src/TestError.tsx
git commit -m "test: archivo con error"
```

#### Resultado Esperado:

```
🔍 Ejecutando validaciones pre-commit...

✖ eslint --fix:
  src/TestError.tsx
    3:7  error  'unusedVariable' is assigned a value but never used  @typescript-eslint/no-unused-vars

✖ lint-staged failed
❌ Pre-commit falló: Se encontraron errores que deben corregirse

💡 Opciones:
   1. Corrige los errores manualmente
   2. Ejecuta: npm run lint:fix && npm run format
   3. Luego intenta el commit nuevamente
```

**✅ COMMIT BLOQUEADO** - El guardián funcionó!

---

### Prueba 2: Commit con Código Limpio

#### Corregir el archivo:

```typescript
// ✅ Sin errores
const TestComponent = () => {
  return <div>Test</div>;
};

export default TestComponent;
```

#### Intentar commit nuevamente:

```bash
git add src/TestError.tsx
git commit -m "test: archivo sin errores"
```

#### Resultado Esperado:

```
🔍 Ejecutando validaciones pre-commit...
✔ Preparing lint-staged...
✔ Running tasks for staged files...
✔ Applying modifications from tasks...
✔ Cleaning up temporary files...
✅ Validaciones pre-commit exitosas

[master abc1234] test: archivo sin errores
 1 file changed, 7 insertions(+)
```

**✅ COMMIT EXITOSO** - El código está limpio!

---

### Prueba 3: Formato Automático

#### Crear archivo con mal formato:

```typescript
const  TestComponent  =  (  )  =>  {
      return   <div>   Test   </div>  ;
}  ;
export   default   TestComponent  ;
```

#### Stage y commit:

```bash
git add src/TestFormat.tsx
git commit -m "test: formato automático"
```

#### Resultado:

```
🔍 Ejecutando validaciones pre-commit...
✔ Running 'prettier --write'
✅ Validaciones pre-commit exitosas
```

**✅ El archivo fue formateado automáticamente antes del commit!**

---

## 🐛 Troubleshooting

### Problema 1: Hook no se ejecuta

**Síntoma**: El commit se hace sin ejecutar lint-staged

**Solución**:
```bash
# Verificar que Husky está instalado
ls -la .husky/

# Reinstalar hooks
npm run prepare

# Verificar permisos
chmod +x .husky/pre-commit
```

---

### Problema 2: "husky command not found"

**Síntoma**: Error al ejecutar git commit

**Solución**:
```bash
# Reinstalar Husky
cd frontend
npm install -D husky
npx husky init
```

---

### Problema 3: Commit bloqueado por errores que no veo

**Síntoma**: Pre-commit falla pero no muestra errores claros

**Solución**:
```bash
# Ver todos los errores de ESLint
npm run lint

# Corregir automáticamente
npm run lint:fix

# Formatear
npm run format

# Intentar commit nuevamente
git commit -m "tu mensaje"
```

---

### Problema 4: Quiero hacer commit SIN validación (emergencia)

**Solución** (solo en casos extremos):
```bash
git commit --no-verify -m "emergency: bypass hooks"
```

⚠️ **NO RECOMENDADO** - Solo usar en emergencias!

---

### Problema 5: Format on Save no funciona en VS Code

**Solución**:
1. Verificar extensiones instaladas:
   - `Ctrl+Shift+X` → Buscar "Prettier" y "ESLint"
   
2. Verificar configuración:
   - `Ctrl+,` → Buscar "format on save" → Debe estar ☑️

3. Verificar formateador por defecto:
   - Abrir archivo `.tsx`
   - Click derecho → "Format Document With..."
   - Seleccionar "Prettier" → "Configure Default Formatter"

4. Recargar VS Code:
   - `Ctrl+Shift+P` → "Reload Window"

---

## 📊 Estadísticas de Validación

Después de configurar, puedes ver estadísticas:

```bash
# Ver cuántos archivos tienen errores
npm run lint 2>&1 | grep "error"

# Ver cuántos archivos necesitan formato
npm run format:check 2>&1 | grep "\[warn\]" | wc -l

# Ver último commit bloqueado
git reflog | head -5
```

---

## ✅ Checklist de Configuración Completa

### IDE
- [ ] Extensiones instaladas (ESLint + Prettier)
- [ ] Format on save activado
- [ ] Code actions on save configurado
- [ ] Verificado con test manual

### Git Hooks
- [ ] Husky instalado
- [ ] lint-staged configurado
- [ ] Pre-commit hook creado
- [ ] Permisos de ejecución dados
- [ ] Prueba de fuego exitosa (commit bloqueado)

### Verificación
- [ ] Commit con error bloqueado ✅
- [ ] Commit sin error exitoso ✅
- [ ] Formato automático funcionando ✅

---

## 🎯 Comandos de Referencia Rápida

```bash
# Verificar configuración
npm run lint              # Ver errores
npm run format:check      # Ver archivos sin formato

# Corregir problemas
npm run lint:fix          # Corregir errores ESLint
npm run format            # Formatear código

# Todo en uno
npm run check:fix         # Verificar y corregir todo

# Git (con hooks)
git add .
git commit -m "mensaje"   # Pre-commit se ejecutará automáticamente

# Bypass (emergencia)
git commit --no-verify -m "emergency"
```

---

## 📚 Archivos Involucrados

```
frontend/
├── .husky/
│   └── pre-commit           # Hook de pre-commit
├── package.json             # Scripts y lint-staged config
├── eslint.config.js         # Reglas ESLint
├── .prettierrc              # Reglas Prettier
└── .prettierignore          # Archivos ignorados

.vscode/
├── settings.json            # Format on save
└── extensions.json          # Extensiones recomendadas
```

---

**¡Configuración completa! 🎉 Ahora tu código siempre estará limpio y formateado.**
