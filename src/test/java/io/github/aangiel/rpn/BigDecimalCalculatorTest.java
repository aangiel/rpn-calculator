package io.github.aangiel.rpn;

import io.github.aangiel.rpn.exception.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class BigDecimalCalculatorTest {

    Calculator<BigDecimal> calculator;

    @Before
    public void setUp() {
        calculator = CalculatorSupplier.getCalculator(BigDecimal.class);

        calculator.getContext().addFunction("**", 2, (a) -> a.get(0).multiply(a.get(1).multiply(a.get(0))));
        calculator.getContext().addFunction("^", 2, (a) -> a.get(0).divide(a.get(1).multiply(a.get(0))));
        calculator.getContext().addFunction("fun", 3, (a) -> a.get(0).multiply(a.get(1)).multiply(a.get(2)));
        calculator.getContext().addFunction("fun2", 4, (a) -> a.get(0).multiply(a.get(1)).multiply(a.get(2)).subtract(a.get(3)));

        /*
         * Operations added from example: https://en.wikipedia.org/wiki/Reverse_Polish_notation
         */
        calculator.getContext().addFunction("−", 2, a -> a.get(0).subtract(a.get(1)));
        calculator.getContext().addFunction("÷", 2, a -> a.get(0).divide(a.get(1)));
//      calculator.getContext().addFunction("+", 2, a -> a.get(0).add(a.get(1)));
        calculator.getContext().addFunction("×", 2, a -> a.get(0).multiply(a.get(1)));
    }

    @Test
    public void calculateCorrectEquations() throws CalculatorException {
        assertEquals(new BigDecimal(14), calculator.calculate("5 1 2 + 4 * + 3 -"));
        assertEquals(new BigDecimal(40), calculator.calculate("12 2 3 4 * 10 5 / + * +"));
        assertEquals(new BigDecimal(4), calculator.calculate("2 2 +"));
//        assertEquals(new BigDecimal(-0.448073616), calculator.calculate("90 cos"));
        assertEquals(new BigDecimal(5), calculator.calculate("15 7 1 1 + - / 3 * 2 1 1 + + -"));

        /*
         * Example copied from https://en.wikipedia.org/wiki/Reverse_Polish_notation
         * Operands added in setUp method
         */
        assertEquals(new BigDecimal(5), calculator.calculate("15 7 1 1 + − ÷ 3 × 2 1 1 + + −"));
    }

    @Test
    public void calculateBadEquationException() {
        BadEquationException exception = assertThrows(BadEquationException.class, () -> calculator.calculate("12 2 3 4 * 10 5 / + * + 3"));
        assertEquals("Left on stack: [40]", exception.getMessage());
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
        assertEquals(new BigDecimal(10), calculator.calculate("5 1 2 ** 4 * + 3 -"));
        assertEquals(BigDecimal.valueOf(-2.875), calculator.calculate("5 1 2 ** 4 * ^ 3 -"));

    }

    @Test
    public void calculateWithCustomFunction() throws CalculatorException {
        assertEquals(new BigDecimal(98), calculator.calculate("5 1 4 3 2 fun * 4 * + 3 -"));
        assertEquals(new BigDecimal(98), calculator.calculate("5 1  4  3  2 fun * 4 * + 3 -"));
        assertEquals(new BigDecimal("102.0"), calculator.calculate("5 1 2 3 4 fun * 4 * + 3.5 0 0 1 fun2 -"));
        assertEquals(new BigDecimal(102), calculator.calculate("5 1 24 * 4 * + -1 -"));
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

//    @Test
//    public void calculateConstructingFunctionsException() {
//        BadEquationException exception =
//                assertThrows(BadEquationException.class, () -> calculator.calculate("08 90 sin"));
//        assertEquals("Left on stack: [8.93996663e-1, 8]", exception.getMessage());
//    }

    @Test
    public void calculateFloatingPoint() throws CalculatorException {
        assertEquals(new BigDecimal("17.547334"), calculator.calculate("12 23.234134 0.34234 12.2344534 * / +"));
    }

//    @Test
//    public void calculateWithMathFunctions() throws CalculatorException {
//        assertEquals(new BigDecimal("5.34323729e12"), calculator.calculate("23 tanh 30 cosh *"));
//    }

    @Test
    public void calculateHardOne() throws CalculatorException {
        assertEquals(BigDecimal.valueOf(-0.00042863),
                calculator.calculate("-0.5  234.4 3 234 + / - 9 ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));

        BadItemException badItemException = assertThrows(BadItemException.class,
                () -> calculator.calculate("-0.5 23 24.234 h 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'h' at position: 4", badItemException.getMessage());

        BadEquationException badEquationException = assertThrows(BadEquationException.class,
                () -> calculator.calculate("-0.5  234.4 8 0 23 4 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [-0.5]", badEquationException.getMessage());

        BadItemException badItemException1 = assertThrows(BadItemException.class,
                () -> calculator.calculate("-0.5  234.4 9  8 234 + / - ** 0.842384 e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'e8' at position: 11", badItemException1.getMessage());

        LackOfArgumentsException notEnoughArgumentsInFunctionException
                = assertThrows(LackOfArgumentsException.class,
                () -> calculator.calculate("-0.5 234.4 9 8 234 + / - ** 0.842384e8 / 5e-8 + 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Lack of arguments for: / at position: 18", notEnoughArgumentsInFunctionException.getMessage());

        LackOfArgumentsException lackOfArgumentsException = assertThrows(LackOfArgumentsException.class,
                () -> calculator.calculate("-0.5 234.4 9 7 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / +"));
        assertEquals("Lack of arguments for: + at position: 20", lackOfArgumentsException.getMessage());

        BadEquationException constructingFunctionsException
                = assertThrows(BadEquationException.class,
                () -> calculator.calculate("-0.5 23 234.4 9 8 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [-0.5]", constructingFunctionsException.getMessage());

    }

    @Test
    public void calculateEmpty() throws CalculatorException {
        EmptyEquationException emptyEquationException = assertThrows(EmptyEquationException.class, () -> calculator.calculate(""));
        assertEquals("Empty equation", emptyEquationException.getMessage());
        assertEquals(new BigDecimal(2), calculator.calculate("2"));
    }

//    @Test
//    public void calculateInnerFunctions() throws CalculatorException {
//        Apfloat radians = ApfloatMath.toRadians(new Apfloat(90, 10));
//        assertEquals(new BigDecimal(1), calculator.calculate(radians + " sin"));
//        assertEquals(new BigDecimal("1.570796326"), calculator.calculate("90 toRadians"));
//        assertEquals(new BigDecimal(1), calculator.calculate("90 toRadians sin"));
//    }

//    @Test
//    public void calculateAllStaticMethods() throws CalculatorException {
//        assertEquals(new BigDecimal("4.49980967"), calculator.calculate("90 log"));
//
//        CalculatorArithmeticException calculatorArithmeticException1
//                = assertThrows(CalculatorArithmeticException.class, () -> calculator.calculate("0 log"));
//        assertEquals("Logarithm of zero", calculatorArithmeticException1.getMessage());
//
//        assertEquals(new BigDecimal(0), calculator.calculate("0 atanh"));
//
//        CalculatorArithmeticException calculatorArithmeticException
//                = assertThrows(CalculatorArithmeticException.class, () -> calculator.calculate("10 atanh"));
//        assertEquals("Logarithm of negative number; result would be complex", calculatorArithmeticException.getMessage());
//
//        assertEquals(new BigDecimal(1), calculator.calculate("0 cos"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 atan"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 cbrt"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 tanh"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 sqrt"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 sin"));
//        assertEquals(new BigDecimal(1), calculator.calculate("0 exp"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 frac"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 tan"));
//        assertEquals(new BigDecimal("3.626860407"), calculator.calculate("2 sinh"));
//        assertEquals(new BigDecimal("1.762747174"), calculator.calculate("3 acosh"));
//        assertEquals(new BigDecimal("2.864788975e2"), calculator.calculate("5 toDegrees"));
//        assertEquals(new BigDecimal("1.047197551"), calculator.calculate("0.5 acos"));
//        assertEquals(new BigDecimal("8.726646259e-2"), calculator.calculate("5 toRadians"));
//        assertEquals(new BigDecimal(1), calculator.calculate("0 cosh"));
//        assertEquals(new BigDecimal(10), calculator.calculate("-10 abs"));
//        assertEquals(new BigDecimal(-5), calculator.calculate("5 negate"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 w"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 asin"));
//        assertEquals(new BigDecimal(0), calculator.calculate("0 asinh"));
//        assertEquals(new BigDecimal("2.4e1"), calculator.calculate("5 gamma"));
//    }

    @Test
    public void calculateCompareAvailableFunctions() {
        List<String> functions = Arrays.asList("**", "fun2", "e", "*", "+", "-", "/", "−", "÷", "×", "pi", "^", "fun");
        assertEquals(functions, new ArrayList<>(calculator.getContext().getFunctions().keySet()));
    }

    @Test
    public void calculateCompareOthers() {
        assertEquals(calculator.getContext().getFunctions().keySet(), calculator.getContext().getFunctions().keySet());
        assertEquals(10, calculator.getContext().getPrecision());
    }

    @Test
    public void calculateConstants() throws CalculatorException {
        assertEquals(new BigDecimal("6.28"), calculator.calculate("pi 2 *"));
        assertEquals(new BigDecimal("3.14"), calculator.calculate("pi"));
        assertEquals(new BigDecimal("2.718281827999999844536205273470841348171234130859375"), calculator.calculate("e"));
        assertEquals(new BigDecimal("1.359140913999999922268102636735420674085617065429688"), calculator.calculate("e 2 /"));
    }
}