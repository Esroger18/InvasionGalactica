package juego;

import edu.epromero.util.Imagen;
import edu.epromero.util.Lienzo;

/*
 * Heroe: La nave del jugador.
 * Extiende ElementoGrafico para heredar coordenadas y visibilidad.
 * Se mueve horizontalmente con las teclas A/D o flechas.
 */
public class Heroe extends ElementoGrafico {

    // Velocidad de movimiento en píxeles por ciclo
    private static final int VELOCIDAD = 5;

    // Tamaño visual de la nave (para dibujarla y limitar bordes)
    private static final int TAMANO = 20;

    // Límites de la pantalla para no salirse
    private int limiteAncho;
    private int limiteAlto;
    
    //ImgSprite para dibujar la imagen (se puede poner como constante creo desde elemento grafico)
    private Imagen imgSprite;

    /*
     * Constructor: recibe las dimensiones de la pantalla
     * para calcular la posición inicial (centro inferior)
     */
    public Heroe(int ancho, int alto) {
        this.limiteAncho = ancho;
        this.limiteAlto  = alto;
        // Posición inicial: centro de la pantalla, pegado al borde inferior
        this.x = ancho / 2;
        this.y = alto - 50;
    }

    /*
     * Mueve la nave según las teclas presionadas.
     * Recibe el objeto Entrada para leer el teclado.
     */
    public void Mueve(Entrada e) {
        /* Solo moverse si está visible/activo */
        if (!vida) {
            return;
        }

        /* Mover a la izquierda */
        if (e.isIzquierda()) {
            x -= VELOCIDAD;
        }

        /* Mover a la derecha */
        if (e.isDerecha()) {
            x += VELOCIDAD;
        }
        
        /* Mover arriba */
        if (e.isArriba()) {
            y += VELOCIDAD;
        }
        
        /* Mover abajo */
        if (e.isAbajo()) {
            y -= VELOCIDAD;
        }

        /* Limitar al borde izquierdo */
        if (x < TAMANO) {
            x = TAMANO;
        }

        /* Limitar al borde derecho */
        if (x > limiteAncho - TAMANO) {
            x = limiteAncho - TAMANO;
        }
        
        /* Limitar al borde superior */
        if (y > limiteAncho - TAMANO) {
            y = limiteAncho - TAMANO;
        }
        
        /* Limitar al borde inferior */
        if (y < TAMANO) {
            y = TAMANO;
        }
    }

    /*
     * Dibuja la nave en el Lienzo de la EPRLIB.
     * NOTA: revisa en la documentación de Lienzo cómo se llama
     * el método para dibujar un polígono o imagen en pantalla.
     * Puede ser algo como:
     *   lienzo.ponColor(r, g, b)        <- para el color
     *   lienzo.dibujaCírculo(x, y, r)   <- o dibuja figura
     *   lienzo.dibujaLinea(x1,y1,x2,y2) <- o con líneas
     * Ajusta los nombres reales de los métodos según el Javadoc.
     */
    
    
    
    public void dibujar(Lienzo lienzo) {
        if (!vida) {
            return;
        }

        /* TODO: consultar en Javadoc el método para poner color */
        // lienzo.ponColor(0, 200, 100);

        /* Dibuja el cuerpo de la nave como triángulo apuntando arriba */
        /* TODO: verificar nombre exacto del método para polígono/líneas */
        // Punta superior
        // lienzo.dibujaLinea(x,       y - TAMANO, x - TAMANO, y + TAMANO);
        // lienzo.dibujaLinea(x,       y - TAMANO, x + TAMANO, y + TAMANO);
        // Base inferior
        // lienzo.dibujaLinea(x - TAMANO, y + TAMANO, x + TAMANO, y + TAMANO);
    }
}