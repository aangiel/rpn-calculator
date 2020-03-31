package io.github.aangiel.rpn.impl;

import io.github.aangiel.rpn.Calculator;
import io.github.aangiel.rpn.interfaces.CalculatorContext;
import io.github.aangiel.rpn.math.FunctionOrOperator;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static io.github.aangiel.rpn.ExceptionsUtil.npe;

/**
 * Implementation of interface {@link Calculator} for Reverse Polish Notation calculations.
 * This is the only one implementation.
 *
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 * @see Calculator
 */
public class CalculatorImpl<T extends Number> implements Calculator<T> {

    private static final Logger LOG = LogManager.getLogger(CalculatorImpl.class);

    private final CalculatorContext<T> context;

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Collector<String, ?, LinkedList<String>>
            LINKED_LIST_COLLECTOR = Collectors.toCollection(LinkedList::new);

    public CalculatorImpl(CalculatorContext<T> context) {
        this.context = context;
    }

    @Override
    public CalculatorContext<T> getContext() {
        return context;
    }

    @Override
    public T calculate(final String equation) {

        Objects.requireNonNull(equation, npe("equation"));
        if (equation.isBlank()) throw new IllegalArgumentException("Empty equation");

        LOG.info(String.format("Calculating equation: %s", equation));

        var stack = new ArrayDeque<T>();
        var position = 0;
        for (String token : WHITESPACE.splitAsStream(equation).collect(LINKED_LIST_COLLECTOR))
            calculateNextTokenAndPushItToStack(token, stack, ++position);

        var result = stack.pop();
        if (!stack.isEmpty())
            throw new IllegalArgumentException(String.format("Left on stack: %s", stack));

        LOG.info(String.format("Result = %s", result));
        return result;

    }

    private void calculateNextTokenAndPushItToStack(String token, Deque<T> stack, int position) {
        assert token != null;
        assert stack != null;
        assert position > 0;

        if (NumberUtils.isCreatable(token)) {
            var applied = context.getNumberConstructor().apply(token);
            stack.push(applied);
        } else {
            calculateOnStack(token, stack, position);
        }
    }

    private void calculateOnStack(String token, Deque<T> stack, int position) {

        assert token != null;
        assert stack != null;
        assert position > 0;

        var functionOrOperator = context.getFunctionOrOperator(token)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Bad item: '%s' at position: %d", token, position)));

        if (stack.size() < functionOrOperator.getParametersCount())
            throw new ArithmeticException(String.format("Lack of arguments for: %s at position: %d", token, position));

        var arguments = popArgumentsForFunctionOrOperator(functionOrOperator, stack);

        var value = functionOrOperator.get().apply(arguments);
        stack.push(value);
    }

    private List<T> popArgumentsForFunctionOrOperator(FunctionOrOperator<T> functionOrOperator, Deque<T> stack) {
        assert functionOrOperator != null;
        assert stack != null;

        var arguments = stack.stream()
                .limit(functionOrOperator.getParametersCount())
                .peek(e -> stack.pop())
                .collect(Collectors.toList());
        Collections.reverse(arguments);
        return arguments;
    }
}
