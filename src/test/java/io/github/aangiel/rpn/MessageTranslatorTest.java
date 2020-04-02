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
        assertEquals("different", MessageTranslator.getMessage("en", "DIFFERENT"));
    }

    @Test
    public void englishLanguage() {
        CalculatorSupplier.INSTANCE.setLanguage(Language.EN);
        assertEquals("Empty equation", MessageTranslator.EMPTY_EQUATION.get());
        assertEquals("different", MessageTranslator.getMessage("DIFFERENT"));
        assertEquals("różna", MessageTranslator.getMessage("pl", "DIFFERENT"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoResourcesFile() {
        CalculatorSupplier.INSTANCE.setLanguage(Language.FR);
        MessageTranslator.EMPTY_EQUATION.get();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noLanguage() {
        MessageTranslator.getMessage("de", "DIFFERENT");
        MessageTranslator.getMessage("de", "EMPTY_EQUATION");
        MessageTranslator.getMessage("de", "LEFT_ON_STACK");
        MessageTranslator.getMessage("de", "UNSUPPORTED_TYPE");
    }

    @Test
    public void createMessages() {
        CalculatorSupplier.INSTANCE.setLanguage(Language.EN);
        assertEquals("Lack of arguments for: + at position: 2", MessageTranslator.createMessage("LACK_OF_ARGUMENTS", "+", 2));
        assertEquals("Zły obiekt: '+' na pozycji: 2", MessageTranslator.createMessage(Language.PL, "BAD_ITEM", "+", 2));
    }
}