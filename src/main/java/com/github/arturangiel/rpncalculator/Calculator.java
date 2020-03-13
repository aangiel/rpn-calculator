package com.github.arturangiel.rpncalculator;

import com.github.arturangiel.rpncalculator.exception.*;
import com.github.arturangiel.rpncalculator.math.FunctionValue;
import com.github.arturangiel.rpncalculator.math.IMathFunction;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class Calculator {

    private Map<String, FunctionValue> functions;
    private long precision = 10;

    public Calculator() {
        functions = new HashMap<>();

        populateOperations();
        populateFunctions();
    }

    private void populateFunctions() {
        Stream.of(ApfloatMath.class.getMethods())
                .filter(m -> Apfloat.class.equals(m.getReturnType()))
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> Arrays.equals(m.getParameterTypes(), new Class[]{Apfloat.class}))
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .forEach(m -> functions.put(m.getName(), FunctionValue.forFunction(1, a -> (Apfloat) m.invoke(null, a))));
    }

    private void populateOperations() {
        functions.put("+", FunctionValue.forFunction(2, a -> a[0].add(a[1])));
        functions.put("-", FunctionValue.forFunction(2, a -> a[0].subtract(a[1])));
        functions.put("*", FunctionValue.forFunction(2, a -> a[0].multiply(a[1])));
        functions.put("/", FunctionValue.forFunction(2, a -> a[0].divide(a[1])));
    }

    public Apfloat calculate(String equation) throws CalculatorException {

        String[] equationSplit = equation.trim().split("\\s+");

        ArrayDeque<Apfloat> stack = new ArrayDeque<>();

        for (int i = 0; i < equationSplit.length; i++) {

            String item = equationSplit[i];
            try {
                stack.push(new Apfloat(item, precision));
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
            FunctionValue functionValue = functions.get(operator);
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
            throw new BadEquationException(stack);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ArithmeticException)
                throw new CalculatorArithmeticException(e.getTargetException().getMessage());
            else
                throw new BadEquationException(stack);
        }
    }

    public Calculator addCustomFunction(String name, int parametersCount, IMathFunction<Apfloat> function) {
        functions.put(name, FunctionValue.forFunction(parametersCount, function));
        return this;
    }

    public Set<String> getAvailableFunctions() {
        return functions.keySet();
    }

    public Map<String, FunctionValue> getFunctions() {
        return functions;
    }

    public long getPrecision() {
        return precision;
    }

    public void setPrecision(long precision) {
        this.precision = precision;
    }

}
