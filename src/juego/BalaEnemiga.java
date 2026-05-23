/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package juego;

/**
 *
 * @author Rockzer18
 */
import edu.epromero.util.Lienzo;
 
/*
 * Bala disparada por las naves enemigas.
 * Puede moverse hacia abajo, en angulo de 45 grados,
 * o siguiendo al heroe (para LanzaMinas).
 * TODO: asigna el sprite cuando tengas la imagen de bala enemiga.
 */
public class BalaEnemiga extends ElementoGrafico {
 
    /* Estados posibles */
    public static final int INACTIVA   = 0;
    public static final int VUELO      = 1;
    public static final int EXPLOTANDO = 2;
 
    /* Angulos de disparo para el Destructor */
    public static final int ANGULO_IZQ  = -1; /* 45 grados izquierda */
    public static final int ANGULO_DER  =  1; /* 45 grados derecha   */
    public static final int ANGULO_ABAJO = 0; /* Recto hacia abajo   */
 
    /* Estado actual */
    private int estadoActual;
 
    /* Contador para animacion de explosion */
    private int contador;
    private static final int FPS_EXPLOSION = 3;
 
    /* Velocidad base de la bala */
    private static final double VELOCIDAD_BALA = 6.0;
 
    /* Direccion horizontal: -1 izq, 0 recto, 1 der */
    private int angulo;
 
    /* Limites de pantalla obtenidos en inicia(canvas) */
    private double limiteDer;
    private double limiteIzq;
    private double limiteSup;
    private double limiteInf;
 
    /* Constructor vacio */
    public BalaEnemiga() {
        super();
        this.estadoActual = INACTIVA;
        this.contador     = 0;
        this.angulo       = ANGULO_ABAJO;
        setSprite("");
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
     * Activa la bala desde la posicion dada con el angulo indicado.
     * Solo dispara si esta INACTIVA.
     */
    public void disparar(double columna, double renglon, int angulo) {
        if (estadoActual != INACTIVA) {
            return;
        }
        setColumna(columna);
        setRenglon(renglon);
        this.angulo = angulo;
        estadoActual = VUELO;
        vida = true;
    }
    
    /*
     * Mueve la bala segun su estado y angulo.
     */
    @Override
    public void mover(Entrada e) {
        switch (estadoActual) {
 
            case INACTIVA:
                vida = false;
                break;
 
            case VUELO:
                /* Siempre baja */
                renglon -= VELOCIDAD_BALA;
 
                /* Componente horizontal segun angulo */
                columna -= VELOCIDAD_BALA * angulo;
 
                /* Si sale de pantalla se inactiva */
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
 