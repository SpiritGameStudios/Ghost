package dev.spiritstudios.ghost.util;

import java.io.IOException;
import java.io.InputStream;

public final class FileUtil {
	public static String getResource(String fileName, ClassLoader classLoader) {
		try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
			if (inputStream == null) return null;
			return new String(inputStream.readAllBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private FileUtil() {
		Util.utilError();
	}
}
