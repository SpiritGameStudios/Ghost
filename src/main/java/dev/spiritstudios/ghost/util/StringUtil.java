package dev.spiritstudios.ghost.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class StringUtil {
	public static String truncate(String string, int length) {
		if (string.length() > length) {
			return string.substring(0, length - 3) + "...";
		}
		return string;
	}

	public static String capitalize(String string) {
		string = string.toLowerCase();
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public static List<String> capitalize(List<String> string) {
		List<String> strings = new ArrayList<>(string);
		strings.replaceAll(StringUtil::capitalize);

		return strings;
	}

	public static String formatDuration(Duration duration) {
		long hours = duration.toHoursPart();
		long minutes = duration.toMinutes();
		long seconds = duration.toSecondsPart();

		return (hours > 0 ? hours + ":" : "") +
			(minutes < 10 ? "0" + minutes : minutes) + ":" +
			(seconds < 10 ? "0" + seconds : seconds);
	}

	private StringUtil() {
		Util.utilError();
	}
}
