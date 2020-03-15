package io.github.arturangiel.rpncalculator;

import io.github.arturangiel.rpncalculator.exception.CalculatorException;

public interface Calculator<T extends Number> {

    T calculate(String equation) throws CalculatorException;

    CalculatorContext getContext();
}
