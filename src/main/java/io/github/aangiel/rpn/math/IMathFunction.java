package io.github.aangiel.rpn.math;

import io.github.aangiel.rpn.exception.CalculatorException;

import java.util.List;

@FunctionalInterface
public interface IMathFunction<T extends Number> {

    T apply(List<T> t) throws CalculatorException;
}
