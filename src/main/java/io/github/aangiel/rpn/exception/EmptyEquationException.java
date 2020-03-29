package io.github.aangiel.rpn.exception;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public final class EmptyEquationException extends CalculatorException {
    public EmptyEquationException() {
        super("Empty equation");
    }
}
