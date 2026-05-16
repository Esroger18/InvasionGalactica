/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package juego;

/**
 *
 * @author Rockzer18
 */
/*
 * BalaLanzaMinas.java
 * Bala especial disparada por el LanzaMinas.
 * A diferencia de las otras balas, esta sigue al heroe:
 * solo puede cambiar 1 columna y 1 renglon por movimiento.
 * Segun las especificaciones del proyecto.
 */

import edu.epromero.util.Lienzo;

/*
 * Bala inteligente que persigue la posicion del heroe.
 * Avanza maximo 1 pixel en X y 1 pixel en Y por frame
 * en direccion al objetivo registrado al momento del disparo.
 */
public class BalaLanzaMinas extends ElementoGrafico {

    /* Estados posibles de la bala */
    public static final int INACTIVA   = 0;
    public static final int VUELO      = 1;
    public static final int EXPLOTANDO = 2;

    /* Estado actual */
    private int estadoActual;

    /* Contador para animacion de explosion */
    private int contador;
    private static final int FPS_EXPLOSION = 3;

    /*
     * Velocidad maxima: 1 unidad por frame en cada eje.
     * Segun especificacion: "solo pueden cambiar un renglon
     * y una columna por movimiento".
     */
    private static final double PASO = 1.0;

    /* Posicion objetivo (del heroe) actualizada cada frame */
    private double objetivoColumna;
    private double objetivoRenglon;

    /* Limites de pantalla obtenidos en inicia(canvas) */
    private double limiteDer;
    private double limiteIzq;
    private double limiteSup;
    private double limiteInf;

    /* Constructor vacio */
    public BalaLanzaMinas() {
        super();
        this.estadoActual    = INACTIVA;
        this.contador        = 0;
        this.objetivoColumna = 0;
        this.objetivoRenglon = 0;
        setSprite("Bala_Enemiga.png");
    }

    /*
     * Carga imagen y guarda limites del canvas.
     */
    @Override
    public void inicia(Lienzo canvas) {
        super.inicia(canvas);
        limiteDer = canvas.pideLimiteXMax();
        limiteIzq = canvas.pideLimiteXMin();
        limiteSup = canvas.pideLimiteYMax();
        limiteInf = canvas.pideLimiteYMin();
    }

    /*
     * Activa la bala desde la posicion del LanzaMinas.
     * Registra la posicion inicial del heroe como objetivo.
     * Solo dispara si esta INACTIVA.
     */
    public void disparar(double colOrigen, double renOrigen,
                         double colHeroe,  double renHeroe) {
        if (estadoActual != INACTIVA) {
            return;
        }
        setColumna(colOrigen);
        setRenglon(renOrigen);
        this.objetivoColumna = colHeroe;
        this.objetivoRenglon = renHeroe;
        estadoActual = VUELO;
        vida = true;
    }

    /*
     * Actualiza el objetivo con la posicion actual del heroe.
     * Se llama cada frame desde LanzaMinas.mover().
     */
    public void actualizarObjetivo(double colHeroe, double renHeroe) {
        this.objetivoColumna = colHeroe;
        this.objetivoRenglon = renHeroe;
    }

    /*
     * Mueve la bala un paso hacia el objetivo cada frame.
     * Maximo 1 unidad en columna y 1 en renglon por movimiento.
     */
    @Override
    public void mover(Entrada e) {
        switch (estadoActual) {

            case INACTIVA:
                vida = false;
                break;

            case VUELO:
                /* Mover 1 paso hacia la columna objetivo */
                if (columna < objetivoColumna) {
                    columna += PASO;
                } else if (columna > objetivoColumna) {
                    columna -= PASO;
                }

                /* Mover 1 paso hacia el renglon objetivo */
                if (renglon < objetivoRenglon) {
                    renglon += PASO;
                } else if (renglon > objetivoRenglon) {
                    renglon -= PASO;
                }

                /* Inactivar si sale de pantalla */
                if (renglon > limiteSup || renglon < limiteInf
                        || columna > limiteDer || columna < limiteIzq) {
                    estadoActual = INACTIVA;
                    vida = false;
                }
                break;

            case EXPLOTANDO:
                contador += 1;
                if (contador >= FPS_EXPLOSION) {
                    estadoActual = INACTIVA;
                    contador     = 0;
                    vida         = false;
                }
                break;
        }
    }

    /* Activa explosion al colisionar con el heroe */
    public void explotar() {
        if (estadoActual == VUELO) {
            estadoActual = EXPLOTANDO;
            contador     = 0;
        }
    }

    /* Retorna true si la bala esta en vuelo, para colisiones */
    public boolean estaEnVuelo() {
        return estadoActual == VUELO;
    }

    /* Retorna el estado actual */
    public int getEstado() {
        return estadoActual;
    }
}