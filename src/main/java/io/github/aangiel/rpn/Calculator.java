package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.CalculatorContext;
import io.github.aangiel.rpn.exception.CalculatorException;

public interface Calculator<T extends Number> {

    T calculate(String equation) throws CalculatorException;

    CalculatorContext<T> getContext();
}
