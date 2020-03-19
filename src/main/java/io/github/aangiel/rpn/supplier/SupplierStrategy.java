package io.github.aangiel.rpn.supplier;

import org.apfloat.Apfloat;

import java.math.BigDecimal;
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
        strategies.put(Double.class, new DoubleDefaultOperationsSupplier());
        strategies.put(BigDecimal.class, new BigDecimalDefaultOperationsSupplier());
    }

    public DefaultOperationsSupplier<T> getSupplier(Class<T> clazz) {
        return strategies.get(clazz);
    }
}
