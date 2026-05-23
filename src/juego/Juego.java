package juego;

import edu.epromero.util.Lienzo;
import edu.epromero.util.Punto2D;
import java.awt.event.KeyEvent;

/*
 * Motor principal del juego. Punto de entrada.
 * Sigue el patrón del profe igual que el Pong:
 * crea miCanvas, inicia objetos, loop: limpia -> mover -> pinta -> mostrar
 */
public class Juego {

    /* Dimensiones de la ventana */
    private static final int ANCHO = 900;
    private static final int ALTO  = 800;
    
     /* Radios de colision por tipo de objeto (60% del sprite, descontando espacio blanco) */
    private static final double RADIO_HEROE       = 40.0; /* Nave.png      132x132 */
    private static final double RADIO_DESTRUCTOR  = 40.0; /* Destructor    132x132 */
    private static final double RADIO_AVE         = 40.0; /* ADP1.png      132x132 */
    private static final double RADIO_LMINAS  = 60.0; /* LMinas.png    198x198 */
    private static final double RADIO_BALA        = 20.0; /* Balas         66x66   */

    private static final int PUNTOS_VIDA_EXTRA = 500;
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
    
    //Menu principal y marcador de puntajes
    private Menu menu;
    private Marcador marcador;

    /* Puntaje acumulado y control de vida extra */
    private int puntos;
    private int puntosUltimaVidaExtra;
    
    /*
     * Radio de colision en pixeles.
     * Si la distancia entre dos objetos es menor a este valor,
     * se considera colision.
     */
    //private static final double RADIO_COLISION = 30.0;
    
    
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
        
