package juego;

/**
 *
 * @author Rockzer18
 */

import edu.epromero.util.Lienzo;
import edu.epromero.util.ComportamientoEnemigo;
import edu.epromero.util.FabricaAudio;
import java.util.Random;

/*
 * Nave enemiga tipo Destructor.
 * Se mueve de lado a lado en la parte superior de la pantalla.
 * Dispara una bala cada 3 segundos en angulo de 45 grados.
 * Da 50 puntos al morir y muere con 1 impacto.
 */
@ComportamientoEnemigo(tipo = "Destructor", resistencia = 1, puntos = 50)
public class Destructor extends NaveEnemiga {

    /* Velocidad de movimiento horizontal */
    private static final int VELOCIDAD = 3;
    
    /* Velocidad actual, se ajusta segun dificultad */
    private int velocidadActual;

    /* Dirección actual: DERECHA o IZQUIERDA (constantes de ElementoGrafico) */
    private int direccion;

    /* Generador de números aleatorios para posición y disparo */
    private Random azar;

    /* Bala del destructor */
    private BalaEnemiga bala;
    
    //Audio
    private FabricaAudio audioDisparo;
    
    /* Frames actuales para disparo, se ajustan segun dificultad */
    private int framesDisparoActual;

    /* Contador de frames para controlar el disparo cada 3 segundos.
     * A 20ms por frame, 3 segundos = 150 frames. */
    private int contadorDisparo;
    private static final int FRAMES_DISPARO = 150;

    /* Constructor vacío requerido por el evaluador */
    public Destructor() {
        super();
        this.puntos = 50;
        this.vidasActuales = 1;
        this.direccion = DERECHA;
        this.azar = new Random();
        this.contadorDisparo = 0;
        this.velocidadActual = VELOCIDAD;
        this.framesDisparoActual = FRAMES_DISPARO;
        setSprite(".\\resources\\Destructor.png");
        /* Crear la bala del destructor */
        bala = new BalaEnemiga();
        bala.setSprite(".\\resources\\BalaDestructor.png");
        //iNICIALIZAR AUDIO
        audioDisparo = new FabricaAudio();
    }
    /*
     * Inicializa posición aleatoria en la parte superior.
     * Llama super.inicia() para cargar sprite y límites.
     */
    @Override
    public void inicia(Lienzo canvas) {
        super.inicia(canvas);
        bala.inicia(canvas);
        
        /* Columna aleatoria dentro de los límites */
        columna = limiteIzq + azar.nextDouble()*(limiteDer - limiteIzq);

        /* Aparece en la parte superior */
        renglon = limiteSup-310;

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
        renglon = limiteSup - 310;
 
        /* Reiniciar direccion y contador de disparo */
        direccion        = DERECHA;
        contadorDisparo  = 0;
    }
    
    /*
     * Mueve el destructor de lado a lado.
     * Cuando llega a un borde invierte la dirección.
     * Gestiona el temporizador de disparo.
     */
    @Override
    public void mover(Entrada e) {
        /* Mover la bala cada frame */
        bala.mover(e);
        /* Solo moverse si está vivo */
        if (!vida) {
            framesMuerta += 1;
            if (framesMuerta >= FRAMES_RESPAWN){
                framesMuerta = 0;
                reaparecer();
            }
            return;
        }

        /* Mover horizontalmente */
        columna += velocidadActual * direccion;

        /* Rebotar en los bordes */
        if (columna >= limiteDer) {
            columna    = limiteDer;
            direccion  = IZQUIERDA;
        }
        if (columna <= limiteIzq) {
            columna    = limiteIzq;
            direccion  = DERECHA;
        }

        /* Contar frames para el disparo */
        contadorDisparo += 1;
        if (contadorDisparo >= framesDisparoActual) {
            disparar();
            contadorDisparo = 0;
        }
    }

    /*
     * Pinta el destructor y su bala.
     */
    @Override
    public void pinta(Lienzo canvas) {
        super.pinta(canvas);
        bala.pinta(canvas);
    }

    /*
     * Dispara la bala en angulo de 45 grados.
     * Aleatoriamente hacia la izquierda o la derecha.
     */
    private void disparar() {
        /* 0 = izquierda, 1 = derecha */
        int ladoDisparo = azar.nextInt(2);
        if (ladoDisparo == 0) {
            bala.disparar(columna, renglon, BalaEnemiga.ANGULO_IZQ);
        } else {
            bala.disparar(columna, renglon, BalaEnemiga.ANGULO_DER);
        }
        audioDisparo.reproducir(".\\resources\\Disp2.wav");
    }

    /* Expone la bala para colisiones en Juego.java */
    public BalaEnemiga getBala() {
        return bala;
    }

    //REVISAR ESTO ALRATO
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
    /*public void ajustarDificultad() {
        if (velocidadActual < 6){
            velocidadActual     = (int)(velocidadActual * 1.5);
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