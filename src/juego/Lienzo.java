package juego;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;

/**
 * Lienzo: Panel gráfico donde se renderiza toda la acción del juego.
 * Usa doble buffer (BufferedImage) para evitar parpadeo.
 * El motor del juego (Juego) actualiza el estado y llama a repaint().
 */
public class Lienzo extends JPanel {

    // Imagen de doble buffer para renderizado sin parpadeo
    private BufferedImage buffer;

    // Contexto gráfico del buffer
    private Graphics2D g2dBuffer;

    // Referencias a los objetos del juego para dibujarlos
    private Heroe         heroe;
    private List<NaveEnemiga> enemigos;
    private List<Explosion>   explosiones;

    // Datos del HUD (Heads-Up Display)
    private int     puntos;
    private int     vidas;
    private int     nivel;
    private boolean juegoIniciado;
    private boolean juegoTerminado;
    private boolean mostrarTabla;
    private List<GestorPuntajes.EntradaPuntaje> tabla;
    private String mensajeEstado;

    // Dimensiones del lienzo
    private final int ancho;
    private final int alto;

    // Fuentes para el HUD
    private static final Font FUENTE_HUD    = new Font("Courier New", Font.BOLD, 16);
    private static final Font FUENTE_TITULO = new Font("Courier New", Font.BOLD, 28);
    private static final Font FUENTE_INFO   = new Font("Courier New", Font.PLAIN, 14);
    private static final Font FUENTE_TABLA  = new Font("Courier New", Font.PLAIN, 13);

    /**
     * Construye el lienzo con las dimensiones dadas.
     * @param ancho ancho en píxeles
     * @param alto  alto en píxeles
     */
    public Lienzo(int ancho, int alto) {
        this.ancho = ancho;
        this.alto  = alto;
        setPreferredSize(new java.awt.Dimension(ancho, alto));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Inicializar buffer de doble renderizado
        buffer    = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        g2dBuffer = buffer.createGraphics();
    }

    /**
     * Actualiza las referencias a los objetos del juego para el renderizado.
     * @param heroe       la nave del jugador
     * @param enemigos    lista de naves enemigas activas
     * @param explosiones lista de explosiones activas
     * @param puntos      puntuación actual
     * @param vidas       vidas restantes
     * @param nivel       nivel actual
     * @param iniciado    si el juego está en curso
     * @param terminado   si el juego ha terminado
     * @param tabla       lista de mejores puntajes (puede ser null)
     * @param mensaje     mensaje de estado para mostrar
     */
    public void actualizar(
        Heroe heroe,
        List<NaveEnemiga> enemigos,
        List<Explosion>   explosiones,
        int     puntos,
        int     vidas,
        int     nivel,
        boolean iniciado,
        boolean terminado,
        List<GestorPuntajes.EntradaPuntaje> tabla,
        String mensaje
    ) {
        this.heroe          = heroe;
        this.enemigos       = enemigos;
        this.explosiones    = explosiones;
        this.puntos         = puntos;
        this.vidas          = vidas;
        this.nivel          = nivel;
        this.juegoIniciado  = iniciado;
        this.juegoTerminado = terminado;
        this.mostrarTabla   = (tabla != null);
        this.tabla          = tabla;
        this.mensajeEstado  = mensaje;
    }

    /**
     * Método de pintado de Swing. Dibuja el buffer en la pantalla.
     * @param g contexto gráfico de Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Limpiar el buffer con fondo negro estelar
        g2dBuffer.setColor(Color.BLACK);
        g2dBuffer.fillRect(0, 0, ancho, alto);

        // Habilitar antialiasing en el buffer
        g2dBuffer.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Dibujar fondo estrellado
        dibujarEstrellas();

        if (!juegoIniciado) {
            // Pantalla de bienvenida
            dibujarPantallaBienvenida();

        } else if (juegoTerminado) {
            // Pantalla de game over
            dibujarPantallaGameOver();

        } else {
            // Juego activo: dibujar todos los elementos
            dibujarJuego();
        }

        // Mostrar tabla de puntajes si aplica
        if (mostrarTabla && tabla != null) {
            dibujarTablaPuntajes();
        }

        // Transferir el buffer al panel visible
        g.drawImage(buffer, 0, 0, null);
    }

    /**
     * Dibuja estrellas de fondo estáticas para ambientar el espacio.
     */
    private void dibujarEstrellas() {
        // Estrellas deterministas basadas en posiciones fijas
        int[][] estrellas = {
            {50, 30}, {120, 80}, {200, 45}, {300, 120}, {450, 60},
            {550, 140}, {650, 30}, {720, 90}, {80, 200}, {160, 250},
            {380, 180}, {500, 220}, {600, 300}, {700, 170}, {30, 350},
            {250, 310}, {420, 380}, {580, 340}, {680, 400}, {100, 420},
            {310, 450}, {490, 480}, {620, 460}, {740, 510}, {70, 530},
            {190, 550}, {330, 520}, {460, 560}, {590, 580}, {710, 540}
        };

        for (int[] pos : estrellas) {
            // Variar el brillo según la posición
            int brillo = 100 + (pos[0] + pos[1]) % 155;
            g2dBuffer.setColor(new Color(brillo, brillo, brillo));
            g2dBuffer.fillRect(pos[0], pos[1], 2, 2);
        }
    }

