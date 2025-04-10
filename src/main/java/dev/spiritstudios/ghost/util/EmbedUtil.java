package dev.spiritstudios.ghost.util;

import dev.spiritstudios.ghost.data.CommonColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public final class EmbedUtil {
	public static MessageEmbed titleOnly(String title, Color color) {
		return new EmbedBuilder()
			.setTitle(title)
			.setColor(color)
			.build();
	}

	public static MessageEmbed error(String error) {
		return titleOnly(error, CommonColors.RED);
	}

	private EmbedUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
