package io.github.aangiel.rpn.translation;

import io.github.aangiel.rpn.translation.interfaces.Language;
import io.github.aangiel.rpn.translation.interfaces.MessageTranslator;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class MessagesSupplier {
    private static Path resourcesPath;
    private static Pattern fileNamePattern;
    private static Pattern propertyPattern;
    private static Map<Enum<? extends Language>, Map<Enum<? extends MessageTranslator>, String>> messages;

    static {
        init();
    }

    public static Map<Enum<? extends Language>, Map<Enum<? extends MessageTranslator>, String>> getMessages() {
        return messages;
    }

    private static void init() {
        resourcesPath = FileSystems.getDefault().getPath("src", "main", "resources", "messages");
        fileNamePattern = Pattern.compile("^(?<language>[a-zA-Z]{2})\\.properties$");
        propertyPattern = Pattern.compile("^\\s*(?<key>.*?)\\s*(?<delimiter>=)\\s*(?<value>.*?)\\s*$");
        messages = new HashMap<>();
        loadProperties();
    }

    private static void loadProperties() {
        for (Path path : getFileResources())
            messages.put(getFileLanguage(path), getProperties(path));
    }

    private static List<Path> getFileResources() {
        try {
            return Files.list(resourcesPath)
                    .filter(path -> fileNamePattern.matcher(path.getFileName().toString()).matches())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private static Enum<? extends Language> getFileLanguage(Path path) {
        assert path != null;
        assert fileNamePattern.matcher(path.getFileName().toString()).matches();

        Matcher matcher = fileNamePattern.matcher(path.getFileName().toString());

        if (!matcher.find()) return MessageTranslator.getLanguage();

        return valueOfLanguage(matcher.group("language"));
    }

    public static Enum<? extends Language> valueOfLanguage(String name) {
        // It always works, because MessageTranslator.getLanguage()
        // returns Enum<? extends Language>
        @SuppressWarnings("unchecked")
        var result = (Enum<? extends Language>) valueOf(name, MessageTranslator.getLanguage().getDeclaringClass());
        return result;
    }

    public static Enum<? extends MessageTranslator> valueOfMessage(String name) {
        // It always works, because MessageTranslator.getAnyMessage()
        // returns Enum<? extends MessageTranslator>
        @SuppressWarnings("unchecked")
        var result = (Enum<? extends MessageTranslator>) valueOf(name, MessageTranslator.getAnyMessage().getDeclaringClass());
        return result;
    }

    private static Enum<?> valueOf(String name, Class<?> clazz) {
        Enum<?> result = null;
        try {
            var valueOf = clazz.getMethod("valueOf", String.class);
            result = (Enum<?>) valueOf.invoke(null, name.toUpperCase());
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof IllegalArgumentException)
                throw (IllegalArgumentException) e.getTargetException();
            e.printStackTrace();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return result;
    }

    @NotNull
    private static Map<Enum<? extends MessageTranslator>, String> getProperties(Path path) {
        assert path != null;
        try {
            return Files.lines(path)
                    .map(propertyPattern::matcher)
                    .filter(Matcher::find)
                    .collect(Collectors.toMap(
                            k -> valueOfMessage(k.group("key")),
                            v -> v.group("value")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
