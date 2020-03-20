package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.CalculatorContext;
import io.github.aangiel.rpn.exception.*;

/**
 * Main interface which you should use.<br>
 * Example usage:<br>
 * <pre>
 *     Calculator&#60;Apfloat&#62; calculator = CalculatorSupplier.getCalculator(Apfloat.class);
 *     Apfloat result = calculator.calculate("5 1 2 + 4 * + 3 -");
 *     System.out.println(result); // should display 14
 * </pre>
 *
 * <br><br>
 * <p>
 * If you want to implement new subclass of {@link Number}:
 *
 * @param <T> extends Number for using with all subtypes of number.
 * @see CalculatorContext
 */
public interface Calculator<T extends Number> {

    /**
     * Main method for Reverse Polish Notation Calculator
     *
     * @param equation String with equation to calculate
     * @return result of calculation of Class given in
     * {@link io.github.aangiel.rpn.CalculatorSupplier#getCalculator(Class) CalculatorSupplier.getCalculator(Class&#60;T extends Number&#62;)}
     * @throws CalculatorException abstract Exception for
     *                             {@link BadEquationException}
     *                             , {@link BadItemException}
     *                             , {@link CalculatorArithmeticException}
     *                             , {@link EmptyEquationException}
     *                             , {@link LackOfArgumentsException}
     *                             , {@link UnexpectedException}
     * @see <a href="https://en.wikipedia.org/wiki/Reverse_Polish_notation">Wikipedia</a>
     */
    T calculate(String equation) throws CalculatorException;

    /**
     * Returns context of {@link Calculator Calculator}
     * if you want to customize it with adding additional functions or operators
     *
     * @return {@link CalculatorContext CalculatorContext}, context used by
     * {@link io.github.aangiel.rpn.CalculatorSupplier} to return calculator of given type.
     */
    CalculatorContext<T> getContext();
}
