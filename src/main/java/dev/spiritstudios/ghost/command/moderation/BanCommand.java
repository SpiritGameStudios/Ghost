package dev.spiritstudios.ghost.command.moderation;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.command.util.EmbedUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;

import java.awt.*;

public class BanCommand implements Command {
    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public SlashCommandBuilder createSlashCommand() {
        return SlashCommand.with(getName(), "Ban a user from this server")
                .addOption(SlashCommandOption.createUserOption(
                        "user",
                        "The user to ban",
                        true
                ))
                .addOption(SlashCommandOption.createStringOption(
                        "reason",
                        "The reason you are banning this user",
                        false
                ))
                .setEnabledInDms(false)
                .setDefaultEnabledForPermissions(PermissionType.BAN_MEMBERS);
    }

    @Override
    public void execute(SlashCommandInteraction interaction, DiscordApi api) {
        User user = interaction.getOptionByName("user")
                .flatMap(SlashCommandInteractionOption::getUserValue)
                .orElseThrow();

        Server server = interaction.getServer().orElseThrow();

        if (!server.canYouBanUser(user)) {
            interaction.createImmediateResponder()
                    .addEmbed(EmbedUtil.error("Ghost does not have permission to ban that user"))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();

            return;
        }

        String reason = interaction.getOptionByName("reason")
                .flatMap(SlashCommandInteractionOption::getStringValue)
                .orElse("No reason provided.");

        interaction.respondLater().thenCompose(updater -> {
            EmbedBuilder dmEmbed = new EmbedBuilder()
                    .setTitle("You have been banned from " + server.getName() + ".")
                    .addInlineField("Moderator", "<@!%d>".formatted(interaction.getUser().getId()))
                    .setColor(Color.RED)
                    .setTimestampToNow();

            server.getIcon()
                    .ifPresentOrElse(
                            icon -> dmEmbed.setAuthor(server.getName(), icon.getUrl().toString(), icon),
                            () -> dmEmbed.setAuthor(server.getName())
                    );

            return user.sendMessage(dmEmbed)
                    .thenCompose(message -> server.banUser(user, null, reason))
                    .whenComplete((ignored, throwable) -> {
                        updater.addEmbed(new EmbedBuilder()
                                .setTitle("Failed")
                                .setColor(Color.RED)
                                .setTimestampToNow()).update();
                    })
                    .thenCompose(ignored -> updater.addEmbed(new EmbedBuilder()
                            .setTitle("Success")
                            .setDescription("Banned <@!%d> from the server".formatted(user.getId()))
                            .setColor(Color.GREEN)
                            .setTimestampToNow()).update());
        });
    }
}
