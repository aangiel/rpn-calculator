package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.math.ConstructorValue;

import java.math.BigDecimal;

public class BigDecimalCalculatorContext extends CalculatorContext<BigDecimal> {

    public BigDecimalCalculatorContext() {
        super(BigDecimal.class, Math.class, 10);
    }

    @Override
    protected void populateDefaultOperations() {
        addFunction("+", 2, a -> a.get(0).add(a.get(1)));
        addFunction("-", 2, a -> a.get(0).subtract(a.get(1)));
        addFunction("*", 2, a -> a.get(0).multiply(a.get(1)));
        addFunction("/", 2, a -> a.get(0).divide(a.get(1), BigDecimal.ROUND_CEILING));
    }

    @Override
    protected void populateConstants() {
        addFunction("pi", 0, (a) -> BigDecimal.valueOf(3.14));
        addFunction("e", 0, (a) -> new BigDecimal("2.718281828"));
    }

    @Override
    public ConstructorValue<BigDecimal> getValue() {
        return a -> new BigDecimal((String) a.get(0));
    }
}
