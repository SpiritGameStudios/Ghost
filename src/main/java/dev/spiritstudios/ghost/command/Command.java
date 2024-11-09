package dev.spiritstudios.ghost.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

public interface Command {
    String getName();

    SlashCommandBuilder createSlashCommand();

    void execute(SlashCommandInteraction interaction, DiscordApi api);

    default void autoComplete(AutocompleteInteraction interaction, DiscordApi api) {}

    /**
     * Only ever use this for debugging. Logging inside commands breaks privacy.
     */
    default Logger getLogger() {
        return LogManager.getLogger(getClass());
    }
}
