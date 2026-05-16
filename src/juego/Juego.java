package juego;

import edu.epromero.util.Lienzo;
import edu.epromero.util.Punto2D;

/*
 * Motor principal del juego. Punto de entrada.
 * Sigue el patrón del profe igual que el Pong:
 * crea miCanvas, inicia objetos, loop: limpia -> mover -> pinta -> mostrar
 */
public class Juego {

    /* Dimensiones de la ventana */
    private static final int ANCHO = 800;
    private static final int ALTO  = 600;

    /* El lienzo de dibujo, igual que miCanvas en Pong */
    private Lienzo miCanvas;
    
    /* El héroe del juego */
    private Heroe heroe;
    
    //Destructor
    private Destructor destructor;
    //Ave de Presa
    private AveDePresa aveDePresa;
    //Lanza Minas
    private LanzaMinas lanzaMinas;

    /* Objeto de entrada compartido */
    private Entrada entrada;

    /*
     * Radio de colision en pixeles.
     * Si la distancia entre dos objetos es menor a este valor,
     * se considera colision.
     */
    private static final double RADIO_COLISION = 30.0;
    
    /* Puntaje acumulado del jugador */
    private int puntos;
    
    /*
     * Constructor: crea el lienzo y todos los objetos.
     * Igual que el Juego del profe.
     */
    public Juego() {
        /* Crear y configurar el lienzo */
        miCanvas = new Lienzo();
        miCanvas.ponTamanioLienzo(ANCHO, ALTO);
        miCanvas.ponEscalaX(0, ANCHO);
        miCanvas.ponEscalaY(0, ALTO);

        /* Crear la entrada y asignarle el canvas */
        entrada = new Entrada();
        entrada.setMiCanvas(miCanvas);

        /* Crear las naves (sin dimensiones, las toma en inicia) */
        heroe = new Heroe();
        destructor = new Destructor();
        aveDePresa = new AveDePresa();
        lanzaMinas = new LanzaMinas();

        /* inicia() carga imágenes y fija posición inicial */
        heroe.inicia(miCanvas);
        destructor.inicia(miCanvas);
        aveDePresa.inicia(miCanvas);
        lanzaMinas.inicia(miCanvas);
        
        //Puntaje inicial
        puntos = 0;
    }

    /* Mueve todos los elementos del juego */
    public void mover() {
        heroe.mover(entrada);
        destructor.mover(entrada);
        aveDePresa.mover(entrada);
        lanzaMinas.mover(entrada);
        /* El LanzaMinas necesita saber donde esta el heroe */
        lanzaMinas.setHeroePos(heroe.getColumna(), heroe.getRenglon());
    }

    /* Pinta todos los elementos del juego */
    public void pinta() {
        heroe.pinta(miCanvas);
        destructor.pinta(miCanvas);
        aveDePresa.pinta(miCanvas);
        lanzaMinas.pinta(miCanvas);
        
        /* Mostrar puntaje y vidas en pantalla */
        miCanvas.texto(80,  20, "PUNTOS: " + puntos);
        miCanvas.texto(380, 20, "VIDAS: "  + heroe.getVidas());
    }

    /*
     * Revisa todas las colisiones del juego.
     * Usa Punto2D.distanceTo() de la EPRLIB para medir distancia.
     *
     * Colisiones posibles:
     *   1. Balas del heroe vs Destructor
     *   2. Balas del heroe vs AveDePresa
     *   3. Balas del heroe vs LanzaMinas
     *   4. Bala del Destructor vs Heroe
     *   5. Bala del AveDePresa vs Heroe
     *   6. Bala del LanzaMinas vs Heroe
     */
    
