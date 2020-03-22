package io.github.aangiel.rpn.math;

import io.github.aangiel.rpn.context.CalculatorContext;

/**
 * Used as lambda in {@link CalculatorContext#getConstructor() CalculatorContext.getValue()}
 * You should implement during extending above class as returning new Object from constructor of type extending
 * {@link Number Number}
 *
 * @param <T> extends Number
 */
@FunctionalInterface
public interface IConstructor<T extends Number> {

    T apply(String value);
}
