package io.github.aangiel.rpn;

import io.github.aangiel.rpn.translation.Language;
import io.github.aangiel.rpn.translation.MessageTranslator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageTranslatorTest {

    @Test
    public void polishLanguage() {
        CalculatorSupplier.INSTANCE.setLanguage(Language.PL);
        assertEquals("Puste równanie", MessageTranslator.EMPTY_EQUATION.get());
        assertEquals("różna", MessageTranslator.getMessage("DIFFERENT"));
    }

    @Test
    public void englishLanguage() {
        CalculatorSupplier.INSTANCE.setLanguage(Language.EN);
        assertEquals("Empty equation", MessageTranslator.EMPTY_EQUATION.get());
        assertEquals("different", MessageTranslator.getMessage("DIFFERENT"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoResourcesFile() {
        CalculatorSupplier.INSTANCE.setLanguage(Language.FR);
        MessageTranslator.EMPTY_EQUATION.get();
    }
}