package io.github.aangiel.rpn.context;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @see CalculatorContext
 */
public final class BigDecimalCalculatorContext extends CalculatorContext<BigDecimal> {

    public BigDecimalCalculatorContext() {
        super(BigDecimal.class);
    }

    @Override
    protected void populateDefaultOperations() {
        addFunctionOrOperator("+", 2, args -> args.get(0).add(args.get(1)));
        addFunctionOrOperator("-", 2, args -> args.get(0).subtract(args.get(1)));
        addFunctionOrOperator("*", 2, args -> args.get(0).multiply(args.get(1)));
        addFunctionOrOperator("/", 2, args -> args.get(0).divide(args.get(1), BigDecimal.ROUND_CEILING));
    }

    @Override
    protected void populateConstants() {
        addFunctionOrOperator("pi", 0, args -> BigDecimal.valueOf(Math.PI));
        addFunctionOrOperator("e", 0, args -> BigDecimal.valueOf(Math.E));
    }

    @Override
    public Function<String, BigDecimal> getConstructor() {
        return BigDecimal::new;
    }

    @Override
    protected CalculatorContext<BigDecimal> self() {
        return this;
    }
}
