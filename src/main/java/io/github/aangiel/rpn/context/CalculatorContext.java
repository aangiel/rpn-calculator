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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base abstract class used by {@link io.github.aangiel.rpn.Calculator Calculator}.
 * This class should be extended if you want to add new type of {@link Number Numbers}
 * which you want to use with RPN Calculator
 *
 * @param <T> extends {@link Number Number}.
 */
public abstract class CalculatorContext<T extends Number> {

    private Map<String, FunctionValue<T>> functions;
    private Class<T> clazz;
    private Class<?> mathClass;
    private long precision;

    /**
     * Should be invoked only by subclass and pass hardcoded parameters.
     * @param clazz Class extending {@link Number Number} which you are implementing (i.e. <pre>double.class</pre>)
     * @param mathClass Class with static, one-parameter math methods (i.e. <pre>Math.class</pre>)
     * @param precision used only with {@link org.apfloat.Apfloat Apfloat} type, which is currently implemented
     */
    protected CalculatorContext(Class<T> clazz, Class<?> mathClass, long precision) {
        this.clazz = clazz;
        this.mathClass = mathClass;
        this.precision = precision;
        populateFunctions();
    }

    /**
     * Should be invoked only by subclass and pass hardcoded parameters.
     * @param clazz Class extending {@link Number Number} which you are implementing (i.e. <pre>double.class</pre>)
     * @param mathClass Class with static, one-parameter math methods (i.e. <pre>Math.class</pre>)
     */
    protected CalculatorContext(Class<T> clazz, Class<?> mathClass) {
        this(clazz, mathClass, 0);
    }

    /**
     * Should be implemented as series of
     * {@link #addFunctionOrOperator(String, int, IMathFunction) addFunctionOrOperator(String, int, IMathFunction)}
     * invokes with default mathematical operations (+, -, *, /)
     */
    protected abstract void populateDefaultOperations();

    /**
     * Should be implemented as series of
     * {@link #addFunctionOrOperator(String, int, IMathFunction) addFunctionOrOperator(String, int, IMathFunction)}
     * invokes with mathematical constants (i.e. PI or e)
     */
    protected abstract void populateConstants();

    /**
     * Lambda for returning new Object of type <pre>&#60;T extends Number&#62;</pre><br><br>
     * Should be implemented as i.e.
     * <pre>
     *     return args -&#62; Double.valueOf(
     *                 (String) args.get(0)
     *         );
     * </pre>
     * @return {@link ConstructorValue ConstructorValue}
     * which is used during parsing of equation while trying to parse number
     */
    public abstract ConstructorValue<T> getValue();

    /**
     * Should be always implemented as <pre>return this;</pre>, because it's used in chaining of
     * {@link #addFunctionOrOperator(String, int, IMathFunction) addFunctionOrOperator(String, int, IMathFunction)}
     * method.
     * @return this
     */
    protected abstract CalculatorContext<T> self();

    /**
     * Adds function or operator for use in equations passed as String to
     * {@link io.github.aangiel.rpn.Calculator#calculate(String) Calculator.calculate(String)}
     * method.
     * @param name Name of the function or operator (i.e. "*" for multiplying or "sin" for sinus)
     * @param parameterCount Number of parameters passed to function (i.e. 2 for "*" or 1 for "sin")
     * @param function Lambda which will be used during parsing equation (i.e. <pre>args -&#62; args.get(0) + args.get(1)</pre>
     *                 <br><pre>args</pre> is defined as
     *                 {@link IMathFunction#apply(List) &#60;T extends Number&#62; T apply(List&#60;T&#62; args)}
     * @return <pre>this</pre> for chaining of adding functions or operators
     */
    public CalculatorContext<T> addFunctionOrOperator(String name, int parameterCount, IMathFunction<T> function) {
        functions.put(name, new FunctionValue<>(parameterCount, function));
        return self();
    }

    /**
     * Returns set of available functions and operators just for check what operations are available with this library.
     * @return Set of available functions and operators.
     */
    public Set<String> getAvailableFunctionsAndOperators() {
        return functions.keySet();
    }

    /**
     * Returns lambda used during parsing of equation passed by
     * {@link io.github.aangiel.rpn.Calculator#calculate(String) Calculator.calculate(String)}
     * @param name function or operator to be returned
     * @return Lambda which is used to calculate value for specified function or operator and its' arguments
     */
    public FunctionValue<T> getFunctionOrOperator(String name) {
        return functions.get(name);
    }

    /**
     * Returns precision used with {@link org.apfloat.Apfloat Apfloat} which is currently implemented.
     * @return precision for {@link org.apfloat.Apfloat Apfloat} type.
     */
    public long getPrecision() {
        return precision;
    }

    private void populateFunctions() {
        functions = new HashMap<>();
        populateDefaultOperations();
        populateDefaultOneParameterMathFunctions();
        populateConstants();
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
