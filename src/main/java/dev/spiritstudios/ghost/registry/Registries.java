package dev.spiritstudios.ghost.registry;

import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.command.Commands;
import dev.spiritstudios.ghost.data.CustomEmoji;
import dev.spiritstudios.ghost.listener.Listeners;
import dev.spiritstudios.ghost.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.listener.GloballyAttachableListener;

public final class Registries {
	private static final Logger LOGGER = LogManager.getLogger(Registries.class);

	public static final CommandRegistry COMMAND = new CommandRegistry();

	public static final Registry<GloballyAttachableListener> LISTENER = new SimpleRegistry<>();

	public static final Registry<KnownCustomEmoji> CUSTOM_EMOJI = new SimpleRegistry<>();

	public static final TagRegistry TAG = new TagRegistry();

	private Registries() {
		Util.utilError();
	}
}
