package io.github.aangiel.rpn.concurrent;

import io.github.aangiel.rpn.Calculator;
import io.github.aangiel.rpn.CalculatorSupplier;

import java.util.Objects;
import java.util.concurrent.Callable;

public final class CalculatorCallable<T extends Number> implements Callable<T> {

    private final String equation;
    private final Calculator<T> calculator;

    /**
     * @param clazz    Class extending {@link Number}
     * @param equation equation to be calculated
     * @throws NullPointerException     if equation is null
     * @throws IllegalArgumentException if there are no calculator for type given in 'clazz' param
     */
    private CalculatorCallable(Class<T> clazz, String equation) {
        Objects.requireNonNull(clazz);
        this.equation = Objects.requireNonNull(equation);
        this.calculator = CalculatorSupplier.INSTANCE.getCalculator(clazz);
    }

    /**
     * @param clazz
     * @param equation
     * @param <T>
     * @return
     * @throws NullPointerException     if equation is null
     * @throws IllegalArgumentException if there are no calculator for type given in 'clazz' param
     */
    public static <T extends Number> CalculatorCallable<T> of(Class<T> clazz, String equation) {
        return new CalculatorCallable<>(clazz, equation);
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    public T call() {
        return calculator.calculate(equation);
    }
}