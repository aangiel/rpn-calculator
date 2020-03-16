package io.github.aangiel.rpn.math;

import io.github.aangiel.rpn.exception.CalculatorException;

@FunctionalInterface
public interface IMathFunction<T extends Number> {

    T apply(T[] t) throws CalculatorException;
}
