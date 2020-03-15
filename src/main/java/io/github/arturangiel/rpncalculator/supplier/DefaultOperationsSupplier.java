package io.github.arturangiel.rpncalculator.supplier;

import io.github.arturangiel.rpncalculator.math.IMathFunction;

public interface DefaultOperationsSupplier<T extends Number> {

    IMathFunction<T> getAddFunction();

    IMathFunction<T> getSubtractFunction();

    IMathFunction<T> getMultiplyFunction();

    IMathFunction<T> getDivideFunction();
}
