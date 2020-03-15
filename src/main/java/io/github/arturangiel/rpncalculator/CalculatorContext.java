package io.github.arturangiel.rpncalculator;

import io.github.arturangiel.rpncalculator.exception.CalculatorArithmeticException;
import io.github.arturangiel.rpncalculator.exception.CalculatorException;
import io.github.arturangiel.rpncalculator.exception.UnexpectedException;
import io.github.arturangiel.rpncalculator.math.FunctionValue;
import io.github.arturangiel.rpncalculator.math.IMathFunction;
import io.github.arturangiel.rpncalculator.supplier.DefaultOperationsSupplier;
import io.github.arturangiel.rpncalculator.supplier.SupplierStrategy;
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
    private Class<T> clazz;

    public CalculatorContext(Class<T> clazz) {
        functions = new HashMap<>();
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public CalculatorContext<T> getContext() {
        return context;
    }

    public CalculatorContext<T> getDefaultContext(Class<T> clazz) {
        context = new CalculatorContext<>(clazz);
        context.populateDefaultOperations();
        context.setPrecision(10);
        return context;
    }

    public CalculatorContext<T> getMathFunctionsContext(Class<T> clazz) {
        context = getDefaultContext(clazz);
        context.populateDefaultOneParameterMathFunctions();
        return context;
    }

    public CalculatorContext<T> getMathFunctionsAndConstantsContext(Class<T> clazz) {
        context = getMathFunctionsContext(clazz);
        context.populateConstants();
        return context;
    }

    public CalculatorContext<T> getEmptyContext(Class<T> clazz) {
        return new CalculatorContext<>(clazz);
    }

    public CalculatorContext<T> addCustomFunction(String name, int parametersCount, IMathFunction<T> function) {
        functions.put(name, new FunctionValue<>(parametersCount, function));
        return this;
    }

    private void populateDefaultOperations() {
        SupplierStrategy<T> supplierStrategy = new SupplierStrategy<>();
        DefaultOperationsSupplier<T> supplier = supplierStrategy.getSupplier(clazz);
        functions.put("+", new FunctionValue<T>(2, supplier.getAddFunction()));
        functions.put("-", new FunctionValue<T>(2, supplier.getSubtractFunction()));
        functions.put("*", new FunctionValue<T>(2, supplier.getMultiplyFunction()));
        functions.put("/", new FunctionValue<T>(2, supplier.getDivideFunction()));
    }

    private void populateConstants() {
        functions.put("pi", new FunctionValue<T>(0, (a) -> (T) ApfloatMath.pi(precision)));
        functions.put("e", new FunctionValue<T>(0, (a) -> (T) new Apfloat(2.718281828)));
    }

    private void populateDefaultOneParameterMathFunctions() {
        for (Method m : getStaticOneParameterMethodsFromApfloatMath()) {
            IMathFunction<T> function = a -> (T) invokeApfloatMathMethod(m, a);
            functions.put(m.getName(), new FunctionValue<T>(1, function));
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

    private T invokeApfloatMathMethod(Method m, T[] a) throws CalculatorException {
        try {
            return (T) m.invoke(null, a);
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
