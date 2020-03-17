package io.github.aangiel.rpn.impl;

import io.github.aangiel.rpn.Calculator;
import io.github.aangiel.rpn.CalculatorContext;
import io.github.aangiel.rpn.exception.*;
import io.github.aangiel.rpn.math.FunctionValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * Class for Reverse Polish Notation calculations
 */
public class CalculatorImpl<T extends Number> implements Calculator<T> {

    Logger logger = LogManager.getLogger(Calculator.class);

    private CalculatorContext<T> context;

    public CalculatorImpl(CalculatorContext<T> context) {
        this.context = context;
    }

    public CalculatorImpl() {
//        context = new CalculatorContext<>();
//        this.context = context.getDefaultContext();
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

        String[] equationSplit = equation.trim().split("\\s+");
        logger.debug("Equation split with whitespace regex: {}", Arrays.toString(equationSplit));

        Deque<T> stack = new ArrayDeque<>();

        for (int i = 0; i < equationSplit.length; i++) {

            String item = equationSplit[i];
            logger.debug("Processing item '{}' at position {}", item, (i + 1));

            calculateItemAndPush(item, stack, i);
        }

        if (stack.size() == 1) {
            logger.info("Result = {}", stack.peek());
            return stack.pop();
        } else {
            logger.error("Unexpectedly left on stack: {}", stack);
            throw new BadEquationException((Deque<Number>) stack);
        }
    }

    private void calculateItemAndPush(String item, Deque<T> stack, int position) throws CalculatorException {
        Constructor<T> constructor;

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
                stack.push(constructor.newInstance(item));
            else
                stack.push(constructor.newInstance(item, context.getPrecision()));

            logger.debug("Item '{}' pushed into the stack: {}", item, stack);
        } catch (InvocationTargetException e) {
            if (NumberFormatException.class.equals(e.getTargetException().getClass()))
                calculateOnStack(item, stack, position);
            else
                throw new UnexpectedException("");
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UnexpectedException("");
        }
    }

    private void calculateOnStack(String operator, Deque<T> stack, int position)
            throws CalculatorException {

        logger.debug("Processing operator/function '{}' at position {}", operator, (position + 1));

        try {
            FunctionValue<T> functionValue = context.getFunctions().get(operator);
            @SuppressWarnings("unchecked")
            T[] arguments = (T[]) Array.newInstance(context.getClazz(), functionValue.getParametersCount());
            for (int i = arguments.length; i > 0; i--) {
                arguments[i - 1] = stack.pop();
            }
            logger.debug("Took {} arguments ({}) from stack: {}", arguments.length, Arrays.toString(arguments), stack);
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
