package io.github.aangiel.rpn.context;

import java.util.function.Function;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 * @see AbstractCalculatorContext
 */
public final class DoubleCalculatorContext extends AbstractCalculatorContext<Double> {

    @Override
    protected void populateDefaultOperations() {
        addFunctionOrOperator("+", args -> args.remove(1) + args.pop());
        addFunctionOrOperator("-", args -> args.remove(1) - args.pop());
        addFunctionOrOperator("*", args -> args.remove(1) * args.pop());
        addFunctionOrOperator("/", args -> args.remove(1) / args.pop());
    }

    @Override
    protected void populateConstants() {
        addFunctionOrOperator("pi", args -> Math.PI);
        addFunctionOrOperator("e", args -> Math.E);
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
        addFunctionOrOperator("IEEEremainder", args -> Math.IEEEremainder(args.remove(1), args.pop()));
        addFunctionOrOperator("abs", args -> Math.abs(args.pop()));
        addFunctionOrOperator("acos", args -> Math.acos(args.pop()));
        addFunctionOrOperator("asin", args -> Math.asin(args.pop()));
        addFunctionOrOperator("atan", args -> Math.atan(args.pop()));
        addFunctionOrOperator("atan2", args -> Math.atan2(args.remove(1), args.pop()));
        addFunctionOrOperator("cbrt", args -> Math.cbrt(args.pop()));
        addFunctionOrOperator("ceil", args -> Math.ceil(args.pop()));
        addFunctionOrOperator("copySign", args -> Math.copySign(args.remove(1), args.pop()));
        addFunctionOrOperator("cos", args -> Math.cos(args.pop()));
        addFunctionOrOperator("cosh", args -> Math.cosh(args.pop()));
        addFunctionOrOperator("exp", args -> Math.exp(args.pop()));
        addFunctionOrOperator("expm1", args -> Math.expm1(args.pop()));
        addFunctionOrOperator("floor", args -> Math.floor(args.pop()));
        addFunctionOrOperator("fma", args -> Math.fma(args.remove(2), args.remove(1), args.pop()));
        addFunctionOrOperator("hypot", args -> Math.hypot(args.remove(1), args.pop()));
        addFunctionOrOperator("log", args -> Math.log(args.pop()));
        addFunctionOrOperator("log10", args -> Math.log10(args.pop()));
        addFunctionOrOperator("log1p", args -> Math.log1p(args.pop()));
        addFunctionOrOperator("max", args -> Math.max(args.remove(1), args.pop()));
        addFunctionOrOperator("min", args -> Math.min(args.remove(1), args.pop()));
        addFunctionOrOperator("nextAfter", args -> Math.nextAfter(args.remove(1), args.pop()));
        addFunctionOrOperator("nextDown", args -> Math.nextDown(args.pop()));
        addFunctionOrOperator("nextUp", args -> Math.nextUp(args.pop()));
        addFunctionOrOperator("pow", args -> Math.pow(args.remove(1), args.pop()));
        addFunctionOrOperator("rint", args -> Math.rint(args.pop()));
        addFunctionOrOperator("signum", args -> Math.signum(args.pop()));
        addFunctionOrOperator("sin", args -> Math.sin(args.pop()));
        addFunctionOrOperator("sinh", args -> Math.sinh(args.pop()));
        addFunctionOrOperator("sqrt", args -> Math.sqrt(args.pop()));
        addFunctionOrOperator("tan", args -> Math.tan(args.pop()));
        addFunctionOrOperator("tanh", args -> Math.tanh(args.pop()));
        addFunctionOrOperator("toDegrees", args -> Math.toDegrees(args.pop()));
        addFunctionOrOperator("toRadians", args -> Math.toRadians(args.pop()));
        addFunctionOrOperator("ulp", args -> Math.ulp(args.pop()));
    }
}
