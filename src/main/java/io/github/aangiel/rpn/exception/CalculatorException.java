package io.github.aangiel.rpn.exception;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public abstract class CalculatorException extends Exception {
    protected CalculatorException(String message, Object... args) {
        super(String.format(message, args));
    }

    public static String npe(String paramName) {
        return String.format("Param '%s' can't be null", paramName);
    }
}
