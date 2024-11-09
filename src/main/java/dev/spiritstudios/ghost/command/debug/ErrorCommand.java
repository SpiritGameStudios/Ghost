package dev.spiritstudios.ghost.command.debug;

import dev.spiritstudios.ghost.command.Command;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ErrorCommand implements Command {
    @Override
    public String getName() {
        return "error";
    }

    @Override
    public SlashCommandBuilder createSlashCommand() {
        return SlashCommand.with(getName(), "Throws an error (Debug command)");
    }

    @Override
    public void execute(SlashCommandInteraction interaction, DiscordApi api) {
        throw new RuntimeException("The error command was executed by %s".formatted(interaction.getUser().getName()));
    }
}
