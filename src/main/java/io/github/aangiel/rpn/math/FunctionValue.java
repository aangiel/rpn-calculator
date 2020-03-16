package io.github.aangiel.rpn.math;

public class FunctionValue<T extends Number> {
    private int parametersCount;
    private IMathFunction<T> function;

    public FunctionValue(int parametersCount, IMathFunction<T> function) {
        this.parametersCount = parametersCount;
        this.function = function;
    }


    public int getParametersCount() {
        return parametersCount;
    }

    public IMathFunction<T> getFunction() {
        return function;
    }
}
