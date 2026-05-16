package juego;

import edu.epromero.util.Lienzo;

/*
 * Objeto de datos que transporta información de entrada
 * Guarda referencia al canvas para leer teclas
 */
import edu.epromero.util.Lienzo;

/*
 * Objeto de datos que transporta información de entrada.
 * Guarda referencia al canvas para leer teclas,
 * igual que en el Pong del profe.
 */
public class Entrada {

    /* Referencia al lienzo para leer teclado */
    private Lienzo miCanvas;

    /* Tecla actualmente pulsada */
    private int tecla;

    /* Getter y setter del canvas */
    public Lienzo getMiCanvas() {
        return miCanvas;
    }

    public void setMiCanvas(Lienzo canvas) {
        this.miCanvas = canvas;
    }

    /* Getter y setter de la tecla */
    public int getTecla() {
        return tecla;
    }

    public void setTecla(int tecla) {
        this.tecla = tecla;
    }
}