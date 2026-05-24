package juego;

import edu.epromero.util.FabricaAudio;
import edu.epromero.util.Lienzo;
import edu.epromero.util.Punto2D;
import java.awt.Font;
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
    private static final int RADIO_HEROE       = 40; /* Nave.png      132x132 132x120*/
    private static final int RADIO_DESTRUCTOR  = 40; /* Destructor    132x132 */
    private static final int RADIO_AVE         = 40; /* ADP1.png      132x132 */
    private static final int RADIO_LMINAS  = 100; /* LMinas.png    198x198 */
    private static final int RADIO_BALA        = 20; /* Balas         66x66   */

    private static final int PUNTOS_VIDA_EXTRA = 500;
    
    /* El lienzo de dibujo, igual que miCanvas en Pong */
    private Lienzo miCanvas;
    //Audio del juego
    private FabricaAudio audio;
    private FabricaAudio audioDisparo;
    private FabricaAudio audioGameOver;
    private FabricaAudio audioVidaE;
    private FabricaAudio audioDanio;
    
    /* El héroe del juego */
    private Heroe heroe;
    
    //Destructor
    private Destructor[] destructor;
    //Ave de Presa
    private AveDePresa[] aveDePresa;
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
    
    /* Cantidad de cada tipo de enemigo en pantalla */
    private static final int TOTAL_DESTRUCTORES = 3;
    private static final int TOTAL_AVES         = 2;
 
     /* Umbrales de puntos para cambiar de tramo de dificultad */
    private static final int UMBRAL_DIFICULTAD_MEDIA = 500;
    private static final int UMBRAL_DIFICULTAD_ALTA  = 1000;
    
    /* Tramo de dificultad actual para no llamar ajustarDificultad cada frame */
    private int tramoDificultadActual;
    
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

        //Audio
        audio = new FabricaAudio();
        //audioGameOver = new FabricaAudio();
        /* Crear la entrada y asignarle el canvas */
        entrada = new Entrada();
        entrada.setMiCanvas(miCanvas);
        
        marcador = new Marcador();
        menu = new Menu(miCanvas, marcador);

        /* Crear las naves (sin dimensiones, las toma en inicia) */
        heroe = new Heroe();
        destructor = new Destructor[TOTAL_DESTRUCTORES];
        for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
            destructor[i] = new Destructor();
        }
        aveDePresa = new AveDePresa[TOTAL_AVES];
        for (int i = 0; i < TOTAL_AVES; i++) {
            aveDePresa[i] = new AveDePresa();
        }
        lanzaMinas = new LanzaMinas();

        /* Inicializar todos los objetos */
        iniciarObjetos();
        /* inicia() carga imágenes y fija posición inicial */
        //heroe.inicia(miCanvas);
        //destructor.inicia(miCanvas);
        //aveDePresa.inicia(miCanvas);
        //lanzaMinas.inicia(miCanvas);
        
        //Puntaje inicial
        puntos = 0;
        puntosUltimaVidaExtra = 0;
        tramoDificultadActual = 0;
    }

    /*
     * Inicializa todos los objetos del juego con el canvas.
     * Se separa del constructor para poder reutilizarse en reiniciar().
     */
    private void iniciarObjetos() {
        heroe.inicia(miCanvas);
 
        for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
            destructor[i].inicia(miCanvas);
        }
        for (int i = 0; i < TOTAL_AVES; i++) {
            aveDePresa[i].inicia(miCanvas);
        }
        lanzaMinas.inicia(miCanvas);
    }
    /*
     * Reinicia el juego para una nueva partida.
     * Restablece posiciones, vidas y puntaje.
    
    public void reiniciar() {
        heroe.inicia(miCanvas);
        destructor.inicia(miCanvas);
        aveDePresa.inicia(miCanvas);
        lanzaMinas.inicia(miCanvas);
        puntos               = 0;
        puntosUltimaVidaExtra = 0;
        menu.reiniciar();
    }*/
    /*
     * Reinicia el juego para una nueva partida.
     * Restablece posiciones, vidas, puntaje y dificultad.
     */
    public void reiniciar() {
        iniciarObjetos();
        puntos                = 0;
        puntosUltimaVidaExtra = 0;
        tramoDificultadActual = 0;
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
            audio.reproducir("VidaE.wav");
            puntosUltimaVidaExtra += PUNTOS_VIDA_EXTRA;
        }
    }
    
    /*
     * Ajusta la dificultad de todos los enemigos segun los puntos actuales.
     * Solo actua cuando cambia de tramo para no recalcular cada frame.
     * Tramo 0 (0-499):    velocidad y frecuencia normales.
     * Tramo 1 (500-999):  velocidad x1.5, dispara mas rapido.
     * Tramo 2 (1000+):    velocidad x2,   dispara al maximo.
     */
    private void actualizarDificultad() {
        int nuevoTramo;
 
        /* Determinar el tramo actual segun puntos */
        if (puntos >= UMBRAL_DIFICULTAD_ALTA) {
            nuevoTramo = 2;
        } else if (puntos >= UMBRAL_DIFICULTAD_MEDIA) {
            nuevoTramo = 1;
        } else {
            nuevoTramo = 0;
        }
 
        /* Solo actualizar si cambio de tramo */
        if (nuevoTramo != tramoDificultadActual) {
            tramoDificultadActual = nuevoTramo;
 
            /* Ajustar dificultad en todos los enemigos */
            for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
                destructor[i].ajustarDificultad(puntos);
            }
            for (int i = 0; i < TOTAL_AVES; i++) {
                aveDePresa[i].ajustarDificultad(puntos);
            }
            lanzaMinas.ajustarDificultad(puntos);
        }
    }
    
    /* Mueve todos los elementos del juego */
    public void mover() {
        heroe.mover(entrada);
         for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
            destructor[i].mover(entrada);
        }
        for (int i = 0; i < TOTAL_AVES; i++) {
            aveDePresa[i].mover(entrada);
        }
        lanzaMinas.mover(entrada);
 
        /* Actualizar dificultad segun puntos actuales */
        actualizarDificultad();
    }

    /* Pinta todos los elementos del juego */
    public void pinta() {
        heroe.pinta(miCanvas);
        for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
            destructor[i].pinta(miCanvas);
        }
        for (int i = 0; i < TOTAL_AVES; i++) {
            aveDePresa[i].pinta(miCanvas);
        }
        lanzaMinas.pinta(miCanvas);
        
        /* Mostrar puntaje y vidas en pantalla */
        miCanvas.ponColorLapiz(0, 0, 0);
        //Fuente del texto
        Font fuenteTexto = new Font("MS UI Gothic", Font.PLAIN, 30);
        miCanvas.ponFuente(fuenteTexto);
        miCanvas.texto(80,  20, "PUNTOS: " + puntos);
        miCanvas.texto(820, 20, "VIDAS: "  + heroe.getVidas());
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
 
        /*/* --- 1. Balas del heroe vs Destructores --- */
        for (int d = 0; d < TOTAL_DESTRUCTORES; d++) {
            if (!destructor[d].isVisible()) {
                continue;
            }
            Punto2D posDestructor = new Punto2D(
                destructor[d].getColumna(), destructor[d].getRenglon());
 
            for (int i = 0; i < balasHeroe.length; i++) {
                if (!balasHeroe[i].estaEnVuelo()) {
                    continue;
                }
                Punto2D posBala = new Punto2D(
                    balasHeroe[i].getColumna(), balasHeroe[i].getRenglon());
 
                if (posBala.distanceTo(posDestructor) < RADIO_BALA + RADIO_DESTRUCTOR) {
                    balasHeroe[i].explotar();
                    if (destructor[d].recibirDanio()) {
                        sumarPuntos(destructor[d].getPuntos());
                        destructor[d].inactivarBala();
                        audio.reproducir("Explosion.wav");
                    }
                }
            }
        }
 
        /* --- 2. Balas del heroe vs AvesDePresa --- */
        for (int a = 0; a < TOTAL_AVES; a++) {
            if (!aveDePresa[a].isVisible()) {
                continue;
            }
            Punto2D posAve = new Punto2D(
                aveDePresa[a].getColumna(), aveDePresa[a].getRenglon());
 
            for (int i = 0; i < balasHeroe.length; i++) {
                if (!balasHeroe[i].estaEnVuelo()) {
                    continue;
                }
                Punto2D posBala = new Punto2D(
                    balasHeroe[i].getColumna(), balasHeroe[i].getRenglon());
 
                if (posBala.distanceTo(posAve) < RADIO_BALA + RADIO_AVE) {
                    balasHeroe[i].explotar();
                    if (aveDePresa[a].recibirDanio()) {
                        sumarPuntos(aveDePresa[a].getPuntos());
                        aveDePresa[a].inactivarBala();
                        audio.reproducir("Explosion2.wav");
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
                        sumarPuntos(lanzaMinas.getPuntos());
                        audio.reproducir("Explosion3.wav");
                    }
                }
            }
        }
 
        /* --- 4. Balas de Destructores vs Heroe --- */
        if (heroe.isVisible()) {
            for (int d = 0; d < TOTAL_DESTRUCTORES; d++) {
                if (!destructor[d].getBala().estaEnVuelo()) {
                    continue;
                }
                Punto2D posBalaDestructor = new Punto2D(
                    destructor[d].getBala().getColumna(),
                    destructor[d].getBala().getRenglon());
 
                if (posBalaDestructor.distanceTo(posHeroe) < RADIO_BALA + RADIO_HEROE) {
                    destructor[d].getBala().explotar();
                    heroe.perderVida();
                }
            }
        }
 
        /* --- 5. Balas de AvesDePresa vs Heroe --- */
        if (heroe.isVisible()) {
            for (int a = 0; a < TOTAL_AVES; a++) {
                if (!aveDePresa[a].getBala().estaEnVuelo()) {
                    continue;
                }
                Punto2D posBalaAve = new Punto2D(
                    aveDePresa[a].getBala().getColumna(),
                    aveDePresa[a].getBala().getRenglon());
 
                if (posBalaAve.distanceTo(posHeroe) < RADIO_BALA + RADIO_HEROE) {
                    aveDePresa[a].getBala().explotar();
                    heroe.perderVida();
                }
            }
        }
 
        /* --- 6. Balas de LanzaMinas vs Heroe --- */
        if (heroe.isVisible()) {
            BalaEnemiga[] balasLanza = lanzaMinas.getBalas();
            for (int i = 0; i < balasLanza.length; i++) {
                if (!balasLanza[i].estaEnVuelo()) {
                    continue;
                }
                Punto2D posBalaLanza = new Punto2D(
                    balasLanza[i].getColumna(), balasLanza[i].getRenglon());
 
                if (posBalaLanza.distanceTo(posHeroe) < RADIO_BALA + RADIO_HEROE) {
                    balasLanza[i].explotar();
                    heroe.perderVida();
                }
            }
        }
 
        /* --- 7. Destructores vs Heroe (colision de naves) --- */
        if (heroe.isVisible()) {
            for (int d = 0; d < TOTAL_DESTRUCTORES; d++) {
                if (!destructor[d].isVisible()) {
                    continue;
                }
                Punto2D posDestructorNave = new Punto2D(
                    destructor[d].getColumna(), destructor[d].getRenglon());
 
                if (posHeroe.distanceTo(posDestructorNave) < RADIO_HEROE + RADIO_DESTRUCTOR) {
                    heroe.perderVida();
                }
            }
        }
 
        /* --- 8. AvesDePresa vs Heroe (colision de naves) --- */
        if (heroe.isVisible()) {
            for (int a = 0; a < TOTAL_AVES; a++) {
                if (!aveDePresa[a].isVisible()) {
                    continue;
                }
                Punto2D posAveNave = new Punto2D(
                    aveDePresa[a].getColumna(), aveDePresa[a].getRenglon());
 
                if (posHeroe.distanceTo(posAveNave) < RADIO_HEROE + RADIO_AVE) {
                    heroe.perderVida();
                }
            }
        }
 
        /* --- 9. LanzaMinas vs Heroe (colision de naves) --- */
        if (heroe.isVisible() && lanzaMinas.isVisible()) {
            Punto2D posLanzaNave = new Punto2D(
                lanzaMinas.getColumna(), lanzaMinas.getRenglon());
 
            if (posHeroe.distanceTo(posLanzaNave) < RADIO_HEROE + RADIO_LMINAS) {
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
                juego.miCanvas.dibujo(450, 400, "Black.png");
                juego.miCanvas.ponColorLapiz(255, 255, 255);
                juego.miCanvas.texto(450, 500, "GAME OVER");
                //audio.reproducir("GameOver.wav");
                juego.miCanvas.texto(450, 350, "Puntaje Final: " + juego.puntos);
                juego.miCanvas.texto(450, 300, "Presiona espacio para jugar de nuevo");
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