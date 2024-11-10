package dev.spiritstudios.ghost.util;

import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileUtil {
    public static String getResource(String fileName, ClassLoader classLoader) {
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) return null;
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getResourcePath(String path) {
        URI uri;
        try {
            uri = Resources.getResource(path).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Path tagsPath;
        try {
            tagsPath = Paths.get(uri);
        } catch (FileSystemNotFoundException e) {
            // If we get here, we are running from a jar
            try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>())) {
                tagsPath = fs.getPath(path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return tagsPath;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private FileUtil() {
        Util.utilError();
    }
}
