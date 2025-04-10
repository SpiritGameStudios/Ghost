package dev.spiritstudios.ghost;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import dev.spiritstudios.ghost.command.GhostCommands;
import dev.spiritstudios.ghost.data.CommonColors;
import dev.spiritstudios.ghost.data.CustomEmoji;
import dev.spiritstudios.ghost.listener.Listeners;
import dev.spiritstudios.ghost.registry.Registries;
import dev.spiritstudios.ghost.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Ghost {
	private static final Logger LOGGER = LogManager.getLogger(Ghost.class);
	private static JDA api;

	public static void main(String[] args) throws InterruptedException {
		JDABuilder apiBuilder = JDABuilder
			.createDefault(GhostConfig.INSTANCE.token())
			.setAudioSendFactory(new NativeAudioSendFactory());

		Listeners.init();
		Registries.LISTENER.freeze();
		Registries.LISTENER.forEach(apiBuilder::addEventListeners);

		GhostCommands.init();
		Registries.COMMAND.freeze();

		Registries.TAG.load();
		Registries.TAG.freeze();

		api = apiBuilder.build().awaitReady();

		Registries.COMMAND.sendCommands(api);

		CustomEmoji.init();
		Registries.CUSTOM_EMOJI.freeze();

		if (GhostConfig.INSTANCE.debug()) {
			api.getPresence().setActivity(Activity.watching("Echo struggle"));
			LOGGER.debug("Debug mode enabled");
		}

		LOGGER.info("Logged in as {}", api.getSelfUser().getName());
	}

	public static void logError(String message, Throwable t) {
		if (!GhostConfig.INSTANCE.debug() || GhostConfig.INSTANCE.channelId() <= 0) return;

		StringWriter stackTrace = new StringWriter();
		PrintWriter writer = new PrintWriter(stackTrace);
		t.printStackTrace(writer);

		TextChannel channel = api.getTextChannelById(GhostConfig.INSTANCE.channelId());
		if (channel == null) return;
		EmbedBuilder embed = new EmbedBuilder()
			.setTitle(message)
			.setDescription("```lisp\n%s```"
				.formatted(StringUtil.truncate(stackTrace.toString(), 2048)))
			.addField("Full message", t.getMessage(), false)
			.setColor(CommonColors.RED);

		channel.sendMessageEmbeds(embed.build()).queue();
	}

	public static JDA getApi() {
		return api;
	}
}
