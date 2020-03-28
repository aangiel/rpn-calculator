package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.interfaces.CalculatorContext;
import io.github.aangiel.rpn.math.FunctionOrOperator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.aangiel.rpn.exception.CalculatorException.npe;

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
        Objects.requireNonNull(mathClass, npe("mathClass"));
        Objects.requireNonNull(clazz, npe("clazz"));
        if (maxParametersCount < 0)
            throw new IllegalArgumentException("Param 'maxParametersCount' must be greater than or equal to zero");

        var mathFunctions = MathHelper.getMathFunctions(mathClass, clazz, maxParametersCount);
        functions.putAll(mathFunctions);
    }

    @Override
    public CalculatorContext<T> addFunctionOrOperator(String name, int parametersCount, Function<List<T>, T> function) {
        Objects.requireNonNull(name, npe("name"));
        Objects.requireNonNull(function, npe("function"));
        if (parametersCount < 0)
            throw new IllegalArgumentException("Param 'parametersCount' must be greater than or equal to zero");

        functions.put(name, new FunctionOrOperator<>(parametersCount, function));
        return self();
    }

    @Override
    public Set<String> getAvailableFunctionsAndOperators() {
        return Collections.unmodifiableSet(functions.keySet());
    }


    @Override
    public FunctionOrOperator<T> getFunctionOrOperator(String name) {
        Objects.requireNonNull(name, npe("name"));
        return Optional
                .ofNullable(functions.get(name))
                .orElseThrow(npen(name));
    }

    private Supplier<NullPointerException> npen(String name) {
        return () -> new NullPointerException(String.format("No operator: %s", name));
    }

    private static final class MathHelper {

        private MathHelper() {
            throw new AssertionError("MathHelper class cannot be instantiated");
        }

        private static <N extends Number> HashMap<String, FunctionOrOperator<N>> getMathFunctions(Class<?> mathClass, Class<N> clazz, int maxParametersCount) {
            assert mathClass != null;
            assert clazz != null;
            assert maxParametersCount >= 0;

            Objects.requireNonNull(mathClass, npe("mathClass"));
            Objects.requireNonNull(clazz, npe("clazz"));

            var result = new HashMap<String, FunctionOrOperator<N>>();

            var availableMethods = getStaticMethodsFromMathClass(mathClass, clazz, maxParametersCount);

            for (Method method : availableMethods) {
                Function<List<N>, N> function = a -> invokeMathMethod(method, a);
                result.put(method.getName(), new FunctionOrOperator<>(method.getParameterCount(), function));
            }

            return result;
        }

        private static <N extends Number> List<Method> getStaticMethodsFromMathClass(Class<?> mathClass, Class<N> clazz, int maxParametersCount) {
            assert mathClass != null;
            assert clazz != null;
            assert maxParametersCount >= 0;

            return Stream.of(mathClass.getMethods())
                    .filter(method -> clazz.equals(method.getReturnType()))
                    .filter(method -> predicate(clazz, method, maxParametersCount))
                    .filter(method -> Modifier.isStatic(method.getModifiers()))
                    .collect(Collectors.toList());
        }

        private static <N extends Number> boolean predicate(Class<N> clazz, Method method, int maxParametersCount) {
            assert clazz != null;
            assert method != null;
            assert maxParametersCount >= 0;

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
            assert method != null;
            assert arguments != null;

            try {
                // It always works (when used in getMathFunctions),
                // because <T and N extends Number> and methods iterated
                // are filtered (in getStaticOneParameterMethodsFromMathClass)
                // to take only those with return type T
                @SuppressWarnings("unchecked")
                var result = (N) method.invoke(null, arguments.toArray());
                return result;
            } catch (IllegalAccessException e) {
                throw new UnsupportedOperationException(e.getMessage());
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ArithmeticException)
                    throw (ArithmeticException) e.getTargetException();
                else
                    throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }
}
