/*
 * Menu.java
 * Pantalla de inicio del juego.
 * Muestra el top 10 de puntajes, pide el nombre del jugador
 * y espera que presione ESPACIO o F2 para iniciar.
 * Se muestra antes de cada partida y al terminar el juego.
 */
package juego;

import edu.epromero.util.Lienzo;
import java.awt.Font;
import java.awt.event.KeyEvent;


/*
 * Gestiona la pantalla de menu principal.
 * Muestra titulo, top 10 y captura el nombre del jugador
 * usando las teclas del canvas igual que el resto del juego.
 */
public class Menu {

    /* Estados del menu */
    public static final int ESCRIBIENDO_NOMBRE = 0;
    public static final int ESPERANDO_INICIO   = 1;
    public static final int LISTO              = 2;

    /* Estado actual del menu */
    private int estadoActual;

    /* Nombre capturado caracter por caracter */
    private String nombreJugador;

    /* Maximo de caracteres permitidos en el nombre */
    private static final int MAX_NOMBRE = 15;

    /* Referencia al marcador para mostrar el top 10 */
    private Marcador marcador;

    /* Canvas para leer teclas y dibujar */
    private Lienzo miCanvas;

    /* Cooldown para evitar que una tecla se registre multiples veces */
    private int cooldownTecla;
    private static final int FPS_COOLDOWN_TECLA = 8;

    /* Constructor */
    public Menu(Lienzo canvas, Marcador marcador) {
        this.miCanvas      = canvas;
        this.marcador      = marcador;
        this.nombreJugador = "";
        this.estadoActual  = ESCRIBIENDO_NOMBRE;
        this.cooldownTecla = 30;
    }

    /*
     * Reinicia el menu para una nueva partida.
     * Limpia el nombre y vuelve al estado inicial.
     */
    public void reiniciar() {
        nombreJugador = "";
        estadoActual  = ESCRIBIENDO_NOMBRE;
        cooldownTecla = 0;
    }

    /*
     * Procesa la entrada del teclado segun el estado actual.
     * En ESCRIBIENDO_NOMBRE captura letras y numeros.
     * En ESPERANDO_INICIO espera ESPACIO o F2 para comenzar.
     */
    public void mover() {
        /* Reducir cooldown cada frame para no repetir teclas */
        if (cooldownTecla > 0) {
            cooldownTecla -= 1;
            return;
        }

        switch (estadoActual) {

            case ESCRIBIENDO_NOMBRE:
                procesarNombre();
                break;

            case ESPERANDO_INICIO:
                /* Esperar ESPACIO o F2 para iniciar el juego */
                if (miCanvas.fuePulsadaTecla(KeyEvent.VK_SPACE)
                        || miCanvas.fuePulsadaTecla(KeyEvent.VK_F2)) {
                    estadoActual  = LISTO;
                    cooldownTecla = FPS_COOLDOWN_TECLA;
                }
                break;

            case LISTO:
                /* El juego principal toma el control */
                break;
        }
    }

    /*
     * Captura el nombre del jugador letra por letra.
     * ENTER confirma el nombre si tiene al menos 1 caracter.
     * BACKSPACE borra el ultimo caracter.
     */
    private void procesarNombre() {
        /* ENTER confirma el nombre */
        if (miCanvas.fuePulsadaTecla(KeyEvent.VK_ENTER)
                && nombreJugador.length() > 0) {
            estadoActual  = ESPERANDO_INICIO;
            cooldownTecla = FPS_COOLDOWN_TECLA;
            return;
        }

        /* BACKSPACE borra el ultimo caracter */
        if (miCanvas.fuePulsadaTecla(KeyEvent.VK_BACK_SPACE)
                && nombreJugador.length() > 0) {
            nombreJugador = nombreJugador.substring(0, nombreJugador.length() - 1);
            cooldownTecla = FPS_COOLDOWN_TECLA;
            return;
        }

        /* Capturar letras A-Z */
        if (nombreJugador.length() < MAX_NOMBRE) {
            for (int tecla = KeyEvent.VK_A; tecla <= KeyEvent.VK_Z; tecla++) {
                if (miCanvas.fuePulsadaTecla(tecla)) {
                    /* Convertir codigo de tecla a caracter */
                    nombreJugador += (char) tecla;
                    cooldownTecla  = FPS_COOLDOWN_TECLA;
                    return;
                }
            }

            /* Capturar numeros 0-9 */
            for (int tecla = KeyEvent.VK_0; tecla <= KeyEvent.VK_9; tecla++) {
                if (miCanvas.fuePulsadaTecla(tecla)) {
                    nombreJugador += (char) tecla;
                    cooldownTecla  = FPS_COOLDOWN_TECLA;
                    return;
                }
            }
        }
    }

    /*
     * Pinta la pantalla del menu: titulo, top 10 y campo de nombre.
     */
    public void pinta() {
        miCanvas.dibujo(450,400, ".\\resources\\Menu2.png");
        //Color del texto
        miCanvas.ponColorLapiz(255, 255, 255);
        //Fuente del texto
        Font fuenteTitulo = new Font("MS UI Gothic", Font.PLAIN, 90);
        miCanvas.ponFuente(fuenteTitulo);
        /* Titulo del juego */
        miCanvas.texto(450, 750, " M I X T L I ");
        //Texto menu
        Font fuenteTexto = new Font("MS UI Gothic", Font.PLAIN, 25);
        //Texto puntajes
        Font fuentePuntajes = new Font("MS UI Gothic", Font.PLAIN, 12);
        miCanvas.ponFuente(fuentePuntajes);
        
        miCanvas.texto(800, 420, "Top 10 Mejores Puntajes:");

        /* Mostrar el top 10 */
        String[] tabla = marcador.getTabla();
        for (int i = 0; i < tabla.length; i++) {
            /* Cada entrada ocupa 30px de alto, empezando en y=270 */
            miCanvas.texto(800, 390 - (i * 30), tabla[i]);
        }

        /* Mensaje segun el estado actual */
        switch (estadoActual) {

            case ESCRIBIENDO_NOMBRE:
                //Texto menu
                
                miCanvas.ponFuente(fuenteTexto);
                miCanvas.ponColorLapiz(0, 0, 0);
                miCanvas.texto(450, 550, "Ingresa tu nombre:");
                miCanvas.texto(450, 480, nombreJugador);
                miCanvas.texto(450, 410, "Presiona ENTER para confirmar");
                break;

            case ESPERANDO_INICIO:
                miCanvas.ponFuente(fuenteTexto);
                miCanvas.ponColorLapiz(0, 0, 0);
                miCanvas.texto(450, 550, "Jugador: " + nombreJugador);
                miCanvas.texto(450, 480, "Presiona ESPACIO o F2 para iniciar");
                break;

            case LISTO:
                /* No se muestra nada, el juego ya inicio */
                break;
        }
    }

    /* Retorna true cuando el jugador confirmo nombre e inicio */
    public boolean estaListo() {
        return estadoActual == LISTO;
    }

    /* Retorna el nombre capturado del jugador */
    public String getNombreJugador() {
        return nombreJugador;
    }
}