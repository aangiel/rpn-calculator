package io.github.aangiel.rpn.translation;

import io.github.aangiel.rpn.CalculatorSupplier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum MessageTranslator {
    EMPTY_EQUATION, LEFT_ON_STACK, LACK_OF_ARGUMENTS, BAD_ITEM, UNSUPPORTED_TYPE;

    private static Path resourcesPath;
    private static Pattern fileNamePattern;
    private static Pattern propertyPattern;
    private static Map<Language, Map<String, String>> messages;

    static {
        init();
    }

    private static void init() {
        resourcesPath = FileSystems.getDefault().getPath("src", "main", "resources", "messages");
        fileNamePattern = Pattern.compile("^(?<language>[a-zA-Z]{2})\\.properties$");
        propertyPattern = Pattern.compile("^\\s*(?<key>.*?)\\s*(?<delimiter>=)\\s*(?<value>.*?)\\s*$");
        messages = new EnumMap<>(Language.class);
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

    private static Language getFileLanguage(Path path) {
        assert path != null;
        assert fileNamePattern.matcher(path.getFileName().toString()).matches();

        Matcher matcher = fileNamePattern.matcher(path.getFileName().toString());
        return matcher.find() ? Language.valueOf(matcher.group("language").toUpperCase()) : Language.EN;
    }

    @NotNull
    private static Map<String, String> getProperties(Path path) {
        assert path != null;
        try {
            return Files.lines(path)
                    .map(propertyPattern::matcher)
                    .filter(Matcher::find)
                    .collect(Collectors.toMap(
                            k -> k.group("key"),
                            v -> v.group("value")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    public String get() {
        return getMessage(name());
    }

    public String get(Object... args) {
        return String.format(get(), args);
    }

    public static String createMessage(String key, Object... args) {
        return String.format(getMessage(key), args);
    }

    public static String createMessage(Language language, String key, Object... args) {
        return String.format(getMessage(language, key), args);
    }

    public static String getMessage(String key) {
        return getMessage(getLanguage(), key);
    }

    public static String getMessage(String language, String key) {
        return getMessage(Language.valueOf(language.toUpperCase()), key);
    }

    public static String getMessage(Language language, String key) {
        Objects.requireNonNull(key);

        var properties = messages.get(language);
        if (properties == null)
            throw new IllegalArgumentException("No translation file for given language");

        var result = properties.get(key);
        if (result == null)
            throw new IllegalArgumentException("No message for key");

        return result;
    }

    private static Language getLanguage() {
        return CalculatorSupplier.INSTANCE.getLanguage();
    }
}
