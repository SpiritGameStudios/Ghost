package dev.spiritstudios.ghost;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.javacord.api.interaction.SlashCommandOptionChoice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class TagManager {
    private static final Map<String, String> tags = new Object2ObjectOpenHashMap<>();
    private static List<SlashCommandOptionChoice> choices;

    public static String get(String name) {
        return tags.get(name);
    }

    public static void init() {
        ClassLoader classLoader = TagManager.class.getClassLoader();

        File tagsFolder = new File(Objects.requireNonNull(classLoader.getResource("tags")).getFile());
        if (!tagsFolder.exists()) throw new IllegalStateException("Tags folder does not exist");
        if (!tagsFolder.isDirectory()) throw new IllegalStateException("Tags folder is not a directory");

        File[] files = tagsFolder.listFiles();
        if (files == null) throw new IllegalStateException("Tags folder is empty");

        for (File file : files) {
            try {
                String content = getResource("tags/" + file.getName());
                if (file.getName().endsWith(".md")) tags.put(file.getName().replace(".md", ""), content);
                if (file.getName().endsWith(".alias"))
                    tags.put(file.getName().replace(".alias", ""), tags.get(content));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        choices = tags.keySet().stream()
                .map(tag -> SlashCommandOptionChoice.create(tag, tag))
                .toList();
    }

    private static String getResource(String fileName) throws IOException {
        ClassLoader classLoader = TagManager.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) return null;
            return new String(inputStream.readAllBytes());
        }
    }

    public static List<SlashCommandOptionChoice> choices() {
        return choices;
    }
}
