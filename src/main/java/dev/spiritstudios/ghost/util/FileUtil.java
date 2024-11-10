package dev.spiritstudios.ghost.util;

import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static List<Path> getFiles(String basePath) {
        Path resPath = Paths.get(Resources.getResource(basePath).getPath());
        try (Stream<Path> stream = Files.walk(resPath)) {
            return stream.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private FileUtil() {
        Util.utilError();
    }
}
