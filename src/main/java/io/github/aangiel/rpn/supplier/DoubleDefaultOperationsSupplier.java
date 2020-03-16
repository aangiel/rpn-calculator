package io.github.aangiel.rpn.supplier;

import io.github.aangiel.rpn.math.IMathFunction;

public class DoubleDefaultOperationsSupplier<T extends Number> implements DefaultOperationsSupplier<Double> {
    @Override
    public IMathFunction<Double> getAddFunction() {
        return a -> a[0] + a[1];
    }

    @Override
    public IMathFunction<Double> getSubtractFunction() {
        return a -> a[0] - a[1];
    }

    @Override
    public IMathFunction<Double> getMultiplyFunction() {
        return a -> a[0] * a[1];
    }

    @Override
    public IMathFunction<Double> getDivideFunction() {
        return a -> a[0] / a[1];
    }
}
