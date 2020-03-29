package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.interfaces.RoundingModeContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;

import static io.github.aangiel.rpn.exception.CalculatorException.npe;

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
        this.roundingMode = Objects.requireNonNull(roundingMode, npe("roundingMode"));
    }

    @Override
    protected void populateDefaultOperations() {
        addFunctionOrOperator("+", 2, args -> args.get(0).add(args.get(1)));
        addFunctionOrOperator("-", 2, args -> args.get(0).subtract(args.get(1)));
        addFunctionOrOperator("*", 2, args -> args.get(0).multiply(args.get(1)));
        addFunctionOrOperator("/", 2, args -> args.get(0).divide(args.get(1), getRoundingMode()));
    }

    @Override
    protected void populateConstants() {
        addFunctionOrOperator("pi", 0, args -> BigDecimal.valueOf(Math.PI));
        addFunctionOrOperator("e", 0, args -> BigDecimal.valueOf(Math.E));
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
