package io.github.aangiel.rpn.exception;

import java.util.Deque;

public final class BadEquationException extends CalculatorException {
    public <T extends Number> BadEquationException(Deque<T> stack) {
        super("Left on stack: %s", stack);
    }
}
