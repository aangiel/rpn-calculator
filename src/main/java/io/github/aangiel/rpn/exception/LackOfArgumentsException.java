package io.github.aangiel.rpn.exception;

public class LackOfArgumentsException extends CalculatorException {

    public LackOfArgumentsException(String item, int position) {
        super("Lack of arguments for: %s at position: %d", item, position);
    }
}
