package juego;
/*
 * NaveZigZag.java
 * Nave enemiga tipo Zig Zag.
 * Se mueve de izquierda a derecha o derecha iazquierda con un patron
 * senoidal en el eje vertical.
 * No dispara, su peligro es el contacto directo con el heroe.
 * Resiste 2 impactos de la nave rebelde antes de morir.
 * Da 30 puntos al morir.
 * Al morir reaparece automaticamente en nueva columna aleatoria.
 */

import edu.epromero.util.Lienzo;
import edu.epromero.util.ComportamientoEnemigo;
import java.util.Random;

@ComportamientoEnemigo(tipo = "NaveZigZag", resistencia = 2, puntos = 30)
public class NaveZigZag extends NaveEnemiga {

    //Velocidad base 
    private static final double VELOCIDAD_BASE = 2.5;

    //Amplitud del zigzag en pixeles: que tan ancho oscila
    private static final double AMPLITUD = 80.0;

    // Velocidad del seno: que tan rapido oscila vertical
    private static final double VELOCIDAD_SENO = 0.07;

    //Frames de espera antes de reaparecer tras morir 
    private static final int FPS_RESPAWN = 150;

    // Velocidad actual, se ajusta segun dificultad
    private double velocidadActual;

    //Columna central alrededor de la cual oscila el zigzag
    private double columnaBase;

    //Angulo actual del seno, crece cada frame
    private double anguloSeno;

    // Contador de frames muerta para el cooldown de respawn
    private int fpsMuerta;

    //Generador de posicion aleatoria
    private Random azar;

    //Direccion actual de movimiento horizontal: DERECHA o IZQUIERDA
    private int direccion;
    
    //Renglon base alrededor del cual oscila el zigzag en Y
    private static final double RENGLON = 200;
    
    // Constructor vacio
    public NaveZigZag() {
        super();
        //Puntos y resistencia coherentes con la anotacion
        this.puntos = 30;
        this.vidasActuales = 2;
        this.azar = new Random();
        this.velocidadActual = VELOCIDAD_BASE;
        this.anguloSeno = 0;
        this.framesMuerta = 0;
        this.columnaBase = 0;
        setSprite(".\\resources\\ADP1.png");
    }
    /*
     * Inicializa posicion aleatoria en la parte superior.
     * Llama super.inicia() para cargar sprite y obtener limites.
     */
    @Override
    public void inicia(Lienzo canvas) {
        super.inicia(canvas);
        elegirLado();
        vida = true;
    }
    
    /*
     * Elige aleatoriamente desde que lado aparece y en que renglon.
     * Si sale por la izquierda va hacia la derecha y viceversa.
     * El renglon base es aleatorio respetando la amplitud para no salir.
     */
    private void elegirLado() {
        /* Elegir lado aleatorio: 0 = izquierda, 1 = derecha */
        int lado = azar.nextInt(2);
 
        if (lado == 0) {
            /* Aparece en el borde izquierdo y va hacia la derecha */
            columna   = limiteIzq;
            direccion = DERECHA;
        } else {
            /* Aparece en el borde derecho y va hacia la izquierda */
            columna   = limiteDer;
            direccion = IZQUIERDA;
        }
        /* Renglon siempre fijo, solo oscila alrededor de este */
        renglon = RENGLON;
        anguloSeno = 0;
    }
 
    /*
     * Reaparece desde un lado aleatorio con vidas restauradas.
     * Llama super.reaparecer() para leer la resistencia de la anotacion.
        
        /*
     * Reaparece en nueva columna aleatoria con vidas restauradas.
     * Llama super.reaparecer() para leer la resistencia de la anotacion.
     */
    @Override
    public void reaparecer() {
        /* Restaurar vidas desde la anotacion */
        super.reaparecer();
        /* Elegir nuevo lado y renglon aleatorio */
        elegirLado();
        /* Reiniciar contador de frames muerta */
        framesMuerta = 0;
    }

    /*
     * Mueve la nave hacia abajo con patron senoidal en el eje horizontal.
     * Si esta muerta espera el cooldown antes de reaparecer.
     * Si sale por el borde inferior reaparece arriba en columna nueva.
     */
    @Override
    public void mover(Entrada e) {
        /* Si esta muerta, esperar cooldown antes de reaparecer */
        if (!vida) {
            framesMuerta += 1;
            if (framesMuerta >= FRAMES_RESPAWN) {
                reaparecer();
            }
            return;
        }

        /* Mover horizontalmente segun la direccion actual */
        columna += velocidadActual * direccion;
 
        /* Avanzar el angulo del seno cada frame */
        anguloSeno += VELOCIDAD_SENO;
 
        /* Oscilar verticalmente alrededor del renglon fijo */
        renglon = RENGLON + Math.sin(anguloSeno) * AMPLITUD;

        /* Si sale por cualquier borde horizontal, elegir nuevo lado */
        if (columna > limiteDer || columna < limiteIzq) {
            elegirLado();
        }
    }

    /*
     * Pinta la nave en el canvas.
     * No tiene balas que pintar.
     */
    @Override
    public void pinta(Lienzo canvas) {
        super.pinta(canvas);
    }

    /*
     * Ajusta la dificultad segun los puntos acumulados.
     * Tramo 1 (0-499):    velocidad normal.
     * Tramo 2 (500-999):  velocidad x1.5.
     * Tramo 3 (1000+):    velocidad x2.
     */
    /*public void ajustarDificultad() {
        if (velocidadActual < 8){
            velocidadActual = velocidadActual * 1.5;
        }
    }

    /* Retorna los puntos configurados en la anotacion */
    @Override
    public int getPuntos() {
        return puntos;
    }
}