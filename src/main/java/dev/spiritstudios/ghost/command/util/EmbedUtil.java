package dev.spiritstudios.ghost.command.util;

import dev.spiritstudios.ghost.data.CommonColors;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public final class EmbedUtil {
    public static EmbedBuilder titleOnly(String title, Color color) {
        return new EmbedBuilder()
                .setTitle(title)
                .setColor(color);
    }

    public static EmbedBuilder error(String error) {
        return titleOnly(error, CommonColors.RED);
    }

    private EmbedUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
