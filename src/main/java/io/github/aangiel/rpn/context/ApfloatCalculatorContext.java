package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.math.IConstructor;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

/**
 * @see CalculatorContext
 */
public final class ApfloatCalculatorContext extends CalculatorContext<Apfloat> {

    public ApfloatCalculatorContext() {
        this(10);
    }

    public ApfloatCalculatorContext(int precision) {
        super(Apfloat.class, ApfloatMath.class, precision);
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
    public IConstructor<Apfloat> getValue() {
        return args -> new Apfloat(
                String.valueOf(args.get(0))
                , (long) args.get(1)
        );
    }

    @Override
    protected CalculatorContext<Apfloat> self() {
        return this;
    }
}
