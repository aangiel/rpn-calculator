package io.github.aangiel.rpn;

import io.github.aangiel.rpn.concurrent.CalculatorCallable;
import io.github.aangiel.rpn.translation.Languages;
import io.github.aangiel.rpn.translation.Messages;
import io.github.aangiel.rpn.translator.MessageTranslator;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ApfloatCalculatorTest {

    Calculator<Apfloat> calculator;

    @Before
    public void setUp() {
        MessageTranslator.setLanguage(Languages.EN);
        MessageTranslator.setAnyMessage(Messages.DIFFERENT);
        calculator = CalculatorSupplier.INSTANCE.getCalculator(Apfloat.class);

        calculator.getContext().addFunctionOrOperator("**", (a) -> a.get(1).multiply(a.remove(1).multiply(a.pop())))
                .addFunctionOrOperator("^", (a) -> a.get(1).divide(a.remove(1).multiply(a.pop())))
                .addFunctionOrOperator("fun", (a) -> a.remove(2).multiply(a.remove(1)).multiply(a.pop()))
                .addFunctionOrOperator("fun2", (a) -> a.remove(3).multiply(a.remove(2)).multiply(a.remove(1)).subtract(a.pop()))

                /*
                 * Operations added from example: https://en.wikipedia.org/wiki/Reverse_Polish_notation
                 */
                .addFunctionOrOperator("−", a -> a.remove(1).subtract(a.pop()))
                .addFunctionOrOperator("÷", a -> a.remove(1).divide(a.pop()))
//      .addFunction("+", a -> a.get(0).add(a.get(1)))
                .addFunctionOrOperator("×", a -> a.remove(1).multiply(a.pop()));
    }

    @Test
    public void calculateCorrectEquations() {
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
    public void calculateBadEquation() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calculator.calculate("12 2 3 4 * 10 5 / + * + 3"));
        assertEquals("Left on stack: [4e1]", exception.getMessage());
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
        assertEquals(new Apfloat(10), calculator.calculate("5 1 2 ** 4 * + 3 -"));
        assertEquals(new Apfloat(-2.875), calculator.calculate("5 1 2 ** 4 * ^ 3 -"));

    }

    @Test
    public void calculateWithCustomFunction() {
        assertEquals(new Apfloat(98), calculator.calculate("5 1 4 3 2 fun * 4 * + 3 -"));
        assertEquals(new Apfloat(98), calculator.calculate("5 1  4  3  2 fun * 4 * + 3 -"));
        assertEquals(new Apfloat(102), calculator.calculate("5 1 2 3 4 fun * 4 * + 3.5 0 0 1 fun2 -"));
        assertEquals(new Apfloat(102), calculator.calculate("5 1 24 * 4 * + -1 -"));
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
        assertEquals(new Apfloat(17.54733345), calculator.calculate("12 23.234134 0.34234 12.2344534 * / +"));
    }

    @Test
    public void calculateWithMathFunctions() {
        assertEquals(new Apfloat(5.34323729e12), calculator.calculate("23 tanh 30 cosh *"));
    }

    @Test
    public void calculateHardOne() {
        assertEquals(new Apfloat(-5.068680686e-9),
                calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));

        IllegalArgumentException badItemException = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 24.234 tan h 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'h' at position: 5", badItemException.getMessage());
//        assertEquals("h", badItemException.getItem());
//        assertEquals(5, badItemException.getPosition());

        IllegalArgumentException badEquationException = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 23 4 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [-5e-1]", badEquationException.getMessage());
