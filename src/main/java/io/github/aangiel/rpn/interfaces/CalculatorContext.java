package io.github.aangiel.rpn.interfaces;

/**
 * @param <T>
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public interface CalculatorContext<T extends Number>
        extends ConstructorContext<T>, FunctionOrOperatorContext<T> {
}
