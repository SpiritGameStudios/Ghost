package dev.spiritstudios.ghost.util;

import dev.spiritstudios.ghost.data.CommonColors;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public final class EmbedUtil {
	public static EmbedBuilder titleOnly(String title, Color color) {
		return new EmbedBuilder()
			.setTitle(title)
			.setColor(color);
	}

	public static EmbedBuilder error(String error) {
		return titleOnly(error, CommonColors.RED);
	}

	public static CompletableFuture<InteractionOriginalResponseUpdater> error(String error, SlashCommandInteraction interaction) {
		return interaction.createImmediateResponder()
			.addEmbed(error(error))
			.setFlags(MessageFlag.EPHEMERAL)
			.respond();
	}

	public static CompletableFuture<Message> error(String error, InteractionOriginalResponseUpdater updater) {
		return updater
			.addEmbed(error(error))
			.setFlags(MessageFlag.EPHEMERAL)
			.update();
	}

	private EmbedUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
