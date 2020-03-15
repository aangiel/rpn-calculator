package io.github.arturangiel.rpncalculator.exception;

public class LackOfArgumentsException extends CalculatorException {

    public LackOfArgumentsException(String item, int position) {
        super(String.format("Lack of arguments for: %s at position: %d", item, ++position));
    }
}
