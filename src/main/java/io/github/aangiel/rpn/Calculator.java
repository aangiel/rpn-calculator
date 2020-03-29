package io.github.aangiel.rpn;

import io.github.aangiel.rpn.interfaces.CalculatorContext;

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
 * @author <a href="mailto:aangiel@tuta.io">Artur Angiel</a>
 */
public interface Calculator<T extends Number> {

    /**
     * Main method for Reverse Polish Notation Calculator
     *
     * @param equation String with equation to calculate
     * @return result of calculation of Class given in
     * {@link io.github.aangiel.rpn.CalculatorSupplier#getCalculator(Class) CalculatorSupplier.getCalculator(Class&#60;T extends Number&#62;)}
     * @throws ArithmeticException      if during calculation won't be enough elements on stack for actual function or operator
     * @throws NullPointerException     if equation is null
     * @throws IllegalArgumentException if equation is empty, contains unsupported items or has bad form and after whole calculation some items left on stack
     * @see <a href="https://en.wikipedia.org/wiki/Reverse_Polish_notation">Wikipedia</a>
     */
    T calculate(String equation);

    /**
     * Returns context of {@link Calculator Calculator}
     * if you want to customize it with adding additional functions or operators
     *
     * @return {@link CalculatorContext CalculatorContext}, context used by
     * {@link io.github.aangiel.rpn.CalculatorSupplier} to return calculator of given type.
     */
    CalculatorContext<T> getContext();
}
