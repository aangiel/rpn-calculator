package io.github.aangiel.rpn.supplier;

import io.github.aangiel.rpn.math.IMathFunction;
import org.apfloat.Apfloat;

public class ApfloatDefaultOperationsSupplier<T extends Number> implements DefaultOperationsSupplier<Apfloat> {
    @Override
    public IMathFunction<Apfloat> getAddFunction() {
        return a -> a.get(0).add(a.get(1));
    }

    @Override
    public IMathFunction<Apfloat> getSubtractFunction() {
        return a -> a.get(0).subtract(a.get(1));
    }

    @Override
    public IMathFunction<Apfloat> getMultiplyFunction() {
        return a -> a.get(0).multiply(a.get(1));
    }

    @Override
    public IMathFunction<Apfloat> getDivideFunction() {
        return a -> a.get(0).divide(a.get(1));
    }
}
