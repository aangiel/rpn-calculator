package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.interfaces.CalculatorContext;
import io.github.aangiel.rpn.math.FunctionOrOperator;

import java.util.*;
import java.util.function.Function;

import static io.github.aangiel.rpn.ExceptionsUtil.npe;

/**
 * Base abstract class used by {@link io.github.aangiel.rpn.Calculator Calculator}.
 * This class should be extended if you want to add new type of {@link Number Numbers}
 * which you want to use with RPN Calculator
 *
 * @param <T> extends {@link Number Number}.
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public abstract class AbstractCalculatorContext<T extends Number> implements CalculatorContext<T> {

    protected final Map<String, FunctionOrOperator<T>> functions;

    protected AbstractCalculatorContext() {
        functions = new HashMap<>();
        populateFunctions();
    }

    private void populateFunctions() {
        populateDefaultOperations();
        populateMathFunctions();
        populateConstants();
    }

    /**
     * Should be implemented as series of
     * {@link #addFunctionOrOperator(String, int, Function) addFunctionOrOperator(String, int, IMathFunction)}
     * invokes with default mathematical operations (+, -, *, /)
     */
    protected abstract void populateDefaultOperations();

    /**
     * Should be implemented as series of
     * {@link #addFunctionOrOperator(String, int, Function) addFunctionOrOperator(String, int, IMathFunction)}
     * invokes with mathematical constants (e.g. PI or e)
     */
    protected abstract void populateConstants();

    /**
     *
     */
    protected abstract void populateMathFunctions();

    @Override
    public CalculatorContext<T> addFunctionOrOperator(String name, int parametersCount, Function<List<T>, T> function) {
        Objects.requireNonNull(name, npe("name"));
        Objects.requireNonNull(function, npe("function"));
        if (parametersCount < 0)
            throw new IllegalArgumentException("Param 'parametersCount' must be greater than or equal to zero");

        functions.put(name, new FunctionOrOperator<>(parametersCount, function));
        return self();
    }

    @Override
    public Set<String> getAvailableFunctionsAndOperators() {
        return Collections.unmodifiableSet(functions.keySet());
    }


    @Override
    public Optional<FunctionOrOperator<T>> getFunctionOrOperator(String name) {
        return Optional.ofNullable(functions.get(name));
    }

}
