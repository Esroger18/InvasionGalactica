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
 * AveDePresa.java
 * Nave enemiga tipo Ave de Presa.
 * Se mueve de arriba hacia abajo por la pantalla.
 * Dispara una bala recta hacia abajo cada 3 segundos.
 * Resiste 3 impactos de la nave rebelde antes de morir.
 * Da 10 puntos al morir.
 */

import edu.epromero.util.Lienzo;
import edu.epromero.util.ComportamientoEnemigo;
import java.util.Random;

/*
 * Ave de presa: nave enemiga que desciende verticalmente.
 * Hereda de NaveEnemiga e implementa Destruible (via NaveEnemiga).
 * El evaluador busca esta clase por nombre para otorgar puntos.
 */
@ComportamientoEnemigo(tipo = "AveDePresa", resistencia = 3, puntos = 10)
public class AveDePresa extends NaveEnemiga {

    /* Velocidad de descenso vertical en pixeles por frame */
    private static final double VELOCIDAD = 2.0;

    /* Generador de posicion aleatoria inicial */
    private Random azar;

    /* Bala que dispara la ave hacia abajo */
    private BalaEnemiga bala;

    /*
     * Contador de frames para controlar el disparo cada 3 segundos.
     * A 20ms por frame => 3 segundos = 150 frames.
     * Se inicializa a la mitad para no disparar al instante.
     */
    private int contadorDisparo;
    private static final int FRAMES_DISPARO = 150;

    /* Constructor vacio requerido por el evaluador para reflexion */
    public AveDePresa() {
        super();
        /* Puntos y resistencia coherentes con la anotacion */
        this.puntos        = 10;
        this.vidasActuales = 3;
        this.azar          = new Random();
        this.contadorDisparo = FRAMES_DISPARO / 2; /* Retraso inicial */
        setSprite("ADP3.png");
        /* Crear la bala de la ave */
        bala = new BalaEnemiga();
        bala.setSprite("BalaADP.png");
        
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
     * Mueve el ave hacia abajo cada frame.
     * Cuando sale por el borde inferior reaparece arriba en columna nueva.
     * Gestiona el temporizador de disparo.
     */
    @Override
    public void mover(Entrada e) {
        /* Solo moverse si esta activa */
        if (!vida) {
            return;
        }

        /* Descender verticalmente */
        renglon -= VELOCIDAD;

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
    }
    
    /*
     * Inactiva la bala inmediatamente.
     * Se llama desde Juego.java cuando el ave muere para
     * evitar que la bala quede pintada en pantalla.
     */
    public void inactivarBala() {
        bala.explotar();
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
