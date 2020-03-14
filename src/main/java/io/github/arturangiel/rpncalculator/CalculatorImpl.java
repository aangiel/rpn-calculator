package io.github.arturangiel.rpncalculator;

import io.github.arturangiel.rpncalculator.exception.*;
import io.github.arturangiel.rpncalculator.math.FunctionValue;
import org.apfloat.Apfloat;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;

/**
 * Class for Reverse Polish Notation calculations
 */
public class CalculatorImpl implements Calculator {

    private CalculatorContext context;

    public CalculatorImpl(CalculatorContext context) {
        this.context = context;
    }

    public CalculatorImpl() {
        this.context = CalculatorContext.getDefaultContext();
    }

    /**
     * Main method for Reverse Polish Notation Calculator
     *
     * @param equation String with equation to calculate
     * @return calculated #Apfloat
     * @throws CalculatorException abstract Exception (@link #BadEquationException), #BadItemException, #CalculatorArithmeticException, #LackOfArgumentsException, #UnexpectedException
     */
    @Override
    public Apfloat calculate(String equation) throws CalculatorException {

        String[] equationSplit = equation.trim().split("\\s+");

        ArrayDeque<Apfloat> stack = new ArrayDeque<>();

        for (int i = 0; i < equationSplit.length; i++) {

            String item = equationSplit[i];
            try {
                stack.push(new Apfloat(item, context.getPrecision()));
            } catch (NumberFormatException e) {
                calculateOnStack(item, stack, i);
            }
        }

        if (stack.size() == 1)
            return stack.pop();
        else
            throw new BadEquationException(stack);
    }

    private void calculateOnStack(String operator, ArrayDeque<Apfloat> stack, int position)
            throws CalculatorException {

        try {
            FunctionValue functionValue = context.getFunctions().get(operator);
            Apfloat[] arguments = new Apfloat[functionValue.getParametersCount()];
            for (int i = arguments.length; i > 0; i--) {
                arguments[i - 1] = stack.pop();
            }
            Apfloat applied = functionValue.getFunction().apply(arguments);
            stack.push(applied);
        } catch (NullPointerException e) {
            throw new BadItemException(operator, position);
        } catch (NoSuchElementException e) {
            throw new LackOfArgumentsException(operator, position);
        } catch (IllegalAccessException e) {
            throw new UnexpectedException(e.getMessage());
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ArithmeticException)
                throw new CalculatorArithmeticException(e.getTargetException().getMessage());
            else
                throw new UnexpectedException(e.getTargetException().getMessage());
        }
    }

    @Override
    public CalculatorContext getContext() {
        return context;
    }
}
