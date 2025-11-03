#!/bin/bash#!/bin/bash



# =============================================================================# Script para iniciar el frontend de Baby Cash

# 🎨 Baby Cash - Script de Inicio del Frontend# Fecha: 28 de octubre de 2025

# =============================================================================

cd "$(dirname "$0")/frontend"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo "🍼 BABY CASH - Iniciando Frontend (React + Vite)"echo "🎨 Iniciando Frontend de Baby Cash..."

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"echo "📍 Directorio: $(pwd)"

echo ""echo "⏳ Por favor espera 5-10 segundos..."

echo ""

# Verificar si estamos en el directorio correcto

if [ ! -f "package.json" ]; thennpm run dev

    echo "⚠️  Error: No se encontró package.json"
    echo "📂 Cambiando al directorio frontend..."
    cd frontend || {
        echo "❌ Error: No se pudo acceder al directorio frontend"
        exit 1
    }
fi

# Verificar Node.js
echo "🔍 Verificando Node.js..."
if command -v node &> /dev/null; then
    NODE_VERSION=$(node -v)
    echo "✅ Node.js $NODE_VERSION detectado"
else
    echo "❌ Error: Node.js no está instalado"
    echo "💡 Instala Node.js 18+ desde: https://nodejs.org/"
    exit 1
fi

# Verificar npm
echo ""
echo "🔍 Verificando npm..."
if command -v npm &> /dev/null; then
    NPM_VERSION=$(npm -v)
    echo "✅ npm $NPM_VERSION detectado"
else
    echo "❌ Error: npm no está instalado"
    exit 1
fi

# Verificar si node_modules existe
if [ ! -d "node_modules" ]; then
    echo ""
    echo "📦 Instalando dependencias..."
    npm install
    
    if [ $? -ne 0 ]; then
        echo ""
        echo "❌ Error al instalar dependencias"
        exit 1
    fi
else
    echo ""
    echo "✅ Dependencias ya instaladas"
fi

# Verificar archivo .env
if [ ! -f ".env" ]; then
    echo ""
    echo "⚠️  Advertencia: No se encontró archivo .env"
    echo "📝 Creando archivo .env con valores por defecto..."
    cat > .env << 'EOF'
# Backend API URL
VITE_API_URL=http://localhost:8080/api

# App Info
VITE_APP_NAME=BabyCash
VITE_APP_VERSION=1.0.0
EOF
    echo "✅ Archivo .env creado"
fi

# Iniciar el servidor de desarrollo
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 Iniciando servidor de desarrollo Vite..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🌐 Frontend estará disponible en: http://localhost:5173"
echo "⚡ Hot Module Replacement (HMR) activado"
echo ""
echo "💡 Para detener el servidor, presiona Ctrl+C"
echo ""

npm run dev
