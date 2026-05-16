package juego;
/*
 * Superclase de todos los objetos visibles en pantalla.
 * Maneja coordenadas y estado de vida (visibilidad).
* Usa miCanvas para dibujar y LienzoStd para leer teclas y limites
 */
import edu.epromero.util.Imagen;
import edu.epromero.util.Lienzo;

/*
 * Superclase de todos los objetos visibles en pantalla.
 * Usa miCanvas para dibujar (patrón del profe)
 * y LienzoStd para leer teclas y límites.
 */
abstract public class ElementoGrafico {

    /* Coordenadas en pantalla */
    protected double columna;
    protected double renglon;

    /* Nombre del archivo de imagen y objeto Imagen */
    protected String sprite;
    protected Imagen imgSprite;

    /* Estado de vida/visibilidad */
    protected boolean vida;

    /* Constantes de dirección */
    public static final int DERECHA   = 1;
    public static final int IZQUIERDA = -1;
    public static final int ARRIBA    = 1;
    public static final int ABAJO     = -1;

    /* Constructor vacío */
    public ElementoGrafico() {
        setColumna(0);
        setRenglon(0);
        setSprite("");
        vida = false;
    }

    /* Constructor con posición inicial */
    public ElementoGrafico(double columna, double renglon) {
        setColumna(columna);
        setRenglon(renglon);
        setSprite("");
        vida = false;
    }

    /*
     * Carga la imagen del sprite.
     * Recibe canvas igual que en el Pong del profe.
     */
    public void inicia(Lienzo canvas) {
        imgSprite = new Imagen(sprite);
    }

    /*
     * Dibuja el sprite usando miCanvas.
     * Solo pinta si el elemento está vivo.
     */
    public void pinta(Lienzo canvas) {
        if (!vida) {
            return;
        }
        canvas.dibujo(getColumna(), getRenglon(), imgSprite);
    }

    /* Activa el elemento (lo hace visible) */
    public void aparecer() {
        vida = true;
    }

    /* Retorna true si el elemento está vivo/visible */
    public boolean isVisible() {
        return vida;
    }

    /* Getters y setters */
    public double getColumna(){
        return columna;
    }
    public void setColumna(double columna){
        this.columna = columna;
    }
    public double getRenglon(){
        return renglon;
    }
    public void setRenglon(double renglon){
        this.renglon = renglon;
    }
    public String getSprite(){
        return sprite;
    }
    public void   setSprite(String sprite){
        this.sprite = sprite;
    }
    public Imagen getImgSprite(){
        return imgSprite;
    }
    public void setImgSprite(Imagen img){
        this.imgSprite = img;
    }
    public double getX(){
        return columna;
    }
    public double getY(){
        return renglon;
    }

    /* Cada subclase define cómo se mueve */
    abstract public void mover(Entrada e);
}