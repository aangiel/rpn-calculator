package io.github.aangiel.rpn.math;

import io.github.aangiel.rpn.exception.CalculatorException;

import java.util.List;

/**
 * Used as lambda in
 * {@link io.github.aangiel.rpn.context.CalculatorContext#addFunctionOrOperator(String, int, IFunction)
 * CalculatorContext.addFunctionOrOperator}
 * for calculating operators (i.e. "+" or "-"), math functions (i.e. "sin" or "cos") and constants (i.e. "pi" or "e")
 *
 * @param <T> extends {@link Number Number}
 */
@FunctionalInterface
public interface IFunction<T extends Number> {

    T apply(List<T> args) throws CalculatorException;
}
