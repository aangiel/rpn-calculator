package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.math.IConstructorValue;

/**
 * @see CalculatorContext
 */
public final class DoubleCalculatorContext extends CalculatorContext<Double> {
    public DoubleCalculatorContext() {
        super(double.class, Math.class);
    }

    @Override
    protected void populateDefaultOperations() {
        addFunctionOrOperator("+", 2, args -> args.get(0) + args.get(1));
        addFunctionOrOperator("-", 2, args -> args.get(0) - args.get(1));
        addFunctionOrOperator("*", 2, args -> args.get(0) * args.get(1));
        addFunctionOrOperator("/", 2, args -> args.get(0) / args.get(1));
    }

    @Override
    protected void populateConstants() {
        addFunctionOrOperator("pi", 0, args -> Math.PI);
        addFunctionOrOperator("e", 0, args -> Math.E);
    }

    @Override
    public IConstructorValue<Double> getValue() {
        return args -> Double.valueOf(
                String.valueOf(args.get(0))
        );
    }

    @Override
    protected CalculatorContext<Double> self() {
        return this;
    }
}
