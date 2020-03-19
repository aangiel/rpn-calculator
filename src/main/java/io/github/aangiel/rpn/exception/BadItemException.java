package io.github.aangiel.rpn.exception;

public class BadItemException extends CalculatorException {

    public BadItemException(String item, int position) {
        super("Bad item: '%s' at position: %d", item, position);
    }
}
