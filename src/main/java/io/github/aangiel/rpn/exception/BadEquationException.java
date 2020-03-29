package io.github.aangiel.rpn.exception;

import java.util.Deque;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public final class BadEquationException extends CalculatorException {
    public <T extends Number> BadEquationException(Deque<T> stack) {
        super("Left on stack: %s", stack);
    }
}
