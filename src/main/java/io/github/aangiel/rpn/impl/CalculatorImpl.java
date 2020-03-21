package io.github.aangiel.rpn.impl;

import io.github.aangiel.rpn.Calculator;
import io.github.aangiel.rpn.context.CalculatorContext;
import io.github.aangiel.rpn.exception.*;
import io.github.aangiel.rpn.math.Function;
import io.github.aangiel.rpn.math.IConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class for Reverse Polish Notation calculations
 *
 * @see Calculator
 */
public class CalculatorImpl<T extends Number> implements Calculator<T> {

    Logger logger = LogManager.getLogger(CalculatorImpl.class);

    private CalculatorContext<T> context;

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Collector<String, ?, LinkedList<String>>
            LINKED_LIST_COLLECTOR = Collectors.toCollection(LinkedList::new);

    public CalculatorImpl(CalculatorContext<T> context) {
        this.context = context;
    }

    @Override
    public T calculate(String equation) throws CalculatorException {

        logger.info(String.format("Calculating equation: %s", equation));

        List<String> tokens = WHITESPACE.splitAsStream(equation).collect(LINKED_LIST_COLLECTOR);
        logger.debug(String.format("Equation split with whitespace regex: %s", tokens));

        if (tokens.isEmpty()) throw new EmptyEquationException();

        Deque<T> stack = new ArrayDeque<>(tokens.size());
        ListIterator<String> iterator = tokens.listIterator();

        while (iterator.hasNext()) calculateTokenAndPush(iterator, stack);

        T result = stack.pop();
        if (!stack.isEmpty()) throw new BadEquationException(stack);

        logger.info(String.format("Result = %s", result));
        return result;

    }

    private void calculateTokenAndPush(ListIterator<String> iterator, Deque<T> stack) throws CalculatorException {

        String token = iterator.next();

        logger.debug(String.format("Processing item '%s' at position %s", token, iterator.nextIndex()));
        IConstructor<T> value = context.getValue();
        try {
            T applied = value.apply(Arrays.asList(token, context.getPrecision()));
            stack.push(applied);
            logger.debug(String.format("Item '%s' pushed into the stack: %s", token, stack));
        } catch (NumberFormatException e) {
            calculateOnStack(iterator, stack);
        }
    }

    private void calculateOnStack(ListIterator<String> iterator, Deque<T> stack)
            throws CalculatorException {

        iterator.previous();
        String operator = iterator.next();

        logger.debug(String.format("Processing operator/function '%s' at position %s", operator, iterator.nextIndex()));

        try {
            Function<T> function = context.getFunctionOrOperator(operator);
            List<T> arguments = new ArrayList<>();
            IntStream.range(0, function.getParametersCount())
                    .forEach(x -> arguments.add(stack.pop()));
            Collections.reverse(arguments);
            logger.debug(String.format("Took %s arguments (%s) from stack: %s", arguments.size(), arguments, stack));
            T applied = function.get().apply(arguments);
            stack.push(applied);
            logger.debug(String.format("Pushed value %s into the stack: %s", applied, stack));
        } catch (NullPointerException e) {
            logger.error(String.format("Operator/function '%s' at position %s unsupported", operator, iterator.nextIndex()));
            throw new BadItemException(operator, iterator.nextIndex());
        } catch (NoSuchElementException e) {
            logger.error(String.format("Unexpected end of stack for operator '%s' at position %s", operator, iterator.nextIndex()));
            throw new LackOfArgumentsException(operator, iterator.nextIndex());
        }
    }

    @Override
    public CalculatorContext<T> getContext() {
        return context;
    }
}
