#!/bin/bash#!/bin/bash



# =============================================================================# Script para iniciar el backend de Baby Cash

# 🚀 Baby Cash - Script de Inicio del Backend# Fecha: 28 de octubre de 2025

# =============================================================================

cd "$(dirname "$0")/backend"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo "🍼 BABY CASH - Iniciando Backend (Spring Boot)"echo "🚀 Iniciando Backend de Baby Cash..."

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"echo "📍 Directorio: $(pwd)"

echo ""echo "⏳ Por favor espera 15-20 segundos..."

echo ""

# Verificar si estamos en el directorio correcto

if [ ! -f "pom.xml" ]; then./mvnw spring-boot:run

    echo "⚠️  Error: No se encontró pom.xml"
    echo "📂 Cambiando al directorio backend..."
    cd backend || {
        echo "❌ Error: No se pudo acceder al directorio backend"
        exit 1
    }
fi

# Verificar Java
echo "🔍 Verificando Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    echo "✅ Java $JAVA_VERSION detectado"
else
    echo "❌ Error: Java no está instalado"
    echo "💡 Instala Java 17 o superior: https://adoptium.net/"
    exit 1
fi

# Verificar PostgreSQL
echo ""
echo "🔍 Verificando PostgreSQL..."
if command -v psql &> /dev/null; then
    echo "✅ PostgreSQL detectado"
else
    echo "⚠️  Advertencia: PostgreSQL no detectado localmente"
    echo "💡 Asegúrate de que PostgreSQL esté corriendo en localhost:5432"
fi

# Limpiar compilaciones anteriores
echo ""
echo "🧹 Limpiando compilaciones anteriores..."
./mvnw clean

# Compilar el proyecto
echo ""
echo "🔨 Compilando proyecto..."
./mvnw install -DskipTests

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Error en la compilación"
    exit 1
fi

# Iniciar el servidor
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 Iniciando servidor Spring Boot..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📡 Backend estará disponible en: http://localhost:8080"
echo "📚 Swagger UI: http://localhost:8080/swagger-ui.html"
echo "🔧 API Docs: http://localhost:8080/api-docs"
echo ""
echo "💡 Para detener el servidor, presiona Ctrl+C"
echo ""

./mvnw spring-boot:run
