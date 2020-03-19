package io.github.aangiel.rpn.supplier;

import io.github.aangiel.rpn.math.ConstructorValue;
import io.github.aangiel.rpn.math.IMathFunction;

public class DoubleDefaultOperationsSupplier<T extends Number> implements DefaultOperationsSupplier<Double> {
    @Override
    public IMathFunction<Double> getAddFunction() {
        return a -> a.get(0) + a.get(1);
    }

    @Override
    public IMathFunction<Double> getSubtractFunction() {
        return a -> a.get(0) - a.get(1);
    }

    @Override
    public IMathFunction<Double> getMultiplyFunction() {
        return a -> a.get(0) * a.get(1);
    }

    @Override
    public IMathFunction<Double> getDivideFunction() {
        return a -> a.get(0) / a.get(1);
    }

    @Override
    public ConstructorValue<Double> getValue() {
        return args -> new Double((String) args.get(0));
    }
}
