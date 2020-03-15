package io.github.arturangiel.rpncalculator.supplier;

import org.apfloat.Apfloat;

import java.util.HashMap;
import java.util.Map;

public class SupplierStrategy<T extends Number> {

    private Map<Class, DefaultOperationsSupplier<T>> strategies;

    public SupplierStrategy() {
        strategies = new HashMap<>();
        populateStrategies();
    }

    private void populateStrategies() {
        strategies.put(Apfloat.class, new ApfloatDefaultOperationsSupplier());
    }

    public DefaultOperationsSupplier<T> getSupplier(Class<T> clazz) {
        return strategies.get(clazz);
    }
}
