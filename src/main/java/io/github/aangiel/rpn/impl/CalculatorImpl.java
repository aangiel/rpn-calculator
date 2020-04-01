package io.github.aangiel.rpn.impl;

import io.github.aangiel.rpn.Calculator;
import io.github.aangiel.rpn.context.interfaces.CalculatorContext;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        return new Calculator<>(context, equation).calculate();
    }

    private static class Calculator<T extends Number> {

        private final CalculatorContext<T> context;
        private final String equation;
        private final LinkedList<T> stack;
        private int currentPosition;

        public Calculator(CalculatorContext<T> context, String equation) {
            this.context = Objects.requireNonNull(context);
            this.equation = Objects.requireNonNull(equation);
            this.stack = new LinkedList<>();
            this.currentPosition = 1;
        }

        private T calculate() {
            checkEquation();
            processEquation();
            return getResult();
        }

        private void checkEquation() {
            if (equation.isBlank())
                throw new IllegalArgumentException("Empty equation");
        }

        private void processEquation() {
            for (String token : getTokens()) {
                calculateNextTokenAndPushItToStack(token);
                currentPosition++;
            }
        }

        private T getResult() {
            var result = stack.pop();
            if (stack.isEmpty())
                return result;
            else
                throw new IllegalArgumentException(String.format("Left on stack: %s", stack));
        }

        private void calculateNextTokenAndPushItToStack(String token) {
            assert token != null;
            if (NumberUtils.isCreatable(token))
                stack.push(createNumber(token));
            else
                stack.push(calculateValue(token));
        }

        private LinkedList<String> getTokens() {
            return WHITESPACE.splitAsStream(equation).collect(LINKED_LIST_COLLECTOR);
        }

        private T createNumber(String token) {
            assert token != null;
            return context.getNumberConstructor().apply(token);
        }

        private T calculateValue(String token) {
            assert token != null;
            try {
                return getFunctionOrOperator(token).apply(stack);
            } catch (IndexOutOfBoundsException e) {
                throw new ArithmeticException(String.format("Lack of arguments for: %s at position: %d", token, currentPosition));
            }
        }

        @NotNull
        @Contract("null -> fail")
        private Function<LinkedList<T>, T> getFunctionOrOperator(String token) {
            assert token != null;
            var result = context.getFunctionOrOperator(token);
            if (result.isPresent())
                return result.get();
            else
                throw new IllegalArgumentException(String.format("Bad item: '%s' at position: %d", token, currentPosition));
        }
    }
}
