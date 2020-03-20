package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.ApfloatCalculatorContext;
import io.github.aangiel.rpn.context.BigDecimalCalculatorContext;
import io.github.aangiel.rpn.context.DoubleCalculatorContext;
import io.github.aangiel.rpn.impl.CalculatorImpl;
import org.apfloat.Apfloat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CalculatorSupplier {

    private static Map<Class<? extends Number>, Calculator<? extends Number>> CALCULATORS;

    private CalculatorSupplier() {
        throw new AssertionError();
    }

    public static <T extends Number> Calculator<T> getCalculator(Class<T> clazz) {
        if (Objects.isNull(CALCULATORS)) populateCalculators();

        try {
            return (Calculator<T>) CALCULATORS.get(clazz);
        } catch (NullPointerException e) {
            throw new UnsupportedOperationException("Unsupported type");
        }
    }

    private static void populateCalculators() {
        CALCULATORS = new HashMap<>();

        CALCULATORS.put(Apfloat.class, new CalculatorImpl<>(new ApfloatCalculatorContext()));
        CALCULATORS.put(BigDecimal.class, new CalculatorImpl<>(new BigDecimalCalculatorContext()));
        CALCULATORS.put(Double.class, new CalculatorImpl<>(new DoubleCalculatorContext()));
    }
}
