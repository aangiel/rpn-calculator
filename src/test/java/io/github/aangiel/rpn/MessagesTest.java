package io.github.aangiel.rpn;

import io.github.aangiel.rpn.translation.Languages;
import io.github.aangiel.rpn.translation.Messages;
import io.github.aangiel.translator.MessageTranslator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessagesTest {

    @Before
    public void setUp() {
        MessageTranslator.setLanguage(Languages.EN);
    }

    @Test
    public void polishLanguage() {
        MessageTranslator.setLanguage(Languages.PL);
        assertEquals("Puste równanie", Messages.EMPTY_EQUATION.get());
        assertEquals("różna", MessageTranslator.getMessage("DIFFERENT"));
        assertEquals("different", MessageTranslator.getMessage(Languages.EN, "DIFFERENT"));
        assertEquals("different", MessageTranslator.getMessage("en", Messages.DIFFERENT));
    }

    @Test
    public void englishLanguage() {
        MessageTranslator.setLanguage(Languages.EN);
        assertEquals("Empty equation", Messages.EMPTY_EQUATION.get());
        assertEquals("different", MessageTranslator.getMessage("DIFFERENT"));
        assertEquals("różna", MessageTranslator.getMessage(Languages.PL, "DIFFERENT"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoResourcesFile() {
        MessageTranslator.setLanguage(Languages.FR);
        Messages.EMPTY_EQUATION.get();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noLanguage() {
        var message1 = MessageTranslator.getMessage("de", "DIFFERENT");
        var message2 = MessageTranslator.getMessage("de", "EMPTY_EQUATION");
        var message3 = MessageTranslator.getMessage("de", "LEFT_ON_STACK");
        var message4 = MessageTranslator.getMessage("de", "UNSUPPORTED_TYPE");
        System.out.println(String.join(",", message1, message2, message3, message4));
    }

    @Test
    public void createMessages() {
        MessageTranslator.setLanguage(Languages.EN);
        assertEquals("Lack of arguments for: + at position: 2", MessageTranslator.createMessage("LACK_OF_ARGUMENTS", "+", 2));
        assertEquals("Lack of arguments for: + at position: 2", MessageTranslator.createMessage(Messages.LACK_OF_ARGUMENTS, "+", 2));
        assertEquals("Zły obiekt: '+' na pozycji: 2", MessageTranslator.createMessage(Languages.PL, "BAD_ITEM", "+", 2));
    }
}