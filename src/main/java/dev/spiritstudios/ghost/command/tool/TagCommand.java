package dev.spiritstudios.ghost.command.tool;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.registry.Registries;
import dev.spiritstudios.ghost.util.EmbedUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.*;

import java.util.Optional;

public class TagCommand implements Command {
    @Override
    public String getName() {
        return "tag";
    }

    @Override
    public SlashCommandBuilder createSlashCommand() {
        return SlashCommand.with(getName(), "Send a tag message")
                .addOption(SlashCommandOption.createStringOption(
                        "name",
                        "The name of the tag",
                        true,
                        true
                ));
    }

    @Override
    public void execute(SlashCommandInteraction interaction, DiscordApi api) {
        String name = interaction.getOptionByName("name").orElseThrow().getStringValue().orElseThrow();

        Optional<String> tag = Registries.TAG.get(name);
        if (tag.isEmpty()) {
            EmbedUtil.error("Tag not found", interaction);
            return;
        }

        interaction.createImmediateResponder()
                .setContent(tag.get())
                .respond();
    }

    @Override
    public void autoComplete(AutocompleteInteraction interaction, DiscordApi api) {
        interaction.respondWithChoices(Registries.TAG.choices());
    }
}
