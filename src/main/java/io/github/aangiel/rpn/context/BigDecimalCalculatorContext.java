package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.math.ConstructorValue;

import java.math.BigDecimal;

public class BigDecimalCalculatorContext extends CalculatorContext<BigDecimal> {

    public BigDecimalCalculatorContext() {
        super(BigDecimal.class, Math.class, 10);
    }

    @Override
    protected void populateDefaultOperations() {
        addFunction("+", 2, args -> args.get(0).add(args.get(1)));
        addFunction("-", 2, args -> args.get(0).subtract(args.get(1)));
        addFunction("*", 2, args -> args.get(0).multiply(args.get(1)));
        addFunction("/", 2, args -> args.get(0).divide(args.get(1), BigDecimal.ROUND_CEILING));
    }

    @Override
    protected void populateConstants() {
        addFunction("pi", 0, args -> BigDecimal.valueOf(3.14));
        addFunction("e", 0, args -> new BigDecimal("2.718281828"));
    }

    @Override
    public ConstructorValue<BigDecimal> getValue() {
        return args -> new BigDecimal(
                (String) args.get(0)
        );
    }
}
