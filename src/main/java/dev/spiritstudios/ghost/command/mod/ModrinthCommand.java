package dev.spiritstudios.ghost.command.mod;

import dev.spiritstudios.ghost.command.CommandWithSubcommand;
import dev.spiritstudios.ghost.data.CustomEmoji;
import dev.spiritstudios.ghost.util.*;
import masecla.modrinth4j.model.project.Project;
import masecla.modrinth4j.model.project.ProjectType;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModrinthCommand implements CommandWithSubcommand {
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
        private static final Map<ProjectType, String> PROJECT_TYPE_NAMES = Map.of(
                ProjectType.MOD, "Mod",
                ProjectType.MODPACK, "Modpack",
                ProjectType.RESOURCEPACK, "Resource Pack"
        );

        public static void info(SlashCommandInteraction interaction, SlashCommandInteractionOptionsProvider options, DiscordApi api) {
            interaction.respondLater().thenCompose(updater -> Constants.MODRINTH_API.projects().get(options.getOptionByName("slug").orElseThrow().getStringValue().orElseThrow())
                    .thenCompose(project -> {
                        if (project == null) return updater.setContent("Mod not found.").update();

                        List<String> categories = project.getCategories();
                        categories.replaceAll(StringUtil::capitalize);

                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle(project.getTitle())
                                .setUrl("https://modrinth.com/mod/%s".formatted(project.getSlug()))
                                .setDescription(project.getDescription())
                                .addField("Categories", String.join("\n", categories), true)
                                .setThumbnail(project.getIconUrl())
                                .setTimestampToNow()
                                .setFooter("%s on Modrinth".formatted(PROJECT_TYPE_NAMES.get(project.getProjectType())), CustomEmoji.MODRINTH.getImage());

                        String bannerUrl = project.getGallery().getFirst().getUrl();
                        if (bannerUrl != null) embed.setImage(bannerUrl);

                        return HttpHelper.getImage(project.getIconUrl())
                                .thenCompose(icon -> {
                                    int color = ImageHelper.getCommonColor(icon);
                                    embed.setColor(new Color(color));

                                    return updater.addEmbed(embed).update();
                                });
                    }));
        }
    }
}