#!/bin/bash
# ─────────────────────────────────────────────────────────────
# compilar.sh  –  Compila el proyecto Invasión Galáctica
# Uso: ./compilar.sh
# ─────────────────────────────────────────────────────────────

DIRECTORIO_SRC="src"
DIRECTORIO_OUT="bin"

echo "╔══════════════════════════════════════╗"
echo "║   INVASIÓN GALÁCTICA – Compilador    ║"
echo "╚══════════════════════════════════════╝"

# Crear directorio de salida si no existe
mkdir -p "$DIRECTORIO_OUT"

# Compilar todas las clases del paquete juego
echo "Compilando fuentes..."
javac -encoding UTF-8 -sourcepath "$DIRECTORIO_SRC" \
      -d "$DIRECTORIO_OUT" \
      "$DIRECTORIO_SRC"/juego/*.java

if [ $? -eq 0 ]; then
    echo "✔  Compilación exitosa. Clases en ./$DIRECTORIO_OUT"
    echo ""
    echo "Para ejecutar: ./ejecutar.sh"
    echo "Para ayuda:    ./ejecutar.sh --ayuda"
else
    echo "✘  Error de compilación. Revisa los mensajes anteriores."
    exit 1
fi
