package juego;
/**
 *
 * @author Rockzer18
 */
/*
 * LanzaMinas.java
 * Nave enemiga tipo Lanza Minas.
 * Solo hay uno en pantalla; al morir reaparece en columna nueva.
 * Dispara 1 bala cada 5 segundos que persigue al heroe.
 * Resiste 4 impactos de la nave rebelde antes de morir.
 * Da 100 puntos al morir.
 */

import edu.epromero.util.Lienzo;
import edu.epromero.util.ComportamientoEnemigo;
import edu.epromero.util.FabricaAudio;
import java.util.Random;

/*
 * Lanza Minas: nave que permanece en la parte superior
 * y dispara minas inteligentes que siguen al heroe.
 * Hereda de NaveEnemiga e implementa Destruible (via NaveEnemiga).
 * El evaluador busca esta clase por nombre para otorgar puntos.
 */
@ComportamientoEnemigo(tipo = "LanzaMinas", resistencia = 4, puntos = 100)

public class LanzaMinas extends NaveEnemiga {

    /* Velocidad de movimiento horizontal lento */
    private static final int VELOCIDAD = 2;

    /* Direccion actual de movimiento horizontal */
    private int direccion;
    
    /* Velocidad actual, se ajusta segun dificultad */
    private double velocidadActual;
 
    /* Frames actuales para disparo, se ajustan segun dificultad */
    private int framesDisparoActual;
 

    /* Generador de posicion aleatoria */
    private Random azar;

    /* Bala de forma radial */
    private static final int TOTAL_BALAS = 8;
    private BalaEnemiga[] balas;
    
     /*
     * Direcciones de las 8 balas: [dirColumna, dirRenglon]
     * Forman una estrella: arriba, arriba-der, der, abajo-der,
     * abajo, abajo-izq, izq, arriba-izq
     */
    private static final int[][] DIRECCIONES = {
        { 0,  1},  /* Arriba        */
        { 1,  1},  /* Arriba-der    */
        { 1,  0},  /* Derecha       */
        { 1, -1},  /* Abajo-der     */
        { 0, -1},  /* Abajo         */
        {-1, -1},  /* Abajo-izq     */
        {-1,  0},  /* Izquierda     */
        {-1,  1}   /* Arriba-izq    */
    };

    /*
     * Contador de frames para el disparo cada 5 segundos.
     * A 20ms por frame => 5 segundos = 250 frames.
     * Se inicializa a mitad para no disparar al instante.
     */
    private int contadorDisparo;
    private static final int FRAMES_DISPARO = 250;
    
    private FabricaAudio audioDisparo;

    /* Constructor vacio requerido por el evaluador para reflexion */
    public LanzaMinas() {
        super();
        /* Puntos y resistencia coherentes con la anotacion */
        this.puntos        = 100;
        this.vidasActuales = 4;
        this.direccion     = DERECHA;
        this.azar          = new Random();
        this.velocidadActual     = VELOCIDAD;
        this.framesDisparoActual = FRAMES_DISPARO;
        this.contadorDisparo = FRAMES_DISPARO / 2; /* Retraso inicial */
        setSprite(".\\resources\\LMinas.png");
        
        //Crear las 8 balas del disparo radial
        balas = new BalaEnemiga[TOTAL_BALAS];
        for (int i = 0; i < TOTAL_BALAS; i++){
            balas[i] = new BalaEnemiga();
            balas[i].setSprite(".\\resources\\BalaLMinas2.png");
        }
        //inicializar el audio
        audioDisparo = new FabricaAudio();
    }

    /*
     * Inicializa posicion aleatoria en la parte superior.
     * Llama super.inicia() para cargar sprite y obtener limites.
     */
    @Override
    public void inicia(Lienzo canvas) {
        super.inicia(canvas);
        //Inicializa cada bala con el canvas
        for (int i = 0; i < TOTAL_BALAS; i++){
            balas[i].inicia(canvas);
        }

        /* Columna aleatoria dentro de los limites */
        columna = limiteIzq + azar.nextDouble() * (limiteDer - limiteIzq);

        /* Aparece en la parte superior */
        renglon = limiteSup - 120;

        /* Activar visibilidad */
        vida = true;
    }

