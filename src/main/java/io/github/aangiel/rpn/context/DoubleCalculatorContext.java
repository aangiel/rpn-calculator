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
    public Function<String, Double> getNumberConstructor() {
        return Double::valueOf;
    }

    @Override
    protected CalculatorContext<Double> self() {
        return this;
    }

    @Override
    protected void populateMathFunctions() {
        addFunctionOrOperator("abs", 1, args -> Math.abs(args.get(0)));
        addFunctionOrOperator("acos", 1, args -> Math.acos(args.get(0)));
        addFunctionOrOperator("asin", 1, args -> Math.asin(args.get(0)));
        addFunctionOrOperator("atan", 1, args -> Math.atan(args.get(0)));
        addFunctionOrOperator("atan2", 2, args -> Math.atan2(args.get(0), args.get(1)));
        addFunctionOrOperator("cbrt", 1, args -> Math.cbrt(args.get(0)));
        addFunctionOrOperator("ceil", 1, args -> Math.ceil(args.get(0)));
        addFunctionOrOperator("copySign", 2, args -> Math.copySign(args.get(0), args.get(1)));
        addFunctionOrOperator("cos", 1, args -> Math.cos(args.get(0)));
        addFunctionOrOperator("cosh", 1, args -> Math.cosh(args.get(0)));
        addFunctionOrOperator("exp", 1, args -> Math.exp(args.get(0)));
        addFunctionOrOperator("expm1", 1, args -> Math.expm1(args.get(0)));
        addFunctionOrOperator("floor", 1, args -> Math.floor(args.get(0)));
        addFunctionOrOperator("hypot", 2, args -> Math.hypot(args.get(0), args.get(1)));
        addFunctionOrOperator("log", 1, args -> Math.log(args.get(0)));
        addFunctionOrOperator("log1p", 1, args -> Math.log1p(args.get(0)));
        addFunctionOrOperator("log10", 1, args -> Math.log10(args.get(0)));
        addFunctionOrOperator("max", 2, args -> Math.max(args.get(0), args.get(1)));
        addFunctionOrOperator("min", 2, args -> Math.min(args.get(0), args.get(1)));
        addFunctionOrOperator("nextAfter", 1, args -> Math.nextAfter(args.get(0), args.get(1)));
        addFunctionOrOperator("sin", 1, args -> Math.sin(args.get(0)));
        addFunctionOrOperator("sinh", 1, args -> Math.sinh(args.get(0)));
        addFunctionOrOperator("sqrt", 1, args -> Math.sqrt(args.get(0)));
        addFunctionOrOperator("tan", 1, args -> Math.tan(args.get(0)));
        addFunctionOrOperator("tanh", 1, args -> Math.tanh(args.get(0)));
        addFunctionOrOperator("toDegrees", 1, args -> Math.toDegrees(args.get(0)));
        addFunctionOrOperator("toRadians", 1, args -> Math.toRadians(args.get(0)));
    }
}
