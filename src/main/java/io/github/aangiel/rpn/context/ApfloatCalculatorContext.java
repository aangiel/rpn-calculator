package io.github.aangiel.rpn.context;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.util.function.Function;

/**
 * @see CalculatorContext
 */
public final class ApfloatCalculatorContext extends CalculatorContext<Apfloat> {

    public ApfloatCalculatorContext(long precision) {
        super(precision);
    }

    public ApfloatCalculatorContext() {
        this(10L);
    }

    @Override
    public Function<String, Apfloat> getNumberConstructor() {
        return value -> new Apfloat(value, getPrecision());
    }

    @Override
    protected CalculatorContext<Apfloat> self() {
        return this;
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
    protected void populateMathFunctions() {

        MathHelper helper = new MathHelper();
        var defaultOneParameterMathFunctions = helper.getDefaultOneParameterMathFunctions(ApfloatMath.class, Apfloat.class);
        functions.putAll(defaultOneParameterMathFunctions);

        addFunctionOrOperator("agm", 2, args -> ApfloatMath.agm(args.get(0), args.get(1)));
        addFunctionOrOperator("atan2", 2, args -> ApfloatMath.atan2(args.get(0), args.get(1)));
        addFunctionOrOperator("copySign", 2, args -> ApfloatMath.copySign(args.get(0), args.get(1)));
        addFunctionOrOperator("fmod", 2, args -> ApfloatMath.fmod(args.get(0), args.get(1)));
        addFunctionOrOperator("max", 2, args -> ApfloatMath.max(args.get(0), args.get(1)));
        addFunctionOrOperator("min", 1, args -> ApfloatMath.min(args.get(0), args.get(1)));
        addFunctionOrOperator("multiplyAdd", 4, args -> ApfloatMath.multiplyAdd(args.get(0), args.get(1), args.get(2), args.get(3)));
        addFunctionOrOperator("multiplySubtract", 4, args -> ApfloatMath.multiplySubtract(args.get(0), args.get(1), args.get(2), args.get(3)));
        addFunctionOrOperator("pow", 2, args -> ApfloatMath.pow(args.get(0), args.get(1)));
    }
}