//        assertTrue(Set.of(new Apfloat("-5e-1")).containsAll(badEquationException.getStack()));

        IllegalArgumentException badItemException1 = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384 e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'e8' at position: 12", badItemException1.getMessage());

        ArithmeticException notEnoughArgumentsInFunctionException
                = assertThrows(ArithmeticException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Lack of arguments for: / at position: 19", notEnoughArgumentsInFunctionException.getMessage());
//        assertEquals("/", notEnoughArgumentsInFunctionException.getItem());
//        assertEquals(19, notEnoughArgumentsInFunctionException.getPosition());

        ArithmeticException lackOfArgumentsException = assertThrows(ArithmeticException.class,
                () -> calculator.calculate("-0.5 23 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / +"));
        assertEquals("Lack of arguments for: + at position: 21", lackOfArgumentsException.getMessage());

        IllegalArgumentException constructingFunctionsException
                = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 4 24.234 tanh 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [-5e-1]", constructingFunctionsException.getMessage());

    }

    @Test
    public void calculateEmpty() {
        IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class, () -> calculator.calculate("   "));
        assertEquals("Empty equation", illegalArgumentException.getMessage());

        assertThrows(NullPointerException.class, () -> calculator.calculate(null));

        assertEquals(new Apfloat(2), calculator.calculate("2"));
    }

    @Test
    public void calculateInnerFunctions() {
        Apfloat radians = ApfloatMath.toRadians(new Apfloat(90, 10));
        assertEquals(new Apfloat(1), calculator.calculate(radians + " sin"));
        assertEquals(new Apfloat(1.570796326), calculator.calculate("90 toRadians"));
        assertEquals(new Apfloat(1), calculator.calculate("90 toRadians sin"));
    }

    @Test
    public void calculateAllStaticMethods() {
        assertEquals(new Apfloat(4.49980967), calculator.calculate("90 log"));

        ArithmeticException arithmeticException1
                = assertThrows(ArithmeticException.class, () -> calculator.calculate("0 log"));
        assertEquals("Logarithm of zero", arithmeticException1.getMessage());

        assertEquals(new Apfloat(0), calculator.calculate("0 atanh"));

        ArithmeticException arithmeticException
                = assertThrows(ArithmeticException.class, () -> calculator.calculate("10 atanh"));
        assertEquals("Logarithm of negative number; result would be complex", arithmeticException.getMessage());

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
        assertEquals(new Apfloat("5.957966013"), calculator.calculate("5 7 agm"));
        assertEquals(new Apfloat("41"), calculator.calculate("5 7 2 3 multiplyAdd"));
    }

    @Test
    public void calculateCompareAvailableFunctions() {
        var functions = Set.of("logWithBase", "**", "log", "atanh", "copySign", "cos", "agm", "multiplySubtract", "atan", "cbrt", "tanh", "−", "min", "multiplyAdd", "sqrt", "×", "sin", "pow", "exp", "frac", "^", "atan2", "tan", "fun2", "sinh", "max", "e", "acosh", "*", "toDegrees", "+", "acos", "toRadians", "fmod", "-", "/", "cosh", "abs", "negate", "w", "÷", "pi", "asin", "asinh", "gamma", "fun");
        assertEquals(functions, calculator.getContext().getAvailableFunctionsAndOperators());
    }

    @Test
    public void calculateConstants() {
        assertEquals(new Apfloat(6.283185307), calculator.calculate("pi 2 *"));
        assertEquals(new Apfloat(3.141592653), calculator.calculate("pi"));
        assertEquals(new Apfloat(Math.E), calculator.calculate("e"));
        assertEquals(new Apfloat("1.359140914"), calculator.calculate("e 2 /"));
    }

//    @Test
    public void performance() {
//        multiThread();
        IntStream.range(0, 4).forEach(e -> {
            oneThread();
            multiThread();
        });
        IntStream.range(0, 4).forEach(e -> oneThread());
        IntStream.range(0, 8).forEach(e -> multiThread());
    }

    public void oneThread() {
        long start = System.nanoTime();
        Apfloat expected = new Apfloat("-1.0026019421e-8");
        String equation = "-0.5 23 24.234 8 * 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / -0.5 23 24.234 9 * 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / +";
        for (int i = 0; i < 10000; i++) {
            assertEquals(expected, calculator.calculate(equation));
        }
        long end = System.nanoTime();
        System.out.println(String.format("One thread performance: %s ms", (end - start) / 1_000_000));
    }

    public void multiThread() {
        long start = System.nanoTime();

        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);

        String equation = "-0.5 23 24.234 8 * 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / -0.5 23 24.234 9 * 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / +";
        List<Callable<Apfloat>> tasks = IntStream.range(0, 10000)
                .mapToObj(i -> CalculatorCallable.of(Apfloat.class, equation))
                .collect(Collectors.toList());
        List<Future<Apfloat>> results = null;
        try {
            results = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Apfloat expected = new Apfloat("-1.0026019421e-8");

        assert results != null;
        for (Future<Apfloat> result : results) {
            try {
                assertEquals(expected, result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        long end = System.nanoTime();
        System.out.println(String.format("Multi-thread performance: %s ms", (end - start) / 1_000_000));
    }
}