    /*
     * Reaparece en nueva columna aleatoria con vidas restauradas.
     * Llama super.reaparecer() para leer la resistencia de la anotacion.
     */
    @Override
    public void reaparecer() {
        /* Restaurar vidas desde la anotacion */
        super.reaparecer();
 
        /* Nueva columna aleatoria en la parte superior */
        columna = limiteIzq + azar.nextDouble() * (limiteDer - limiteIzq);
        renglon = limiteSup - 120;
 
        /* Reiniciar direccion y contador de disparo con retraso inicial */
        direccion       = DERECHA;
        contadorDisparo = FRAMES_DISPARO / 2;
    }

    /*
     * Mueve el LanzaMinas de lado a lado en la parte superior.
     * Gestiona el temporizador de disparo.
     * Al morir reaparece en columna nueva con vidas restauradas.
     */
    @Override
    public void mover(Entrada e) {
        /* Solo moverse si esta activo */
        if (!vida) {
            /* Reaparecer en nueva posicion con vidas restauradas */
            reaparecer();
            return;
        }

        /* Mover horizontalmente */
        columna += VELOCIDAD * direccion;

        /* Rebotar en los bordes */
        if (columna >= limiteDer) {
            columna   = limiteDer;
            direccion = IZQUIERDA;
        }
        if (columna <= limiteIzq) {
            columna   = limiteIzq;
            direccion = DERECHA;
        }

        /* Contar frames para el disparo cada 5 segundos */
        contadorDisparo += 1;
        if (contadorDisparo >= framesDisparoActual) {
            disparar();
            contadorDisparo = 0;
        }
        /* ── LÍNEA FALTANTE: mover las 8 balas cada frame ── */
        for (int i = 0; i < TOTAL_BALAS; i++) {
            balas[i].mover(e);
        }
    }
    /*
     * Pinta el LanzaMinas y su bala en el canvas.
     */
    @Override
    public void pinta(Lienzo canvas) {
        super.pinta(canvas);
        /* Solo pintar las balas si el LanzaMinas esta vivo */
        if (vida) {
            for (int i = 0; i < TOTAL_BALAS; i++) {
                balas[i].pinta(canvas);
            }
        }
    }
    /*
     * Dispara la bala inteligente hacia la posicion actual del heroe.
     */
    private void disparar(){
        for (int i = 0; i < TOTAL_BALAS; i++) {
            balas[i].disparar(columna, renglon,
                    DIRECCIONES[i][0], DIRECCIONES[i][1]);
        }
        /* Sonido del disparo radial */
        audioDisparo.reproducir(".\\resources\\Disp3.wav");
    }

  

    /*
     * Expone las balas para deteccion de colisiones en Juego.java.
     * Retorna el arreglo completo de las 8 balas.
     */
    public BalaEnemiga[] getBalas() {
        return balas;
    }
    
    /*
     * Ajusta la dificultad segun los puntos acumulados.
     * Tramo 1 (0-499):    velocidad y frecuencia normales.
     * Tramo 2 (500-999):  velocidad x1.5, dispara mas rapido.
     * Tramo 3 (1000+):    velocidad x2,   dispara al maximo.
     */
    /*public void ajustarDificultad() {
        if (velocidadActual < 10){
            velocidadActual     = velocidadActual * 1.5;
        }
        if (framesDisparoActual > 40){
            framesDisparoActual = (int)(framesDisparoActual * 0.67);
        }
    }
    /* Retorna los puntos configurados en la anotacion */
    @Override
    public int getPuntos() {
        return puntos;
    }
}
