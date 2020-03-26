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
        var mathFunctions = helper.getMathFunctions(ApfloatMath.class, Apfloat.class);
        functions.putAll(mathFunctions);
    }
}
