#!/bin/bash
# ─────────────────────────────────────────────────────────────
# ejecutar.sh  –  Ejecuta el juego Invasión Galáctica
# Uso: ./ejecutar.sh [--ayuda]
# ─────────────────────────────────────────────────────────────

DIRECTORIO_OUT="bin"

# Verificar que existan las clases compiladas
if [ ! -d "$DIRECTORIO_OUT" ]; then
    echo "No se encontró el directorio de clases. Ejecuta primero ./compilar.sh"
    exit 1
fi

# Pasar cualquier argumento al programa Java
java -cp "$DIRECTORIO_OUT" juego.Juego "$@"
