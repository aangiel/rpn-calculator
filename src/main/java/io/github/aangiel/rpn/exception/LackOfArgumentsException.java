package io.github.aangiel.rpn.exception;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public final class LackOfArgumentsException extends CalculatorException {

    public LackOfArgumentsException(String item, int position) {
        super("Lack of arguments for: %s at position: %d", item, position);
    }
}
