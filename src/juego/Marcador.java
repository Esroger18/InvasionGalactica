/*
 * Marcador.java
 * Gestiona el top 10 de mejores puntajes del juego.
 * Lee y escribe en un archivo de texto plano (puntajes.txt).
 * Cada linea del archivo tiene el formato: Nombre,Puntos,Fecha
 * Los datos persisten aunque se cierre el juego.
 */
package juego;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

/*
 * Almacena y ordena los 10 mejores puntajes en archivo de texto.
 * Usa arreglos paralelos para nombres, puntos y fechas,
 * ya que no se pueden usar listas en este proyecto.
 */
public class Marcador {

    /* Maximo de puntajes que se guardan */
    private static final int MAX_PUNTAJES = 10;

    /* Nombre del archivo donde se persisten los puntajes */
    private static final String ARCHIVO = "puntajes.txt";

    /* Separador de campos en el archivo de texto */
    private static final String SEPARADOR = ",";

    /* Arreglos paralelos para los datos del top 10 */
    private String[] nombres;
    private int[]    puntos;
    private String[] fechas;

    /* Cantidad de entradas actualmente cargadas */
    private int totalEntradas;

    /* Constructor: carga los puntajes existentes del archivo */
    public Marcador() {
        nombres       = new String[MAX_PUNTAJES];
        puntos        = new int[MAX_PUNTAJES];
        fechas        = new String[MAX_PUNTAJES];
        totalEntradas = 0;
        cargarDesdArchivo();
    }

    /*
     * Lee el archivo de puntajes y llena los arreglos.
     * Si el archivo no existe, los arreglos quedan vacios.
     */
    private void cargarDesdArchivo() {
        try {
            BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO));
            String linea = lector.readLine();

            /* Leer hasta MAX_PUNTAJES lineas o fin de archivo */
            while (linea != null && totalEntradas < MAX_PUNTAJES) {
                String[] partes = linea.split(SEPARADOR);

                /* Validar que la linea tenga los 3 campos esperados */
                if (partes.length == 3) {
                    nombres[totalEntradas] = partes[0];
                    puntos[totalEntradas]  = Integer.parseInt(partes[1].trim());
                    fechas[totalEntradas]  = partes[2];
                    totalEntradas += 1;
                }
                linea = lector.readLine();
            }
            lector.close();

        } catch (IOException ex) {
            /* Si el archivo no existe aun, se empieza con tabla vacia */
            totalEntradas = 0;
        }
    }

    /*
     * Guarda todos los puntajes actuales en el archivo.
     * Sobreescribe el archivo con el estado actual del arreglo.
     */
    private void guardarEnArchivo() {
        try {
            PrintWriter escritor = new PrintWriter(new FileWriter(ARCHIVO, false));

            /* Escribir cada entrada en su propia linea */
            for (int i = 0; i < totalEntradas; i++) {
                escritor.println(nombres[i] + SEPARADOR
                               + puntos[i]  + SEPARADOR
                               + fechas[i]);
            }
            escritor.close();

        } catch (IOException ex) {
            System.out.println("Error al guardar puntajes: " + ex.getMessage());
        }
    }

    /*
     * Intenta agregar un nuevo puntaje al top 10.
     * Si el puntaje califica (mayor que alguno existente o hay espacio),
     * lo inserta en la posicion correcta manteniendo el orden descendente.
     * Retorna true si el puntaje entro al top 10.
     */
    public boolean agregarPuntaje(String nombre, int puntaje) {
        /* Obtener la fecha actual en formato YYYY-MM-DD */
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = formato.format(new Date());

        /* Verificar si califica: hay espacio o supera al ultimo */
        boolean califica = totalEntradas < MAX_PUNTAJES
                        || puntaje > puntos[totalEntradas - 1];

        if (!califica) {
            return false;
        }

        /* Si ya hay 10 entradas, el ultimo lugar se descarta */
        if (totalEntradas < MAX_PUNTAJES) {
            totalEntradas += 1;
        }

        /* Insertar en la posicion correcta (orden descendente por puntos) */
        /* Desplazar hacia abajo los puntajes menores */
        int posicion = totalEntradas - 1;
        while (posicion > 0 && puntos[posicion - 1] < puntaje) {
            nombres[posicion] = nombres[posicion - 1];
            puntos[posicion]  = puntos[posicion - 1];
            fechas[posicion]  = fechas[posicion - 1];
            posicion -= 1;
        }

        /* Colocar el nuevo puntaje en su posicion */
        nombres[posicion] = nombre;
        puntos[posicion]  = puntaje;
        fechas[posicion]  = fecha;

        /* Persistir inmediatamente en el archivo */
        guardarEnArchivo();
        return true;
    }

    /*
     * Retorna una representacion del top 10 lista para mostrar en pantalla.
     * Cada elemento del arreglo es una linea formateada.
     */
    public String[] getTabla() {
        String[] tabla = new String[totalEntradas];
        for (int i = 0; i < totalEntradas; i++) {
            tabla[i] = (i + 1) + ". " + nombres[i]
                     + "   " + puntos[i] + " pts"
                     + "   " + fechas[i];
        }
        return tabla;
    }

    /* Retorna el total de entradas actualmente en el top 10 */
    public int getTotalEntradas() {
        return totalEntradas;
    }

    /* Retorna el puntaje minimo actual del top 10 (el ultimo lugar) */
    public int getPuntajeMinimo() {
        if (totalEntradas == 0) {
            return 0;
        }
        return puntos[totalEntradas - 1];
    }
}