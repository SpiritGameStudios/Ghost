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

import java.util.concurrent.TimeUnit;

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
            EmbedUtil.error("Ghost does not have permission to ban that user", interaction);
            return;
        }

        String reason = interaction.getOptionByName("reason")
                .flatMap(SlashCommandInteractionOption::getStringValue)
                .orElse("No reason provided.");

        interaction.respondLater().thenCompose(updater -> {
            EmbedBuilder dmEmbed = new EmbedBuilder()
                    .setTitle("You have been banned from " + server.getName() + ".")
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
                        updater.addEmbed(EmbedUtil.error("Failed to notify user"))
                                .setFlags(MessageFlag.EPHEMERAL);

                        return null;
                    })
                    .thenCompose(message -> server.banUser(user, 0, TimeUnit.SECONDS, reason))
                    .thenCompose(ignored -> updater.addEmbed(new EmbedBuilder()
                            .setTitle("Success")
                            .setDescription("Banned <@!%d> from the server".formatted(user.getId()))
                            .setColor(CommonColors.GREEN)
                            .setTimestampToNow()).setFlags(MessageFlag.EPHEMERAL).update());
        });
    }
}
