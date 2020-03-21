package io.github.aangiel.rpn.math;

/**
 * Used as value class in {@link java.util.HashMap HashMap} in
 * {@link io.github.aangiel.rpn.context.CalculatorContext#getFunctionOrOperator(String)
 * CalculatorContext.getFunctionOrOperator(String)}
 *
 * <br><br>
 * <p>
 * It holds quantity of parameters for lambda provided to function of type {@link IFunction}.
 *
 * <br><br>
 * <p>
 * {@link #getParametersCount()} is used by {@link io.github.aangiel.rpn.impl.CalculatorImpl}
 * to know how many arguments take from stack
 *
 * @param <T> extends Number
 */
public final class Function<T extends Number> {

    private final int parametersCount;
    private final IFunction<T> function;

    public Function(int parametersCount, IFunction<T> function) {
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
    public IFunction<T> get() {
        return function;
    }
}
