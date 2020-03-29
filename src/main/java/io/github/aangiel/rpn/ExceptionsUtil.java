package io.github.aangiel.rpn;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public final class ExceptionsUtil {
    public static String npe(String paramName) {
        return String.format("Param '%s' can't be null", paramName);
    }
}
