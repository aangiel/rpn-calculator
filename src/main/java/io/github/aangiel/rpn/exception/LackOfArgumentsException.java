package io.github.aangiel.rpn.exception;

import java.util.Objects;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public final class LackOfArgumentsException extends CalculatorException {

    private final String item;
    private final int position;

    public LackOfArgumentsException(String item, int position) {
        super("Lack of arguments for: %s at position: %d", item, position);
        this.item = Objects.requireNonNull(item);
        this.position = position;
    }

    public String getItem() {
        return item;
    }

    public int getPosition() {
        return position;
    }


}
