package io.github.aangiel.rpn.supplier;

import io.github.aangiel.rpn.math.ConstructorValue;
import io.github.aangiel.rpn.math.IMathFunction;

import java.math.BigDecimal;

public class BigDecimalDefaultOperationsSupplier<T extends Number> implements DefaultOperationsSupplier<BigDecimal> {
    @Override
    public IMathFunction<BigDecimal> getAddFunction() {
        return a -> a.get(0).add(a.get(1));
    }

    @Override
    public IMathFunction<BigDecimal> getSubtractFunction() {
        return a -> a.get(0).subtract(a.get(1));
    }

    @Override
    public IMathFunction<BigDecimal> getMultiplyFunction() {
        return a -> a.get(0).multiply(a.get(1));
    }

    @Override
    public IMathFunction<BigDecimal> getDivideFunction() {
        return a -> a.get(0).divide(a.get(1));
    }

    @Override
    public ConstructorValue<BigDecimal> getValue() {
        return a -> new BigDecimal((String) a.get(0));
    }
}
