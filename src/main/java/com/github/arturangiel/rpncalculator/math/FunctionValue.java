package com.github.arturangiel.rpncalculator.math;

import org.apfloat.Apfloat;

public class FunctionValue {
    private int parametersCount;
    private IMathFunction<Apfloat> function;

    public FunctionValue(int parametersCount, IMathFunction<Apfloat> function) {
        this.parametersCount = parametersCount;
        this.function = function;
    }

    public static FunctionValue forFunction(int parametersCount, IMathFunction<Apfloat> function) {
        return new FunctionValue(parametersCount, function);
    }

    public int getParametersCount() {
        return parametersCount;
    }

    public IMathFunction<Apfloat> getFunction() {
        return function;
    }
}
