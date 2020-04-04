package io.github.aangiel.rpn;

import org.apfloat.Apfloat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculatorSupplierTest {

    @Test(expected = ArithmeticException.class)
    public void testCalculator() {
        Calculator<Apfloat> calculator = CalculatorSupplier.INSTANCE.getCalculator(Apfloat.class);
        assertEquals(new Apfloat(14), calculator.calculate("2 *"));
    }
}