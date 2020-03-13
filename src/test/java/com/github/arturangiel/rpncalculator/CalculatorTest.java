package com.github.arturangiel.rpncalculator;

import com.github.arturangiel.rpncalculator.exception.*;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CalculatorTest {

    Calculator calculator;

    @Before
    public void setUp() {
        calculator = new Calculator();

        calculator
                .addCustomFunction("**", 2, (a) -> a[0].multiply(a[1].multiply(a[0])))
                .addCustomFunction("^", 2, (a) -> a[0].divide(a[1].multiply(a[0])))
                .addCustomFunction("fun", 3, (array) -> array[0].multiply(array[1]).multiply(array[2]))
                .addCustomFunction("fun2", 4, (array) -> array[0].multiply(array[1]).multiply(array[2]).subtract(array[3]))

                /*
                 * Operations added from example: https://en.wikipedia.org/wiki/Reverse_Polish_notation
                 */
                .addCustomFunction("−", 2, a -> a[0].subtract(a[1]))
                .addCustomFunction("÷", 2, a -> a[0].divide(a[1]))
                .addCustomFunction("+", 2, a -> a[0].add(a[1]))
                .addCustomFunction("×", 2, a -> a[0].multiply(a[1]));

    }

    @Test
    public void calculateCorrectEquations() throws CalculatorException {
        assertEquals(new Apfloat(14), calculator.calculate("5 1 2 + 4 * + 3 -"));
        assertEquals(new Apfloat(40), calculator.calculate("12 2 3 4 * 10 5 / + * +"));
        assertEquals(new Apfloat(4), calculator.calculate("2 2 +"));
        assertEquals(new Apfloat(-0.448073616), calculator.calculate("90 cos"));
        assertEquals(new Apfloat(5), calculator.calculate("15 7 1 1 + - / 3 * 2 1 1 + + -"));

        /*
         * Example copied from https://en.wikipedia.org/wiki/Reverse_Polish_notation
         * Operands added in setUp method
         */
        assertEquals(new Apfloat(5), calculator.calculate("15 7 1 1 + − ÷ 3 × 2 1 1 + + −"));
    }

    @Test
    public void calculateBadEquationException() {
        BadEquationException exception = assertThrows(BadEquationException.class, () -> calculator.calculate("12 2 3 4 * 10 5 / + * + 3"));
        assertEquals("Left on stack: [3, 4e1]", exception.getMessage());
    }

    @Test
    public void calculateBadItemException() {
        BadItemException exception = assertThrows(BadItemException.class, () -> calculator.calculate("12 2 3 4 * 10 5 / + * ++"));
        assertEquals("Bad item: '++' at position: 11", exception.getMessage());
        BadItemException exception1 = assertThrows(BadItemException.class, () -> calculator.calculate("12 2 3 sinx 4 * 10 5 / + * +"));
        assertEquals("Bad item: 'sinx' at position: 4", exception1.getMessage());

    }

    @Test
    public void calculateLackOfArgumentsException() {
        LackOfArgumentsException exception = assertThrows(LackOfArgumentsException.class, () -> calculator.calculate("12 * 2 3 4 * 10 5 / + * +"));
        assertEquals("Lack of arguments for: * at position: 2", exception.getMessage());
    }

    @Test
    public void calculateWithCustomOperation() throws CalculatorException {
        assertEquals(new Apfloat(10), calculator.calculate("5 1 2 ** 4 * + 3 -"));
        assertEquals(new Apfloat(-2.875), calculator.calculate("5 1 2 ** 4 * ^ 3 -"));

    }

    @Test
    public void calculateWithCustomFunction() throws CalculatorException {
        assertEquals(new Apfloat(98), calculator.calculate("5 1 4 3 2 fun * 4 * + 3 -"));
        assertEquals(new Apfloat(98), calculator.calculate("5 1  4  3  2 fun * 4 * + 3 -"));
        assertEquals(new Apfloat(102), calculator.calculate("5 1 2 3 4 fun * 4 * + 3.5 0 0 1 fun2 -"));
        assertEquals(new Apfloat(102), calculator.calculate("5 1 24 * 4 * + -1 -"));
    }

    @Test
    public void calculateWithCustomFunctionBadItemException() {
        BadItemException exception =
                assertThrows(
                        BadItemException.class,
                        () -> calculator.calculate("5 1 4 d 2 fun * 4 * + 3 -"));
        assertEquals("Bad item: 'd' at position: 4", exception.getMessage());
    }

    @Test
    public void calculateWithCustomFunctionNotEnoughArgumentsInFunctionException() {
        LackOfArgumentsException exception =
                assertThrows(
                        LackOfArgumentsException.class,
                        () -> calculator.calculate("5 1 3 2 fun * 4 * + 3 -"));
        assertEquals("Lack of arguments for: + at position: 9", exception.getMessage());
    }

    @Test
    public void calculateConstructingFunctionsException() {
        BadEquationException exception =
                assertThrows(BadEquationException.class, () -> calculator.calculate("08 90 sin"));
        assertEquals("Left on stack: [8.93996663e-1, 8]", exception.getMessage());
    }

    @Test
    public void calculateFloatingPoint() throws CalculatorException {
        assertEquals(new Apfloat(17.54733345), calculator.calculate("12 23.234134 0.34234 12.2344534 * / +"));
    }

    @Test
    public void calculateWithMathFunctions() throws CalculatorException {
        assertEquals(new Apfloat(5.34323729e12), calculator.calculate("23 tanh 30 cosh *"));
    }

    @Test
    public void calculateHardOne() throws CalculatorException {
        assertEquals(new Apfloat(-5.068680686e-9),
                calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));

        BadItemException badItemException = assertThrows(BadItemException.class,
                () -> calculator.calculate("-0.5 23 24.234 tan h 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'h' at position: 5", badItemException.getMessage());

        BadEquationException badEquationException = assertThrows(BadEquationException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 23 4 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [2.065501056e-6, -5e-1]", badEquationException.getMessage());

        BadItemException badItemException1 = assertThrows(BadItemException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384 e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'e8' at position: 12", badItemException1.getMessage());

        LackOfArgumentsException notEnoughArgumentsInFunctionException
                = assertThrows(LackOfArgumentsException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Lack of arguments for: / at position: 19", notEnoughArgumentsInFunctionException.getMessage());

        LackOfArgumentsException lackOfArgumentsException = assertThrows(LackOfArgumentsException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / +"));
        assertEquals("Lack of arguments for: + at position: 21", lackOfArgumentsException.getMessage());

        BadEquationException constructingFunctionsException
                = assertThrows(BadEquationException.class,
                () -> calculator.calculate("-0.5 23 4 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [-1.078258835e-6, -5e-1]", constructingFunctionsException.getMessage());

    }

    @Test
    public void calculateEmpty() throws CalculatorException {
        BadItemException badItemException = assertThrows(BadItemException.class, () -> calculator.calculate(""));
        assertEquals("Bad item: '' at position: 1", badItemException.getMessage());
        assertEquals(new Apfloat(2), calculator.calculate("2"));
    }

    @Test
    public void calculateInnerFunctions() throws CalculatorException {
        Apfloat radians = ApfloatMath.toRadians(new Apfloat(90, 10));
        assertEquals(new Apfloat(1), calculator.calculate(radians + " sin"));
        assertEquals(new Apfloat(1.570796326), calculator.calculate("90 toRadians"));
        assertEquals(new Apfloat(1), calculator.calculate("90 toRadians sin"));
    }

    @Test
    public void calculateAllStaticMethods() throws CalculatorException {
        assertEquals(new Apfloat(4.49980967), calculator.calculate("90 log"));

        CalculatorArithmeticException calculatorArithmeticException1
                = assertThrows(CalculatorArithmeticException.class, () -> calculator.calculate("0 log"));
        assertEquals("Logarithm of zero", calculatorArithmeticException1.getMessage());

        assertEquals(new Apfloat(0), calculator.calculate("0 atanh"));

        CalculatorArithmeticException calculatorArithmeticException
                = assertThrows(CalculatorArithmeticException.class, () -> calculator.calculate("10 atanh"));
        assertEquals("Logarithm of negative number; result would be complex", calculatorArithmeticException.getMessage());

        assertEquals(new Apfloat(1), calculator.calculate("0 cos"));
        assertEquals(new Apfloat(0), calculator.calculate("0 atan"));
        assertEquals(new Apfloat(0), calculator.calculate("0 cbrt"));
        assertEquals(new Apfloat(0), calculator.calculate("0 tanh"));
        assertEquals(new Apfloat(0), calculator.calculate("0 sqrt"));
        assertEquals(new Apfloat(0), calculator.calculate("0 sin"));
        assertEquals(new Apfloat(1), calculator.calculate("0 exp"));
        assertEquals(new Apfloat(0), calculator.calculate("0 frac"));
        assertEquals(new Apfloat(0), calculator.calculate("0 tan"));
        assertEquals(new Apfloat(3.626860407, 10), calculator.calculate("2 sinh"));
        assertEquals(new Apfloat(1.762747174, 10), calculator.calculate("3 acosh"));
        assertEquals(new Apfloat(2.864788975e2), calculator.calculate("5 toDegrees"));
        assertEquals(new Apfloat(1.047197551), calculator.calculate("0.5 acos"));
        assertEquals(new Apfloat(8.726646259e-2), calculator.calculate("5 toRadians"));
        assertEquals(new Apfloat(1), calculator.calculate("0 cosh"));
        assertEquals(new Apfloat(10), calculator.calculate("-10 abs"));
        assertEquals(new Apfloat(-5), calculator.calculate("5 negate"));
        assertEquals(new Apfloat(0), calculator.calculate("0 w"));
        assertEquals(new Apfloat(0), calculator.calculate("0 asin"));
        assertEquals(new Apfloat(0), calculator.calculate("0 asinh"));
        assertEquals(new Apfloat(2.4e1), calculator.calculate("5 gamma"));
    }

    @Test
    public void calculateCompareAvailableFunctions() {
        List<String> functions = Arrays.asList("**", "log", "atanh", "cos", "atan", "cbrt", "tanh", "−", "sqrt", "×", "sin", "exp", "frac", "^", "tan", "fun2", "sinh", "acosh", "*", "toDegrees", "+", "acos", "toRadians", "-", "/", "cosh", "abs", "negate", "w", "÷", "asin", "asinh", "gamma", "fun");
        assertEquals(functions, calculator.getAvailableFunctions().stream().collect(Collectors.toList()));
    }

    @Test
    public void calculateCompareOthers() {
        assertEquals(calculator.getFunctions().keySet(), calculator.getAvailableFunctions());
        calculator.setPrecision(10);
        assertEquals(10, calculator.getPrecision());
    }
}