package io.github.arturangiel.rpncalculator.math;

import io.github.arturangiel.rpncalculator.exception.CalculatorException;

@FunctionalInterface
public interface IMathFunction<T> {

    T apply(T[] t) throws CalculatorException;
}
