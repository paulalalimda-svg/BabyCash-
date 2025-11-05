# 🔧 Configuración de Linters y Formateadores - Baby Cash

Guía completa para configurar y usar los linters y formateadores en el proyecto.

---

## 📚 Tabla de Contenidos

1. [Frontend - ESLint + Prettier](#-frontend---eslint--prettier)
2. [Backend - Checkstyle + Spotless](#-backend---checkstyle--spotless)
3. [Comandos Rápidos](#-comandos-rápidos)
4. [Integración con VS Code](#-integración-con-vs-code)
5. [Integración con IntelliJ IDEA](#-integración-con-intellij-idea)
6. [CI/CD Integration](#-cicd-integration)

---

## 🎨 Frontend - ESLint + Prettier

### Herramientas Configuradas

- **ESLint 8.57**: Linter para TypeScript/JavaScript
- **Prettier 3.x**: Formateador de código
- **Plugins ESLint**:
  - `@typescript-eslint` - Reglas TypeScript
  - `eslint-plugin-react` - Reglas React
  - `eslint-plugin-react-hooks` - Reglas React Hooks
  - `eslint-plugin-react-refresh` - Reglas Vite HMR
  - `eslint-plugin-jsx-a11y` - Accesibilidad
  - `eslint-plugin-tailwindcss` - Tailwind CSS
  - `eslint-config-prettier` - Desactivar reglas que conflictan con Prettier

### Archivos de Configuración

```
frontend/
├── eslint.config.js          # Configuración ESLint (Flat Config)
├── .prettierrc              # Configuración Prettier
├── .prettierignore          # Archivos ignorados por Prettier
└── tsconfig.json            # TypeScript config (requerido por ESLint)
```

### Comandos Frontend

```bash
cd frontend

# 🔍 Linting
npm run lint              # Verificar errores de código
npm run lint:fix          # Corregir errores automáticamente

# 💅 Formateo
npm run format            # Formatear todo el código
npm run format:check      # Solo verificar formato (no modifica)

# 📝 Type Checking
npm run type-check        # Verificar tipos TypeScript

# ✅ Verificación Completa
npm run check             # Ejecutar type-check + lint + format:check
npm run check:fix         # Ejecutar type-check + lint:fix + format
```

### Reglas ESLint Principales

```javascript
{
  // TypeScript
  "@typescript-eslint/no-explicit-any": "warn",
  "@typescript-eslint/no-unused-vars": ["warn", { argsIgnorePattern: "^_" }],
  
  // React
  "react/react-in-jsx-scope": "off",          // No necesario en React 17+
  "react/prop-types": "off",                  // Usamos TypeScript
  "react-hooks/rules-of-hooks": "error",      // Hooks correctos
  "react-hooks/exhaustive-deps": "warn",      // Dependencias hooks
  
  // Accesibilidad
  "jsx-a11y/click-events-have-key-events": "warn",
  
  // Tailwind
  "tailwindcss/classnames-order": "warn",     // Orden clases Tailwind
  
  // General
  "no-console": ["warn", { allow: ["warn", "error"] }],
  "prefer-const": "warn"
}
```

### Configuración Prettier

```json
{
  "semi": true,                    // Punto y coma al final
  "singleQuote": true,            // Comillas simples
  "tabWidth": 2,                  // 2 espacios de indentación
  "trailingComma": "es5",         // Comas finales en objetos/arrays
  "printWidth": 100,              // Máximo 100 caracteres por línea
  "arrowParens": "always",        // Paréntesis en arrow functions
  "endOfLine": "auto",            // Saltos de línea automáticos
  "bracketSpacing": true,         // Espacios en objetos { foo: bar }
  "jsxSingleQuote": false,        // Comillas dobles en JSX
  "jsxBracketSameLine": false     // > en nueva línea en JSX
}
```

---

## 🔧 Backend - Checkstyle + Spotless

### Herramientas Configuradas

- **Checkstyle 10.12**: Linter para Java (basado en Google Java Style)
- **Spotless 2.41**: Formateador de código Java
- **Google Java Format 1.17**: Estilo de formateo

### Archivos de Configuración

```
backend/
├── pom.xml               # Maven config con plugins
├── checkstyle.xml        # Reglas Checkstyle
└── src/
    └── main/java/        # Código Java
```

### Comandos Backend

```bash
cd backend

# 🔍 Checkstyle (Linting)
./mvnw checkstyle:check             # Verificar estilo de código
./mvnw checkstyle:checkstyle        # Generar reporte HTML

# 💅 Spotless (Formatting)
./mvnw spotless:check               # Verificar formato
./mvnw spotless:apply               # Aplicar formato automáticamente

# ✅ Verificación Completa
./mvnw clean verify                 # Compila + tests + checkstyle + spotless

# 📊 Ver reporte Checkstyle
# El reporte HTML se genera en: target/site/checkstyle.html
```

### Reglas Checkstyle Principales

```xml
<!-- Tamaño de línea -->
<module name="LineLength">
    <property name="max" value="120"/>
</module>

<!-- Nomenclatura -->
<module name="PackageName">
    <property name="format" value="^[a-z]+(\.[a-z][a-z0-9]*)*$"/>
</module>
<module name="TypeName">           <!-- Clases: PascalCase -->
    <property name="format" value="^[A-Z][a-zA-Z0-9]*$"/>
</module>
<module name="MethodName">         <!-- Métodos: camelCase -->
    <property name="format" value="^[a-z][a-zA-Z0-9]*$"/>
</module>
<module name="ConstantName">       <!-- Constantes: UPPER_CASE -->
    <property name="format" value="^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"/>
</module>

<!-- Tamaño de métodos -->
<module name="MethodLength">
    <property name="max" value="150"/>
</module>
<module name="ParameterNumber">
    <property name="max" value="7"/>
</module>

<!-- Complejidad ciclomática -->
<module name="CyclomaticComplexity">
    <property name="max" value="15"/>
</module>

<!-- Imports -->
<module name="AvoidStarImport"/>
<module name="UnusedImports"/>
<module name="ImportOrder">
    <property name="groups" value="java,javax,org,com"/>
</module>

<!-- Buenas prácticas -->
<module name="EqualsHashCode"/>
<module name="SimplifyBooleanExpression"/>
<module name="StringLiteralEquality"/>
<module name="OneStatementPerLine"/>
```

### Configuración Spotless

```xml
<configuration>
    <java>
        <!-- Google Java Format -->
        <googleJavaFormat>
            <version>1.17.0</version>
            <style>GOOGLE</style>
        </googleJavaFormat>
        
        <!-- Eliminar imports no usados -->
        <removeUnusedImports />
        
        <!-- Ordenar imports -->
        <importOrder>
            <order>java,javax,org,com</order>
        </importOrder>
        
        <!-- Trim trailing whitespace -->
        <trimTrailingWhitespace />
        
        <!-- End files with newline -->
        <endWithNewline />
    </java>
</configuration>
```

---

## ⚡ Comandos Rápidos

### Frontend

```bash
# Verificar todo antes de commit
npm run check:fix

# Solo formatear
npm run format

# Solo lint
npm run lint:fix
```

### Backend

```bash
# Formatear código automáticamente
./mvnw spotless:apply

# Verificar estilo + compilar
./mvnw clean verify

# Solo checkstyle
./mvnw checkstyle:check
```

### Ambos (desde raíz)

```bash
# Verificar frontend
cd frontend && npm run check

# Verificar backend
cd backend && ./mvnw checkstyle:check

# Formatear frontend
cd frontend && npm run format

# Formatear backend
cd backend && ./mvnw spotless:apply
```

---

## 🔌 Integración con VS Code

### Extensiones Recomendadas

Crea `.vscode/extensions.json`:

```json
{
  "recommendations": [
    // Frontend
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "bradlc.vscode-tailwindcss",
    
    // Backend
    "vscjava.vscode-java-pack",
    "josevseb.google-java-format-for-vs-code",
    "shengchen.vscode-checkstyle",
    
    // General
    "editorconfig.editorconfig"
  ]
}
```

### Configuración VS Code

Crea `.vscode/settings.json`:

```json
{
  // Frontend - ESLint
  "eslint.enable": true,
  "eslint.validate": [
    "javascript",
    "javascriptreact",
    "typescript",
    "typescriptreact"
  ],
  
  // Frontend - Prettier
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.formatOnSave": true,
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[typescriptreact]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  
  // Backend - Java
  "[java]": {
    "editor.defaultFormatter": "josevseb.google-java-format-for-vs-code",
    "editor.formatOnSave": true
  },
  "java.format.settings.url": "backend/checkstyle.xml",
  "java.checkstyle.configuration": "${workspaceFolder}/backend/checkstyle.xml",
  
  // General
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true,
    "source.organizeImports": true
  },
  "files.trimTrailingWhitespace": true,
  "files.insertFinalNewline": true
}
```

---

## 💡 Integración con IntelliJ IDEA

### Configurar Checkstyle

1. **Instalar plugin Checkstyle**:
   - `Settings` → `Plugins` → Buscar "Checkstyle-IDEA" → Install

2. **Configurar Checkstyle**:
   - `Settings` → `Tools` → `Checkstyle`
   - Click en `+` para agregar configuración
   - Seleccionar `backend/checkstyle.xml`
   - Marcar como activo

### Configurar Google Java Format

1. **Instalar plugin**:
   - `Settings` → `Plugins` → Buscar "google-java-format" → Install

2. **Activar**:
   - `Settings` → `Other Settings` → `google-java-format Settings`
   - Check "Enable google-java-format"

3. **Format on save**:
   - `Settings` → `Tools` → `Actions on Save`
   - Check "Reformat code"

### Atajos IntelliJ

```
Ctrl+Alt+L          # Formatear código
Ctrl+Alt+O          # Optimizar imports
Ctrl+Alt+Shift+L    # Mostrar opciones de formato
```

---

## 🚀 CI/CD Integration

### GitHub Actions Workflow

Crea `.github/workflows/code-quality.yml`:

```yaml
name: Code Quality

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  frontend-lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'
      - name: Install dependencies
        run: cd frontend && npm ci
      - name: Run ESLint
        run: cd frontend && npm run lint
      - name: Check Prettier
        run: cd frontend && npm run format:check
      - name: Type check
        run: cd frontend && npm run type-check

  backend-lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Run Checkstyle
        run: cd backend && ./mvnw checkstyle:check
      - name: Check Spotless
        run: cd backend && ./mvnw spotless:check
```

---

## 📋 Pre-commit Hooks (Opcional)

### Instalar Husky

```bash
# Frontend
cd frontend
npm install -D husky lint-staged

# Configurar
npx husky init
```

### Configurar lint-staged

En `frontend/package.json`:

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

---

## 🆘 Solución de Problemas

### Frontend

**Error: "Cannot find module 'globals'"**
```bash
cd frontend
npm install -D globals
```

**Error: ESLint no funciona**
```bash
# Limpiar caché
rm -rf node_modules package-lock.json
npm install
```

**Prettier y ESLint en conflicto**
```bash
# Verificar que eslint-config-prettier está instalado
npm install -D eslint-config-prettier
```

### Backend

**Error: "Checkstyle configuration file not found"**
```bash
# Verificar que checkstyle.xml está en backend/
ls backend/checkstyle.xml
```

**Error: Spotless falla**
```bash
# Aplicar formato automáticamente
./mvnw spotless:apply

# Luego verificar
./mvnw spotless:check
```

**Conflictos de formato con IDE**
```bash
# Desactivar el formateador del IDE y usar solo Spotless
# O configurar el IDE para usar Google Java Format
```

---

## 📊 Reportes

### Frontend

```bash
# ESLint genera output en consola
npm run lint

# Para formato HTML (opcional)
npm run lint -- -f html -o eslint-report.html
```

### Backend

```bash
# Checkstyle genera reporte HTML
./mvnw checkstyle:checkstyle

# Ver reporte en:
open target/site/checkstyle.html
```

---

## 🎯 Mejores Prácticas

### Frontend

1. ✅ **Ejecutar `npm run check:fix` antes de cada commit**
2. ✅ **Configurar format on save en el IDE**
3. ✅ **No desactivar reglas sin justificación**
4. ✅ **Usar `// eslint-disable-next-line` solo cuando sea necesario**
5. ✅ **Mantener `printWidth` en 100 caracteres**

### Backend

1. ✅ **Ejecutar `./mvnw spotless:apply` regularmente**
2. ✅ **Mantener métodos < 150 líneas**
3. ✅ **Mantener complejidad ciclomática < 15**
4. ✅ **No usar imports con `*`**
5. ✅ **Seguir convenciones de nombres Java**

---

## 📚 Referencias

### Frontend

- [ESLint Documentation](https://eslint.org/docs/latest/)
- [Prettier Documentation](https://prettier.io/docs/en/)
- [TypeScript ESLint](https://typescript-eslint.io/)
- [React ESLint Plugin](https://github.com/jsx-eslint/eslint-plugin-react)

### Backend

- [Checkstyle Documentation](https://checkstyle.sourceforge.io/)
- [Spotless Maven Plugin](https://github.com/diffplug/spotless)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Google Java Format](https://github.com/google/google-java-format)

---

## ✅ Checklist de Configuración

### Frontend
- [x] ESLint configurado
- [x] Prettier configurado
- [x] Scripts npm agregados
- [x] Plugins instalados
- [ ] VS Code configurado (opcional)
- [ ] Pre-commit hooks (opcional)

### Backend
- [x] Checkstyle configurado
- [x] Spotless configurado
- [x] Plugins Maven agregados
- [x] checkstyle.xml creado
- [ ] IntelliJ configurado (opcional)
- [ ] CI/CD configurado (opcional)

---

**¡Listo! 🎉 Ahora tienes configuración profesional de linting y formateo para todo el proyecto.**
