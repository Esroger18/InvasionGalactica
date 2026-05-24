package juego;

/**
 * Destruible: Interfaz que garantiza que el motor de colisiones
 * puede interactuar con cualquier objeto destructible del juego.
 * Toda nave enemiga debe implementarla.
 */
public interface Destruible {

    /**
     * Aplica daño al objeto. Resta una unidad de resistencia.
     * @return true SOLO en el impacto fatal (resistencia llega a 0)
     */
    boolean recibirDanio();

    /**
     * Permite auditar el estado de salud actual del objeto.
     * @return número de vidas/resistencia actuales
     */
    int getVidasActuales();
}
