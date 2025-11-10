# 🔗 Integración Frontend-Backend (MVC)

Este documento explica cómo integrar el frontend React con el backend Spring Boot en una arquitectura MVC.

---

## 📋 Tabla de Contenidos

1. [Arquitectura](#arquitectura)
2. [Prerrequisitos](#prerrequisitos)
3. [Integración Automática](#integración-automática)
4. [Integración Manual](#integración-manual)
5. [Configuración Backend](#configuración-backend)
6. [Estructura de Carpetas](#estructura-de-carpetas)
7. [Despliegue](#despliegue)
8. [Troubleshooting](#troubleshooting)

---

## 🏗️ Arquitectura

### Modelo MVC Híbrido

```
┌─────────────────────────────────────────┐
│         Cliente (Navegador)             │
│  http://localhost:8080/                 │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│     Spring Boot (Puerto 8080)           │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │  FrontendController                │ │
│  │  - Sirve index.html (/, /products) │ │
│  │  - React Router maneja navegación  │ │
│  └────────────────────────────────────┘ │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │  REST API Controllers              │ │
│  │  - /api/products                   │ │
│  │  - /api/auth                       │ │
│  │  - /api/orders                     │ │
│  └────────────────────────────────────┘ │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │  Static Resources                  │ │
│  │  - /assets/index.js                │ │
│  │  - /assets/index.css               │ │
│  │  - /favicon.ico                    │ │
│  └────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

### Flujo de Peticiones

1. **Peticiones API** (`/api/*`): Manejadas por REST Controllers
   - `GET /api/products` → ProductController
   - `POST /api/auth/login` → AuthController
   - etc.

2. **Peticiones Frontend** (todas las demás): Manejadas por FrontendController
   - `/` → index.html
   - `/products` → index.html (React Router)
   - `/admin/products` → index.html (React Router)
   - etc.

3. **Recursos Estáticos**: Servidos automáticamente desde `/static`
   - `/assets/index.js`
   - `/assets/index.css`
   - `/favicon.ico`

---

## ✅ Prerrequisitos

- **Node.js**: v18+ (para construir el frontend)
- **NPM**: v9+ (incluido con Node.js)
- **Java**: JDK 21+
- **Maven**: 3.8+
- **Git**: Para control de versiones

---

## 🚀 Integración Automática

### Opción 1: Script de Shell (Recomendado)

```bash
# Dar permisos de ejecución
chmod +x integrate-frontend.sh

# Ejecutar el script
./integrate-frontend.sh
```

El script realiza automáticamente:
1. ✅ Limpia builds anteriores
2. ✅ Construye el frontend (`npm run build`)
3. ✅ Copia archivos a `backend/src/main/resources/static`
4. ✅ Copia `index.html` a `backend/src/main/resources/templates`
5. ✅ Verifica la integración

### Opción 2: NPM Script

Agregar a `frontend/package.json`:

```json
{
  "scripts": {
    "build:backend": "vite build && npm run copy:backend",
    "copy:backend": "node scripts/copy-to-backend.js"
  }
}
```

Crear `frontend/scripts/copy-to-backend.js`:

```javascript
const fs = require('fs-extra');
const path = require('path');

const distDir = path.join(__dirname, '..', 'dist');
const backendStatic = path.join(__dirname, '..', '..', 'backend', 'src', 'main', 'resources', 'static');
const backendTemplates = path.join(__dirname, '..', '..', 'backend', 'src', 'main', 'resources', 'templates');

// Limpiar destinos
fs.emptyDirSync(backendStatic);
fs.emptyDirSync(backendTemplates);

// Copiar assets
fs.copySync(path.join(distDir, 'assets'), path.join(backendStatic, 'assets'));

// Copiar otros archivos estáticos
const files = fs.readdirSync(distDir);
files.forEach(file => {
  if (file !== 'index.html' && file !== 'assets') {
    fs.copySync(path.join(distDir, file), path.join(backendStatic, file));
  }
});

// Copiar index.html a templates
fs.copySync(path.join(distDir, 'index.html'), path.join(backendTemplates, 'index.html'));

console.log('✅ Frontend integrado en backend');
```

Ejecutar:

```bash
cd frontend
npm run build:backend
```

---

## 🔧 Integración Manual

### Paso 1: Construir el Frontend

```bash
cd frontend
npm install
npm run build
```

Esto genera el directorio `frontend/dist/` con:
```
dist/
├── assets/
│   ├── index-abc123.js
│   ├── index-def456.css
│   └── [otras dependencias]
├── index.html
├── favicon.ico
└── [otros archivos]
```

### Paso 2: Copiar Archivos al Backend

```bash
# Limpiar carpeta static
rm -rf backend/src/main/resources/static/*

# Copiar assets
cp -r frontend/dist/assets backend/src/main/resources/static/

# Copiar archivos estáticos (excepto index.html)
cp frontend/dist/favicon.ico backend/src/main/resources/static/
cp frontend/dist/manifest.json backend/src/main/resources/static/
# ... copiar otros archivos según necesites

# Copiar index.html a templates
cp frontend/dist/index.html backend/src/main/resources/templates/
```

### Paso 3: Verificar Estructura

```
backend/src/main/resources/
├── static/
│   ├── assets/
│   │   ├── index-abc123.js
│   │   └── index-def456.css
│   ├── favicon.ico
│   └── manifest.json
└── templates/
    └── index.html
```

---

## ⚙️ Configuración Backend

### 1. FrontendController.java

Ya está creado en `backend/src/main/java/com/babycash/backend/controller/FrontendController.java`

Maneja todas las rutas no-API y las redirige a `index.html`.

### 2. WebMvcConfig (Opcional)

Si necesitas configuración adicional:

```java
package com.babycash.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos estáticos desde /static
        registry
            .addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600); // Cache de 1 hora
    }
}
```

### 3. application.properties

```properties
# Frontend Configuration
spring.web.resources.static-locations=classpath:/static/
spring.web.resources.cache.period=3600

# Thymeleaf (para servir index.html)
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false
```

---

## 📂 Estructura de Carpetas

### Antes de la Integración

```
Babycash/
├── frontend/
│   ├── src/
│   ├── public/
│   ├── dist/          # Generado por 'npm run build'
│   └── package.json
└── backend/
    └── src/main/
        ├── java/
        └── resources/
            ├── static/         # Vacío
            └── templates/      # Vacío
```

### Después de la Integración

```
Babycash/
├── frontend/
│   ├── src/
│   ├── public/
│   ├── dist/          # Build de React
│   └── package.json
└── backend/
    └── src/main/
        ├── java/
        │   └── com/babycash/backend/
        │       └── controller/
        │           └── FrontendController.java  # ✨ Nuevo
        └── resources/
            ├── static/                          # ✨ Con archivos
            │   ├── assets/
            │   │   ├── index-abc123.js
            │   │   └── index-def456.css
            │   └── favicon.ico
            └── templates/                       # ✨ Con index.html
                └── index.html
```

---

## 🚢 Despliegue

### Desarrollo Local

```bash
# Integrar frontend
./integrate-frontend.sh

# Construir backend
cd backend
mvn clean package

# Ejecutar
java -jar target/babycash-0.0.1-SNAPSHOT.jar

# Abrir navegador
# http://localhost:8080
```

### Producción

```bash
# 1. Integrar frontend
./integrate-frontend.sh

# 2. Construir JAR con frontend incluido
cd backend
mvn clean package -DskipTests

# 3. El JAR resultante incluye el frontend
# backend/target/babycash-0.0.1-SNAPSHOT.jar

# 4. Desplegar en servidor
scp target/babycash-*.jar usuario@servidor:/opt/babycash/
ssh usuario@servidor 'java -jar /opt/babycash/babycash-*.jar'
```

### Docker (Opcional)

```dockerfile
# Dockerfile en la raíz del proyecto
FROM node:18 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

FROM maven:3.8-openjdk-21 AS backend-build
WORKDIR /app
COPY backend/pom.xml ./
COPY backend/src ./src
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static/
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=backend-build /app/target/babycash-*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

Construir y ejecutar:

```bash
docker build -t babycash:latest .
docker run -p 8080:8080 babycash:latest
```

---

## 🐛 Troubleshooting

### Problema: Rutas del Frontend Dan 404

**Causa**: FrontendController no está capturando las rutas.

**Solución**:
1. Verificar que `FrontendController.java` está en el package correcto
2. Agregar la ruta específica en `@GetMapping`
3. Reiniciar el servidor

### Problema: Assets (JS/CSS) No Cargan

**Causa**: Archivos no están en `/static` o rutas incorrectas.

**Solución**:
1. Verificar que los archivos están en `backend/src/main/resources/static/assets/`
2. Verificar que `index.html` tiene rutas absolutas (`/assets/index.js` no `assets/index.js`)
3. Limpiar cache del navegador (Ctrl+Shift+R)

### Problema: Página en Blanco

**Causa**: Error de configuración en Vite.

**Solución** en `frontend/vite.config.ts`:

```typescript
export default defineConfig({
  base: '/',  // ✅ Importante para Spring Boot
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
  },
});
```

### Problema: API No Responde

**Causa**: CORS o rutas mal configuradas.

**Solución**:
1. Verificar que las peticiones van a `/api/*`
2. Configurar CORS si es necesario
3. Verificar `application.properties`:

```properties
# CORS Configuration
server.cors.allowed-origins=http://localhost:8080
server.cors.allowed-methods=GET,POST,PUT,DELETE
```

### Problema: Navegación React Router No Funciona

**Causa**: Spring Boot no está redirigiendo correctamente.

**Solución**:
1. Verificar que usas `<BrowserRouter>` no `<HashRouter>`
2. Asegurar que FrontendController tiene todas las rutas
3. Agregar comodín si es necesario:

```java
@GetMapping("/{path:[^\\.]*}")
public String forward() {
    return "forward:/index.html";
}
```

---

## 📝 Notas Importantes

1. **Desarrollo**: Durante el desarrollo, sigue usando `npm run dev` en el frontend para hot-reload
2. **Integración**: Solo integra cuando quieras probar el sistema completo o para producción
3. **Cache**: Spring Boot cachea recursos estáticos. Limpia con `mvn clean` si hay problemas
4. **Build**: Cada cambio en el frontend requiere re-ejecutar `npm run build` e integrar
5. **Git**: No commitear `dist/` ni los archivos en `backend/src/main/resources/static/` (usar .gitignore)

---

## 🎯 Checklist de Integración

- [ ] Frontend construido (`npm run build`)
- [ ] Archivos copiados a `backend/src/main/resources/static/`
- [ ] `index.html` copiado a `backend/src/main/resources/templates/`
- [ ] `FrontendController.java` creado
- [ ] Backend compilado (`mvn clean package`)
- [ ] Servidor ejecutándose
- [ ] Navegación funciona (todas las rutas)
- [ ] API responde correctamente
- [ ] Assets cargan correctamente
- [ ] No hay errores en consola del navegador

---

**Última actualización**: 8 de Noviembre de 2025  
**Autor**: Baby Cash Team  
**Versión**: 1.0
