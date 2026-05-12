package juego;

/*
 * Superclase de todos los objetos visibles en pantalla.
 * Maneja coordenadas y estado de vida (visibilidad).
 */

import edu.epromero.util.LienzoStd
import edu.epromero.util.Imagen;

abstract public class ElementoGrafico {

    // Coordenadas en pantalla
    private int columna;
    private int renglon;

    // Nombre del archivo de imagen y objeto Imagen
    private String sprite;
    private Imagen imgSprite;

    // Indica si el elemento está activo/visible
    protected boolean vida;

    // Constantes de dirección (igual que en Pong)
    public static final int DERECHA   =  1;
    public static final int IZQUIERDA = -1;
    public static final int ARRIBA    =  1;
    public static final int ABAJO     = -1;
    
/* Constructor vacío: posición en origen */
    public ElementoGrafico() {
        setColumna(0);
        setRenglon(0);
        setSprite("");
        vida = false;
    }

    /* Constructor con posición inicial */
    public ElementoGrafico(double col, double ren) {
        setColumna(col);
        setRenglon(ren);
        setSprite("");
        vida = false;
    }

    /* Carga la imagen del sprite desde el archivo */
    public void inicia() {
        imgSprite = new Imagen(getSprite());
    }

    /* Dibuja el sprite en pantalla usando LienzoStd */
    public void Pinta() {
        /* Solo pintar si está vivo/visible */
        if (!vida) {
            return;
        }
        LienzoStd.dibujo(getColumna(), getRenglon(), getImgSprite());
    }

    /* Activa el elemento (lo hace visible) */
    public void aparecer() {
        vida = true;
    }

    /* Retorna true si el elemento está vivo/visible */
    public boolean isVisible() {
        return vida;
    }

    /**
     * @return the columna
     */
    public int getColumna() {
        return columna;
    }

    /**
     * @return the renglon
     */
    public int getRenglon() {
        return renglon;
    }

    /**
     * @return the sprite
     */
    public String getSprite() {
        return sprite;
    }

    /**
     * @return the imgSprite
     */
    public Imagen getImgSprite() {
        return imgSprite;
    }

