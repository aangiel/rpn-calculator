package io.github.aangiel.rpn.context;

import java.util.function.Function;

/**
 * @see CalculatorContext
 */
public final class DoubleCalculatorContext extends CalculatorContext<Double> {

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
    public Function<String, Double> getConstructor() {
        return Double::valueOf;
    }

    @Override
    protected CalculatorContext<Double> self() {
        return this;
    }

    @Override
    protected void populateDefaultOneParameterMathFunctions() {
        addFunctionOrOperator("abs", 1, args -> Math.abs(args.get(0)));
        addFunctionOrOperator("acos", 1, args -> Math.acos(args.get(0)));
        addFunctionOrOperator("asin", 1, args -> Math.asin(args.get(0)));
        addFunctionOrOperator("atan", 1, args -> Math.atan(args.get(0)));
        addFunctionOrOperator("atan2", 2, args -> Math.atan2(args.get(0), args.get(1)));
        addFunctionOrOperator("cosh", 1, args -> Math.cosh(args.get(0)));
        addFunctionOrOperator("nextAfter", 1, args -> Math.nextAfter(args.get(0), args.get(1)));
        addFunctionOrOperator("sin", 1, args -> Math.sin(args.get(0)));
        addFunctionOrOperator("tan", 1, args -> Math.tan(args.get(0)));
        addFunctionOrOperator("tanh", 1, args -> Math.tanh(args.get(0)));
        addFunctionOrOperator("toRadians", 1, args -> Math.toRadians(args.get(0)));
    }
}
