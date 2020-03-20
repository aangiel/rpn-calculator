package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.exception.CalculatorArithmeticException;
import io.github.aangiel.rpn.exception.CalculatorException;
import io.github.aangiel.rpn.exception.UnexpectedException;
import io.github.aangiel.rpn.math.ConstructorValue;
import io.github.aangiel.rpn.math.FunctionValue;
import io.github.aangiel.rpn.math.IMathFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CalculatorContext<T extends Number> {

    private Map<String, FunctionValue<T>> functions;
    private Class<T> clazz;
    private Class<?> mathClass;
    private long precision;

    private CalculatorContext() {
        throw new AssertionError();
    }

    public CalculatorContext(Class<T> clazz, Class<?> mathClass, long precision) {
        this.clazz = clazz;
        this.mathClass = mathClass;
        this.precision = precision;
        populateFunctions();
    }

    private void populateFunctions() {
        functions = new HashMap<>();
        populateDefaultOperations();
        populateDefaultOneParameterMathFunctions();
        populateConstants();
    }

    protected abstract void populateDefaultOperations();

    protected abstract void populateConstants();

    public abstract ConstructorValue<T> getValue();

    public Map<String, FunctionValue<T>> getFunctions() {
        return functions;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public long getPrecision() {
        return precision;
    }

    public void addFunction(String name, int parameterCount, IMathFunction<T> function) {
        functions.put(name, new FunctionValue<>(parameterCount, function));
    }

    private void populateDefaultOneParameterMathFunctions() {
        for (Method m : getStaticOneParameterMethodsFromMathClass()) {
            IMathFunction<T> function = a -> (T) invokeMathMethod(m, a);
            functions.put(m.getName(), new FunctionValue<>(1, function));
        }
    }

    private List<Method> getStaticOneParameterMethodsFromMathClass() {
        return Stream.of(mathClass.getMethods())
                .filter(m -> clazz.equals(m.getReturnType()))
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> Arrays.equals(m.getParameterTypes(), new Class[]{clazz}))
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    private T invokeMathMethod(Method m, List<T> a) throws CalculatorException {
        try {
            return (T) m.invoke(null, a.toArray());
        } catch (IllegalAccessException e) {
            throw new UnexpectedException(e.getMessage());
        } catch (InvocationTargetException e) {
            if (ArithmeticException.class.equals(e.getCause().getClass()))
                throw new CalculatorArithmeticException(e.getCause().getMessage());
            else
                throw new UnexpectedException(e.getCause().getMessage());
        }
    }
}