    public void revisarColisiones() {
        /* Punto de posicion del heroe para calcular distancias */
        Punto2D posHeroe = new Punto2D(heroe.getColumna(), heroe.getRenglon());
 
        /* Obtener las balas del heroe para revisar contra enemigos */
        Bala[] balasHeroe = heroe.getBalas();
 
        /* --- 1. Balas del heroe vs Destructor --- */
        if (destructor.isVisible()) {
            Punto2D posDestructor = new Punto2D(
                destructor.getColumna(), destructor.getRenglon());
 
            for (int i = 0; i < balasHeroe.length; i++) {
                /* Solo revisar balas en vuelo */
                if (!balasHeroe[i].estaEnVuelo()) {
                    continue;
                }
                Punto2D posBala = new Punto2D(
                    balasHeroe[i].getColumna(), balasHeroe[i].getRenglon());
 
                /* Si la bala esta cerca del destructor, impacto */
                if (posBala.distanceTo(posDestructor) < RADIO_COLISION) {
                    balasHeroe[i].explotar();
                    /* recibirDanio() retorna true si fue impacto fatal */
                    if (destructor.recibirDanio()) {
                        puntos += destructor.getPuntos();
                    }
                }
            }
        }
 
        /* --- 2. Balas del heroe vs AveDePresa --- */
        if (aveDePresa.isVisible()) {
            Punto2D posAve = new Punto2D(
                aveDePresa.getColumna(), aveDePresa.getRenglon());
 
            for (int i = 0; i < balasHeroe.length; i++) {
                if (!balasHeroe[i].estaEnVuelo()) {
                    continue;
                }
                Punto2D posBala = new Punto2D(
                    balasHeroe[i].getColumna(), balasHeroe[i].getRenglon());
 
                if (posBala.distanceTo(posAve) < RADIO_COLISION) {
                    balasHeroe[i].explotar();
                    if (aveDePresa.recibirDanio()) {
                        puntos += aveDePresa.getPuntos();
                    }
                }
            }
        }
 
        /* --- 3. Balas del heroe vs LanzaMinas --- */
        if (lanzaMinas.isVisible()) {
            Punto2D posLanza = new Punto2D(
                lanzaMinas.getColumna(), lanzaMinas.getRenglon());
 
            for (int i = 0; i < balasHeroe.length; i++) {
                if (!balasHeroe[i].estaEnVuelo()) {
                    continue;
                }
                Punto2D posBala = new Punto2D(
                    balasHeroe[i].getColumna(), balasHeroe[i].getRenglon());
 
                if (posBala.distanceTo(posLanza) < RADIO_COLISION) {
                    balasHeroe[i].explotar();
                    if (lanzaMinas.recibirDanio()) {
                        puntos += lanzaMinas.getPuntos();
                    }
                }
            }
        }
 
        /* --- 4. Bala del Destructor vs Heroe --- */
        if (heroe.isVisible() && destructor.getBala().estaEnVuelo()) {
            Punto2D posBalaDestructor = new Punto2D(
                destructor.getBala().getColumna(),
                destructor.getBala().getRenglon());
 
            if (posBalaDestructor.distanceTo(posHeroe) < RADIO_COLISION) {
                destructor.getBala().explotar();
                heroe.perderVida();
            }
        }
 
        /* --- 5. Bala del AveDePresa vs Heroe --- */
        if (heroe.isVisible() && aveDePresa.getBala().estaEnVuelo()) {
            Punto2D posBalaAve = new Punto2D(
                aveDePresa.getBala().getColumna(),
                aveDePresa.getBala().getRenglon());
 
            if (posBalaAve.distanceTo(posHeroe) < RADIO_COLISION) {
                aveDePresa.getBala().explotar();
                heroe.perderVida();
            }
        }
 
        /* --- 6. Bala del LanzaMinas vs Heroe --- */
        if (heroe.isVisible() && lanzaMinas.getBala().estaEnVuelo()) {
            Punto2D posBalaLanza = new Punto2D(
                lanzaMinas.getBala().getColumna(),
                lanzaMinas.getBala().getRenglon());
 
            if (posBalaLanza.distanceTo(posHeroe) < RADIO_COLISION) {
                lanzaMinas.getBala().explotar();
                heroe.perderVida();
            }
        }
    }
 
    /* Punto de entrada: loop igual al del Pong del profe */
    public static void main(String[] args) {

        Juego juego = new Juego();

        try {
            /* El juego corre mientras el héroe esté visible */
            while (juego.heroe.isVisible()) {
                juego.miCanvas.limpia();
                juego.mover();
                juego.revisarColisiones();
                juego.pinta();
                juego.miCanvas.mostrar(20);
            }
        } catch (Exception ex) {
            System.out.println("Hilo interrumpido: " + ex.getMessage());
        }
    }
}