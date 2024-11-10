package dev.spiritstudios.ghost.command.mod;

import dev.callmeecho.maze.model.Project;
import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.command.CommandWithSubcommands;
import dev.spiritstudios.ghost.command.util.EmbedUtil;
import dev.spiritstudios.ghost.data.CommonColors;
import dev.spiritstudios.ghost.data.CustomEmoji;
import dev.spiritstudios.ghost.util.Constants;
import dev.spiritstudios.ghost.util.HttpHelper;
import dev.spiritstudios.ghost.util.ImageHelper;
import dev.spiritstudios.ghost.util.StringUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class ModrinthCommand implements CommandWithSubcommands {
    @Override
    public Map<String, Subcommand> getSubcommands() {
        return Map.of(
                "projects info", Projects::info
        );
    }

    @Override
    public String getName() {
        return "modrinth";
    }

    @Override
    public SlashCommandBuilder createSlashCommand() {
        return SlashCommand.with(getName(), "Interact with Modrinth", List.of(
                SlashCommandOption.createSubcommandGroup("projects", "Interact with projects", List.of(
                        SlashCommandOption.createSubcommand(
                                "info",
                                "Get information about a project",
                                List.of(SlashCommandOption.createStringOption("slug", "The slug of the mod", true))
                        )
                ))
        ));
    }

    private static class Projects {
        private static final Map<Project.Type, String> PROJECT_TYPE_NAMES = Map.of(
                Project.Type.MOD, "Mod",
                Project.Type.MODPACK, "Mod Pack",
                Project.Type.RESOURCE_PACK, "Resource Pack",
                Project.Type.SHADER, "Shader Pack"
        );

        public static void info(SlashCommandInteraction interaction, SlashCommandInteractionOptionsProvider options, DiscordApi api) {
            String slug = options.getOptionByName("slug")
                    .flatMap(SlashCommandInteractionOption::getStringValue)
                    .orElseThrow();

            interaction.respondLater().thenCompose(updater -> Constants.MODRINTH_API.project().get(slug).thenApply(project -> {
                if (project == null) return updater.addEmbed(EmbedUtil.error("Mod not found.")).update();

                List<String> categories = StringUtil.capitalize(project.categories());

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(project.title())
                        .setUrl("https://modrinth.com/mod/%s".formatted(project.slug()))
                        .setDescription(project.description())
                        .addField("Categories", String.join("\n", categories), true)
                        .setTimestampToNow()
                        .setFooter(PROJECT_TYPE_NAMES.get(project.projectType()) + " on Modrinth", CustomEmoji.MODRINTH.getImage());


                if (project.loaders() != null) {
                    List<String> loaders = StringUtil.capitalize(project.loaders());
                    loaders.replaceAll(loader -> switch (loader) {
                        case "Fabric" -> CustomEmoji.FABRIC.getMentionTag();
                        case "Forge" -> CustomEmoji.LEXFORGE.getMentionTag();
                        case "Neoforge" -> CustomEmoji.NEOFORGE.getMentionTag();
                        case "Quilt" -> CustomEmoji.QUILT.getMentionTag();
                        default -> CustomEmoji.UNKNOWN.getMentionTag();
                    } + " " + loader);

                    embed.addInlineField("Loaders", String.join("\n", loaders));
                }

                if (project.gameVersions() != null) {
                    embed.addInlineField("Versions", StringUtil.truncate(String.join("\n", project.gameVersions()), 256));
                }


                if (project.iconUrl() != null) {
                    embed.setThumbnail(project.iconUrl());
                    return HttpHelper.getImage(project.iconUrl())
                            .thenCompose(icon -> {
                                embed.setColor(new Color(ImageHelper.getCommonColor(icon)));
                                return updater.addEmbed(embed).update();
                            }).whenComplete((ignored, throwable) -> {
                                if (throwable == null) return;

                                Ghost.logError("", throwable);
                            });
                }


                embed.setColor(CommonColors.BLURPLE);
                return updater.addEmbed(embed).update();
            })).whenComplete((ignored, throwable) -> {
                if (throwable == null) return;

                Ghost.logError("", throwable);
            });
        }
    }
}