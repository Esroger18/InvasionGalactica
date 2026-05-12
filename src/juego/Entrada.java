package juego;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/*
 * Entrada: Captura el estado del teclado.
 * Extiende KeyAdapter de AWT (no Swing) para escuchar teclas.
 * Se registra en el Lienzo para recibir eventos.
 */
public class Entrada extends KeyAdapter {

    // Estado de cada tecla que nos interesa
    private boolean izquierda;
    private boolean derecha;
    private boolean arriba;
    private boolean abajo;
    private boolean disparo;

    /* Constructor: todo empieza en falso */
    public Entrada() {
        izquierda = false;
        derecha   = false;
        arriba = false;
        abajo   = false;
        disparo   = false;
    }

    /* Se llama automáticamente cuando el jugador PRESIONA una tecla */
    @Override
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();

        /* Flecha izquierda o tecla A */
        if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_A) {
            izquierda = true;
        }

        /* Flecha derecha o tecla D */
        if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_D) {
            derecha = true;
        }

        /* Flecha arriba o tecla W */
        if (codigo == KeyEvent.VK_UP || codigo == KeyEvent.VK_W) {
            arriba = true;
        }
        
        /* Flecha abajo o tecla S */
        if (codigo == KeyEvent.VK_DOWN || codigo == KeyEvent.VK_S) {
            abajo = true;
        }
        
        /* Barra espaciadora para disparar */
        if (codigo == KeyEvent.VK_SPACE) {
            disparo = true;
        }
    }

    /* Se llama automáticamente cuando el jugador SUELTA una tecla */
    @Override
    public void keyReleased(KeyEvent e) {
        int codigo = e.getKeyCode();

        if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_A) {
            izquierda = false;
        }

        if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_D) {
            derecha = false;
        }

        if (codigo == KeyEvent.VK_UP || codigo == KeyEvent.VK_W) {
            arriba = false;
        }
        
        if (codigo == KeyEvent.VK_DOWN || codigo == KeyEvent.VK_S) {
            abajo = false;
        }
        
        if (codigo == KeyEvent.VK_SPACE) {
            disparo = false;
        }
    }

    /* Retorna true si se está presionando izquierda */
    public boolean isIzquierda() { return izquierda; }

    /* Retorna true si se está presionando derecha */
    public boolean isDerecha()   { return derecha;   }
    
    /* Retorna true si se está presionando arriba */
    public boolean isArriba()   { return arriba;   }
    
    /* Retorna true si se está presionando abajo */
    public boolean isAbajo()   { return abajo;   }

    /* Retorna true si se está presionando espacio */
    public boolean isDisparo()   { return disparo;   }
}