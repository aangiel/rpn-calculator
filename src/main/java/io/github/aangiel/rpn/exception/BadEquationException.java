package io.github.aangiel.rpn.exception;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public final class BadEquationException extends CalculatorException {

    private final Collection<? extends Number> stack;

    public BadEquationException(Deque<? extends Number> stack) {
        super("Left on stack: %s", stack);
        this.stack = Collections.unmodifiableCollection(stack);
    }

    public Collection<? extends Number> getStack() {
        return stack;
    }
}
