package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.interfaces.RoundingModeContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 * @see AbstractCalculatorContext
 */
public final class BigDecimalCalculatorContext extends AbstractCalculatorContext<BigDecimal> implements RoundingModeContext {

    private final RoundingMode roundingMode;

    public BigDecimalCalculatorContext() {
        this(RoundingMode.CEILING);
    }

    public BigDecimalCalculatorContext(RoundingMode roundingMode) {
        this.roundingMode = Objects.requireNonNull(roundingMode);
    }

    @Override
    protected void populateDefaultOperations() {
        addFunctionOrOperator("+", args -> args.remove(1).add(args.pop()));
        addFunctionOrOperator("-", args -> args.remove(1).subtract(args.pop()));
        addFunctionOrOperator("*", args -> args.remove(1).multiply(args.pop()));
        addFunctionOrOperator("/", args -> args.remove(1).divide(args.pop(), getRoundingMode()));
    }

    @Override
    protected void populateConstants() {
        addFunctionOrOperator("pi", args -> BigDecimal.valueOf(Math.PI));
        addFunctionOrOperator("e", args -> BigDecimal.valueOf(Math.E));
    }

    @Override
    protected void populateMathFunctions() {
    }

    @Override
    public Function<String, BigDecimal> getNumberConstructor() {
        return BigDecimal::new;
    }

    @Override
    public BigDecimalCalculatorContext self() {
        return this;
    }

    @Override
    public RoundingMode getRoundingMode() {
        return roundingMode;
    }
}
