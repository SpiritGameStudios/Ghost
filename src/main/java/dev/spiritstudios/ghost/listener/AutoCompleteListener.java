package dev.spiritstudios.ghost.listener;

import dev.spiritstudios.ghost.registry.Registries;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public final class AutoCompleteListener extends ListenerAdapter {
	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		Registries.COMMAND.get(event.getCommandIdLong())
			.ifPresent(c -> c.autoComplete(event));
	}
}
