package io.github.aangiel.rpn.context.impl;

import io.github.aangiel.rpn.context.interfaces.CalculatorContext;

import java.util.*;
import java.util.function.Function;

/**
 * Base abstract class used by {@link io.github.aangiel.rpn.Calculator Calculator}.
 * This class should be extended if you want to add new type of {@link Number Numbers}
 * which you want to use with RPN Calculator
 *
 * @param <T> extends {@link Number Number}.
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public abstract class AbstractCalculatorContext<T extends Number> implements CalculatorContext<T> {

    private final Map<String, Function<LinkedList<T>, T>> functions;

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
     * {@link #addFunctionOrOperator(String, Function) addFunctionOrOperator(String, int, IMathFunction)}
     * invokes with default mathematical operations (+, -, *, /)
     */
    protected abstract void populateDefaultOperations();

    /**
     * Should be implemented as series of
     * {@link #addFunctionOrOperator(String, Function) addFunctionOrOperator(String, int, IMathFunction)}
     * invokes with mathematical constants (e.g. PI or e)
     */
    protected abstract void populateConstants();

    /**
     *
     */
    protected abstract void populateMathFunctions();

    /**
     * @param name     Name of the function or operator (e.g. "*" for multiplying or "sin" for sinus)
     * @param function Lambda which will be used during parsing equation (e.g. args -&#62; args.get(0) + args.get(1)<br>
     *                 args is defined as {@link Function &#60;T extends Number&#62; T apply(List&#60;T&#62; args)}
     * @return this
     * @throws NullPointerException if one of parameters is null
     * @throws RuntimeException     like {@link Map#put(Object, Object)}
     */
    @Override
    public CalculatorContext<T> addFunctionOrOperator(String name, Function<LinkedList<T>, T> function) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(function);

        functions.put(name, function);
        return self();
    }

    @Override
    public Set<String> getAvailableFunctionsAndOperators() {
        return Collections.unmodifiableSet(functions.keySet());
    }


    @Override
    public Optional<Function<LinkedList<T>, T>> getFunctionOrOperator(String name) {
        Objects.requireNonNull(name);
        return Optional.ofNullable(functions.get(name));
    }

}
