package io.github.aangiel.rpn;

import io.github.aangiel.rpn.concurrent.CalculatorCallable;
import io.github.aangiel.rpn.translation.Languages;
import io.github.aangiel.rpn.translation.Messages;
import io.github.aangiel.translator.MessageTranslator;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class BigDecimalCalculatorTest {

    Calculator<BigDecimal> calculator;

    @Before
    public void setUp() {
        MessageTranslator.setLanguage(Languages.EN);
        MessageTranslator.setAnyMessage(Messages.EMPTY_EQUATION);
        calculator = CalculatorSupplier.INSTANCE.getCalculator(BigDecimal.class);

        calculator.getContext().addFunctionOrOperator("**", (a) -> a.get(1).multiply(a.remove(1).multiply(a.pop())))
                .addFunctionOrOperator("^", (a) -> a.get(1).divide(a.remove(1).multiply(a.pop()), 3, RoundingMode.DOWN))
                .addFunctionOrOperator("fun", (a) -> a.remove(2).multiply(a.remove(1)).multiply(a.pop()))
                .addFunctionOrOperator("fun2", (a) -> a.remove(3).multiply(a.remove(2)).multiply(a.remove(1)).subtract(a.pop()));

        /*
         * Operations added from example: https://en.wikipedia.org/wiki/Reverse_Polish_notation
         */
        calculator.getContext().addFunctionOrOperator("−", a -> a.remove(1).subtract(a.pop()));
        calculator.getContext().addFunctionOrOperator("÷", a -> a.remove(1).divide(a.pop(), 3, RoundingMode.DOWN));
//      calculator.getContext().addFunction("+", a -> a.get(0).add(a.get(1)));
        calculator.getContext().addFunctionOrOperator("×", a -> a.remove(1).multiply(a.pop()));
    }

    @Test
    public void calculateCorrectEquations() {
        assertEquals(new BigDecimal(14), calculator.calculate("5 1 2 + 4 * + 3 -"));
        assertEquals(new BigDecimal(40), calculator.calculate("12 2 3 4 * 10 5 / + * +"));
        assertEquals(new BigDecimal(4), calculator.calculate("2 2 +"));
//        assertEquals(new BigDecimal(-0.448073616), calculator.calculate("90 cos"));
        assertEquals(new BigDecimal(5), calculator.calculate("15 7 1 1 + - / 3 * 2 1 1 + + -"));

        /*
         * Example copied from https://en.wikipedia.org/wiki/Reverse_Polish_notation
         * Operands added in setUp method
         */
        assertEquals(new BigDecimal("5.000"), calculator.calculate("15 7 1 1 + − ÷ 3 × 2 1 1 + + −"));
    }

    @Test
    public void calculateBadEquation() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calculator.calculate("12 2 3 4 * 10 5 / + * + 3"));
        assertEquals("Left on stack: [40]", exception.getMessage());
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
        assertEquals(new BigDecimal(10), calculator.calculate("5 1 2 ** 4 * + 3 -"));
        assertEquals(BigDecimal.valueOf(-2.875), calculator.calculate("5 1 2 ** 4 * ^ 3 -"));

    }

    @Test
    public void calculateWithCustomFunction() {
        assertEquals(new BigDecimal(98), calculator.calculate("5 1 4 3 2 fun * 4 * + 3 -"));
        assertEquals(new BigDecimal(98), calculator.calculate("5 1  4  3  2 fun * 4 * + 3 -"));
        assertEquals(new BigDecimal("102.0"), calculator.calculate("5 1 2 3 4 fun * 4 * + 3.5 0 0 1 fun2 -"));
        assertEquals(new BigDecimal(102), calculator.calculate("5 1 24 * 4 * + -1 -"));
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
    public void calculateFloatingPoint() {
        assertEquals(new BigDecimal("17.547334"), calculator.calculate("12 23.234134 0.34234 12.2344534 * / +"));
    }

    @Test
    public void calculateHardOne() {
        assertEquals(BigDecimal.valueOf(-0.00042863),
                calculator.calculate("-0.5  234.4 3 234 + / - 9 ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));

        IllegalArgumentException badItemException = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 24.234 h 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'h' at position: 4", badItemException.getMessage());

        IllegalArgumentException badEquationException = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5  234.4 8 0 23 4 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [-0.5]", badEquationException.getMessage());

        IllegalArgumentException badItemException1 = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5  234.4 9  8 234 + / - ** 0.842384 e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Bad item: 'e8' at position: 11", badItemException1.getMessage());

        ArithmeticException notEnoughArgumentsInFunctionException
                = assertThrows(ArithmeticException.class,
                () -> calculator.calculate("-0.5 234.4 9 8 234 + / - ** 0.842384e8 / 5e-8 + 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Lack of arguments for: / at position: 18", notEnoughArgumentsInFunctionException.getMessage());

        ArithmeticException lackOfArgumentsException = assertThrows(ArithmeticException.class,
                () -> calculator.calculate("-0.5 234.4 9 7 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / +"));
        assertEquals("Lack of arguments for: + at position: 20", lackOfArgumentsException.getMessage());

        IllegalArgumentException constructingFunctionsException
                = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate("-0.5 23 234.4 9 8 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 /"));
        assertEquals("Left on stack: [-0.5]", constructingFunctionsException.getMessage());

    }

    @Test
    public void calculateEmpty() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> calculator.calculate(""));
        assertEquals("Empty equation", illegalArgumentException.getMessage());

        assertThrows(NullPointerException.class, () -> calculator.calculate(null));

        assertEquals(new BigDecimal(2), calculator.calculate("2"));
    }

    @Test
    public void calculateCompareAvailableFunctions() {
        Set<String> functions = Set.of("**", "fun2", "e", "*", "+", "-", "/", "−", "÷", "×", "pi", "^", "fun");
        assertEquals(functions, calculator.getContext().getAvailableFunctionsAndOperators());
    }

    @Test
    public void calculateConstants() {
        assertEquals(new BigDecimal(String.valueOf(2 * Math.PI)), calculator.calculate("pi 2 *"));
        assertEquals(new BigDecimal(String.valueOf(Math.PI)), calculator.calculate("pi"));
        assertEquals(new BigDecimal(String.valueOf(Math.E)), calculator.calculate("e"));
        assertEquals(new BigDecimal("1.359140914229523"), calculator.calculate("e 2 /"));
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
        BigDecimal expected = new BigDecimal("-8.6E-7");
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

        List<Future<BigDecimal>> results = new ArrayList<>(10000);
        String equation = "-0.5 23 24.234 8 * 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / -0.5 23 24.234 9 * 234.4 234 + / - ** 0.842384e8 / 5e-8 + 5 0 3.5e-8 23.33 fun2 / +";
        for (int i = 0; i < 10000; i++) {
            Future<BigDecimal> submit = executor.submit(CalculatorCallable.of(BigDecimal.class, equation));
            results.add(submit);
        }
        long middle = System.nanoTime();
        System.out.println(String.format("Multi-thread performance (start to middle): %s ms", (middle - start) / 1_000_000));
        BigDecimal expected = new BigDecimal("-8.6E-7");

        for (int i = 0; i < 10000; i++) {
            try {
                assertEquals(expected, results.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        long end = System.nanoTime();
        System.out.println(String.format("Multi-thread performance (start to end): %s ms", (end - start) / 1_000_000));
        System.out.println(results.size());
    }
}