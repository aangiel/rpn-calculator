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

        LOG.info(String.format("Calculating equation: %s", equation));

        if (equation.isBlank()) throw new IllegalArgumentException("Empty equation");

        var tokens = WHITESPACE.splitAsStream(equation).collect(LINKED_LIST_COLLECTOR);
        LOG.debug(String.format("Equation split with whitespace regex: %s", tokens));

        var stack = new ArrayDeque<T>(tokens.size());
        var position = 0;
        for (String token : tokens)
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

        LOG.debug(String.format("Processing item '%s' at position %s", token, position));
        if (NumberUtils.isCreatable(token)) {
            var applied = context.getNumberConstructor().apply(token);
            stack.push(applied);
            LOG.debug(String.format("Item '%s' pushed into the stack: %s", token, stack));
        } else {
            calculateOnStack(token, stack, position);
        }
    }

    private void calculateOnStack(String token, Deque<T> stack, int position) {

        assert token != null;
        assert stack != null;
        assert position > 0;

        LOG.debug(String.format("Processing operator/function '%s' at position %s", token, position));

        var functionOrOperator = context.getFunctionOrOperator(token)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Bad item: '%s' at position: %d", token, position)));

        if (stack.size() < functionOrOperator.getParametersCount())
            throw new ArithmeticException(String.format("Lack of arguments for: %s at position: %d", token, position));

        var arguments = popArgumentsForFunctionOrOperator(functionOrOperator, stack);
        LOG.debug(String.format("Took %s arguments (%s) from stack: %s", arguments.size(), arguments, stack));

        var value = functionOrOperator.get().apply(arguments);
        stack.push(value);
        LOG.debug(String.format("Pushed value %s into the stack: %s", value, stack));
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
