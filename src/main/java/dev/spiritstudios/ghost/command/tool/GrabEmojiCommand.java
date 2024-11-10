package dev.spiritstudios.ghost.command.tool;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.command.util.EmbedUtil;
import dev.spiritstudios.ghost.data.CommonColors;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class GrabEmojiCommand implements Command {
    @Override
    public String getName() {
        return "grabemoji";
    }

    @Override
    public SlashCommandBuilder createSlashCommand() {
        return SlashCommand.with(getName(), "Grab an emoji from a message")
                .addOption(SlashCommandOption.createStringOption(
                        "name",
                        "The name of the emoji",
                        true
                ))
                .addOption(SlashCommandOption.createStringOption(
                        "url",
                        "The URL of the emoji",
                        true))
                .setEnabledInDms(false)
                .setDefaultEnabledForPermissions(PermissionType.MANAGE_EMOJIS);
    }

    @Override
    public void execute(SlashCommandInteraction interaction, DiscordApi api) {
        Server server = interaction.getServer().orElseThrow();

        if (!server.canYouManageEmojis()) {
            interaction.createImmediateResponder()
                    .addEmbed(EmbedUtil.error("Ghost does not have permission to manage emojis"))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        String name = interaction.getOptionByName("name").orElseThrow().getStringValue().orElseThrow();

        URL url;
        try {
            url = URI.create(interaction.getOptionByName("url").orElseThrow().getStringValue().orElseThrow()).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            interaction.createImmediateResponder()
                    .addEmbed(EmbedUtil.error("Invalid URL"))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        server.createCustomEmojiBuilder()
                .setName(name)
                .setImage(url)
                .create()
                .thenCompose(emoji -> {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Emoji Created")
                            .setDescription(":%s: has been created".formatted(name))
                            .setColor(CommonColors.GREEN)
                            .setThumbnail(url.toString());

                    return interaction.createImmediateResponder()
                            .addEmbed(embed)
                            .respond();
                });
    }
}
