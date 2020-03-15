package io.github.arturangiel.rpncalculator.supplier;

import io.github.arturangiel.rpncalculator.math.IMathFunction;
import org.apfloat.Apfloat;

public class ApfloatDefaultOperationsSupplier<T extends Number> implements DefaultOperationsSupplier<Apfloat> {
    @Override
    public IMathFunction<Apfloat> getAddFunction() {
        return a -> a[0].add(a[1]);
    }

    @Override
    public IMathFunction<Apfloat> getSubtractFunction() {
        return a -> a[0].subtract(a[1]);
    }

    @Override
    public IMathFunction<Apfloat> getMultiplyFunction() {
        return a -> a[0].multiply(a[1]);
    }

    @Override
    public IMathFunction<Apfloat> getDivideFunction() {
        return a -> a[0].divide(a[1]);
    }
}
