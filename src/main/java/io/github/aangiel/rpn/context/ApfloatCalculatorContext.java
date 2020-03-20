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
        addFunction("+", 2, a -> a.get(0).add(a.get(1)));
        addFunction("-", 2, a -> a.get(0).subtract(a.get(1)));
        addFunction("*", 2, a -> a.get(0).multiply(a.get(1)));
        addFunction("/", 2, a -> a.get(0).divide(a.get(1)));
    }

    @Override
    protected void populateConstants() {
        addFunction("pi", 0, (a) -> ApfloatMath.pi(getPrecision()));
        addFunction("e", 0, (a) -> new Apfloat(2.718281828));
    }

    @Override
    public ConstructorValue<Apfloat> getValue() {
        return a -> new Apfloat((String) a.get(0), (long) a.get(1));
    }
}
