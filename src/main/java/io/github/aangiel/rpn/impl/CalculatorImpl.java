package io.github.aangiel.rpn.impl;

import io.github.aangiel.rpn.Calculator;
import io.github.aangiel.rpn.context.interfaces.CalculatorContext;
import io.github.aangiel.rpn.translation.MessageTranslator;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

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

    public CalculatorImpl(CalculatorContext<T> context) {
        this.context = context;
    }

    @Override
    public CalculatorContext<T> getContext() {
        return context;
    }

    @Override
    public T calculate(final String equation) {
        return new Calculator<>(context, equation).calculateEquation();
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

        private T calculateEquation() {
            checkEquation();
            processEquation();
            return getResult();
        }

        private void checkEquation() {
            if (equation.isBlank())
                throw new IllegalArgumentException(MessageTranslator.EMPTY_EQUATION.get());
        }

        private void processEquation() {
            for (String token : WHITESPACE.split(equation)) {
                processNext(token);
                currentPosition++;
            }
        }

        private T getResult() {
            var result = stack.pop();
            if (stack.isEmpty())
                return result;
            else
                throw new IllegalArgumentException(MessageTranslator.LEFT_ON_STACK.get(stack));
        }

        private void processNext(String token) {
            assert token != null;
            if (NumberUtils.isCreatable(token))
                stack.push(createNumber(token));
            else
                stack.push(calculateValue(token));
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
                throw new ArithmeticException(MessageTranslator.LACK_OF_ARGUMENTS.get(token, currentPosition));
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
                throw new IllegalArgumentException(MessageTranslator.BAD_ITEM.get(token, currentPosition));
        }
    }
}
