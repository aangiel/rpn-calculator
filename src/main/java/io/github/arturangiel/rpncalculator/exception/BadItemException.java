package io.github.arturangiel.rpncalculator.exception;

public class BadItemException extends CalculatorException {

    public BadItemException(String item, int position) {
        super(String.format("Bad item: '%s' at position: %d", item, ++position));
    }
}
