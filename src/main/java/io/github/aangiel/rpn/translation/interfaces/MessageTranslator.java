package io.github.aangiel.rpn.translation.interfaces;

import io.github.aangiel.rpn.CalculatorSupplier;
import io.github.aangiel.rpn.translation.Languages;
import io.github.aangiel.rpn.translation.MessagesSupplier;

import java.util.Map;
import java.util.Objects;

public interface MessageTranslator {

    default String get() {
        return getMessage(getName());
    }

    /**
     * @return name of Enum
     * @implSpec {@code return name();}
     */
    String getName();

    default String get(Object... args) {
        return String.format(get(), args);
    }

    static String createMessage(String key, Object... args) {
        return String.format(getMessage(key), args);
    }

    static String createMessage(Language languages, String key, Object... args) {
        return String.format(getMessage(languages, key), args);
    }

    static String getMessage(String key) {
        return getMessage(getLanguage(), key);
    }

    static String getMessage(String language, String key) {
        return getMessage(Languages.valueOf(language.toUpperCase()), key);
    }

    static String getMessage(Language languages, String key) {
        Objects.requireNonNull(key);

        var properties = getMessages().get(languages);
        if (properties == null)
            throw new IllegalArgumentException("No translation file for given language");

        var result = properties.get(key);
        if (result == null)
            throw new IllegalArgumentException("No message for key");

        return result;
    }

    static Map<Language, Map<String, String>> getMessages() {
        return MessagesSupplier.getMessages();
    }

    static Language getLanguage() {
        return CalculatorSupplier.INSTANCE.getLanguage();
    }
}
