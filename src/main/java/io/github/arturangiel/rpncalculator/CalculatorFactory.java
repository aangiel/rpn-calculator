package io.github.arturangiel.rpncalculator;

import io.github.arturangiel.rpncalculator.impl.CalculatorImpl;

public class CalculatorFactory<T extends Number> {
    private Calculator<T> defaultCalculator;
    private Calculator<T> mathFunctionsCalculator;
    private Calculator<T> mathFunctionsAndConstantsCalculator;
    private Calculator<T> emptyCalculator;

    private CalculatorContext<T> context;

    public CalculatorFactory(CalculatorContext<T> context) {
        this.context = context;
    }

    public Calculator<T> getDefaultCalculator() {
        if (defaultCalculator == null)
            defaultCalculator = new CalculatorImpl<>();
        return defaultCalculator;
    }

    public Calculator<T> getMathFunctionsCalculator() {
        if (mathFunctionsCalculator == null)
            mathFunctionsCalculator = new CalculatorImpl<>(context.getMathFunctionsContext(context.getClazz()));
        return mathFunctionsCalculator;
    }

    public Calculator<T> getMathFunctionsAndConstantsCalculator() {
        if (mathFunctionsAndConstantsCalculator == null)
            mathFunctionsAndConstantsCalculator = new CalculatorImpl<>(context.getMathFunctionsAndConstantsContext(context.getClazz()));
        return mathFunctionsAndConstantsCalculator;
    }

    public Calculator<T> getEmptyCalculator() {
        if (emptyCalculator == null)
            emptyCalculator = new CalculatorImpl<>(context.getEmptyContext(context.getClazz()));
        return emptyCalculator;
    }

    public Calculator<T> getCalculatorWithCustomContext(CalculatorContext<T> context) {
        return new CalculatorImpl<>(context);
    }
}
