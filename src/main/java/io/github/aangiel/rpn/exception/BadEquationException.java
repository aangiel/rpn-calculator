package io.github.aangiel.rpn.exception;

public class BadEquationException extends CalculatorException {
    public BadEquationException(Object stack) {
        super("Left on stack: %s", stack);
    }
}
