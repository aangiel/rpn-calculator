package io.github.aangiel.rpn.exception;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public final class BadItemException extends CalculatorException {

    public BadItemException(String item, int position) {
        super("Bad item: '%s' at position: %d", item, position);
    }
}