    /**
     * Dibuja la pantalla de inicio con instrucciones de juego.
     */
    private void dibujarPantallaBienvenida() {
        // Título principal
        g2dBuffer.setFont(FUENTE_TITULO);
        g2dBuffer.setColor(new Color(0, 200, 255));
        dibujarTextoCentrado("INVASION GALACTICA", alto / 2 - 80);

        // Subtítulo
        g2dBuffer.setFont(FUENTE_HUD);
        g2dBuffer.setColor(new Color(180, 180, 220));
        dibujarTextoCentrado("La ultima nave rebelde terrestre", alto / 2 - 40);

        // Instrucciones
        g2dBuffer.setFont(FUENTE_INFO);
        g2dBuffer.setColor(Color.WHITE);
        dibujarTextoCentrado("Presiona ESPACIO o F2 para comenzar", alto / 2 + 10);

        // Controles
        g2dBuffer.setColor(new Color(150, 150, 200));
        dibujarTextoCentrado("Mover: Flechas / A-D    Disparar: ESPACIO", alto / 2 + 50);
        dibujarTextoCentrado("Salir: ESC", alto / 2 + 75);

        // Tabla de enemigos y puntos
        g2dBuffer.setColor(new Color(255, 80, 80));
        dibujarTextoCentrado("Destructor = 50 pts  |  Ave de Presa = 10 pts  |  LanzaMinas = 100 pts",
            alto / 2 + 120);

        // Mensaje parpadeante
        if (mensajeEstado != null) {
            g2dBuffer.setColor(Color.YELLOW);
            g2dBuffer.setFont(FUENTE_HUD);
            dibujarTextoCentrado(mensajeEstado, alto / 2 + 160);
        }
    }

    /**
     * Dibuja todos los elementos del juego activo: naves, balas, explosiones y HUD.
     */
    private void dibujarJuego() {
        // Dibujar enemigos y sus balas
        if (enemigos != null) {
            for (NaveEnemiga enemigo : enemigos) {
                enemigo.dibujar(g2dBuffer);
            }
        }

        // Dibujar explosiones
        if (explosiones != null) {
            for (Explosion exp : explosiones) {
                exp.dibujar(g2dBuffer);
            }
        }

        // Dibujar al héroe y sus balas
        if (heroe != null) {
            heroe.dibujar(g2dBuffer);
        }

        // HUD: barra inferior con stats
        dibujarHUD();
    }

    /**
     * Dibuja el HUD inferior: puntos, vidas y nivel.
     */
    private void dibujarHUD() {
        // Barra negra semitransparente en la parte inferior
        g2dBuffer.setColor(new Color(0, 0, 0, 180));
        g2dBuffer.fillRect(0, alto - 35, ancho, 35);

        // Línea separadora
        g2dBuffer.setColor(new Color(0, 150, 255));
        g2dBuffer.drawLine(0, alto - 36, ancho, alto - 36);

        g2dBuffer.setFont(FUENTE_HUD);

        // Puntuación (izquierda)
        g2dBuffer.setColor(new Color(255, 220, 0));
        g2dBuffer.drawString("PUNTOS: " + puntos, 20, alto - 12);

        // Vidas (centro)
        g2dBuffer.setColor(new Color(0, 255, 100));
        dibujarTextoCentrado("VIDAS: " + vidas, alto - 12);

        // Nivel (derecha)
        g2dBuffer.setColor(new Color(100, 180, 255));
        String textoNivel = "NIVEL: " + nivel;
        FontMetrics fm = g2dBuffer.getFontMetrics();
        g2dBuffer.drawString(textoNivel, ancho - fm.stringWidth(textoNivel) - 20, alto - 12);
    }

