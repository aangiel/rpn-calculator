package io.github.aangiel.rpn.exception;

public class EmptyEquationException extends CalculatorException {
    public EmptyEquationException() {
        super("Empty equation");
    }
}
