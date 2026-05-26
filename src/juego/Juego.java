/*
*   MIXTLI (NUBES)
*   
*   JUEGO DESARROLLADO POR: Espinoza Velázquez Rogelio
*   FACULTAD DE ESTUDIOS SUPERIORES ARAGÓN
*   INGENIERIA EN COMPUTACIÓN   
*   PROGRAMACIÓN ORIENTADA A OBJETOS
*   GRUPO: 2210
*   
*   FECHA: 26/05/2026
*/
package juego;

import edu.epromero.util.FabricaAudio;
import edu.epromero.util.Lienzo;
import edu.epromero.util.Punto2D;
import java.awt.Font;
import java.awt.event.KeyEvent;

/*
*   CLASE: JUEGO
*   Motor principal del juego y punto de entrada.
*/

public class Juego {

    //ATRIBUTOS:
    
    //Tamaño del Canva
    private static final int ANCHO = 900;
    private static final int ALTO  = 800;
    
     //Hitboxes
    private static final int RADIO_HEROE = 70; /* Nave.png      132x132 */
    private static final int RADIO_DESTRUCTOR = 80; /* Destructor    132x132 */
    private static final int RADIO_AVE = 80; /* ADP1.png      132x132 */
    private static final int RADIO_LMINAS = 120; /* LMinas.png    198x198 */
    private static final int RADIO_ZIGZAG = 80; /* ZigZag 132x132 */
    private static final int RADIO_BALA = 40; /* Balas 66x66   */
    
    //Puntuación para vida extra
    private static final int PUNTOS_VIDA_EXTRA = 500;
    
    //Lienzo
    private Lienzo miCanvas;
    
    //Audio del juego
    private FabricaAudio audio;
    
    //Héroe del juego
    private Heroe heroe;
    
    //Destructor
    private Destructor[] destructor;
    
    //Ave de Presa
    private AveDePresa[] aveDePresa;
    
    //Lanza Minas
    private LanzaMinas lanzaMinas;
    
    //ZigZag
    private NaveZigZag naveZigZag;

    //Objeto de entrada
    private Entrada entrada;
    
    //Menu principal
    private Menu menu;
    
    //Marcador de puntajes
    private Marcador marcador;

    //Puntaje acumulado
    private int puntos;
    
    //Control de vida extra
    private int puntosUltimaVidaExtra;
    
    //Cantidad de cada tipo de enemigo en pantalla
    private static final int TOTAL_DESTRUCTORES = 3;
    private static final int TOTAL_AVES         = 3;
 
    //Umbrales de puntos para cambiar de tramo de dificultad
    private static final int DIF_MEDIA = 500;
    private static final int DIF_MEDIA2 = 1000;
    private static final int DIF_DIFICIL = 1500;
    
    //Tramo de dificultad actual para no llamar ajustarDificultad cada frame
    private int tramoDificultadActual;
    
    //---CONSTRUCTORES---
    
    //Constructor principal del juego
    public Juego() {
        
        //Crear y configura el lienzo
        miCanvas = new Lienzo();
        miCanvas.ponTamanioLienzo(ANCHO, ALTO);
        miCanvas.ponEscalaX(0, ANCHO);
        miCanvas.ponEscalaY(0, ALTO);
        
        //Audio
        audio = new FabricaAudio();
        
        //Crear la entrada y asignarle el canvas
        entrada = new Entrada();
        entrada.setMiCanvas(miCanvas);
        
        //Crea el marcador y el menu
        marcador = new Marcador();
        menu = new Menu(miCanvas, marcador);

        //Crear las naves (sin dimensiones, las toma en inicia)
        heroe = new Heroe();
        
        //Destructor
        destructor = new Destructor[TOTAL_DESTRUCTORES];
        //
        for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
            destructor[i] = new Destructor();
        }
        
        //Ave de Presa
        aveDePresa = new AveDePresa[TOTAL_AVES];
        //
        for (int i = 0; i < TOTAL_AVES; i++) {
            aveDePresa[i] = new AveDePresa();
        }
        //Lanza Minas
        lanzaMinas = new LanzaMinas();
        
        //Nave ZigZag
        naveZigZag = new NaveZigZag();
        
        //Inicializar todos los objetos
        iniciarObjetos();
        
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
        
        //Se inician los objetos en el Canvas
        
        //Heroe
        heroe.inicia(miCanvas);
        
