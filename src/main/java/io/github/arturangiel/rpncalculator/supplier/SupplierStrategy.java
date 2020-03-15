package io.github.arturangiel.rpncalculator.supplier;

import org.apfloat.Apfloat;

import java.util.HashMap;
import java.util.Map;

public class SupplierStrategy<T extends Number> {

    private Map<Class, DefaultOperationsSupplier> strategies;

    public SupplierStrategy() {
        strategies = new HashMap<>();
        populateStrategies();
    }

    private void populateStrategies() {
        strategies.put(Apfloat.class, new ApfloatDefaultOperationsSupplier());
    }

    public DefaultOperationsSupplier getSupplier(Class clazz) {
        return strategies.get(clazz);
    }
}
