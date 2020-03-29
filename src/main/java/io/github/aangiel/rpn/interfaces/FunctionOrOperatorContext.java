package io.github.aangiel.rpn.interfaces;

import io.github.aangiel.rpn.math.FunctionOrOperator;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @param <T>
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public interface FunctionOrOperatorContext<T extends Number> {

    /**
     * Returns lambda used during parsing of equation passed by
     * {@link io.github.aangiel.rpn.Calculator#calculate(String) Calculator.calculate(String)}
     *
     * @param name function or operator to be returned
     * @return Lambda which is used to calculate value for specified function or operator and its' arguments
     */
    FunctionOrOperator<T> getFunctionOrOperator(String name);

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
    CalculatorContext<T> addFunctionOrOperator(String name, int parameterCount, Function<List<T>, T> function);

    /**
     * Returns set of available functions and operators just for check what operations are available with this library.
     *
     * @return Set of available functions and operators.
     */
    Set<String> getAvailableFunctionsAndOperators();

    /**
     * Should be always implemented as {@code return this;}, because it's used in chaining of
     * {@link #addFunctionOrOperator(String, int, Function) addFunctionOrOperator(String, int, IMathFunction)}
     * method.
     *
     * @return this
     */
    CalculatorContext<T> self();
}
