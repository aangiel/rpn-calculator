package io.github.aangiel.rpn.context.interfaces;

/**
 * @param <T> extends {@link Number}
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public interface CalculatorContext<T extends Number>
        extends ConstructorContext<T>, FunctionOrOperatorContext<T> {
}
