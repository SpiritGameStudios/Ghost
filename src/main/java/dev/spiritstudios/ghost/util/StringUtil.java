package dev.spiritstudios.ghost.util;

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

    private StringUtil() {
        Util.utilError();
    }
}
