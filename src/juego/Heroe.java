package juego;
/*
 * Heroe: La nave del jugador.
 * Extiende ElementoGrafico para heredar coordenadas y visibilidad.
 * Se mueve horizontalmente con las teclas A/D o flechas.
 */
import edu.epromero.util.Lienzo;
import java.awt.event.KeyEvent;

/*
 * Nave rebelde controlada por el jugador.
 * Inicia en el centro inferior de la pantalla.
 * Puede disparar una ráfaga de hasta 3 balas simultáneas.
 */
public class Heroe extends ElementoGrafico {

    /* Velocidad de movimiento en píxeles por frame */
    private double velocidad;

    /* Límites de la pantalla, se asignan en inicia() */
    private double limiteDer;
    private double limiteIzq;
    private double limiteSup;
    private double limiteInf;

    /* Máximo de balas simultáneas en pantalla */
    private static final int MAX_BALAS = 3;

    /* Arreglo de balas para la ráfaga */
    private Bala[] balas;

    /* Cooldown entre disparos para controlar la ráfaga */
    private int cooldownDisparo;
    private static final int FPS_COOLDOWN = 8;

    /* Constructor: solo asigna sprite y velocidad */
    public Heroe() {
        velocidad = 5;
        cooldownDisparo = 0;
        setSprite("pixil-frame-0.png");

        /* Crear las balas, se inicializarán en inicia() */
        balas = new Bala[MAX_BALAS];
        for (int i = 0; i < MAX_BALAS; i++) {
            balas[i] = new Bala();
        }
    }

    /*
     * Inicializa posición y carga imágenes.
     * Usa los límites del canvas igual que RaquetaSinIA.
     */
    @Override
    public void inicia(Lienzo canvas) {
        /* Cargar sprite del héroe */
        super.inicia(canvas);

        /* Guardar límites de pantalla para usarlos en mover() */
        limiteDer = canvas.pideLimiteXMax();
        limiteIzq   = canvas.pideLimiteXMin();
        limiteSup  = canvas.pideLimiteYMax();
        limiteInf   = canvas.pideLimiteYMin();

        /* Posición inicial: centro horizontal, borde inferior */
        columna = (limiteDer - limiteIzq) / 2;
        renglon = limiteInf;

        /* Inicializar cada bala con el canvas */
        for (int i = 0; i < MAX_BALAS; i++) {
            balas[i].inicia(canvas);
        }

        /* Activar visibilidad */
        vida = true;
    }

    /*
     * Pinta el héroe y todas sus balas activas.
     */
    @Override
    public void pinta(Lienzo canvas) {
        /* Pinta el sprite del héroe (superclase) */
        super.pinta(canvas);

        /* Pinta cada bala activa */
        for (int i = 0; i < MAX_BALAS; i++) {
            balas[i].pinta(canvas);
        }
    }

    /*
     * Busca la primera bala inactiva y la dispara.
     * Si las 3 están en vuelo no dispara nada.
     */
    private boolean dispararBala(double columna, double renglon) {
        for (int i = 0; i < MAX_BALAS; i++) {
            if (!balas[i].estaEnVuelo()) {
                balas[i].disparar(columna, renglon);
                return true;
            }
        }
        return false;
    }

    /*
     * Mueve la nave leyendo el teclado desde e.getMiCanvas().
     * Gestiona cooldown, disparo y movimiento de todas las balas.
     */
    @Override
    public void mover(Entrada e) {
        /* Obtener canvas desde la entrada para leer teclas */
        Lienzo canvas = e.getMiCanvas();

        /* ── Movimiento izquierda ── */
        if (canvas.fuePulsadaTecla(KeyEvent.VK_LEFT) ||
            canvas.fuePulsadaTecla(KeyEvent.VK_A)) {
            columna -= velocidad;
        }

        /* ── Movimiento derecha ── */
        if (canvas.fuePulsadaTecla(KeyEvent.VK_RIGHT) ||
            canvas.fuePulsadaTecla(KeyEvent.VK_D)) {
            columna += velocidad;
        }

        /* ── Movimiento arriba ── */
        if (canvas.fuePulsadaTecla(KeyEvent.VK_UP) ||
            canvas.fuePulsadaTecla(KeyEvent.VK_W)) {
            renglon += velocidad;
        }

        /* ── Movimiento abajo ── */
        if (canvas.fuePulsadaTecla(KeyEvent.VK_DOWN) ||
            canvas.fuePulsadaTecla(KeyEvent.VK_S)) {
            renglon -= velocidad;
        }

        /* ── Límites de pantalla ── */
        if (renglon < limiteInf)   renglon = limiteInf;
        if (renglon > limiteSup)  renglon = limiteSup;
        if (columna < limiteIzq)   columna = limiteIzq;
        if (columna > limiteDer) columna = limiteDer;

        /* ── Reducir cooldown cada frame ── */
        if (cooldownDisparo > 0) {
            cooldownDisparo -= 1;
        }

        /* ── Disparo con barra espaciadora ── */
        if (canvas.fuePulsadaTecla(KeyEvent.VK_SPACE)
                && cooldownDisparo == 0) {
            /*
             * La bala sale del centro-superior de la nave.
             * TODO: ajusta +20 y -10 según el tamaño de Heroe.png
             */
            dispararBala(columna + 20, renglon - 10);
            cooldownDisparo = FPS_COOLDOWN;
        }

        /* ── Mover todas las balas cada frame ── */
        for (int i = 0; i < MAX_BALAS; i++) {
            balas[i].mover(e);
        }
    }

    /* Expone las balas para colisiones en Juego.java */
    public Bala[] getBalas() {
        return balas;
    }
}