package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.impl.ApfloatCalculatorContext;
import io.github.aangiel.rpn.context.impl.BigDecimalCalculatorContext;
import io.github.aangiel.rpn.context.impl.DoubleCalculatorContext;
import io.github.aangiel.rpn.context.interfaces.CalculatorContext;
import io.github.aangiel.rpn.impl.CalculatorImpl;
import io.github.aangiel.rpn.translation.Languages;
import io.github.aangiel.rpn.translation.Messages;
import io.github.aangiel.rpn.translation.interfaces.Language;
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

    private final Map<Class<? extends Number>, Calculator<? extends Number>> calculators;

    private final Function<CalculatorContext<? extends Number>, Calculator<? extends Number>> implementation;

    private Language language;

    CalculatorSupplier(Function<CalculatorContext<? extends Number>, Calculator<? extends Number>> implementation) {
        this.implementation = Objects.requireNonNull(implementation);
        calculators = new HashMap<>();
        language = Languages.EN;
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
        var calculator = (Calculator<T>) calculators.get(clazz);

        if (calculator == null)
            throw new IllegalArgumentException(Messages.UNSUPPORTED_TYPE.get(clazz));

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

        calculators.put(clazz, implementation.apply(contextImplementation));
    }

    private void populateCalculators() {
        addCalculator(Apfloat.class, new ApfloatCalculatorContext());
        addCalculator(BigDecimal.class, new BigDecimalCalculatorContext());
        addCalculator(Double.class, new DoubleCalculatorContext());
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = Objects.requireNonNull(language);
    }
}
