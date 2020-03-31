package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.interfaces.PrecisionContext;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.util.function.Function;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 * @see AbstractCalculatorContext
 */
public final class ApfloatCalculatorContext extends AbstractCalculatorContext<Apfloat> implements PrecisionContext {

    private final long precision;

    public ApfloatCalculatorContext(long precision) {
        this.precision = precision;
    }

    public ApfloatCalculatorContext() {
        this(10L);
    }

    @Override
    public Function<String, Apfloat> getNumberConstructor() {
        return value -> new Apfloat(value, getPrecision());
    }

    @Override
    public ApfloatCalculatorContext self() {
        return this;
    }

    @Override
    protected void populateDefaultOperations() {
        addFunctionOrOperator("+", args -> args.remove(1).add(args.pop()));
        addFunctionOrOperator("-", args -> args.remove(1).subtract(args.pop()));
        addFunctionOrOperator("*", args -> args.remove(1).multiply(args.pop()));
        addFunctionOrOperator("/", args -> args.remove(1).divide(args.pop()));
    }

    @Override
    protected void populateConstants() {
        addFunctionOrOperator("pi", args -> ApfloatMath.pi(getPrecision()));
        addFunctionOrOperator("e", args -> new Apfloat(Math.E));
    }

    @Override
    protected void populateMathFunctions() {
        addFunctionOrOperator("abs", args -> ApfloatMath.abs(args.pop()));
        addFunctionOrOperator("acos", args -> ApfloatMath.acos(args.pop()));
        addFunctionOrOperator("acosh", args -> ApfloatMath.acosh(args.pop()));
        addFunctionOrOperator("agm", args -> ApfloatMath.agm(args.remove(1), args.pop()));
        addFunctionOrOperator("asin", args -> ApfloatMath.asin(args.pop()));
        addFunctionOrOperator("asinh", args -> ApfloatMath.asinh(args.pop()));
        addFunctionOrOperator("atan", args -> ApfloatMath.atan(args.pop()));
        addFunctionOrOperator("atan2", args -> ApfloatMath.atan2(args.remove(1), args.pop()));
        addFunctionOrOperator("atanh", args -> ApfloatMath.atanh(args.pop()));
        addFunctionOrOperator("cbrt", args -> ApfloatMath.cbrt(args.pop()));
        addFunctionOrOperator("copySign", args -> ApfloatMath.copySign(args.remove(1), args.pop()));
        addFunctionOrOperator("cos", args -> ApfloatMath.cos(args.pop()));
        addFunctionOrOperator("cosh", args -> ApfloatMath.cosh(args.pop()));
        addFunctionOrOperator("exp", args -> ApfloatMath.exp(args.pop()));
        addFunctionOrOperator("fmod", args -> ApfloatMath.fmod(args.remove(1), args.pop()));
        addFunctionOrOperator("frac", args -> ApfloatMath.frac(args.pop()));
        addFunctionOrOperator("gamma", args -> ApfloatMath.gamma(args.pop()));
        addFunctionOrOperator("log", args -> ApfloatMath.log(args.pop()));
        addFunctionOrOperator("logWithBase", args -> ApfloatMath.log(args.remove(1), args.pop()));
        addFunctionOrOperator("max", args -> ApfloatMath.max(args.remove(1), args.pop()));
        addFunctionOrOperator("min", args -> ApfloatMath.min(args.remove(1), args.pop()));
        addFunctionOrOperator("multiplyAdd", args -> ApfloatMath.multiplyAdd(args.remove(3), args.remove(2), args.remove(1), args.pop()));
        addFunctionOrOperator("multiplySubtract", args -> ApfloatMath.multiplySubtract(args.remove(3), args.remove(2), args.remove(1), args.pop()));
        addFunctionOrOperator("negate", args -> ApfloatMath.negate(args.pop()));
        addFunctionOrOperator("pow", args -> ApfloatMath.pow(args.remove(1), args.pop()));
        addFunctionOrOperator("sin", args -> ApfloatMath.sin(args.pop()));
        addFunctionOrOperator("sinh", args -> ApfloatMath.sinh(args.pop()));
        addFunctionOrOperator("sqrt", args -> ApfloatMath.sqrt(args.pop()));
        addFunctionOrOperator("tan", args -> ApfloatMath.tan(args.pop()));
        addFunctionOrOperator("tanh", args -> ApfloatMath.tanh(args.pop()));
        addFunctionOrOperator("toDegrees", args -> ApfloatMath.toDegrees(args.pop()));
        addFunctionOrOperator("toRadians", args -> ApfloatMath.toRadians(args.pop()));
        addFunctionOrOperator("w", args -> ApfloatMath.w(args.pop()));
    }

    @Override
    public long getPrecision() {
        return precision;
    }
}
