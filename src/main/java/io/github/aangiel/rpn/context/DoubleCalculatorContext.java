package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.math.ConstructorValue;

public class DoubleCalculatorContext extends CalculatorContext<Double> {
    public DoubleCalculatorContext() {
        super(double.class, Math.class, 0);
    }

    @Override
    protected void populateDefaultOperations() {
        addFunction("+", 2, a -> a.get(0) + a.get(1));
        addFunction("-", 2, a -> a.get(0) - a.get(1));
        addFunction("*", 2, a -> a.get(0) * a.get(1));
        addFunction("/", 2, a -> a.get(0) / a.get(1));
    }

    @Override
    protected void populateConstants() {
        addFunction("pi", 0, a -> Math.PI);
        addFunction("e", 0, a -> Math.E);
    }

    @Override
    public ConstructorValue<Double> getValue() {
        return a -> Double.valueOf((String) a.get(0));
    }
}
