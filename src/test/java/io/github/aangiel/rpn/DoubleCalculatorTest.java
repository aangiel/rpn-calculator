package io.github.aangiel.rpn;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class DoubleCalculatorTest {

    Calculator<Double> calculator;

    @Before
    public void setUp() {
        calculator = CalculatorSupplier.INSTANCE.getCalculator(Double.class);

        calculator.getContext().addFunctionOrOperator("**", 2, (a) -> a.get(0) * (a.get(1) * (a.get(0))));
        calculator.getContext().addFunctionOrOperator("^", 2, (a) -> a.get(0) / (a.get(1) * (a.get(0))));
        calculator.getContext().addFunctionOrOperator("fun", 3, (a) -> a.get(0) * (a.get(1)) * (a.get(2)));
        calculator.getContext().addFunctionOrOperator("fun2", 4, (a) -> a.get(0) * (a.get(1)) * (a.get(2)) - (a.get(3)));

        /*
         * Operations added from example: https://en.wikipedia.org/wiki/Reverse_Polish_notation
         */
        calculator.getContext().addFunctionOrOperator("−", 2, a -> a.get(0) - (a.get(1)));
        calculator.getContext().addFunctionOrOperator("÷", 2, a -> a.get(0) / (a.get(1)));
//      calculator.getContext().addFunction("+", 2, a -> a.get(0).add(a.get(1)));
        calculator.getContext().addFunctionOrOperator("×", 2, a -> a.get(0) * (a.get(1)));
    }

    @Test
    public void calculateCorrectEquations() {
        assertEquals(Double.valueOf(14.0), calculator.calculate("5 1 2 + 4 * + 3 -"));
        assertEquals(Double.valueOf(40), calculator.calculate("12 2 3 4 * 10 5 / + * +"));
        assertEquals(Double.valueOf(4), calculator.calculate("2 2 +"));
//        assertEquals(Double.valueOf(-0.448073616), calculator.calculate("90 cos"));
        assertEquals(Double.valueOf(5), calculator.calculate("15 7 1 1 + - / 3 * 2 1 1 + + -"));

        /*
         * Example copied from https://en.wikipedia.org/wiki/Reverse_Polish_notation
         * Operands added in setUp method
         */
        assertEquals(Double.valueOf(5), calculator.calculate("15 7 1 1 + − ÷ 3 × 2 1 1 + + −"));
    }

    @Test
    public void calculateBadEquation() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calculator.calculate("12 2 3 4 * 10 5 / + * + 3"));
        assertEquals("Left on stack: [40.0]", exception.getMessage());
    }

    @Test
    public void calculateIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calculator.calculate("12 2 3 4 * 10 5 / + * ++"));
        assertEquals("Bad item: '++' at position: 11", exception.getMessage());
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> calculator.calculate("12 2 3 sinx 4 * 10 5 / + * +"));
        assertEquals("Bad item: 'sinx' at position: 4", exception1.getMessage());

    }

    @Test
    public void calculateLackOfArgumentsException() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> calculator.calculate("12 * 2 3 4 * 10 5 / + * +"));
        assertEquals("Lack of arguments for: * at position: 2", exception.getMessage());
    }

    @Test
    public void calculateWithCustomOperation() {
        assertEquals(Double.valueOf(10), calculator.calculate("5 1 2 ** 4 * + 3 -"));
        assertEquals(Double.valueOf(-2.875), calculator.calculate("5 1 2 ** 4 * ^ 3 -"));

    }

    @Test
    public void calculateWithCustomFunction() {
        assertEquals(Double.valueOf(98), calculator.calculate("5 1 4 3 2 fun * 4 * + 3 -"));
        assertEquals(Double.valueOf(98), calculator.calculate("5 1  4  3  2 fun * 4 * + 3 -"));
        assertEquals(Double.valueOf(102), calculator.calculate("5 1 2 3 4 fun * 4 * + 3.5 0 0 1 fun2 -"));
        assertEquals(Double.valueOf(102), calculator.calculate("5 1 24 * 4 * + -1 -"));
    }

    @Test
    public void calculateWithCustomFunctionIllegalArgumentException() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> calculator.calculate("5 1 4 d 2 fun * 4 * + 3 -"));
        assertEquals("Bad item: 'd' at position: 4", exception.getMessage());
    }

    @Test
    public void calculateWithCustomFunctionNotEnoughArgumentsInFunctionException() {
        ArithmeticException exception =
                assertThrows(
                        ArithmeticException.class,
                        () -> calculator.calculate("5 1 3 2 fun * 4 * + 3 -"));
        assertEquals("Lack of arguments for: + at position: 9", exception.getMessage());
    }

    @Test
    public void calculateConstructingFunctionsException() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> calculator.calculate("08 90 sin"));
        assertEquals("Bad item: '08' at position: 1", exception.getMessage());
    }

    @Test
    public void calculateFloatingPoint() {
        assertEquals(Double.valueOf(17.54733345318171), calculator.calculate("12 23.234134 0.34234 12.2344534 * / +"));
    }

    @Test
    public void calculateWithMathFunctions() {
        assertEquals(Double.valueOf(5.343237290762231E12), calculator.calculate("23 tanh 30 cosh *"));
    }

    @Test
    public void calculateHardOne() {
        assertEquals(Double.valueOf(-5.068680686506064E-9),
                calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));

        IllegalArgumentException badItemException = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 24.234 tan h 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'h' at position: 5", badItemException.getMessage());

        IllegalArgumentException badEquationException = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 23 4 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [-0.5]", badEquationException.getMessage());

        IllegalArgumentException badItemException1 = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384 e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'e8' at position: 12", badItemException1.getMessage());

        ArithmeticException notEnoughArgumentsInFunctionException
                = assertThrows(ArithmeticException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Lack of arguments for: / at position: 19", notEnoughArgumentsInFunctionException.getMessage());

        ArithmeticException lackOfArgumentsException = assertThrows(ArithmeticException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / +"));
        assertEquals("Lack of arguments for: + at position: 21", lackOfArgumentsException.getMessage());

        IllegalArgumentException constructingFunctionsException
                = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 4 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [-0.5]", constructingFunctionsException.getMessage());

    }

    @Test
    public void calculateEmpty() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> calculator.calculate(""));
        assertEquals("Empty equation", illegalArgumentException.getMessage());

        NullPointerException nullPointerException
                = assertThrows(NullPointerException.class, () -> calculator.calculate(null));
        assertEquals("Param 'equation' can't be null", nullPointerException.getMessage());

        assertEquals(Double.valueOf(2), calculator.calculate("2"));
    }

    @Test
    public void calculateInnerFunctions() {
        Apfloat radians = ApfloatMath.toRadians(new Apfloat(90, 10));
        assertEquals(Double.valueOf(1), calculator.calculate(radians + " sin"));
        assertEquals(Double.valueOf(1.5707963267948966), calculator.calculate("90 toRadians"));
        assertEquals(Double.valueOf(1), calculator.calculate("90 toRadians sin"));
    }

    @Test
    public void calculateAllStaticMethods() {
        assertEquals(Double.valueOf(4.499809670330265), calculator.calculate("90 log"));

//        CalculatorArithmeticException calculatorArithmeticException1
//                = assertThrows(CalculatorArithmeticException.class, () -> calculator.calculate("0 log"));
//        assertEquals("Logarithm of zero", calculatorArithmeticException1.getMessage());

//        assertEquals(Double.valueOf(0), calculator.calculate("0 atanh"));
//
//        CalculatorArithmeticException calculatorArithmeticException
//                = assertThrows(CalculatorArithmeticException.class, () -> calculator.calculate("10 atanh"));
//        assertEquals("Logarithm of negative number; result would be complex", calculatorArithmeticException.getMessage());

        assertEquals(Double.valueOf(1), calculator.calculate("0 cos"));
        assertEquals(Double.valueOf(0), calculator.calculate("0 atan"));
        assertEquals(Double.valueOf(0), calculator.calculate("0 cbrt"));
        assertEquals(Double.valueOf(0), calculator.calculate("0 tanh"));
        assertEquals(Double.valueOf(0), calculator.calculate("0 sqrt"));
        assertEquals(Double.valueOf(0), calculator.calculate("0 sin"));
        assertEquals(Double.valueOf(1), calculator.calculate("0 exp"));
//        assertEquals(Double.valueOf(0), calculator.calculate("0 frac"));
        assertEquals(Double.valueOf(0), calculator.calculate("0 tan"));
        assertEquals(Double.valueOf(3.626860407847019), calculator.calculate("2 sinh"));
//        assertEquals(Double.valueOf(1.762747174), calculator.calculate("3 acosh"));
        assertEquals(Double.valueOf(286.4788975654116), calculator.calculate("5 toDegrees"));
        assertEquals(Double.valueOf(1.0471975511965979), calculator.calculate("0.5 acos"));
        assertEquals(Double.valueOf(0.08726646259971647), calculator.calculate("5 toRadians"));
        assertEquals(Double.valueOf(1), calculator.calculate("0 cosh"));
        assertEquals(Double.valueOf(10), calculator.calculate("-10 abs"));
//        assertEquals(Double.valueOf(-5), calculator.calculate("5 negate"));
//        assertEquals(Double.valueOf(0), calculator.calculate("0 w"));
        assertEquals(Double.valueOf(0), calculator.calculate("0 asin"));
//        assertEquals(Double.valueOf(0), calculator.calculate("0 asinh"));
//        assertEquals(Double.valueOf(2.4e1), calculator.calculate("5 gamma"));
    }

    @Test
    public void calculateCompareAvailableFunctions() {
        var functions = Set.of("**", "log", "copySign", "cos", "log10", "expm1", "log1p", "rint", "nextAfter", "atan", "cbrt", "tanh", "nextUp", "−", "min", "sqrt", "hypot", "×", "sin", "pow", "exp", "floor", "^", "atan2", "tan", "signum", "fun2", "sinh", "max", "e", "*", "toDegrees", "nextDown", "+", "acos", "toRadians", "ceil", "-", "/", "cosh", "IEEEremainder", "abs", "÷", "ulp", "pi", "asin", "fma", "fun");
        assertEquals(functions, calculator.getContext().getAvailableFunctionsAndOperators());
    }

    @Test
    public void calculateConstants() {
        assertEquals(Double.valueOf(2 * Math.PI), calculator.calculate("pi 2 *"));
        assertEquals(Double.valueOf(Math.PI), calculator.calculate("pi"));
        assertEquals(Double.valueOf(Math.E), calculator.calculate("e"));
        assertEquals(Double.valueOf(Math.E / 2.0), calculator.calculate("e 2 /"));
    }
}