        //Destructores
        for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
            destructor[i].inicia(miCanvas);
        }
        
        //Ave de presa
        for (int i = 0; i < TOTAL_AVES; i++) {
            aveDePresa[i].inicia(miCanvas);
        }
        
        //LMinas
        lanzaMinas.inicia(miCanvas);

        //ZigZag
        naveZigZag.inicia(miCanvas);
    }
    /*
     * Reinicia el juego para una nueva partida.
     * Restablece posiciones, vidas, puntaje y dificultad.
     */
    public void reiniciar() {
        iniciarObjetos();
        puntos = 0;
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
            audio.reproducir(".\\resources\\VidE.wav");
            puntosUltimaVidaExtra += PUNTOS_VIDA_EXTRA;
        }
    }
    
    /*
     * Ajusta la dificultad de todos los enemigos segun los puntos actuales.
     * Solo actua cuando cambia de tramo para no recalcular cada frame.
     * Tramo 0 (0-499):    velocidad y frecuencia normales.
     * Tramo 1 (500-999):  velocidad x1.5, dispara mas rapido.
     * Tramo 2 (1000+):    velocidad x2,   dispara al maximo.
     *
    /*private void actualizarDificultad() {
    /* Cada 500 puntos se aplica un aumento acumulativo del 50% 
    int nivelesAlcanzados = puntos / 500;

    /* Solo actuar cuando se cruce un nuevo umbral de 500 puntos 
    if (nivelesAlcanzados > tramoDificultadActual) {
        tramoDificultadActual = nivelesAlcanzados;

        /* Llamar una sola vez para aplicar el aumento acumulativo 
        for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
            destructor[i].ajustarDificultad();
        }
        for (int i = 0; i < TOTAL_AVES; i++) {
            aveDePresa[i].ajustarDificultad();
        }
        lanzaMinas.ajustarDificultad();
        naveZigZag.ajustarDificultad();
    }
}
    
    Mueve todos los elementos del juego */
    public void mover() {
        //moverFondo();
        heroe.mover(entrada);
         for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
            destructor[i].mover(entrada);
        }
        for (int i = 0; i < TOTAL_AVES; i++) {
            aveDePresa[i].mover(entrada);
        }
        lanzaMinas.mover(entrada);
        naveZigZag.mover(entrada);
 
        /* Actualizar dificultad segun puntos actuales */
        //actualizarDificultad();
    }

    /* Pinta todos los elementos del juego */
    public void pinta() {
        miCanvas.dibujo(450, 400, ".\\resources\\FJ45.png");
        //miCanvas.dibujo(ANCHO / 2, fondoJ2, "FJ46.png");
        heroe.pinta(miCanvas);
        for (int i = 0; i < TOTAL_DESTRUCTORES; i++) {
            destructor[i].pinta(miCanvas);
        }
        for (int i = 0; i < TOTAL_AVES; i++) {
            aveDePresa[i].pinta(miCanvas);
        }
        lanzaMinas.pinta(miCanvas);
        naveZigZag.pinta(miCanvas);
        
        /* Mostrar puntaje y vidas en pantalla */
        miCanvas.ponColorLapiz(255, 212, 0);
        //Fuente del texto
        Font fuenteTexto = new Font("MS UI Gothic", Font.BOLD, 40);
        miCanvas.ponFuente(fuenteTexto);
        miCanvas.texto(820,  20, "PUNTOS: " + puntos);
        miCanvas.texto(80, 20, "VIDAS: "  + heroe.getVidas());
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
     *   7. Bala del heroe vs NaveZigZag
     *   8. 
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
                        //destructor[d].inactivarBala();
                        audio.reproducir(".\\resources\\Explosion.wav");
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
                        //aveDePresa[a].inactivarBala();
                        audio.reproducir(".\\resources\\Explosion2.wav");
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
                        audio.reproducir(".\\resources\\Explosion3.wav");
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
        
        /* --- 10. Balas del heroe vs NaveZigZag --- */
        if (naveZigZag.isVisible()) {
            Punto2D posZigZag = new Punto2D(
                naveZigZag.getColumna(), naveZigZag.getRenglon());

            for (int i = 0; i < balasHeroe.length; i++) {
                if (!balasHeroe[i].estaEnVuelo()) {
                    continue;
                }
                Punto2D posBala = new Punto2D(
                    balasHeroe[i].getColumna(), balasHeroe[i].getRenglon());
                
                if (posBala.distanceTo(posZigZag) < RADIO_BALA + RADIO_ZIGZAG) {
                    balasHeroe[i].explotar();
                    if (naveZigZag.recibirDanio()) {
                        sumarPuntos(naveZigZag.getPuntos());
                        audio.reproducir(".\\resources\\Explosion.wav");
                    }
                }
            }
        }
        /* --- 11. heroe vs NaveZigZag Colision--- */
        if (heroe.isVisible() && naveZigZag.isVisible()){
            Punto2D posZigZagNave = new Punto2D(
                naveZigZag.getColumna(), naveZigZag.getRenglon());
            if (posHeroe.distanceTo(posZigZagNave) < RADIO_HEROE + RADIO_ZIGZAG){
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
                juego.miCanvas.dibujo(450, 400, ".\\resources\\Black.png");
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