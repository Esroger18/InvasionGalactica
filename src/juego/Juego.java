package juego;

import edu.epromero.util.Lienzo;

/*
 * Motor principal del juego. Punto de entrada.
 * Sigue el patrón del profe igual que el Pong:
 * crea miCanvas, inicia objetos, loop: limpia -> mover -> pinta -> mostrar
 */
public class Juego {

    /* Dimensiones de la ventana */
    private static final int ANCHO = 800;
    private static final int ALTO  = 600;

    /* El lienzo de dibujo, igual que miCanvas en Pong */
    private Lienzo miCanvas;

    /* El héroe del juego */
    private Heroe heroe;

    /* Objeto de entrada compartido */
    private Entrada entrada;

    /*
     * Constructor: crea el lienzo y todos los objetos.
     * Igual que el Juego del profe.
     */
    public Juego() {
        /* Crear y configurar el lienzo */
        miCanvas = new Lienzo();
        miCanvas.ponTamanioLienzo(ANCHO, ALTO);
        miCanvas.ponEscalaX(0, ANCHO);
        miCanvas.ponEscalaY(0, ALTO);

        /* Crear la entrada y asignarle el canvas */
        entrada = new Entrada();
        entrada.setMiCanvas(miCanvas);

        /* Crear el héroe (sin dimensiones, las toma en inicia) */
        heroe = new Heroe();

        /* inicia() carga imágenes y fija posición inicial */
        heroe.inicia(miCanvas);
    }

    /* Mueve todos los elementos del juego */
    public void mover() {
        heroe.mover(entrada);
    }

    /* Pinta todos los elementos del juego */
    public void pinta() {
        heroe.pinta(miCanvas);
    }

    /* Punto de entrada: loop igual al del Pong del profe */
    public static void main(String[] args) {

        Juego juego = new Juego();

        try {
            /* El juego corre mientras el héroe esté visible */
            while (juego.heroe.isVisible()) {
                juego.miCanvas.limpia();
                juego.mover();
                juego.pinta();
                juego.miCanvas.mostrar(20);
            }
        } catch (Exception ex) {
            System.out.println("Hilo interrumpido: " + ex.getMessage());
        }
    }
}