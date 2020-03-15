package io.github.arturangiel.rpncalculator.math;

import io.github.arturangiel.rpncalculator.exception.CalculatorException;

@FunctionalInterface
public interface IMathFunction<T extends Number> {

    T apply(T[] t) throws CalculatorException;
}
