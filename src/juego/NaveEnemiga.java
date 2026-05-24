/*
 * NaveEnemiga.java
 * Clase base de todas las naves enemigas.
 * Extiende ElementoGrafico e implementa Destruible.
 * Cada subclase debe agregar @ComportamientoEnemigo con sus valores.
 */
package juego;

import edu.epromero.util.Lienzo;
import edu.epromero.util.Destruible;
import edu.epromero.util.ComportamientoEnemigo;

@ComportamientoEnemigo(tipo = "NaveEnemiga", resistencia = 1, puntos = 0)
abstract public class NaveEnemiga extends ElementoGrafico implements Destruible {

    /* Vidas actuales de la nave, se asignan en el constructor */
    protected int vidasActuales;

    /* Puntos que otorga al morir, cada subclase los define */
    protected int puntos;

    /* Limites de la pantalla, se obtienen en inicia(canvas) */
    protected double limiteDer;
    protected double limiteIzq;
    protected double limiteSup;
    protected double limiteInf;
    
    //Cooldown respawn
    protected int framesMuerta;
    protected static final int FRAMES_RESPAWN = 120;

    /* Constructor vacio requerido por el evaluador para reflexion */
    public NaveEnemiga() {
        super();
        vidasActuales = 1;
        puntos        = 0;
    }

    /*
     * Guarda los limites del canvas para usarlos en mover().
     * Cada subclase llama super.inicia(canvas) y luego
     * asigna su posicion inicial aleatoria.
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
     * Reactiva la nave leyendo la resistencia directamente
     * de la anotacion @ComportamientoEnemigo para evitar
     * numeros magicos en las subclases.
     * Cada subclase hace @Override para agregar su posicion aleatoria
     * y llama super.reaparecer() para restaurar las vidas.
     */
    public void reaparecer() {
        /* Leer la anotacion de la subclase concreta en tiempo de ejecucion */
        ComportamientoEnemigo anotacion =
            this.getClass().getAnnotation(ComportamientoEnemigo.class);

        /* Si tiene anotacion, tomar la resistencia de ahi */
        if (anotacion != null) {
            vidasActuales = anotacion.resistencia();
        }
        /* Reactivar visibilidad */
        vida = true;
    }

    /*
     * Resta una vida y retorna true solo en el impacto fatal.
     * Implementacion del contrato Destruible.
     * Si ya esta muerta, no hace nada (estabilidad post-mortem).
     */
    @Override
    public boolean recibirDanio() {
        /* Si ya esta muerta no cambiar estado */
        if (!vida) {
            return false;
        }
        vidasActuales -= 1;

        /* Si quedo sin vidas, desactivar visibilidad */
        if (vidasActuales <= 0) {
            vida = false;
            return true; /* Impacto fatal */
        }
        return false; /* Aun viva */
    }

    /*
     * Retorna las vidas actuales para auditoria del evaluador.
     * Implementacion del contrato Destruible.
     */
    @Override
    public int getVidasActuales() {
        return vidasActuales;
    }

    /*
     * Retorna los puntos que otorga esta nave al morir.
     * Cada subclase asigna su valor en el constructor.
     */
    public int getPuntos() {
        return puntos;
    }

    /*
     * Activa la nave y la hace visible para el combate.
     * El evaluador siempre llama aparecer() antes de mover().
     */
    @Override
    public void aparecer() {
        vida = true;
    }

    /* Cada subclase define como se mueve */
    @Override
    abstract public void mover(Entrada e);
}