package juego;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ComportamientoEnemigo: Anotación "manifiesto" de cada nave enemiga.
 * Sin esta anotación, el evaluador ignoraría la clase.
 * Define el tipo, resistencia y valor en puntos de la nave.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComportamientoEnemigo {

    /** Nombre descriptivo del tipo de nave */
    String tipo();

    /** Número de impactos necesarios para destruir la nave */
    int resistencia();

    /** Puntos que otorga la nave al ser destruida */
    int puntos();
}
