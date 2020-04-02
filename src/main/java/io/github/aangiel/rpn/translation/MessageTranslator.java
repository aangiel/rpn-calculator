package io.github.aangiel.rpn.translation;

import io.github.aangiel.rpn.CalculatorSupplier;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public enum MessageTranslator {
    EMPTY_EQUATION, LEFT_ON_STACK, LACK_OF_ARGUMENTS, BAD_ITEM, UNSUPPORTED_TYPE;

    private final static String FILE_NAME_PATTERN = "[a-z]{2}\\.properties";
    private final static String RESOURCES_PATH = "src/main/resources/messages";
    private static Map<Language, Properties> properties;
    private final Map<Language, String> messages;

    MessageTranslator() {
        messages = new EnumMap<>(Language.class);
        loadMessages();
    }

    private static Language getFileLanguage(Path path) {
        assert path != null;
        assert path.getFileName().toString().matches(FILE_NAME_PATTERN);

        return Language.valueOf(path.getFileName().toString().split("[.]")[0].toUpperCase());
    }

    private static void loadProperties() {
        properties = new EnumMap<>(Language.class);
        for (Path path : getFileResources()) {
            properties.put(getFileLanguage(path), getProperties(path));
        }
    }

    private static List<Path> getFileResources() {
        try {
            return Files.list(Path.of(RESOURCES_PATH))
                    .filter(path -> path.getFileName().toString().matches(FILE_NAME_PATTERN))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static String getMessage(String key) {
        Objects.requireNonNull(key);

        var properties = MessageTranslator.properties.get(getLanguage());
        if (properties == null)
            throw new IllegalArgumentException("No translation file for given language");

        var result = properties.getProperty(key);
        if (result == null)
            throw new IllegalArgumentException("No message for key");

        return result;
    }

    @NotNull
    private static Properties getProperties(Path path) {
        assert path != null;
        var properties = new Properties();
        try {
            properties.load(getReader(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static BufferedReader getReader(Path path) {
        assert path != null;
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(new byte[]{})));
        }
    }

    private void loadMessages() {
        if (properties == null) loadProperties();
        for (Map.Entry<Language, Properties> entry : properties.entrySet()) {
            var lang = entry.getKey();
            var message = entry.getValue().getProperty(name());
            messages.put(lang, message);
        }
    }

    private static Language getLanguage() {
        return CalculatorSupplier.INSTANCE.getLanguage();
    }

    public String get(Object... args) {
        return String.format(get(), args);
    }

    public String get() {
        var result = messages.get(getLanguage());
        if (result == null)
            throw new IllegalArgumentException("No translation file for given language");
        return result;
    }
}