    /**
     * Dibuja la pantalla de Game Over con el puntaje final.
     */
    private void dibujarPantallaGameOver() {
        // Texto principal
        g2dBuffer.setFont(FUENTE_TITULO);
        g2dBuffer.setColor(new Color(255, 60, 60));
        dibujarTextoCentrado("GAME OVER", alto / 2 - 60);

        // Puntaje final
        g2dBuffer.setFont(FUENTE_HUD);
        g2dBuffer.setColor(Color.WHITE);
        dibujarTextoCentrado("Puntaje final: " + puntos, alto / 2 - 20);

        // Instrucción para reiniciar
        g2dBuffer.setColor(new Color(0, 200, 255));
        dibujarTextoCentrado("Presiona ESPACIO para volver a jugar", alto / 2 + 30);

        if (mensajeEstado != null) {
            g2dBuffer.setColor(Color.YELLOW);
            dibujarTextoCentrado(mensajeEstado, alto / 2 + 70);
        }
    }

    /**
     * Dibuja la tabla de mejores puntajes superpuesta.
     */
    private void dibujarTablaPuntajes() {
        // Panel semitransparente para la tabla
        int panelX = ancho / 2 - 200;
        int panelY = 50;
        int panelW = 400;
        int panelH = 280;

        g2dBuffer.setColor(new Color(0, 0, 30, 220));
        g2dBuffer.fillRoundRect(panelX, panelY, panelW, panelH, 15, 15);

        g2dBuffer.setColor(new Color(0, 150, 255));
        g2dBuffer.setStroke(new java.awt.BasicStroke(2));
        g2dBuffer.drawRoundRect(panelX, panelY, panelW, panelH, 15, 15);

        // Título de la tabla
        g2dBuffer.setFont(FUENTE_HUD);
        g2dBuffer.setColor(new Color(255, 220, 0));
        dibujarTextoCentrado("TOP 10 - MEJORES PUNTAJES", panelY + 30);

        // Encabezados de columna
        g2dBuffer.setFont(FUENTE_TABLA);
        g2dBuffer.setColor(new Color(150, 200, 255));
        g2dBuffer.drawString("#", panelX + 15, panelY + 55);
        g2dBuffer.drawString("NOMBRE", panelX + 40, panelY + 55);
        g2dBuffer.drawString("FECHA", panelX + 180, panelY + 55);
        g2dBuffer.drawString("PUNTOS", panelX + 300, panelY + 55);

        // Línea separadora
        g2dBuffer.setColor(new Color(0, 100, 200));
        g2dBuffer.drawLine(panelX + 10, panelY + 60, panelX + panelW - 10, panelY + 60);

        // Filas de datos
        g2dBuffer.setColor(Color.WHITE);
        int filaY = panelY + 78;

        for (int i = 0; i < tabla.size() && i < 10; i++) {
            GestorPuntajes.EntradaPuntaje entrada = tabla.get(i);

            // Resaltar el primero en dorado
            if (i == 0) {
                g2dBuffer.setColor(new Color(255, 215, 0));
            } else {
                g2dBuffer.setColor(Color.WHITE);
            }

            g2dBuffer.drawString((i + 1) + ".",         panelX + 15,  filaY);
            g2dBuffer.drawString(entrada.getNombre(),   panelX + 40,  filaY);
            g2dBuffer.drawString(entrada.getFecha(),    panelX + 180, filaY);
            g2dBuffer.drawString(String.valueOf(entrada.getPuntaje()), panelX + 300, filaY);

            filaY += 20;
        }
    }

    /**
     * Dibuja texto centrado horizontalmente en el lienzo.
     * @param texto el texto a dibujar
     * @param y     posición vertical del texto
     */
    private void dibujarTextoCentrado(String texto, int y) {
        FontMetrics fm = g2dBuffer.getFontMetrics();
        int x = (ancho - fm.stringWidth(texto)) / 2;
        g2dBuffer.drawString(texto, x, y);
    }
}
