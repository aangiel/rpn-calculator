package io.github.aangiel.rpn.math;

import io.github.aangiel.rpn.context.CalculatorContext;

import java.util.List;

/**
 * Used as lambda in {@link CalculatorContext#getValue() CalculatorContext.getValue()}
 * You should implement during extending above class as returning new Object from constructor of type extending
 * {@link Number Number}
 *
 * @param <T> extends Number
 */
@FunctionalInterface
public interface IConstructor<T extends Number> {

    T apply(List<Object> args);
}
