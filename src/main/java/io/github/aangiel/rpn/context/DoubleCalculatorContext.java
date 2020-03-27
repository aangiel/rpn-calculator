package io.github.aangiel.rpn.context;

import java.util.function.Function;

/**
 * @see AbstractCalculatorContext
 */
public final class DoubleCalculatorContext extends AbstractCalculatorContext<Double> {

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
    public Function<String, Double> getNumberConstructor() {
        return Double::valueOf;
    }

    @Override
    public DoubleCalculatorContext self() {
        return this;
    }

    @Override
    protected void populateMathFunctions() {
        populateDefaultMathFunctions(Math.class, double.class, 3);
    }
}
