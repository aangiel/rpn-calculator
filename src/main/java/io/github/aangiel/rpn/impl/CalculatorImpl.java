package io.github.aangiel.rpn.impl;

import io.github.aangiel.rpn.Calculator;
import io.github.aangiel.rpn.context.CalculatorContext;
import io.github.aangiel.rpn.exception.*;
import io.github.aangiel.rpn.math.ConstructorValue;
import io.github.aangiel.rpn.math.FunctionValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class for Reverse Polish Notation calculations
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

    /**
     * Main method for Reverse Polish Notation Calculator
     *
     * @param equation String with equation to calculate
     * @return result of calculation
     * @throws CalculatorException abstract Exception (@link #BadEquationException), #BadItemException, #CalculatorArithmeticException, #LackOfArgumentsException, #UnexpectedException
     */
    @Override
    public T calculate(String equation) throws CalculatorException {

        logger.info("Calculating equation: {}", equation);

        List<String> tokens = WHITESPACE.splitAsStream(equation).collect(LINKED_LIST_COLLECTOR);
        logger.debug("Equation split with whitespace regex: {}", tokens);

        if (tokens.isEmpty()) throw new EmptyEquationException();

        Deque<T> stack = new ArrayDeque<>(tokens.size());
        ListIterator<String> iterator = tokens.listIterator();

        while (iterator.hasNext()) calculateTokenAndPush(iterator, stack);

        T result = stack.pop();
        if (!stack.isEmpty()) throw new BadEquationException(stack);

        logger.info("Result = {}", result);
        return result;

    }

    private void calculateTokenAndPush(ListIterator<String> iterator, Deque<T> stack) throws CalculatorException {

        String token = iterator.next();

        logger.debug("Processing item '{}' at position {}", token, iterator.nextIndex());
        ConstructorValue<T> value = context.getValue();
        try {
            T applied = value.apply(Arrays.asList(token, context.getPrecision()));
            stack.push(applied);
            logger.debug("Item '{}' pushed into the stack: {}", token, stack);
        } catch (NumberFormatException e) {
            calculateOnStack(iterator, stack);
        }
    }

    private void calculateOnStack(ListIterator<String> iterator, Deque<T> stack)
            throws CalculatorException {

        iterator.previous();
        String operator = iterator.next();

        logger.debug("Processing operator/function '{}' at position {}", operator, iterator.nextIndex());

        try {
            FunctionValue<T> functionValue = context.getFunction(operator);
            List<T> arguments = new ArrayList<>();
            IntStream.range(0, functionValue.getParametersCount())
                    .forEach(x -> arguments.add(stack.pop()));
            Collections.reverse(arguments);
            logger.debug("Took {} arguments ({}) from stack: {}", arguments.size(), arguments, stack);
            T applied = functionValue.getFunction().apply(arguments);
            stack.push(applied);
            logger.debug("Pushed value {} into the stack: {}", applied, stack);
        } catch (NullPointerException e) {
            logger.error("Operator/function '{}' at position {} unsupported", operator, iterator.nextIndex());
            throw new BadItemException(operator, iterator.nextIndex());
        } catch (NoSuchElementException e) {
            logger.error("Unexpected end of stack for operator '{}' at position {}", operator, iterator.nextIndex());
            throw new LackOfArgumentsException(operator, iterator.nextIndex());
        }
    }

    @Override
    public CalculatorContext<T> getContext() {
        return context;
    }
}
