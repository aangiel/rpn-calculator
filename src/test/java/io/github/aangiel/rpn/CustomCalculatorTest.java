package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.CalculatorContext;
import io.github.aangiel.rpn.exception.CalculatorException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CustomCalculatorTest {

    Calculator<BigInteger> calculator;

    @Before
    public void setUp() {
        CalculatorSupplier.INSTANCE.addCalculator(BigInteger.class, new BigIntegerContext22());
        calculator = CalculatorSupplier.INSTANCE.getCalculator(BigInteger.class);
    }

    @Test
    public void calculate() throws CalculatorException {
        assertEquals(new BigInteger("5"), calculator.calculate("2 3 +"));
    }

    @Test
    public void checkFunctions() {
        assertEquals("[+]", calculator.getContext().getAvailableFunctionsAndOperators().toString());
    }

    @Test
    public void unsupportedType() {
        UnsupportedOperationException unsupportedOperationException
                = assertThrows(UnsupportedOperationException.class
                , () -> CalculatorSupplier.INSTANCE.getCalculator(Integer.class));
        assertEquals("Unsupported type: class java.lang.Integer", unsupportedOperationException.getMessage());

    }

    public static class BigIntegerContext22 extends CalculatorContext<BigInteger> {

        @Override
        protected void populateDefaultOperations() {
            addFunctionOrOperator("+", 2, args -> args.get(0).add(args.get(1)));
        }

        @Override
        protected void populateConstants() {
        }

        @Override
        public Function<String, BigInteger> getConstructor() {
            return BigInteger::new;
        }

        @Override
        protected CalculatorContext<BigInteger> self() {
            return this;
        }

        @Override
        protected void populateDefaultOneParameterMathFunctions() {
        }

    }
}
