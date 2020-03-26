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
        MathHelper helper = new MathHelper();
        var defaultOneParameterMathFunctions = helper.getDefaultOneParameterMathFunctions(Math.class, double.class);
        functions.putAll(defaultOneParameterMathFunctions);

        addFunctionOrOperator("atan2", 2, args -> Math.atan2(args.get(0), args.get(1)));
        addFunctionOrOperator("copySign", 2, args -> Math.copySign(args.get(0), args.get(1)));
        addFunctionOrOperator("fma", 3, args -> Math.fma(args.get(0), args.get(1), args.get(2)));
        addFunctionOrOperator("hypot", 2, args -> Math.hypot(args.get(0), args.get(1)));
        addFunctionOrOperator("IEEEremainder", 2, args -> Math.IEEEremainder(args.get(0), args.get(1)));
        addFunctionOrOperator("max", 2, args -> Math.max(args.get(0), args.get(1)));
        addFunctionOrOperator("min", 2, args -> Math.min(args.get(0), args.get(1)));
        addFunctionOrOperator("nextAfter", 2, args -> Math.nextAfter(args.get(0), args.get(1)));
        addFunctionOrOperator("pow", 2, args -> Math.pow(args.get(0), args.get(1)));
    }
}
