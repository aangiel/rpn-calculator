package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.AbstractCalculatorContext;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Set;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CustomCalculatorTest {

    Calculator<BigInteger> calculator;

    @Before
    public void setUp() {
        CalculatorSupplier.INSTANCE.addCalculator(BigInteger.class, new BigIntegerContext());
        calculator = CalculatorSupplier.INSTANCE.getCalculator(BigInteger.class);
    }

    @Test
    public void calculate() {
        assertEquals(new BigInteger("5"), calculator.calculate("2 3 +"));
    }

    @Test
    public void checkFunctions() {
        var functions = Set.of("+");
        assertEquals(functions, calculator.getContext().getAvailableFunctionsAndOperators());
    }

    @Test
    public void unsupportedType() {
        IllegalArgumentException unsupportedOperationException
                = assertThrows(IllegalArgumentException.class
                , () -> CalculatorSupplier.INSTANCE.getCalculator(Integer.class));
        assertEquals("Unsupported type: class java.lang.Integer", unsupportedOperationException.getMessage());

    }

    @Test(expected = NullPointerException.class)
    public void nullCalculator() {
        CalculatorSupplier.INSTANCE.getCalculator(null);
    }

    public static class BigIntegerContext extends AbstractCalculatorContext<BigInteger> {

        @Override
        protected void populateDefaultOperations() {
            addFunctionOrOperator("+", args -> args.remove(1).add(args.pop()));
        }

        @Override
        protected void populateConstants() {
        }

        @Override
        public Function<String, BigInteger> getNumberConstructor() {
            return BigInteger::new;
        }

        @Override
        public BigIntegerContext self() {
            return this;
        }

        @Override
        protected void populateMathFunctions() {
        }

    }
}
