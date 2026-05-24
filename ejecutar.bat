@echo off
REM ─────────────────────────────────────────────────────────────
REM ejecutar.bat  –  Ejecuta el juego Invasión Galáctica
REM Uso: ejecutar.bat [--ayuda]
REM ─────────────────────────────────────────────────────────────

if not exist bin (
    echo No se encontro el directorio de clases. Ejecuta primero compilar.bat
    pause
    exit /b 1
)

java -cp bin juego.Juego %*
