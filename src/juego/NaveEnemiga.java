/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package juego;

/**
 *
 * @author Rockzer18
 *
 * Clase base de todas las naves enemigas.
 * Extiende ElementoGrafico e implementa Destruible.
 * Cada subclase debe agregar @ComportamientoEnemigo con sus valores.
 *
 */
import edu.epromero.util.Lienzo;
import edu.epromero.util.Destruible;
import edu.epromero.util.ComportamientoEnemigo;

@ComportamientoEnemigo(tipo="Destructor", resistencia=1, puntos=50)
abstract public class NaveEnemiga extends ElementoGrafico implements Destruible {
    //Vidas actuales de la nave se asignan en el constructor
    protected int vidasActuales;
    //Puntos que otorga al morir, cada subclase los define
    protected int puntos;
    //Limites de la pantalla se obtienen en inicia(canvas)
    double limiteDer;
    double limiteIzq;
    private double limiteSup;
    double limiteInf;
    /* Constructor vacío requerido por el evaluador para reflexión */
    public NaveEnemiga() {
        super();
        vidasActuales = 1;
        puntos        = 0;
    }
 
    /*
     * Guarda los límites del canvas para usarlos en mover().
     * Cada subclase llama super.inicia(canvas) y luego
     * asigna su posición inicial aleatoria.
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
     * Resta una vida y retorna true solo en el impacto fatal.
     * Implementación del contrato Destruible.
     * Si ya está muerta, no hace nada (estabilidad post-mortem).
     */
    @Override
    public boolean recibirDanio() {
        /* Si ya está muerta no cambiar estado */
        if (!vida) {
            return false;
        }
        vidasActuales -= 1;
 
        /* Si quedó sin vidas, desactivar visibilidad */
        if (vidasActuales <= 0) {
            vida = false;
            return true; /* Impacto fatal */
        }
        return false; /* Aún viva */
    }
 
    /*
     * Retorna las vidas actuales para auditoría del evaluador.
     * Implementación del contrato Destruible.
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

    @Override
    abstract public void mover(Entrada e);
}
