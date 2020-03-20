package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.math.ConstructorValue;

public class DoubleCalculatorContext extends CalculatorContext<Double> {
    public DoubleCalculatorContext() {
        super(double.class, Math.class, 0);
    }

    @Override
    protected void populateDefaultOperations() {
        addFunction("+", 2, args -> args.get(0) + args.get(1));
        addFunction("-", 2, args -> args.get(0) - args.get(1));
        addFunction("*", 2, args -> args.get(0) * args.get(1));
        addFunction("/", 2, args -> args.get(0) / args.get(1));
    }

    @Override
    protected void populateConstants() {
        addFunction("pi", 0, args -> Math.PI);
        addFunction("e", 0, args -> Math.E);
    }

    @Override
    public ConstructorValue<Double> getValue() {
        return args -> Double.valueOf(
                (String) args.get(0)
        );
    }

    @Override
    protected CalculatorContext<Double> self() {
        return this;
    }
}
