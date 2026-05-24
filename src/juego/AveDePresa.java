package juego;

/**
 *
 * @author Rockzer18
 */
/*
 * AveDePresa.java
 * Nave enemiga tipo Ave de Presa.
 * Se mueve de arriba hacia abajo por la pantalla.
 * Dispara una bala recta hacia abajo cada 3 segundos.
 * Resiste 3 impactos de la nave rebelde antes de morir.
 * Da 10 puntos al morir.
 */

import edu.epromero.util.Lienzo;
import edu.epromero.util.ComportamientoEnemigo;
import edu.epromero.util.FabricaAudio;
import edu.epromero.util.Imagen;
import java.util.Random;

/*
 * Ave de presa: nave enemiga que desciende verticalmente.
 * Hereda de NaveEnemiga e implementa Destruible (via NaveEnemiga).
 * El evaluador busca esta clase por nombre para otorgar puntos.
 */
@ComportamientoEnemigo(tipo = "AveDePresa", resistencia = 3, puntos = 10)
public class AveDePresa extends NaveEnemiga {

    /* Velocidad de descenso vertical en pixeles por frame */
    private static final int VELOCIDAD = 2;

    /* Generador de posicion aleatoria inicial */
    private Random azar;

    /* Bala que dispara la ave hacia abajo */
    private BalaEnemiga bala;
    
     //Audio
    private FabricaAudio audioDisparo;

    /*
     * Contador de frames para controlar el disparo cada 3 segundos.
     * A 20ms por frame => 3 segundos = 150 frames.
     * Se inicializa a la mitad para no disparar al instante.
     */
    private int contadorDisparo;
    
    private static final int FRAMES_DISPARO = 150;

    /* Velocidad actual, se ajusta segun dificultad */
    private int velocidadActual;
 
    /* Frames actuales para disparo, se ajustan segun dificultad */
    private int framesDisparoActual;
    
    private String spriteRandom;
    
    /* Constructor vacio requerido por el evaluador para reflexion */
    public AveDePresa() {
        super();
        /* Puntos y resistencia coherentes con la anotacion */
        this.puntos        = 10;
        this.vidasActuales = 3;
        this.azar          = new Random();
        this.velocidadActual     = VELOCIDAD;
        this.framesDisparoActual = FRAMES_DISPARO;
        this.contadorDisparo = FRAMES_DISPARO / 2; /* Retraso inicial */
        int numero = azar.nextInt(3) + 1; // 1, 2 o 3
        setSprite("ADP" + numero + ".png");
        imgSprite = new Imagen(sprite);
        /*Random random = new Random();
        int numero = random.nextInt(1, 3);
        if (numero == 1){
            spriteRandom = "ADP1.png";
        }
        if(numero == 2){
            spriteRandom = "ADP2.png";
        }
        if(numero == 3){
            spriteRandom = "ADP3.png";
        }
        setSprite(spriteRandom);
        /* Crear la bala de la ave */
        bala = new BalaEnemiga();
        bala.setSprite("BalaADP.png");
        
        audioDisparo = new FabricaAudio();
        
    }

    /*
     * Inicializa posicion aleatoria en la parte superior de la pantalla.
     * Llama super.inicia() para cargar sprite y obtener limites.
     */
    @Override
    public void inicia(Lienzo canvas) {
        super.inicia(canvas);
        bala.inicia(canvas);

        /* Columna aleatoria dentro de los limites horizontales */
        columna = limiteIzq + azar.nextDouble() * (limiteDer - limiteIzq);

        /* Aparece en la parte superior de la pantalla */
        renglon = limiteSup;

        /* Activar visibilidad */
        vida = true;
    }
    
    /*
     * Reaparece en nueva columna aleatoria con vidas restauradas.
     * Llama super.reaparecer() para leer la resistencia de la anotacion.
     */
    @Override
    public void reaparecer() {
        /* Elegir sprite aleatorio al reaparecer */
        int numero = azar.nextInt(3) + 1; // 1, 2 o 3
        setSprite("ADP" + numero + ".png");
        imgSprite = new Imagen(sprite); // Recargar la imagen
    
        /* Restaurar vidas desde la anotacion */
        super.reaparecer();
 
        /* Nueva columna aleatoria en la parte superior */
        columna = limiteIzq + azar.nextDouble() * (limiteDer - limiteIzq);
        renglon = limiteSup;
 
        /* Reiniciar contador de disparo con retraso inicial */
        contadorDisparo = FRAMES_DISPARO / 2;
    }

    /*
     * Mueve el ave hacia abajo cada frame.
     * Cuando sale por el borde inferior reaparece arriba en columna nueva.
     * Gestiona el temporizador de disparo.
     */
    @Override
    public void mover(Entrada e) {
        /* Solo moverse si esta activa */
        if (!vida) {
            framesMuerta += 1;
            if (framesMuerta >= FRAMES_RESPAWN){
                framesMuerta = 0;
                reaparecer();
            }
            return;
        }

        /* Descender verticalmente */
        renglon -= velocidadActual;

        /* Si sale por abajo, reaparece en la parte superior con columna aleatoria */
        if (renglon < limiteInf) {
            renglon = limiteSup - 80;
            columna = limiteIzq + azar.nextDouble() * (limiteDer - limiteIzq);
        }

        /* Contar frames para el disparo cada 3 segundos */
        contadorDisparo += 1;
        if (contadorDisparo >= FRAMES_DISPARO) {
            disparar();
            contadorDisparo = 0;
        }

        /* Mover la bala cada frame */
        bala.mover(e);
    }

    /*
     * Pinta el ave y su bala en el canvas.
     */
    @Override
    public void pinta(Lienzo canvas) {
        super.pinta(canvas);
        /* Solo pintar la bala si el ave esta viva.
         * Si el ave murio, la bala se oculta inmediatamente. */
        if (vida){
            bala.pinta(canvas);
        }
    }

    /*
     * Dispara la bala recta hacia abajo (angulo = 0).
     */
    private void disparar() {
        bala.disparar(columna, renglon, BalaEnemiga.ANGULO_ABAJO);
        audioDisparo.reproducir("Disp3.wav");
    }
    
    /*
     * Inactiva la bala inmediatamente.
     * Se llama desde Juego.java cuando el ave muere para
     * evitar que la bala quede pintada en pantalla.
     */
    public void inactivarBala() {
        bala.explotar();
    }

    /*
     * Ajusta la dificultad segun los puntos acumulados.
     * Tramo 1 (0-499):    velocidad y frecuencia normales.
     * Tramo 2 (500-999):  velocidad x1.5, dispara mas rapido.
     * Tramo 3 (1000+):    velocidad x2,   dispara al maximo.
     */
    public void ajustarDificultad(int puntos) {
        if (puntos >= 1000) {
            velocidadActual      = (int) (VELOCIDAD * 2.0);
            framesDisparoActual  = (int)(FRAMES_DISPARO * 0.5);
        } else if (puntos >= 500) {
            velocidadActual      = (int) (VELOCIDAD * 1.5);
            framesDisparoActual  = (int)(FRAMES_DISPARO * 0.75);
        } else {
            velocidadActual      = VELOCIDAD;
            framesDisparoActual  = FRAMES_DISPARO;
        }
    }
    
    /* Expone la bala para deteccion de colisiones en Juego.java */
    public BalaEnemiga getBala() {
        return bala;
    }

    /* Retorna los puntos configurados en la anotacion */
    @Override
    public int getPuntos() {
        return puntos;
    }
}
