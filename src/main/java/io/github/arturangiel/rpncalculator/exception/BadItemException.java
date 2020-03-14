package io.github.arturangiel.rpncalculator.exception;

public class BadItemException extends CalculatorException {

    public BadItemException(String item, int position) {
        super("Bad item: '" + item + "' at position: " + (position + 1));
    }
}
