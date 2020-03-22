package io.github.aangiel.rpn.context;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.util.function.Function;

/**
 * @see CalculatorContext
 */
public final class ApfloatCalculatorContext extends CalculatorContext<Apfloat> {

    public ApfloatCalculatorContext() {
        super(10);
    }

    @Override
    protected void populateDefaultOperations() {
        addFunctionOrOperator("+", 2, args -> args.get(0).add(args.get(1)));
        addFunctionOrOperator("-", 2, args -> args.get(0).subtract(args.get(1)));
        addFunctionOrOperator("*", 2, args -> args.get(0).multiply(args.get(1)));
        addFunctionOrOperator("/", 2, args -> args.get(0).divide(args.get(1)));
    }

    @Override
    protected void populateConstants() {
        addFunctionOrOperator("pi", 0, args -> ApfloatMath.pi(getPrecision()));
        addFunctionOrOperator("e", 0, args -> new Apfloat(Math.E));
    }

    @Override
    public Function<String, Apfloat> getConstructor() {
        return value -> new Apfloat(value, getPrecision());
    }

    @Override
    protected CalculatorContext<Apfloat> self() {
        return this;
    }

    @Override
    protected void populateDefaultOneParameterMathFunctions() {
        addFunctionOrOperator("abs", 1, args -> ApfloatMath.abs(args.get(0)));
        addFunctionOrOperator("acos", 1, args -> ApfloatMath.acos(args.get(0)));
        addFunctionOrOperator("acosh", 1, args -> ApfloatMath.acosh(args.get(0)));
        addFunctionOrOperator("agm", 2, args -> ApfloatMath.agm(args.get(0), args.get(1)));
        addFunctionOrOperator("asin", 1, args -> ApfloatMath.asin(args.get(0)));
        addFunctionOrOperator("asinh", 1, args -> ApfloatMath.asinh(args.get(0)));
        addFunctionOrOperator("atan", 1, args -> ApfloatMath.atan(args.get(0)));
        addFunctionOrOperator("atan2", 2, args -> ApfloatMath.atan2(args.get(0), args.get(1)));
        addFunctionOrOperator("atanh", 1, args -> ApfloatMath.atanh(args.get(0)));
        addFunctionOrOperator("cbrt", 1, args -> ApfloatMath.cbrt(args.get(0)));
        addFunctionOrOperator("ceil", 1, args -> ApfloatMath.ceil(args.get(0)));
        addFunctionOrOperator("copySign", 2, args -> ApfloatMath.copySign(args.get(0), args.get(1)));
        addFunctionOrOperator("cos", 1, args -> ApfloatMath.cos(args.get(0)));
        addFunctionOrOperator("cosh", 1, args -> ApfloatMath.cosh(args.get(0)));
        addFunctionOrOperator("exp", 1, args -> ApfloatMath.exp(args.get(0)));
        addFunctionOrOperator("floor", 1, args -> ApfloatMath.floor(args.get(0)));
        addFunctionOrOperator("fmod", 2, args -> ApfloatMath.fmod(args.get(0), args.get(1)));
        addFunctionOrOperator("frac", 1, args -> ApfloatMath.frac(args.get(0)));
        addFunctionOrOperator("gamma", 1, args -> ApfloatMath.gamma(args.get(0)));
        addFunctionOrOperator("log", 1, args -> ApfloatMath.log(args.get(0)));
        addFunctionOrOperator("max", 2, args -> ApfloatMath.max(args.get(0), args.get(1)));
        addFunctionOrOperator("min", 1, args -> ApfloatMath.min(args.get(0), args.get(1)));
        addFunctionOrOperator("multiplyAdd", 4, args -> ApfloatMath.multiplyAdd(args.get(0), args.get(1), args.get(2), args.get(3)));
        addFunctionOrOperator("multiplySubtract", 4, args -> ApfloatMath.multiplySubtract(args.get(0), args.get(1), args.get(2), args.get(3)));
        addFunctionOrOperator("pow", 2, args -> ApfloatMath.pow(args.get(0), args.get(1)));
        addFunctionOrOperator("sin", 1, args -> ApfloatMath.sin(args.get(0)));
        addFunctionOrOperator("sinh", 1, args -> ApfloatMath.sinh(args.get(0)));
        addFunctionOrOperator("sqrt", 1, args -> ApfloatMath.sqrt(args.get(0)));
        addFunctionOrOperator("toRadians", 1, args -> ApfloatMath.toRadians(args.get(0)));
        addFunctionOrOperator("tan", 1, args -> ApfloatMath.tan(args.get(0)));
        addFunctionOrOperator("tanh", 1, args -> ApfloatMath.tanh(args.get(0)));
        addFunctionOrOperator("toDegrees", 1, args -> ApfloatMath.toDegrees(args.get(0)));
        addFunctionOrOperator("truncate", 1, args -> ApfloatMath.truncate(args.get(0)));
        addFunctionOrOperator("w", 1, args -> ApfloatMath.w(args.get(0)));
    }
}
