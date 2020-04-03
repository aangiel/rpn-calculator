package io.github.aangiel.rpn.translation.interfaces;

import io.github.aangiel.rpn.translation.MessagesSupplier;

import java.util.Map;
import java.util.Objects;

public interface MessageTranslator {

    default String get() {
        // It always works IF this interface is implemented by enum,
        // because it's projected to be implemented only by enums.
        @SuppressWarnings("unchecked")
        var key = (Enum<? extends MessageTranslator>) this;
        return getMessage(key);
    }

    default String get(Object... args) {
        return String.format(get(), args);
    }

    static String getMessage(String key) {
        return getMessage(MessagesSupplier.valueOfMessage(key));
    }

    static String getMessage(Enum<? extends Language> language, String key) {
        return getMessage(language, MessagesSupplier.valueOfMessage(key));
    }

    static String getMessage(String language, String key) {
        return getMessage(MessagesSupplier.valueOfLanguage(language), MessagesSupplier.valueOfMessage(key));
    }

    static String getMessage(Enum<? extends MessageTranslator> key) {
        return getMessage(Holder.language, key);
    }

    static String getMessage(String language, Enum<? extends MessageTranslator> key) {
        return getMessage(MessagesSupplier.valueOfLanguage(language), key);
    }

    static String createMessage(Enum<? extends Language> language, String key, Object... args) {
        return createMessage(language, MessagesSupplier.valueOfMessage(key), args);
    }

    static String createMessage(Enum<? extends MessageTranslator> key, Object... args) {
        return String.format(getMessage(key), args);
    }

    static String createMessage(String key, Object... args) {
        return String.format(getMessage(MessagesSupplier.valueOfMessage(key)), args);
    }

    static String createMessage(Enum<? extends Language> language, Enum<? extends MessageTranslator> key, Object... args) {
        return String.format(getMessage(language, key), args);
    }

    static String getMessage(Enum<? extends Language> language, Enum<? extends MessageTranslator> key) {
        Objects.requireNonNull(key);

        var properties = getMessages().get(language);
        if (properties == null)
            throw new IllegalArgumentException("No translation file for given language");

        var result = properties.get(key);
        if (result == null)
            throw new IllegalArgumentException("No message for key");

        return result;
    }

    static Map<Enum<? extends Language>, Map<Enum<? extends MessageTranslator>, String>> getMessages() {
        return MessagesSupplier.getMessages();
    }

    static void setLanguage(Enum<? extends Language> language) {
        Holder.language = language;
    }

    static Enum<? extends Language> getLanguage() {
        return Holder.language;
    }

    static void setAnyMessage(Enum<? extends MessageTranslator> clazz) {
        Holder.messages = clazz;
    }

    static Enum<? extends MessageTranslator> getAnyMessage() {
        return Holder.messages;
    }

    class Holder {
        private static Enum<? extends Language> language;
        private static Enum<? extends MessageTranslator> messages;
    }
}
