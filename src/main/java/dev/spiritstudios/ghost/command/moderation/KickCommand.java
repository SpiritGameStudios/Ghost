package dev.spiritstudios.ghost.command.moderation;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.util.EmbedUtil;
import dev.spiritstudios.ghost.data.CommonColors;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;

public class KickCommand implements Command {
    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public SlashCommandBuilder createSlashCommand() {
        return SlashCommand.with(getName(), "Kick a user from this server")
                .addOption(SlashCommandOption.createUserOption(
                        "user",
                        "The user to kick",
                        true
                ))
                .addOption(SlashCommandOption.createStringOption(
                        "reason",
                        "The reason you are kicking this user",
                        false
                ))
                .setEnabledInDms(false)
                .setDefaultEnabledForPermissions(PermissionType.KICK_MEMBERS);
    }

    @Override
    public void execute(SlashCommandInteraction interaction, DiscordApi api) {
        User user = interaction.getOptionByName("user")
                .flatMap(SlashCommandInteractionOption::getUserValue)
                .orElseThrow();

        Server server = interaction.getServer().orElseThrow();

        if (!server.canYouKickUser(user)) {
            interaction.createImmediateResponder()
                    .addEmbed(EmbedUtil.error("Ghost does not have permission to kick that user"))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();

            return;
        }

        String reason = interaction.getOptionByName("reason")
                .flatMap(SlashCommandInteractionOption::getStringValue)
                .orElse("No reason provided.");

        interaction.respondLater().thenCompose(updater -> {
            EmbedBuilder dmEmbed = new EmbedBuilder()
                    .setTitle("You have been kicked from " + server.getName() + ".")
                    .addInlineField("Moderator", "<@!%d>".formatted(interaction.getUser().getId()))
                    .setColor(CommonColors.RED)
                    .setTimestampToNow();

            server.getIcon()
                    .ifPresentOrElse(
                            icon -> dmEmbed.setAuthor(server.getName(), icon.getUrl().toString(), icon),
                            () -> dmEmbed.setAuthor(server.getName())
                    );

            return user.sendMessage(dmEmbed)
                    .exceptionally(throwable -> {
                        updater.addEmbed(EmbedUtil.error("Failed to notify user")).setFlags(MessageFlag.EPHEMERAL);
                        return null;
                    })
                    .thenCompose(message -> server.kickUser(user, reason))
                    .thenCompose(ignored -> updater.addEmbed(new EmbedBuilder()
                            .setTitle("Success")
                            .setDescription("Kicked <@!%d> from the server".formatted(user.getId()))
                            .setColor(CommonColors.GREEN)
                            .setTimestampToNow()).setFlags(MessageFlag.EPHEMERAL).update());
        });
    }
}
