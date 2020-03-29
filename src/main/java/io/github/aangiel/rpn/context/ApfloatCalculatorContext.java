package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.interfaces.PrecisionContext;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.util.function.Function;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 * @see AbstractCalculatorContext
 */
public final class ApfloatCalculatorContext extends AbstractCalculatorContext<Apfloat> implements PrecisionContext {

    private final long precision;

    public ApfloatCalculatorContext(long precision) {
        this.precision = precision;
    }

    public ApfloatCalculatorContext() {
        this(10L);
    }

    @Override
    public Function<String, Apfloat> getNumberConstructor() {
        return value -> new Apfloat(value, getPrecision());
    }

    @Override
    public ApfloatCalculatorContext self() {
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
        populateDefaultMathFunctions(ApfloatMath.class, Apfloat.class, 4);
        addFunctionOrOperator("log", 1, args -> ApfloatMath.log(args.get(0)));
        addFunctionOrOperator("logWithBase", 2, args -> ApfloatMath.log(args.get(0), args.get(1)));
    }

    @Override
    public long getPrecision() {
        return precision;
    }
}
