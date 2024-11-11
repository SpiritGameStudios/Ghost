package dev.spiritstudios.ghost.listener;

import dev.spiritstudios.ghost.registry.Registries;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;

public final class AutoCompleteListener implements AutocompleteCreateListener {
	@Override
	public void onAutocompleteCreate(AutocompleteCreateEvent event) {
		Registries.COMMAND.get(event.getAutocompleteInteraction().getCommandId())
			.ifPresent(c -> c.autoComplete(event.getAutocompleteInteraction(), event.getApi()));
	}
}
