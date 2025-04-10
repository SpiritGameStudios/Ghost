package dev.spiritstudios.ghost.registry;

import dev.spiritstudios.ghost.util.Util;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Registries {
	private static final Logger LOGGER = LogManager.getLogger(Registries.class);

	public static final CommandRegistry COMMAND = new CommandRegistry();

	public static final Registry<ListenerAdapter> LISTENER = new SimpleRegistry<>();

	public static final Registry<RichCustomEmoji> CUSTOM_EMOJI = new SimpleRegistry<>();

	public static final TagRegistry TAG = new TagRegistry();

	private Registries() {
		Util.utilError();
	}
}