        marcador = new Marcador();
        menu = new Menu(miCanvas, marcador);

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
        puntosUltimaVidaExtra = 0;
    }

    /*
     * Reinicia el juego para una nueva partida.
     * Restablece posiciones, vidas y puntaje.
     */
    public void reiniciar() {
        heroe.inicia(miCanvas);
        destructor.inicia(miCanvas);
        aveDePresa.inicia(miCanvas);
        lanzaMinas.inicia(miCanvas);
        puntos               = 0;
        puntosUltimaVidaExtra = 0;
        menu.reiniciar();
    }
    
    /*
     * Suma puntos y verifica si se gano una vida extra.
     * Cada 500 puntos se otorga una vida adicional al heroe.
     */
    private void sumarPuntos(int cantidad) {
        puntos += cantidad;
        /* Verificar si se alcanza el umbral de vida extra */
        if (puntos - puntosUltimaVidaExtra >= PUNTOS_VIDA_EXTRA) {
            heroe.ganarVida();
            puntosUltimaVidaExtra += PUNTOS_VIDA_EXTRA;
        }
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
                if (posBala.distanceTo(posDestructor) < RADIO_BALA + RADIO_DESTRUCTOR) {
                    balasHeroe[i].explotar();
                    /* recibirDanio() retorna true si fue impacto fatal */
                    if (destructor.recibirDanio()) {
                        puntos += destructor.getPuntos();
                        //Al morir el ave, inactivar su bala para que no quede en pantalla
                        destructor.inactivarBala();
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
 
                if (posBala.distanceTo(posAve) < RADIO_BALA + RADIO_AVE) {
                    balasHeroe[i].explotar();
                    if (aveDePresa.recibirDanio()) {
                        puntos += aveDePresa.getPuntos();
                        //Al morir el ave, inactivar su bala para que no quede en pantalla
                        aveDePresa.inactivarBala();
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
 
                if (posBala.distanceTo(posLanza) < RADIO_BALA + RADIO_LMINAS) {
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
 
            if (posBalaDestructor.distanceTo(posHeroe) < RADIO_BALA + RADIO_HEROE) {
                destructor.getBala().explotar();
                heroe.perderVida();
            }
        }
 
        /* --- 5. Bala del AveDePresa vs Heroe --- */
        if (heroe.isVisible() && aveDePresa.getBala().estaEnVuelo()) {
            Punto2D posBalaAve = new Punto2D(
                aveDePresa.getBala().getColumna(),
                aveDePresa.getBala().getRenglon());
 
            if (posBalaAve.distanceTo(posHeroe) < RADIO_BALA + RADIO_HEROE) {
                aveDePresa.getBala().explotar();
                heroe.perderVida();
            }
        }
 
        /* --- 6. Bala del LanzaMinas vs Heroe --- */
        if (heroe.isVisible() && lanzaMinas.getBala().estaEnVuelo()) {
            Punto2D posBalaLanza = new Punto2D(
                lanzaMinas.getBala().getColumna(),
                lanzaMinas.getBala().getRenglon());
 
            if (posBalaLanza.distanceTo(posHeroe) < RADIO_BALA + RADIO_HEROE) {
                lanzaMinas.getBala().explotar();
                heroe.perderVida();
            }
        }
        /* --- 7. Destructor vs Heroe (colision de naves) --- */
        if (heroe.isVisible() && destructor.isVisible()) {
            Punto2D posDestructorNave = new Punto2D(
                destructor.getColumna(), destructor.getRenglon());
 
            /* Umbral = radio heroe + radio destructor */
            if (posHeroe.distanceTo(posDestructorNave) < (RADIO_HEROE + RADIO_DESTRUCTOR)) {
                /* El destructor no muere por colision con otras naves segun el documento */
                heroe.perderVida();
            }
        }
 
        /* --- 8. AveDePresa vs Heroe (colision de naves) --- */
        if (heroe.isVisible() && aveDePresa.isVisible()) {
            Punto2D posAveNave = new Punto2D(
                aveDePresa.getColumna(), aveDePresa.getRenglon());
 
            /* Umbral = radio heroe + radio ave */
            if (posHeroe.distanceTo(posAveNave) < (RADIO_HEROE + RADIO_AVE)) {
                /* El ave no muere por colision con otras naves segun el documento */
                heroe.perderVida();
            }
        }
 
        /* --- 9. LanzaMinas vs Heroe (colision de naves) --- */
        if (heroe.isVisible() && lanzaMinas.isVisible()) {
            Punto2D posLanzaNave = new Punto2D(
                lanzaMinas.getColumna(), lanzaMinas.getRenglon());
 
            /* Umbral = radio heroe + radio lanzaminas */
            if (posHeroe.distanceTo(posLanzaNave) < (RADIO_HEROE + RADIO_LMINAS)) {
                /* El lanzaminas no muere por colision con otras naves segun el documento */
                heroe.perderVida();
            }
        }
    }
 
    /* Punto de entrada: loop igual al del Pong del profe */
    public static void main(String[] args) {

        Juego juego = new Juego();

        try {
            //Ciclo externo permite jugar varias partidas
            while(true){
                //Menu mostrar top 10 y captura nombre
                while (!juego.menu.estaListo()) {
                    juego.miCanvas.limpia();
                    juego.menu.mover();
                    juego.menu.pinta();
                    juego.miCanvas.mostrar(20);
            }
            //FASE JUEGO game loop
                while(juego.heroe.isVisible()){
                    juego.miCanvas.limpia();
                    juego.mover();
                    juego.revisarColisiones();
                    juego.pinta();
                    juego.miCanvas.mostrar(20);
                }
                //FASE FIN Guardar los puntajes en el top 10
                juego.marcador.agregarPuntaje(juego.menu.getNombreJugador(), 
                        juego.puntos);
                //Mostar pantalla de game over
                juego.miCanvas.limpia();
                juego.miCanvas.texto(280, 320, "GAME OVER");
                juego.miCanvas.texto(200, 280, "Puntaje Final" + juego.puntos);
                juego.miCanvas.texto(180, 240, "Presiona espacio para jugar de nuevo");
                juego.miCanvas.mostrar(20);
                //Espera a que el jugador presione espacio
                boolean esperando = true;
                while(esperando) {
                    juego.miCanvas.mostrar(20);
                    if(juego.miCanvas.fuePulsadaTecla(KeyEvent.VK_SPACE)){
                        esperando = false;
                    }
                }
                //Reiniciar todo para la nueva partida
                juego.reiniciar();
            } 
        } catch (Exception ex) {
            System.out.println("Hilo interrumpido: " + ex.getMessage());
        }
    }
}