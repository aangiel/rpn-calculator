package io.github.aangiel.rpn;

import io.github.aangiel.rpn.context.impl.AbstractCalculatorContext;
import io.github.aangiel.rpn.translation.Languages;
import io.github.aangiel.rpn.translation.Messages;
import io.github.aangiel.translator.MessageTranslator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Set;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class CustomCalculatorTest {

    static Calculator<BigInteger> calculator;

    @BeforeClass
    public static void beforeClass() {
        MessageTranslator.setLanguage(Languages.EN);
        MessageTranslator.setAnyMessage(Messages.EMPTY_EQUATION);
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

    @Test(expected = IllegalArgumentException.class)
    public void unsupportedType() {
        CalculatorSupplier.INSTANCE.getCalculator(Integer.class);
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
