package dev.spiritstudios.ghost.command.mod;

import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.command.CommandWithSubcommand;
import dev.spiritstudios.ghost.command.util.EmbedUtil;
import dev.spiritstudios.ghost.data.CustomEmoji;
import dev.spiritstudios.ghost.data.modrinth.ModrinthProject;
import dev.spiritstudios.ghost.util.*;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.checkerframework.checker.units.qual.C;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.*;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
//        private static final Map<ProjectType, String> PROJECT_TYPE_NAMES = Map.of(
//                ProjectType.MOD, "Mod",
//                ProjectType.MODPACK, "Modpack",
//                ProjectType.RESOURCEPACK, "Resource Pack"
//        );

        public static void info(SlashCommandInteraction interaction, SlashCommandInteractionOptionsProvider options, DiscordApi api) {
            interaction.respondLater().thenCompose(updater -> {
                String slug = options.getOptionByName("slug")
                        .flatMap(SlashCommandInteractionOption::getStringValue)
                        .orElseThrow();

                String url = "/project/" + slug;

                return Constants.MODRINTH_API.createRequest(url, Map.of()).thenCompose(req -> {
                    req.method("GET", null);
                    return Constants.MODRINTH_API.execute(req.build());
                }).thenApply(response -> {
                    ResponseBody body = response.body();
                    try {
                        return HttpHelper.checkResponse(body, ModrinthProject.CODEC);
                    } catch (IOException e) {
                        LoggerFactory.getLogger(Projects.class).error("", e);
                        throw new RuntimeException(e);
                    }
                }).thenApply(projResult -> {
                    if (projResult == null) return updater.addEmbed(EmbedUtil.error("Mod not found.")).update();

                    ModrinthProject project = projResult.getOrThrow();

                    List<String> categories = project.categories();
//                    categories.replaceAll(StringUtil::capitalize);

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(project.title())
                            .setUrl("https://modrinth.com/mod/%s".formatted(project.slug()))
                            .setDescription(project.description())
                            .addField("Categories", String.join("\n", categories), true)
                            .setTimestampToNow()
                            .setFooter("on Modrinth", CustomEmoji.MODRINTH.getImage());

                    if (project.iconUrl().isPresent()) {
                        embed.setThumbnail(project.iconUrl().get());
                        return HttpHelper.getImage(project.iconUrl().get())
                                .thenCompose(icon -> {
                                    embed.setColor(new Color(ImageHelper.getCommonColor(icon)));
                                    return updater.addEmbed(embed).update();
                                }).whenComplete((future, throwable) -> {
                                    if (throwable != null) updater.addEmbed(EmbedUtil.error("something")).update();
                                    Ghost.logError("Failed modrinth command", throwable);
                                });
                    }

                    embed.setColor(Color.BLACK);
                    return updater.addEmbed(embed).update();
                }).whenComplete((future, throwable) -> {
                    if (throwable != null) updater.addEmbed(EmbedUtil.error("something")).update();
                    Ghost.logError("Failed modrinth command", throwable);
                });
            });

//            interaction.respondLater().thenCompose(updater -> Constants.MODRINTH_API.projects().get(options.getOptionByName("slug").orElseThrow().getStringValue().orElseThrow())
//                    .thenCompose(project -> {
//

//
//                        String bannerUrl = project.getGallery().getFirst().getUrl();
//                        if (bannerUrl != null) embed.setImage(bannerUrl);
//

//                    }));
        }
    }
}