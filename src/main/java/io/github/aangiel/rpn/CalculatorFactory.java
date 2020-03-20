package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.ApfloatCalculatorContext;
import io.github.aangiel.rpn.impl.CalculatorImpl;
import org.apfloat.Apfloat;

import java.util.HashMap;
import java.util.Map;

public final class CalculatorFactory<T extends Number> {

    private Map<Class, Calculator> calculators = new HashMap<>();

    public CalculatorFactory() {
        populateCalculators();
    }

    private void populateCalculators() {
        calculators.put(Apfloat.class, new CalculatorImpl(new ApfloatCalculatorContext()));
    }

    public Calculator<T> getCalculator(Class<T> clazz) {
        try {
            return calculators.get(clazz);
        } catch (NullPointerException e) {
            throw new UnsupportedOperationException("Unsupported type");
        }
    }
}
