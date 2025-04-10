package dev.spiritstudios.ghost.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Command {
	String getName();

	SlashCommandData createSlashCommand();

	void execute(CommandContext context);

	default void autoComplete(CommandAutoCompleteInteraction interaction) {
	}

	/**
	 * Only ever use this for debugging. Logging inside commands breaks privacy.
	 */
	default Logger getLogger() {
		return LogManager.getLogger(getClass());
	}
}
