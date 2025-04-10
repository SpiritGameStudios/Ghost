package dev.spiritstudios.ghost.listener;

import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.command.CommandContext;
import dev.spiritstudios.ghost.exception.CommandException;
import dev.spiritstudios.ghost.util.EmbedUtil;
import dev.spiritstudios.ghost.registry.Registries;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CommandListener extends ListenerAdapter {
	private final ExecutorService executorService = Executors.newCachedThreadPool();

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		Optional<Command> command = Registries.COMMAND.get(event.getCommandIdLong());

		if (command.isEmpty()) {
			event.replyEmbeds(EmbedUtil.error("Command not found")).setEphemeral(true).queue();
			return;
		}

		CompletableFuture.runAsync(() -> command.get().execute(new CommandContext(event)), executorService)
			.exceptionally(throwable -> {
				if (throwable instanceof CommandException) {
					event.replyEmbeds(EmbedUtil.error(throwable.getMessage()))
						.setEphemeral(true)
						.queue();

					return null;
				}

				event.replyEmbeds(EmbedUtil.error("An error occurred while executing that command"))
					.setEphemeral(true)
					.queue();

				Ghost.logError("An error occurred while executing a command", throwable);
				return null;
			});
	}
}
