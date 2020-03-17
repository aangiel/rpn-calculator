package io.github.aangiel.rpn.exception;

import java.util.Deque;

public class BadEquationException extends CalculatorException {
    public BadEquationException(Deque<Number> stack) {
        super("Left on stack: %s", stack);
    }
}
