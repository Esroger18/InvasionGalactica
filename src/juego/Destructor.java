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
import edu.epromero.util.ComportamientoEnemigo;
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
    private static final double VELOCIDAD = 3.0;

    /* Dirección actual: DERECHA o IZQUIERDA (constantes de ElementoGrafico) */
    private int direccion;

    /* Generador de números aleatorios para posición y disparo */
    private Random azar;

    /* Bala del destructor */
    private BalaEnemiga bala;

    /* Contador de frames para controlar el disparo cada 3 segundos.
     * A 20ms por frame, 3 segundos = 150 frames. */
    private int contadorDisparo;
    private static final int FRAMES_DISPARO = 150;

    /* Constructor vacío requerido por el evaluador */
    public Destructor() {
        super();
        this.puntos       = 50;
        this.vidasActuales = 1;
        this.direccion    = DERECHA;
        this.azar         = new Random();
        this.contadorDisparo = 0;
        setSprite("Destructor.png");

        /* Crear la bala del destructor */
        bala = new BalaEnemiga();
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
        columna = limiteIzq + azar.nextDouble() * (limiteDer - limiteIzq);

        /* Aparece en la parte superior */
        renglon = limiteInf + 20;

        /* Activar visibilidad */
        vida = true;
    }

    /*
     * Mueve el destructor de lado a lado.
     * Cuando llega a un borde invierte la dirección.
     * Gestiona el temporizador de disparo.
     */
    @Override
    public void mover(Entrada e) {
        /* Solo moverse si está vivo */
        if (!vida) {
            return;
        }

        /* Mover horizontalmente */
        columna += VELOCIDAD * direccion;

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
        if (contadorDisparo >= FRAMES_DISPARO) {
            disparar();
            contadorDisparo = 0;
        }

        /* Mover la bala cada frame */
        bala.mover(e);
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
    }

    /* Expone la bala para colisiones en Juego.java */
    public BalaEnemiga getBala() {
        return bala;
    }

    /* Retorna los puntos configurados en la anotacion */
    @Override
    public int getPuntos() {
        return puntos;
    }
}