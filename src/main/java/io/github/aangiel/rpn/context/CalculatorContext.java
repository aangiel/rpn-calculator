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

    public long getPrecision() {
        return precision;
    }

    public void addFunction(String name, int parameterCount, IMathFunction<T> function) {
        functions.put(name, new FunctionValue<>(parameterCount, function));
    }

    private void populateDefaultOneParameterMathFunctions() {
        for (Method method : getStaticOneParameterMethodsFromMathClass()) {
            IMathFunction<T> function = a -> (T) invokeMathMethod(method, a);
            functions.put(method.getName(), new FunctionValue<>(1, function));
        }
    }

    private List<Method> getStaticOneParameterMethodsFromMathClass() {
        return Stream.of(mathClass.getMethods())
                .filter(method -> clazz.equals(method.getReturnType()))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> Arrays.equals(method.getParameterTypes(), new Class[]{clazz}))
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private T invokeMathMethod(Method method, List<T> arguments) throws CalculatorException {
        try {
            return (T) method.invoke(null, arguments.toArray());
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
