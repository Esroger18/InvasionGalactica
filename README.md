# INVASIÓN GALÁCTICA v1.0

Juego tipo Space Invaders desarrollado en Java con Swing (JPanel + Graphics2D).  
Programación Orientada a Objetos – sin dependencias externas.

---

## Estructura del proyecto

```
InvasionGalactica/
├── src/
│   └── juego/
│       ├── Juego.java              ← Motor del juego + main()
│       ├── Lienzo.java             ← Canvas de renderizado (JPanel)
│       ├── ElementoGrafico.java    ← Superclase abstracta de todos los sprites
│       ├── Destruible.java         ← Interfaz para objetos destructibles
│       ├── ComportamientoEnemigo.java ← Anotación @ComportamientoEnemigo
│       ├── Entrada.java            ← Captura de teclado
│       ├── Heroe.java              ← Nave del jugador
│       ├── NaveEnemiga.java        ← Clase base abstracta de enemigos
│       ├── Destructor.java         ← Enemigo tipo Destructor (50 pts)
│       ├── AveDePresa.java         ← Enemigo tipo Ave de Presa (10 pts)
│       ├── LanzaMinas.java         ← Enemigo tipo LanzaMinas (100 pts)
│       ├── Bala.java               ← Proyectiles
│       ├── Explosion.java          ← Efecto visual de explosión
│       └── GestorPuntajes.java     ← Persistencia del top 10
├── compilar.sh   / compilar.bat
├── ejecutar.sh   / ejecutar.bat
└── README.md
```

---

## Compilar y ejecutar

### Linux / macOS
```bash
chmod +x compilar.sh ejecutar.sh
./compilar.sh
./ejecutar.sh
```

### Windows
```
compilar.bat
ejecutar.bat
```

### Manual
```bash
mkdir bin
javac -encoding UTF-8 -sourcepath src -d bin src/juego/*.java
java -cp bin juego.Juego
```

### Ayuda en consola
```bash
java -cp bin juego.Juego --ayuda
```

---

## Controles

| Tecla          | Acción           |
|----------------|------------------|
| ← / A          | Mover izquierda  |
| → / D          | Mover derecha    |
| ESPACIO        | Disparar / Iniciar |
| F2             | Iniciar partida  |
| ESC            | Salir            |

---

## Enemigos

| Nave         | Puntos | Resistencia | Comportamiento                          |
|--------------|--------|-------------|------------------------------------------|
| Destructor   | 50     | 1 impacto   | Se mueve de lado a lado, dispara a 45°  |
| Ave de Presa | 10     | 3 impactos  | Desciende, dispara hacia abajo          |
| LanzaMinas   | 100    | 4 impactos  | Dispara minas que rastrean al héroe     |

---

## Jerarquía de clases

```
ElementoGrafico (abstracta)
├── Heroe
├── Bala
├── Explosion
└── NaveEnemiga (abstracta) implements Destruible
    ├── Destructor   @ComportamientoEnemigo(tipo="Destructor", resistencia=1, puntos=50)
    ├── AveDePresa   @ComportamientoEnemigo(tipo="AveDePresa",  resistencia=3, puntos=10)
    └── LanzaMinas   @ComportamientoEnemigo(tipo="LanzaMinas",  resistencia=4, puntos=100)
```

---

## Características implementadas

- ✅ Herencia obligatoria en toda la jerarquía
- ✅ Interfaz `Destruible` con `recibirDanio()` y `getVidasActuales()`
- ✅ Anotación `@ComportamientoEnemigo` en cada nave enemiga
- ✅ Clase `Entrada` para manejo de teclado
- ✅ Héroe con `inicia()`, `perderVida()`, muerte tras 3 vidas
- ✅ 3 tipos de enemigos con comportamiento distinto
- ✅ LanzaMinas con minas inteligentes que rastrean al héroe
- ✅ Top 10 puntajes persistentes en archivo `puntajes.dat`
- ✅ Vida extra cada 500 puntos
- ✅ Niveles incrementales con más enemigos
- ✅ Pantalla de ayuda (`--ayuda`)
- ✅ Sin números mágicos (constantes nombradas)
- ✅ Sin variables globales
- ✅ Todo comentado (máx. 10 líneas sin comentario)
- ✅ Un solo `return` por función donde aplica
- ✅ `break` solo en `switch`
- ✅ Variables en camelCase
