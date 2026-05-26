/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package juego;

/*
 * Representa una bala disparada por el héroe.
 * Se mueve hacia arriba hasta salir de pantalla.
 * Usa el sprite Bala.png para su representación visual.
 */
import edu.epromero.util.Lienzo;

/*
 * Representa una bala disparada por el héroe.
 * Se mueve hacia arriba hasta salir de pantalla.
 * Usa el sprite Bala.png para su representación visual.
 */
public class Bala extends ElementoGrafico {

    /* Estados posibles de la bala */
    public static final int INACTIVA = 0;
    public static final int VUELO = 1;
    public static final int EXPLOTANDO = 2;

    /* Estado actual de la bala */
    private int estadoActual;

    /* Contador de frames para la animación de explosión */
    private int contador;

    /* Velocidad de movimiento vertical hacia arriba */
    private static final double VELOCIDAD_BALA = 10.0;

    /* Frames que dura la explosión antes de inactivarse */
    private static final int FRAMES_EXPLOSION = 3;

    /* Límites de la pantalla, se asignan en inicia(canvas) */
    private double limiteDer;
    private double limiteIzq;
    private double limiteSup;
    private double limiteInf;


    /* Constructor vacío: igual que RaquetaSinIA del profe */
    public Bala() {
        super();
        this.estadoActual = INACTIVA;
        this.contador = 0;
        setSprite(".\\resources\\BalaNave.png");
    }

    /*
     * Carga la imagen y guarda los límites del canvas.
     * Se llama desde Heroe.inicia(canvas).
     */
    @Override
    public void inicia(Lienzo canvas) {
        super.inicia(canvas);
        limiteSup = canvas.pideLimiteYMax();
        limiteInf = canvas.pideLimiteYMin();
        limiteDer = canvas.pideLimiteXMax();
        limiteIzq = canvas.pideLimiteXMin();
    }

    /*
     * Lanza la bala desde la posición dada.
     * Solo se activa si está INACTIVA.
     */
    public void disparar(double columna, double renglon) {
        if (estadoActual != INACTIVA) {
            return;
        }
        setColumna(columna);
        setRenglon(renglon);
        estadoActual = VUELO;
        vida = true;
    }

    /*
     * Mueve la bala según su estado.
     * La bala se mueve sola, no necesita leer teclas.
     */
    @Override
    public void mover(Entrada e) {
        switch (estadoActual) {

            case INACTIVA:
                vida = false;
                break;

            case VUELO:
                /* Sube restando al renglón */
                renglon += VELOCIDAD_BALA;

                /* Si sale de pantalla se inactiva */
                if (renglon < limiteInf || renglon > limiteSup//Checa
                        || columna < limiteIzq || columna > limiteDer) {
                    estadoActual = INACTIVA;
                    vida = false;
                }
                break;

            case EXPLOTANDO:
                contador += 1;
                if (contador >= FRAMES_EXPLOSION) {
                    estadoActual = INACTIVA;
                    contador = 0;
                    vida = false;
                }
                break;
        }
    }

    /* Cambia el estado a EXPLOTANDO al recibir un impacto */
    public void explotar() {
        if (estadoActual == VUELO) {
            estadoActual = EXPLOTANDO;
            contador = 0;
        }
    }

    /* Retorna true si la bala está volando, útil para colisiones */
    public boolean estaEnVuelo() {
        return estadoActual == VUELO;
    }

    /* Retorna el estado actual de la bala */
    public int getEstado() {
        return estadoActual;
    }
}
