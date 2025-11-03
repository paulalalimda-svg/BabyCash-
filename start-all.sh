#!/bin/bash

# =============================================================================
# 🚀 Baby Cash - Script de Inicio Completo (Backend + Frontend)
# =============================================================================

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🍼 BABY CASH - Iniciando Aplicación Completa"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Obtener el directorio del script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Verificar que los scripts existan
if [ ! -f "$SCRIPT_DIR/start-backend.sh" ]; then
    echo "❌ Error: No se encontró start-backend.sh"
    exit 1
fi

if [ ! -f "$SCRIPT_DIR/start-frontend.sh" ]; then
    echo "❌ Error: No se encontró start-frontend.sh"
    exit 1
fi

# Dar permisos de ejecución
chmod +x "$SCRIPT_DIR/start-backend.sh"
chmod +x "$SCRIPT_DIR/start-frontend.sh"

echo "📋 Este script iniciará:"
echo "   1️⃣  Backend (Spring Boot) en http://localhost:8080"
echo "   2️⃣  Frontend (React) en http://localhost:5173"
echo ""
echo "⚠️  Ambos servicios se ejecutarán en paralelo"
echo ""

# Preguntar si desea continuar
read -p "¿Deseas continuar? (s/n): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[SsYy]$ ]]; then
    echo "❌ Operación cancelada"
    exit 0
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 Iniciando servicios..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Crear directorio para logs
mkdir -p logs

# Función para limpiar procesos al salir
cleanup() {
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🛑 Deteniendo servicios..."
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    if [ ! -z "$BACKEND_PID" ]; then
        echo "🔴 Deteniendo Backend (PID: $BACKEND_PID)..."
        kill -TERM "$BACKEND_PID" 2>/dev/null
    fi
    
    if [ ! -z "$FRONTEND_PID" ]; then
        echo "🔴 Deteniendo Frontend (PID: $FRONTEND_PID)..."
        kill -TERM "$FRONTEND_PID" 2>/dev/null
    fi
    
    echo ""
    echo "✅ Servicios detenidos"
    echo "👋 ¡Hasta luego!"
    exit 0
}

# Capturar señales de terminación
trap cleanup SIGINT SIGTERM

# Iniciar Backend
echo "1️⃣  Iniciando Backend..."
cd "$SCRIPT_DIR/backend" && ../start-backend.sh > ../logs/backend.log 2>&1 &
BACKEND_PID=$!
echo "   ✅ Backend iniciado (PID: $BACKEND_PID)"
echo "   📄 Logs: logs/backend.log"

# Esperar un poco antes de iniciar el frontend
sleep 3

# Iniciar Frontend
echo ""
echo "2️⃣  Iniciando Frontend..."
cd "$SCRIPT_DIR/frontend" && ../start-frontend.sh > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "   ✅ Frontend iniciado (PID: $FRONTEND_PID)"
echo "   📄 Logs: logs/frontend.log"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ SERVICIOS INICIADOS"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🔗 URLs disponibles:"
echo "   🌐 Frontend:  http://localhost:5173"
echo "   📡 Backend:   http://localhost:8080"
echo "   📚 Swagger:   http://localhost:8080/swagger-ui.html"
echo ""
echo "📊 Monitoreo:"
echo "   Backend:  tail -f logs/backend.log"
echo "   Frontend: tail -f logs/frontend.log"
echo ""
echo "🛑 Para detener ambos servicios, presiona Ctrl+C"
echo ""

# Mantener el script corriendo y mostrar los logs
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📄 Mostrando logs en tiempo real (Ctrl+C para detener)..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Mostrar logs de ambos servicios
tail -f logs/backend.log logs/frontend.log &
TAIL_PID=$!

# Esperar a que los procesos terminen
wait
