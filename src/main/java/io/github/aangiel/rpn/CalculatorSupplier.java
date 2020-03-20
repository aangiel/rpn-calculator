package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.ApfloatCalculatorContext;
import io.github.aangiel.rpn.context.BigDecimalCalculatorContext;
import io.github.aangiel.rpn.context.CalculatorContext;
import io.github.aangiel.rpn.context.DoubleCalculatorContext;
import io.github.aangiel.rpn.impl.CalculatorImpl;
import org.apfloat.Apfloat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class CalculatorSupplier {

    private final static Map<Class<? extends Number>, Calculator<? extends Number>> CALCULATORS = new HashMap<>();

    private CalculatorSupplier() {
        throw new AssertionError();
    }

    static {
        populateCalculators();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> Calculator<T> getCalculator(Class<T> clazz) {
        try {
            return (Calculator<T>) CALCULATORS.get(clazz);
        } catch (NullPointerException e) {
            throw new UnsupportedOperationException("Unsupported type");
        }
    }

    public static <T extends Number> void addCalculator(Class<T> clazz, CalculatorContext<T> contextImplementation) {
        CALCULATORS.put(clazz, new CalculatorImpl<>(contextImplementation));
    }

    private static void populateCalculators() {
        addCalculator(Apfloat.class, new ApfloatCalculatorContext());
        addCalculator(BigDecimal.class, new BigDecimalCalculatorContext());
        addCalculator(Double.class, new DoubleCalculatorContext());
    }
}
