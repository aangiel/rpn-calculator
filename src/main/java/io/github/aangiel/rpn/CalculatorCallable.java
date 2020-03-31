package io.github.aangiel.rpn;

import java.util.concurrent.Callable;

public final class CalculatorCallable<T extends Number> implements Callable<T> {

    private final String equation;
    private final Calculator<T> calculator;

    private CalculatorCallable(Class<T> clazz, String equation) {
        this.equation = equation;
        this.calculator = CalculatorSupplier.INSTANCE.getCalculator(clazz);
    }

    public static <T extends Number> CalculatorCallable<T> of(Class<T> clazz, String equation) {
        return new CalculatorCallable<>(clazz, equation);
    }

    @Override
    public T call() {
        return calculator.calculate(equation);
    }
}