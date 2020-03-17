package io.github.aangiel.rpn.exception;

public class UnexpectedException extends CalculatorException {
    public UnexpectedException(String message, Object... args) {
        super(message, args);
    }
}
