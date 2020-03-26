package io.github.aangiel.rpn.exception;

public final class EmptyEquationException extends CalculatorException {
    public EmptyEquationException() {
        super("Empty equation");
    }
}
