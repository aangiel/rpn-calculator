package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.impl.ApfloatCalculatorContext;
import io.github.aangiel.rpn.context.impl.BigDecimalCalculatorContext;
import io.github.aangiel.rpn.context.impl.DoubleCalculatorContext;
import io.github.aangiel.rpn.context.interfaces.CalculatorContext;
import io.github.aangiel.rpn.impl.CalculatorImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apfloat.Apfloat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public enum CalculatorSupplier {

    INSTANCE(CalculatorImpl::new);

    private final Logger LOG = LogManager.getLogger(CalculatorSupplier.class);

    private final Map<Class<? extends Number>, Calculator<? extends Number>> CALCULATORS;

    private final Function<CalculatorContext<? extends Number>, Calculator<? extends Number>> implementation;

    CalculatorSupplier(Function<CalculatorContext<? extends Number>, Calculator<? extends Number>> implementation) {
        this.implementation = Objects.requireNonNull(implementation);
        CALCULATORS = new HashMap<>();
        populateCalculators();
    }

    /**
     * @param clazz type for which calculator should be returned
     * @param <T>   extends Number
     * @return {@link Calculator} of type given in 'clazz' parame
     * @throws NullPointerException     if param 'clazz' is null
     * @throws IllegalArgumentException if there are no calculator for type given in 'clazz' param
     */
    public <T extends Number> Calculator<T> getCalculator(Class<T> clazz) {
        Objects.requireNonNull(clazz);

        // It always works, because <T extends Number> and
        // value in CALCULATORS map is Calculator<? extends Number>
        @SuppressWarnings("unchecked")
        var calculator = (Calculator<T>) CALCULATORS.get(clazz);

        if (calculator == null)
            throw new IllegalArgumentException(String.format("Unsupported type: %s", clazz));

        return calculator;
    }

    /**
     * @param clazz                 type of calculator being added
     * @param contextImplementation instance of {@link CalculatorContext} of type corresponding to 'clazz' parameter
     * @param <T>                   extends Number
     * @throws NullPointerException if at least one parameter is null
     */
    public <T extends Number> void addCalculator(Class<T> clazz, CalculatorContext<T> contextImplementation) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(contextImplementation);

        CALCULATORS.put(clazz, implementation.apply(contextImplementation));
        LOG.debug(String.format("Calculator of type: %s added", clazz));
    }

    private void populateCalculators() {
        addCalculator(Apfloat.class, new ApfloatCalculatorContext());
        addCalculator(BigDecimal.class, new BigDecimalCalculatorContext());
        addCalculator(Double.class, new DoubleCalculatorContext());
    }
}
