package dev.spiritstudios.ghost.command.util;

import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public final class EmbedUtil {
    public static EmbedBuilder titleOnly(String title, Color color) {
        return new EmbedBuilder()
                .setTitle(title)
                .setColor(color);
    }

    public static EmbedBuilder titleOnly(String title) {
        return titleOnly(title, Color.BLACK);
    }

    public static EmbedBuilder error(String error) {
        return titleOnly(error, Color.RED);
    }

    private EmbedUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
