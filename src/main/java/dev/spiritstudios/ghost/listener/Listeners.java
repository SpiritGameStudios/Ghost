package dev.spiritstudios.ghost.listener;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.registry.Registries;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public final class Listeners {
	public static final AutoCompleteListener AUTO_COMPLETE = register("auto_complete", new AutoCompleteListener());

	public static final CommandListener COMMAND = register("command", new CommandListener());
	public static final ButtonListener BUTTON = register("button", new ButtonListener());

	private static <T extends ListenerAdapter> T register(String id, T entry) {
		Registries.LISTENER.register(id, entry);
		return entry;
	}

	public static void init() {
		// NO-OP
	}
}
