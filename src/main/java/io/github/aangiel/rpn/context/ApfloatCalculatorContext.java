package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.math.ConstructorValue;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

public class ApfloatCalculatorContext extends CalculatorContext<Apfloat> {

    public ApfloatCalculatorContext() {
        this(10);
    }

    public ApfloatCalculatorContext(int precision) {
        super(Apfloat.class, ApfloatMath.class, precision);
    }

    @Override
    protected void populateDefaultOperations() {
        addFunction("+", 2, args -> args.get(0).add(args.get(1)));
        addFunction("-", 2, args -> args.get(0).subtract(args.get(1)));
        addFunction("*", 2, args -> args.get(0).multiply(args.get(1)));
        addFunction("/", 2, args -> args.get(0).divide(args.get(1)));
    }

    @Override
    protected void populateConstants() {
        addFunction("pi", 0, args -> ApfloatMath.pi(getPrecision()));
        addFunction("e", 0, args -> new Apfloat(2.718281828));
    }

    @Override
    public ConstructorValue<Apfloat> getValue() {
        return args -> new Apfloat(
                (String) args.get(0)
                , (long) args.get(1)
        );
    }
}
