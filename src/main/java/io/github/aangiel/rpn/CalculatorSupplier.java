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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.aangiel.rpn.exception.CalculatorException.npe;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public enum CalculatorSupplier {

    INSTANCE(CalculatorImpl::new);

    private final Logger LOG = LogManager.getLogger(CalculatorSupplier.class);

    private final Map<Class<? extends Number>, Calculator<? extends Number>> CALCULATORS;

    private final Function<CalculatorContext<? extends Number>, Calculator<? extends Number>> implementation;

    CalculatorSupplier(Function<CalculatorContext<? extends Number>, Calculator<? extends Number>> implementation) {
        this.implementation = Objects.requireNonNull(implementation, npe("implementation"));
        CALCULATORS = new HashMap<>();
        populateCalculators();
    }

    public <T extends Number> Calculator<T> getCalculator(Class<T> clazz) {
        Objects.requireNonNull(clazz, npe("clazz"));

        // It always works, because <T extends Number> and
        // value in CALCULATORS map is Calculator<? extends Number>
        @SuppressWarnings("unchecked")
        var calculator = (Calculator<T>) CALCULATORS.get(clazz);

        return Optional
                .ofNullable(calculator)
                .orElseThrow(iae(clazz));
    }

    public <T extends Number> void addCalculator(Class<T> clazz, CalculatorContext<T> contextImplementation) {
        Objects.requireNonNull(clazz, npe("clazz"));
        Objects.requireNonNull(contextImplementation, npe("contextImplementation"));

        CALCULATORS.put(clazz, implementation.apply(contextImplementation));
        LOG.debug(String.format("Calculator of type: %s added", clazz));
    }

    private void populateCalculators() {
        addCalculator(Apfloat.class, new ApfloatCalculatorContext());
        addCalculator(BigDecimal.class, new BigDecimalCalculatorContext());
        addCalculator(Double.class, new DoubleCalculatorContext());
    }

    private <T extends Number> Supplier<IllegalArgumentException> iae(Class<T> clazz) {
        assert clazz != null;
        return () -> new IllegalArgumentException(String.format("Unsupported type: %s", clazz));
    }
}
