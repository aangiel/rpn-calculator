package io.github.aangiel.rpn.exception;

public abstract class CalculatorException extends Exception {
    public CalculatorException(String message, Object... args) {
        super(String.format(message, args));
    }
}
