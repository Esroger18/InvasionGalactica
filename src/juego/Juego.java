package juego;

import edu.epromero.util.Lienzo;

/*
 * Juego: Clase principal y motor del videojuego.
 * Contiene el Game Loop, instancia el Lienzo y es el punto de entrada.
 */
public class Juego {

    // Dimensiones de la ventana
    private static final int ANCHO = 800;
    private static final int ALTO  = 600;

    // Pausa entre ciclos en milisegundos (aprox. 60 FPS)
    //IIIIIIAAAAAA
    private static final int MS_POR_CICLO = 16;

    public static void main(String[] args) {

        Lienzo miCanvas = new Lienzo();
        miCanvas.ponTamanioLienzo(ANCHO, ALTO);
        miCanvas.ponEscalaX(0, ANCHO);
        miCanvas.ponEscalaY(0, ALTO);

        /* Crear el objeto de entrada (teclado) */
        Entrada teclado = new Entrada();

        /*
         * TODO: registrar el listener de teclado en el Lienzo.
         * Revisa en el Javadoc si Lienzo tiene un método como:
         *   miCanvas.addKeyListener(entrada)
         * o si hereda de Canvas/Frame y se hace diferente.
         */
        miCanvas.addKeyListener(teclado);

        /* Crear al héroe en el centro inferior de la pantalla */
        Heroe heroe = new Heroe(ANCHO, ALTO);
        heroe.aparecer(); // Activarlo para que sea visible

        /*
         * TODO: verificar en el Javadoc si el Lienzo necesita
         * algún método para "iniciar" o "mostrar" antes del loop,
         * por ejemplo: miCanvas.inicia() o miCanvas.setVisible(true)
         */

        /* ── GAME LOOP ─────────────────────────────────────── */
        boolean corriendo = true;

        while (corriendo) {

            /* 1. Leer entrada y mover al héroe */
            heroe.Mueve(entrada);

            /* 2. Limpiar la pantalla antes de redibujar */
            /*
             * TODO: verificar en el Javadoc el método para limpiar
             * el lienzo. Puede ser algo como:
             *   miCanvas.limpiar()
             *   miCanvas.borrar()
             *   miCanvas.ponFondo(r, g, b)
             */
            // miCanvas.limpiar();

            /* 3. Dibujar al héroe */
            heroe.dibujar(miCanvas);

            /* 4. Mostrar lo dibujado en pantalla */
            /*
             * TODO: verificar en el Javadoc el método para
             * "presentar" o "volcar" el buffer a pantalla. Puede ser:
             *   miCanvas.pinta()
             *   miCanvas.actualiza()
             *   miCanvas.repaint()
             */
            // miCanvas.pinta();

            /* 5. Esperar para mantener la velocidad del juego */
            try {
                Thread.sleep(MS_POR_CICLO);
            } catch (InterruptedException ex) {
                corriendo = false;
            }
        }
    }
}
