package dev.spiritstudios.ghost.command.tool;

import dev.spiritstudios.ghost.TagManager;
import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.command.util.EmbedUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.*;

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

        String tag = TagManager.get(name);
        if (tag == null) {
            interaction.createImmediateResponder()
                    .addEmbed(EmbedUtil.error("Tag not found"))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();

            return;
        }

        interaction.createImmediateResponder()
                .setContent(tag)
                .respond();
    }

    @Override
    public void autoComplete(AutocompleteInteraction interaction, DiscordApi api) {
        interaction.respondWithChoices(TagManager.choices());
    }
}
