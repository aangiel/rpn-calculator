package io.github.aangiel.rpn.math;

import java.util.List;
import java.util.function.Function;

/**
 * Used as value class in {@link java.util.HashMap HashMap} in
 * {@link io.github.aangiel.rpn.interfaces.FunctionOrOperatorContext#getFunctionOrOperator(String)
 * FunctionOrOperatorContext.getFunctionOrOperator(String)}
 *
 * <br><br>
 * <p>
 * It holds quantity of parameters for lambda provided to function of type {@link Function}.
 *
 * <br><br>
 * <p>
 * {@link #getParametersCount()} is used by {@link io.github.aangiel.rpn.impl.CalculatorImpl}
 * to know how many arguments take from stack
 *
 * @param <T> extends Number
 */
public final class FunctionOrOperator<T extends Number> {

    private final int parametersCount;
    private final Function<List<T>, T> function;

    public FunctionOrOperator(int parametersCount, Function<List<T>, T> function) {
        this.parametersCount = parametersCount;
        this.function = function;
    }

    /**
     * Used by {@link io.github.aangiel.rpn.impl.CalculatorImpl}
     * to know how many arguments take from stack
     *
     * @return int with quantity of parameters for function provided with lambda by {@link #get()}
     */
    public int getParametersCount() {
        return parametersCount;
    }

    /**
     * Used by {@link io.github.aangiel.rpn.impl.CalculatorImpl CalculatorImpl} for obtaining lambda used for calculating
     * values from equation which will be pushed do operational stack.
     *
     * @return Lambda for calculating operations and functions
     */
    public Function<List<T>, T> get() {
        return function;
    }
}
