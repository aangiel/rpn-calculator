package io.github.aangiel.rpn.exception;

import java.util.Objects;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public final class BadItemException extends CalculatorException {

    private final String item;
    private final int position;

    public BadItemException(String item, int position) {
        super("Bad item: '%s' at position: %d", item, position);
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
