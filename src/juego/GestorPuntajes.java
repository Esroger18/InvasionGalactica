package juego;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * GestorPuntajes: Administra el almacenamiento persistente de los
 * 10 mejores puntajes del juego.
 * Guarda: nombre del jugador, fecha de la partida y puntuación.
 * Los datos sobreviven al cierre del juego (archivo de texto plano).
 */
public class GestorPuntajes {

    // Nombre del archivo donde se persisten los puntajes
    private static final String ARCHIVO_PUNTAJES = "puntajes.dat";

    // Número máximo de entradas en la tabla
    private static final int MAX_ENTRADAS = 10;

    // Separador de campos dentro del archivo
    private static final String SEPARADOR = "|";

    // Lista de entradas en memoria
    private List<EntradaPuntaje> tabla;

    /**
     * Crea el gestor y carga los puntajes guardados (si existen).
     */
    public GestorPuntajes() {
        tabla = new ArrayList<>();
        cargarDesdeDisco();
    }

    /**
     * Registra una nueva partida en la tabla.
     * Solo persiste si el puntaje entra en el top 10.
     * @param nombreJugador nombre del jugador
     * @param puntaje       puntuación obtenida
     */
    public void registrar(String nombreJugador, int puntaje) {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        tabla.add(new EntradaPuntaje(nombreJugador, fecha, puntaje));

        // Ordenar de mayor a menor puntaje
        tabla.sort(Comparator.comparingInt(EntradaPuntaje::getPuntaje).reversed());

        // Mantener solo los 10 mejores
        if (tabla.size() > MAX_ENTRADAS) {
            tabla = tabla.subList(0, MAX_ENTRADAS);
        }

        guardarEnDisco();
    }

    /**
     * Verifica si un puntaje merece entrar al top 10.
     * @param puntaje puntuación a evaluar
     * @return true si es suficientemente alto
     */
    public boolean esRecordNuevo(int puntaje) {
        if (tabla.size() < MAX_ENTRADAS) {
            return puntaje > 0;
        }
        return puntaje > tabla.get(tabla.size() - 1).getPuntaje();
    }

    /**
     * @return copia de la lista de entradas ordenada por puntaje
     */
    public List<EntradaPuntaje> getTabla() {
        return new ArrayList<>(tabla);
    }

    /**
     * Lee los puntajes guardados desde el archivo de disco.
     * Ignora líneas mal formadas silenciosamente.
     */
    private void cargarDesdeDisco() {
        File archivo = new File(ARCHIVO_PUNTAJES);

        if (!archivo.exists()) {
            return; // Primera ejecución: no hay puntajes previos
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.split("\\" + SEPARADOR);

                // Validar que la línea tenga los 3 campos esperados
                if (partes.length == 3) {
                    String nombre = partes[0].trim();
                    String fecha  = partes[1].trim();
                    int puntos    = Integer.parseInt(partes[2].trim());
                    tabla.add(new EntradaPuntaje(nombre, fecha, puntos));
                }
            }

            // Ordenar al cargar por si el archivo estaba desordenado
            tabla.sort(Comparator.comparingInt(EntradaPuntaje::getPuntaje).reversed());

        } catch (IOException | NumberFormatException ex) {
            // Si hay error, continuar con tabla vacía
            tabla = new ArrayList<>();
        }
    }

    /**
     * Escribe la tabla actual en el archivo de disco.
     * Sobreescribe el contenido previo con los datos actualizados.
     */
    private void guardarEnDisco() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARCHIVO_PUNTAJES))) {
            for (EntradaPuntaje entrada : tabla) {
                escritor.write(
                    entrada.getNombre() + SEPARADOR +
                    entrada.getFecha()  + SEPARADOR +
                    entrada.getPuntaje()
                );
                escritor.newLine();
            }
        } catch (IOException ex) {
            // Error de escritura: no es crítico para el juego
            System.err.println("Error al guardar puntajes: " + ex.getMessage());
        }
    }

    /**
     * EntradaPuntaje: Clase interna que representa una fila de la tabla.
     */
    public static class EntradaPuntaje {

        private final String nombre;
        private final String fecha;
        private final int    puntaje;

        /**
         * Crea una entrada con los datos de la partida.
         * @param nombre  nombre del jugador
         * @param fecha   fecha de la partida
         * @param puntaje puntuación obtenida
         */
        public EntradaPuntaje(String nombre, String fecha, int puntaje) {
            this.nombre  = nombre;
            this.fecha   = fecha;
            this.puntaje = puntaje;
        }

        /** @return nombre del jugador */
        public String getNombre() { return nombre; }

        /** @return fecha de la partida */
        public String getFecha() { return fecha; }

        /** @return puntuación */
        public int getPuntaje() { return puntaje; }
    }
}
