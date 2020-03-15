package io.github.arturangiel.rpncalculator.math;

public class FunctionValue<T extends Number> {
    private int parametersCount;
    private IMathFunction<T> function;

    public FunctionValue(int parametersCount, IMathFunction<T> function) {
        this.parametersCount = parametersCount;
        this.function = function;
    }

    public static FunctionValue forFunction(int parametersCount, IMathFunction function) {
        return new FunctionValue(parametersCount, function);
    }

    public int getParametersCount() {
        return parametersCount;
    }

    public IMathFunction<T> getFunction() {
        return function;
    }
}
