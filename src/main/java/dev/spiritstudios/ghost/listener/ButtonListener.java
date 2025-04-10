package dev.spiritstudios.ghost.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ButtonListener extends ListenerAdapter {
	private static final Map<String, Consumer<ButtonInteraction>> callbacks = new LinkedHashMap<>() {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Consumer<ButtonInteraction>> eldest) {
			return size() > 50;
		}
	};

	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		Consumer<ButtonInteraction> callback = callbacks.get(event.getComponentId());
		if (callback == null) return;
		callback.accept(event);
	}

	public static void register(String buttonId, Consumer<ButtonInteraction> callback) {
		callbacks.put(buttonId, callback);
	}

	public static void remove(String buttonId) {
		callbacks.remove(buttonId);
	}
}
