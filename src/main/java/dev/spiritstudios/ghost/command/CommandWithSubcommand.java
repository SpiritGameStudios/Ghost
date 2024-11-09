package dev.spiritstudios.ghost.command;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOptionsProvider;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CommandWithSubcommand extends Command {
    Map<String, Subcommand> getSubcommands();

    @Override
    default void execute(SlashCommandInteraction interaction, DiscordApi api) {
        String subcommandName = interaction.getFullCommandName().replaceFirst(getName() + " ", "");
        Subcommand subcommand = getSubcommands().get(subcommandName);

        if (subcommand == null) throw new IllegalArgumentException("Subcommand %s not found".formatted(subcommandName));
        SlashCommandInteractionOptionsProvider options = interaction;
        while (options.getOptionByIndex(0).orElseThrow().isSubcommandOrGroup())
            options = options.getOptionByIndex(0).orElseThrow();

        subcommand.execute(interaction, options, api);
    }

    @FunctionalInterface
    interface Subcommand {
        void execute(SlashCommandInteraction interaction, SlashCommandInteractionOptionsProvider options, DiscordApi api);
    }
}
