package juego;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Explosion: Efecto visual que aparece cuando una nave es destruida.
 * Se anima expandiéndose y desvaneciéndose hasta desaparecer.
 */
public class Explosion extends ElementoGrafico {

    // Radio actual de la explosión
    private int radio;

    // Radio máximo antes de desaparecer
    private static final int RADIO_MAX = 45;

    // Velocidad de expansión por ciclo
    private static final int VELOCIDAD_EXPANSION = 3;

    // Transparencia actual (255 = opaco, 0 = transparente)
    private int alpha;

    // Color base de la explosión
    private final Color colorBase;

    /**
     * Crea una explosión en la posición dada.
     * @param x     centro de la explosión
     * @param y     centro de la explosión
     * @param color color característico del tipo de nave destruida
     */
    public Explosion(int x, int y, Color color) {
        super(x, y);
        this.radio     = 5;
        this.alpha     = 255;
        this.colorBase = color;
        this.visible   = true;
    }

    /**
     * Activa la explosión (ya comienza activa al crearse).
     */
    @Override
    public void aparecer() {
        visible = true;
        radio   = 5;
        alpha   = 255;
    }

    /**
     * Actualiza el estado de la explosión: la expande y desvanece.
     */
    public void actualizar() {
        if (!visible) {
            return;
        }

        // Expandir el radio
        radio += VELOCIDAD_EXPANSION;

        // Reducir la opacidad proporcionalmente
        alpha = (int)(255 * (1.0 - (double)radio / RADIO_MAX));

        // Desactivar cuando alcanza el radio máximo
        if (radio >= RADIO_MAX) {
            visible = false;
        }
    }

    /**
     * Dibuja la explosión como círculos concéntricos que se desvanecen.
     * @param g2d contexto gráfico
     */
    @Override
    public void dibujar(Graphics2D g2d) {
        if (!visible || alpha <= 0) {
            return;
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Círculo exterior (más transparente)
        int alphaExt = Math.max(0, alpha - 60);
        g2d.setColor(new Color(
            colorBase.getRed(),
            colorBase.getGreen(),
            colorBase.getBlue(),
            alphaExt
        ));
        g2d.fillOval(x - radio, y - radio, radio * 2, radio * 2);

        // Círculo interior (más brillante)
        int radioInt = radio * 2 / 3;
        g2d.setColor(new Color(255, 200, 50, Math.min(255, alpha + 50)));
        g2d.fillOval(x - radioInt, y - radioInt, radioInt * 2, radioInt * 2);

        // Núcleo blanco
        int radioNucleo = radio / 3;
        g2d.setColor(new Color(255, 255, 255, Math.min(255, alpha + 80)));
        g2d.fillOval(x - radioNucleo, y - radioNucleo, radioNucleo * 2, radioNucleo * 2);
    }
}
