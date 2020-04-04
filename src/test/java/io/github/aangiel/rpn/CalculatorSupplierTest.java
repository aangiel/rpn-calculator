package io.github.aangiel.rpn;

import io.github.aangiel.rpn.translation.Languages;
import io.github.aangiel.rpn.translation.Messages;
import io.github.aangiel.translator.MessageTranslator;
import org.apfloat.Apfloat;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculatorSupplierTest {

    @BeforeClass
    public static void beforeClass() {
        MessageTranslator.setAnyMessage(Messages.EMPTY_EQUATION);
        MessageTranslator.setLanguage(Languages.EN);
    }

    @Test(expected = ArithmeticException.class)
    public void testCalculator() {
        Calculator<Apfloat> calculator = CalculatorSupplier.INSTANCE.getCalculator(Apfloat.class);
        assertEquals(new Apfloat(14), calculator.calculate("2 *"));
    }
}