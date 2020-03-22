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

public enum CalculatorSupplier {

    INSTANCE;

    private final Logger LOG = LogManager.getLogger(CalculatorSupplier.class);

    private final Map<Class<? extends Number>, Calculator<? extends Number>> CALCULATORS = new HashMap<>();

    CalculatorSupplier() {
        populateCalculators();
    }

    @SuppressWarnings("unchecked")
    public <T extends Number> Calculator<T> getCalculator(Class<T> clazz) {
        Calculator<T> calculator = (Calculator<T>) CALCULATORS.get(clazz);
        if (Objects.isNull(calculator)) {
            LOG.error(String.format("Unsupported type: %s", clazz));
            throw new UnsupportedOperationException(String.format("Unsupported type: %s", clazz));
        }
        return calculator;
    }

    public <T extends Number> void addCalculator(Class<T> clazz, CalculatorContext<T> contextImplementation) {
        CALCULATORS.put(clazz, new CalculatorImpl<>(contextImplementation));
        LOG.debug(String.format("Calculator of type: %s added", clazz));
    }

    private void populateCalculators() {
        addCalculator(Apfloat.class, new ApfloatCalculatorContext());
        addCalculator(BigDecimal.class, new BigDecimalCalculatorContext());
        addCalculator(Double.class, new DoubleCalculatorContext());
    }
}
