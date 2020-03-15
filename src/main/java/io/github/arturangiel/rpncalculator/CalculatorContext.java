package io.github.arturangiel.rpncalculator;

import io.github.arturangiel.rpncalculator.exception.CalculatorArithmeticException;
import io.github.arturangiel.rpncalculator.exception.CalculatorException;
import io.github.arturangiel.rpncalculator.exception.UnexpectedException;
import io.github.arturangiel.rpncalculator.math.FunctionValue;
import io.github.arturangiel.rpncalculator.math.IMathFunction;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CalculatorContext<T extends Number> {

    private Map<String, FunctionValue<T>> functions;
    private long precision;
    private CalculatorContext<T> context;

    public CalculatorContext() {
        functions = new HashMap<>();
    }

    public CalculatorContext<T> getContext() {
        return context;
    }

    public CalculatorContext<T> getDefaultContext() {
        context = new CalculatorContext();
        context.populateDefaultOperations();
        context.setPrecision(10);
        return context;
    }

    public CalculatorContext<T> getMathFunctionsContext() {
        context = getDefaultContext();
        context.populateDefaultOneParameterMathFunctions();
        return context;
    }

    public CalculatorContext<T> getMathFunctionsAndConstantsContext() {
        context = getMathFunctionsContext();
        context.populateConstants();
        return context;
    }

    public CalculatorContext<T> getEmptyContext() {
        return new CalculatorContext<T>();
    }

    public CalculatorContext<T> addCustomFunction(String name, int parametersCount, IMathFunction<T> function) {
        functions.put(name, FunctionValue.forFunction(parametersCount, function));
        return this;
    }

    private void populateDefaultOperations() {
        functions.put("+", FunctionValue.forFunction(2, a -> ((Apfloat) a[0]).add((Apfloat) a[1])));
        functions.put("-", FunctionValue.forFunction(2, a -> ((Apfloat) a[0]).subtract((Apfloat) a[1])));
        functions.put("*", FunctionValue.forFunction(2, a -> ((Apfloat) a[0]).multiply((Apfloat) a[1])));
        functions.put("/", FunctionValue.forFunction(2, a -> ((Apfloat) a[0]).divide((Apfloat) a[1])));
    }

    private void populateConstants() {
        functions.put("pi", FunctionValue.forFunction(0, (a) -> ApfloatMath.pi(precision)));
        functions.put("e", FunctionValue.forFunction(0, (a) -> new Apfloat(2.718281828)));
    }

    private void populateDefaultOneParameterMathFunctions() {
        for (Method m : getStaticOneParameterMethodsFromApfloatMath()) {
            IMathFunction<T> function = a -> (T) invokeApfloatMathMethod(m, a);
            functions.put(m.getName(), FunctionValue.forFunction(1, function));
        }
    }

    private List<Method> getStaticOneParameterMethodsFromApfloatMath() {
        return Stream.of(ApfloatMath.class.getMethods())
                .filter(m -> Apfloat.class.equals(m.getReturnType()))
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> Arrays.equals(m.getParameterTypes(), new Class[]{Apfloat.class}))
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    private Apfloat invokeApfloatMathMethod(Method m, T[] a) throws CalculatorException {
        try {
            return (Apfloat) m.invoke(null, a);
        } catch (IllegalAccessException e) {
            throw new UnexpectedException(e.getMessage());
        } catch (InvocationTargetException e) {
            if (ArithmeticException.class.equals(e.getCause().getClass()))
                throw new CalculatorArithmeticException(e.getCause().getMessage());
            else
                throw new UnexpectedException(e.getCause().getMessage());
        }
    }

    public Map<String, FunctionValue<T>> getFunctions() {
        return functions;
    }

    public void setFunctions(Map<String, FunctionValue<T>> functions) {
        this.functions = functions;
    }

    public long getPrecision() {
        return precision;
    }

    public void setPrecision(long precision) {
        this.precision = precision;
    }

    public Set<String> getAvailableFunctions() {
        return functions.keySet();
    }
}
