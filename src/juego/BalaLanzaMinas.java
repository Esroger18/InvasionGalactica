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
    
    /*
     * Direccion fija calculada al disparar hacia el heroe.
     * -1 = izquierda/abajo, 0 = recto, 1 = derecha/arriba.
     * Se calcula una sola vez al disparar y no cambia,
     * garantizando que la bala siempre avance y salga de pantalla.
     */
    private int dirColumna;
    private int dirRenglon;
    
    /* Posicion objetivo (del heroe) actualizada cada frame
    private double objetivoColumna;
    private double objetivoRenglon;*/

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
        this.dirColumna = 0;
        this.dirRenglon = -1;
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
        
        /* Calcular direccion en columna hacia el heroe */
        if (colHeroe > colOrigen) {
            dirColumna = 1;
        } else if (colHeroe < colOrigen) {
            dirColumna = -1;
        } else {
            dirColumna = 0;
        }
 
        /* Calcular direccion en renglon hacia el heroe */
        if (renHeroe < renOrigen) {
            dirRenglon = -1;
        } else if (renHeroe > renOrigen) {
            dirRenglon = 1;
        } else {
            dirRenglon = -1; /* Si esta al mismo nivel, baja por defecto */
        }
        
        estadoActual = VUELO;
        vida = true;
    }
    
    /*
     * Mueve la bala 1 paso por eje en la direccion fija calculada al disparar.
     * La direccion es constante: siempre avanza y sale de pantalla.
     */
    
    @Override
    public void mover(Entrada e) {
        switch (estadoActual) {

            case INACTIVA:
                vida = false;
                break;

            case VUELO:
                /* Avanzar 1 pixel en cada eje segun la direccion fija */
                columna += PASO * dirColumna;
                renglon += PASO * dirRenglon;
                
                /* Inactivar cuando sale de los limites de pantalla */
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