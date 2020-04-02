package io.github.aangiel.rpn.translation;

import io.github.aangiel.rpn.translation.interfaces.Language;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
    private static Map<Language, Map<String, String>> messages;

    static {
        init();
    }

    public static Map<Language, Map<String, String>> getMessages() {
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

    private static Languages getFileLanguage(Path path) {
        assert path != null;
        assert fileNamePattern.matcher(path.getFileName().toString()).matches();

        Matcher matcher = fileNamePattern.matcher(path.getFileName().toString());
        return matcher.find() ? Languages.valueOf(matcher.group("language").toUpperCase()) : Languages.EN;
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
}
