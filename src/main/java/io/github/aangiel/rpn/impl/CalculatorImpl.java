package io.github.aangiel.rpn.impl;

import io.github.aangiel.rpn.Calculator;
import io.github.aangiel.rpn.CalculatorContext;
import io.github.aangiel.rpn.exception.*;
import io.github.aangiel.rpn.math.FunctionValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class for Reverse Polish Notation calculations
 */
public class CalculatorImpl<T extends Number> implements Calculator<T> {

    Logger logger = LogManager.getLogger(Calculator.class);

    private CalculatorContext<T> context;

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public CalculatorImpl(CalculatorContext<T> context) {
        this.context = context;
    }

    private CalculatorImpl() {
        throw new AssertionError();
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

        List<String> tokens = WHITESPACE.splitAsStream(equation).collect(Collectors.toCollection(LinkedList::new));
        logger.debug("Equation split with whitespace regex: {}", tokens);

        if (tokens.isEmpty()) throw new EmptyEquationException();

        Deque<T> stack = new ArrayDeque<>();

        ListIterator<String> tokenIterator = tokens.listIterator();
        while (tokenIterator.hasNext())
            calculateTokenAndPush(tokenIterator, stack);


        if (stack.size() != 1) throw new BadEquationException(stack);

        logger.info("Result = {}", stack.peek());
        return stack.pop();

    }

    private void calculateTokenAndPush(ListIterator<String> iterator, Deque<T> stack) throws CalculatorException {

        Constructor<T> constructor;
        String token = iterator.next();

        logger.debug("Processing item '{}' at position {}", token, (iterator.previousIndex() + 1));

        try {
            /*
             * Used with Apfloat
             */
            constructor = context.getClazz().getConstructor(String.class, long.class);
        } catch (NoSuchMethodException e) {
            try {
                /*
                 * Used with other classes extending Number (i.e. Double, BigDecimal)
                 */
                constructor = context.getClazz().getConstructor(String.class);
            } catch (NoSuchMethodException ex) {
                logger.error("No constructor");
                throw new UnexpectedException("No constructors (String, long) and (String)");
            }
        }

        try {
            if (constructor.getParameterCount() == 1)
                stack.push(constructor.newInstance(token));
            else
                stack.push(constructor.newInstance(token, context.getPrecision()));

            logger.debug("Item '{}' pushed into the stack: {}", token, stack);
        } catch (InvocationTargetException e) {
            if (NumberFormatException.class.equals(e.getTargetException().getClass()))
                calculateOnStack(iterator, stack);
            else
                throw new UnexpectedException("");
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UnexpectedException("");
        }
    }

    private void calculateOnStack(ListIterator<String> iterator, Deque<T> stack)
            throws CalculatorException {

        iterator.previous();
        String operator = iterator.next();
        int position = iterator.previousIndex();

        logger.debug("Processing operator/function '{}' at position {}", operator, (position + 1));

        try {
            FunctionValue<T> functionValue = context.getFunctions().get(operator);
            List<T> arguments = new ArrayList<>();
            IntStream.range(0, functionValue.getParametersCount())
                    .forEach(x -> arguments.add(stack.pop()));
            Collections.reverse(arguments);
            logger.debug("Took {} arguments ({}) from stack: {}", arguments.size(), arguments, stack);
            T applied = functionValue.getFunction().apply(arguments);
            stack.push(applied);
            logger.debug("Pushed value {} into the stack: {}", applied, stack);
        } catch (NullPointerException e) {
            logger.error("Operator/function '{}' at position {} unsupported", operator, (position + 1));
            throw new BadItemException(operator, position);
        } catch (NoSuchElementException e) {
            logger.error("Unexpected end of stack for operator '{}' at position {}", operator, (position + 1));
            throw new LackOfArgumentsException(operator, position);
        }
    }

    @Override
    public CalculatorContext<T> getContext() {
        return context;
    }
}
