#!/bin/bash

# =============================================================================
# 🔍 Baby Cash - Script de Verificación de Código
# =============================================================================

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔍 BABY CASH - Verificación de Calidad de Código"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
HAS_ERRORS=0

# =============================================================================
# FRONTEND
# =============================================================================

echo "┌─────────────────────────────────────────────────────────────────────┐"
echo "│ 🎨 FRONTEND - ESLint + Prettier + TypeScript                      │"
echo "└─────────────────────────────────────────────────────────────────────┘"
echo ""

if [ ! -d "$SCRIPT_DIR/frontend/node_modules" ]; then
    echo "⚠️  Dependencias no instaladas"
    echo "📦 Instalando dependencias..."
    cd "$SCRIPT_DIR/frontend" && npm install
fi

cd "$SCRIPT_DIR/frontend"

# Type Check
echo "📝 Verificando tipos TypeScript..."
npm run type-check
if [ $? -ne 0 ]; then
    echo "❌ Type check falló"
    HAS_ERRORS=1
else
    echo "✅ Type check OK"
fi
echo ""

# ESLint
echo "🔍 Ejecutando ESLint..."
npm run lint > /tmp/eslint-output.txt 2>&1
ESLINT_CODE=$?
WARNINGS=$(grep -o "warning" /tmp/eslint-output.txt | wc -l)
ERRORS=$(grep -o "error" /tmp/eslint-output.txt | wc -l)

if [ $ESLINT_CODE -ne 0 ]; then
    echo "⚠️  ESLint encontró problemas:"
    echo "   - Errores: $ERRORS"
    echo "   - Warnings: $WARNINGS"
    echo "   💡 Ejecuta: npm run lint:fix"
else
    echo "✅ ESLint OK"
fi
echo ""

# Prettier
echo "💅 Verificando formato Prettier..."
npm run format:check > /tmp/prettier-output.txt 2>&1
if [ $? -ne 0 ]; then
    UNFORMATTED=$(grep -o "\[warn\]" /tmp/prettier-output.txt | wc -l)
    echo "⚠️  $UNFORMATTED archivos sin formatear"
    echo "   💡 Ejecuta: npm run format"
else
    echo "✅ Prettier OK"
fi
echo ""

# =============================================================================
# BACKEND
# =============================================================================

echo "┌─────────────────────────────────────────────────────────────────────┐"
echo "│ 🔧 BACKEND - Checkstyle                                            │"
echo "└─────────────────────────────────────────────────────────────────────┘"
echo ""

cd "$SCRIPT_DIR/backend"

# Checkstyle
echo "🔍 Ejecutando Checkstyle..."
./mvnw checkstyle:check > /tmp/checkstyle-output.txt 2>&1
VIOLATIONS=$(grep "You have" /tmp/checkstyle-output.txt | grep -o "[0-9]\+" | head -1)

if [ ! -z "$VIOLATIONS" ]; then
    echo "⚠️  Checkstyle encontró $VIOLATIONS violaciones"
    echo "   📄 Ver reporte: ./mvnw checkstyle:checkstyle"
    echo "   📊 Reporte HTML: target/site/checkstyle.html"
else
    echo "✅ Checkstyle OK"
fi
echo ""

# =============================================================================
# RESUMEN
# =============================================================================

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 RESUMEN"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "Frontend:"
echo "  - TypeScript: $([ $? -eq 0 ] && echo "✅" || echo "❌")"
echo "  - ESLint: ⚠️  $WARNINGS warnings, $ERRORS errors"
echo "  - Prettier: $(grep -q "All matched files" /tmp/prettier-output.txt && echo "✅" || echo "⚠️")"
echo ""
echo "Backend:"
echo "  - Checkstyle: $([ ! -z "$VIOLATIONS" ] && echo "⚠️  $VIOLATIONS violations" || echo "✅")"
echo ""

if [ $HAS_ERRORS -eq 1 ]; then
    echo "❌ Se encontraron errores que deben corregirse"
    echo ""
    echo "💡 Comandos para arreglar:"
    echo "   cd frontend && npm run check:fix"
    echo "   cd backend && ./mvnw spotless:apply"
    exit 1
else
    echo "✅ Código verificado correctamente"
    echo ""
    echo "💡 Para arreglar warnings automáticamente:"
    echo "   cd frontend && npm run lint:fix && npm run format"
    exit 0
fi
