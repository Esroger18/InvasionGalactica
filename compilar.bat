@echo off
REM ─────────────────────────────────────────────────────────────
REM compilar.bat  –  Compila el proyecto Invasión Galáctica
REM Uso: compilar.bat
REM ─────────────────────────────────────────────────────────────

set SRC=src
set OUT=bin

echo ╔══════════════════════════════════════╗
echo ║   INVASIÓN GALÁCTICA – Compilador    ║
echo ╚══════════════════════════════════════╝

if not exist %OUT% mkdir %OUT%

echo Compilando fuentes...
javac -encoding UTF-8 -sourcepath %SRC% -d %OUT% %SRC%\juego\*.java

if %errorlevel% == 0 (
    echo Compilacion exitosa. Clases en .\%OUT%
    echo Para ejecutar: ejecutar.bat
) else (
    echo Error de compilacion.
)
