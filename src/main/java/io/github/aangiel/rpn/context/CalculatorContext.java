package io.github.aangiel.rpn.context;

import io.github.aangiel.rpn.math.FunctionOrOperator;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Base abstract class used by {@link io.github.aangiel.rpn.Calculator Calculator}.
 * This class should be extended if you want to add new type of {@link Number Numbers}
 * which you want to use with RPN Calculator
 *
 * @param <T> extends {@link Number Number}.
 */
public abstract class CalculatorContext<T extends Number> {

    private final Map<String, FunctionOrOperator<T>> functions;
    private final long precision;
    private final RoundingMode roundingMode;

    protected CalculatorContext() {
        this(0);
    }

    /**
     * Should be invoked only by subclass and pass hardcoded parameters.
     *
     * @param precision used only with {@link org.apfloat.Apfloat Apfloat} type, which is currently implemented
     */
    protected CalculatorContext(long precision) {
        this(precision, RoundingMode.CEILING);
    }

    protected CalculatorContext(RoundingMode roundingMode) {
        this(0, roundingMode);
    }

    private CalculatorContext(long precision, RoundingMode roundingMode) {
        this.precision = precision;
        this.roundingMode = roundingMode;
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

    /**
     * Lambda for returning new Object of type <pre>&#60;T extends Number&#62;</pre><br><br>
     * Should be implemented as e.g. {@code return args -> Double.valueOf(args.get(0));}
     *
     * @return {@link Function Function&#60;String, T extends Number&#62;}
     * which is used during parsing of equation while trying to parse number
     */
    public abstract Function<String, T> getNumberConstructor();

    /**
     * Should be always implemented as {@code return this;}, because it's used in chaining of
     * {@link #addFunctionOrOperator(String, int, Function) addFunctionOrOperator(String, int, IMathFunction)}
     * method.
     *
     * @return this
     */
    protected abstract CalculatorContext<T> self();

    /**
     * Adds function or operator for use in equations passed as String to
     * {@link io.github.aangiel.rpn.Calculator#calculate(String) Calculator.calculate(String)}
     * method.
     *
     * @param name           Name of the function or operator (e.g. "*" for multiplying or "sin" for sinus)
     * @param parameterCount Number of parameters passed to function (e.g. 2 for "*" or 1 for "sin")
     * @param function       Lambda which will be used during parsing equation (e.g. args -&#62; args.get(0) + args.get(1)<br>
     *                       args is defined as {@link Function &#60;T extends Number&#62; T apply(List&#60;T&#62; args)}
     * @return this for chaining of adding functions or operators
     */
    public CalculatorContext<T> addFunctionOrOperator(String name, int parameterCount, Function<List<T>, T> function) {
        functions.put(name, new FunctionOrOperator<>(parameterCount, function));
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
     *
     * @param name function or operator to be returned
     * @return Lambda which is used to calculate value for specified function or operator and its' arguments
     */
    public FunctionOrOperator<T> getFunctionOrOperator(String name) {
        return functions.get(name);
    }

    /**
     * Returns precision used with {@link org.apfloat.Apfloat Apfloat} which is currently implemented.
     *
     * @return precision for {@link org.apfloat.Apfloat Apfloat} type.
     */
    public long getPrecision() {
        return precision;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

}
