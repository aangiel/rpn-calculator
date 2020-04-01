package io.github.aangiel.rpn.impl;

import io.github.aangiel.rpn.Calculator;
import io.github.aangiel.rpn.context.interfaces.CalculatorContext;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

        Objects.requireNonNull(equation);
        if (equation.isBlank()) throw new IllegalArgumentException("Empty equation");

        LOG.info(String.format("Calculating equation: %s", equation));

        var stack = new LinkedList<T>();
        var position = 0;
        for (String token : WHITESPACE.splitAsStream(equation).collect(LINKED_LIST_COLLECTOR))
            calculateNextTokenAndPushItToStack(Token.of(token, stack, ++position));

        var result = stack.pop();
        if (!stack.isEmpty()) throw new IllegalArgumentException(String.format("Left on stack: %s", stack));

        LOG.info(String.format("Result = %s", result));
        return result;

    }

    private void calculateNextTokenAndPushItToStack(Token<T> token) {
        assert token != null;

        if (NumberUtils.isCreatable(token.getToken()))
            token.getStack().push(createNumber(token));
        else
            token.getStack().push(calculateValue(token));
    }

    private T createNumber(Token<T> token) {
        assert token != null;
        return context.getNumberConstructor().apply(token.getToken());
    }

    private T calculateValue(Token<T> token) {
        assert token != null;
        try {
            return getFunctionOrOperator(token).apply(token.getStack());
        } catch (IndexOutOfBoundsException e) {
            throw new ArithmeticException(String.format("Lack of arguments for: %s at position: %d", token, token.getPosition()));
        }
    }

    private Function<LinkedList<T>, T> getFunctionOrOperator(Token<T> token) {
        assert token != null;
        return context.getFunctionOrOperator(token.getToken())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Bad item: '%s' at position: %d", token, token.getPosition())));
    }

    private static class Token<T extends Number> {
        private final String token;
        private final LinkedList<T> stack;
        private final int position;

        private Token(String token, LinkedList<T> stack, int position) {
            this.token = Objects.requireNonNull(token);
            this.stack = Objects.requireNonNull(stack);
            this.position = position;
        }

        public static <T extends Number> Token<T> of(String token, LinkedList<T> stack, int position) {
            return new Token<>(token, stack, position);
        }

        public String getToken() {
            return token;
        }

        public LinkedList<T> getStack() {
            return stack;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return token;
        }
    }
}
