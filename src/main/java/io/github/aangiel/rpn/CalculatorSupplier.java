package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.ApfloatCalculatorContext;
import io.github.aangiel.rpn.context.BigDecimalCalculatorContext;
import io.github.aangiel.rpn.context.DoubleCalculatorContext;
import io.github.aangiel.rpn.impl.CalculatorImpl;
import io.github.aangiel.rpn.interfaces.CalculatorContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apfloat.Apfloat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public enum CalculatorSupplier {

    INSTANCE(CalculatorImpl::new);

    private final Logger LOG = LogManager.getLogger(CalculatorSupplier.class);

    private final Map<Class<? extends Number>, Calculator<? extends Number>> CALCULATORS = new HashMap<>();

    private final Function<CalculatorContext<? extends Number>, Calculator<? extends Number>> implementation;

    CalculatorSupplier(Function<CalculatorContext<? extends Number>, Calculator<? extends Number>> implementation) {
        this.implementation = implementation;
        populateCalculators();
    }

    public <T extends Number> Calculator<T> getCalculator(Class<T> clazz) {

        // It always works, because <T extends Number> and
        // value in CALCULATORS map is Calculator<? extends Number>
        @SuppressWarnings("unchecked")
        var calculator = (Calculator<T>) CALCULATORS.get(clazz);

        return Optional
                .ofNullable(calculator)
                .orElseThrow(uoe(clazz));
    }

    public <T extends Number> void addCalculator(Class<T> clazz, CalculatorContext<T> contextImplementation) {
        CALCULATORS.put(clazz, implementation.apply(contextImplementation));
        LOG.debug(String.format("Calculator of type: %s added", clazz));
    }

    private void populateCalculators() {
        addCalculator(Apfloat.class, new ApfloatCalculatorContext());
        addCalculator(BigDecimal.class, new BigDecimalCalculatorContext());
        addCalculator(Double.class, new DoubleCalculatorContext());
    }

    private <T extends Number> Supplier<UnsupportedOperationException> uoe(Class<T> clazz) {
        return () -> new UnsupportedOperationException(String.format("Unsupported type: %s", clazz));
    }
}
