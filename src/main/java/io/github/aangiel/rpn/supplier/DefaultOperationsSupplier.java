package io.github.aangiel.rpn.supplier;

import io.github.aangiel.rpn.math.IMathFunction;

public interface DefaultOperationsSupplier<T extends Number> {

    IMathFunction<T> getAddFunction();

    IMathFunction<T> getSubtractFunction();

    IMathFunction<T> getMultiplyFunction();

    IMathFunction<T> getDivideFunction();
}
