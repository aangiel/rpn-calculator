package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.ApfloatCalculatorContext;
import io.github.aangiel.rpn.context.BigDecimalCalculatorContext;
import io.github.aangiel.rpn.context.CalculatorContext;
import io.github.aangiel.rpn.context.DoubleCalculatorContext;
import io.github.aangiel.rpn.impl.CalculatorImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apfloat.Apfloat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CalculatorSupplier {

    private final static Logger logger = LogManager.getLogger(CalculatorSupplier.class);

    private final static Map<Class<? extends Number>, Calculator<? extends Number>> CALCULATORS = new HashMap<>();

    private CalculatorSupplier() {
        throw new AssertionError();
    }

    static {
        populateCalculators();
        logger.info(String.format("Available calculator types: %s", CALCULATORS.keySet()));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> Calculator<T> getCalculator(Class<T> clazz) {
        Calculator<T> calculator = (Calculator<T>) CALCULATORS.get(clazz);
        if (Objects.isNull(calculator)) {
            logger.error(String.format("Unsupported type: %s", clazz));
            throw new UnsupportedOperationException(String.format("Unsupported type: %s", clazz));
        }
        return calculator;
    }

    public static <T extends Number> void addCalculator(Class<T> clazz, CalculatorContext<T> contextImplementation) {
        CALCULATORS.put(clazz, new CalculatorImpl<>(contextImplementation));
        logger.debug(String.format("Calculator of type: %s added", clazz));
    }

    private static void populateCalculators() {
        addCalculator(Apfloat.class, new ApfloatCalculatorContext());
        addCalculator(BigDecimal.class, new BigDecimalCalculatorContext());
        addCalculator(Double.class, new DoubleCalculatorContext());
    }
}
