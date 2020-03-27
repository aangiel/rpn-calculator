package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.interfaces.CalculatorContext;
import io.github.aangiel.rpn.math.FunctionOrOperator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base abstract class used by {@link io.github.aangiel.rpn.Calculator Calculator}.
 * This class should be extended if you want to add new type of {@link Number Numbers}
 * which you want to use with RPN Calculator
 *
 * @param <T> extends {@link Number Number}.
 */
public abstract class AbstractCalculatorContext<T extends Number> implements CalculatorContext<T> {

    protected final Map<String, FunctionOrOperator<T>> functions;

    protected AbstractCalculatorContext() {
        functions = new HashMap<>();
        populateFunctions();
    }

    private void populateFunctions() {
        populateDefaultOperations();
        populateMathFunctions();
        populateConstants();
    }

    /**
     * Should be implemented as series of
     * {@link #addFunctionOrOperator(String, int, Function) addFunctionOrOperator(String, int, IMathFunction)}
     * invokes with default mathematical operations (+, -, *, /)
     */
    protected abstract void populateDefaultOperations();

    /**
     * Should be implemented as series of
     * {@link #addFunctionOrOperator(String, int, Function) addFunctionOrOperator(String, int, IMathFunction)}
     * invokes with mathematical constants (e.g. PI or e)
     */
    protected abstract void populateConstants();

    /**
     *
     */
    protected abstract void populateMathFunctions();

    protected final void populateDefaultMathFunctions(Class<?> mathClass, Class<T> clazz, int maxParametersCount) {
        var mathFunctions = MathHelper.getMathFunctions(mathClass, clazz, maxParametersCount);
        functions.putAll(mathFunctions);
    }

    @Override
    public CalculatorContext<T> addFunctionOrOperator(String name, int parameterCount, Function<List<T>, T> function) {
        functions.put(name, new FunctionOrOperator<>(parameterCount, function));
        return self();
    }

    @Override
    public Set<String> getAvailableFunctionsAndOperators() {
        return functions.keySet();
    }


    @Override
    public FunctionOrOperator<T> getFunctionOrOperator(String name) {
        return functions.get(name);
    }

    private static final class MathHelper {

        private static <N extends Number> HashMap<String, FunctionOrOperator<N>> getMathFunctions(Class<?> mathClass, Class<N> clazz, int maxParametersCount) {
            Objects.requireNonNull(mathClass, "Argument 'mathClass' is null");
            Objects.requireNonNull(clazz, "Argument 'clazz' is null");

            var result = new HashMap<String, FunctionOrOperator<N>>();

            var availableMethods = getStaticOneParameterMethodsFromMathClass(mathClass, clazz, maxParametersCount);

            for (Method method : availableMethods) {
                Function<List<N>, N> function = a -> invokeMathMethod(method, a);
                result.put(method.getName(), new FunctionOrOperator<>(method.getParameterCount(), function));
            }

            return result;
        }

        private static <N extends Number> List<Method> getStaticOneParameterMethodsFromMathClass(Class<?> mathClass, Class<N> clazz, int maxParametersCount) {
            return Stream.of(mathClass.getMethods())
                    .filter(method -> clazz.equals(method.getReturnType()))
                    .filter(method -> predicate(clazz, method, maxParametersCount))
                    .filter(method -> Modifier.isStatic(method.getModifiers()))
                    .collect(Collectors.toList());
        }

        private static <N extends Number> boolean predicate(Class<N> clazz, Method method, int maxParametersCount) {
            List<Class<N>> classes = new ArrayList<>();
            Predicate<Method> predicate = m -> Arrays.equals(m.getParameterTypes(), classes.toArray());
            classes.add(clazz);
            for (int i = 0; i < maxParametersCount; i++) {
                if (predicate.test(method)) return true;
                classes.add(clazz);
                predicate = m -> Arrays.equals(m.getParameterTypes(), classes.toArray());
            }
            return false;
        }

        private static <N extends Number> N invokeMathMethod(Method method, List<N> arguments) {
            try {
                // It always works (when used in getMathFunctions),
                // because <T extends Number> and methods iterated
                // are filtered (in getStaticOneParameterMethodsFromMathClass)
                // to take only those with return type T
                @SuppressWarnings("unchecked")
                var result = (N) method.invoke(null, arguments.toArray());
                return result;
            } catch (IllegalAccessException e) {
                throw new UnsupportedOperationException(e.getMessage());
            } catch (InvocationTargetException e) {
                if (ArithmeticException.class.equals(e.getTargetException().getClass()))
                    throw (ArithmeticException) e.getTargetException();
                else
                    throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }
}
