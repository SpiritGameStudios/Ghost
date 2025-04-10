package dev.spiritstudios.ghost.command.mod;

import dev.callmeecho.maze.model.Project;
import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.command.CommandContext;
import dev.spiritstudios.ghost.command.CommandWithSubcommands;
import dev.spiritstudios.ghost.data.CommonColors;
import dev.spiritstudios.ghost.data.CustomEmoji;
import dev.spiritstudios.ghost.util.EmbedUtil;
import dev.spiritstudios.ghost.util.HttpHelper;
import dev.spiritstudios.ghost.util.ImageHelper;
import dev.spiritstudios.ghost.util.SharedConstants;
import dev.spiritstudios.ghost.util.StringUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
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
	public SlashCommandData createSlashCommand() {
		return Commands.slash(getName(), "Interact with Modrinth")
			.addSubcommandGroups(new SubcommandGroupData("projects", "Interact with projects")
				.addSubcommands(new SubcommandData(
						"info",
						"Get information about a project"
					).addOption(
						OptionType.STRING,
						"slug", "The slug of the mod",
						true
					)
				)
			);
	}

	private static class Projects {
		private static final Map<Project.Type, String> PROJECT_TYPE_NAMES = Map.of(
			Project.Type.MOD, "Mod",
			Project.Type.MODPACK, "Mod Pack",
			Project.Type.RESOURCE_PACK, "Resource Pack",
			Project.Type.SHADER, "Shader Pack"
		);

		public static void info(CommandContext context) {
			String slug = context.getStringOption("slug").orElseThrow();

			context.defer().queue(hook -> SharedConstants.MODRINTH_API.project().get(slug).thenAccept(project -> {
				if (project == null) {
					hook.sendMessageEmbeds(EmbedUtil.error("Mod not found.")).queue();
					return;
				}

				List<String> categories = StringUtil.capitalize(project.categories());

				EmbedBuilder embed = new EmbedBuilder()
					.setTitle(project.title())
					.setUrl("https://modrinth.com/mod/%s".formatted(project.slug()))
					.setDescription(project.description())
					.addField("Categories", String.join("\n", categories), true)
					.setTimestamp(Instant.now())
					.setFooter(PROJECT_TYPE_NAMES.get(project.projectType()) + " on Modrinth", CustomEmoji.MODRINTH.getImageUrl());

				if (project.loaders() != null) {
					List<String> loaders = StringUtil.capitalize(project.loaders());
					loaders.replaceAll(loader -> switch (loader) {
						case "Fabric" -> CustomEmoji.FABRIC.getAsMention();
						case "Forge" -> CustomEmoji.LEXFORGE.getAsMention();
						case "Neoforge" -> CustomEmoji.NEOFORGE.getAsMention();
						case "Quilt" -> CustomEmoji.QUILT.getAsMention();
						case "Paper" -> CustomEmoji.PAPER.getAsMention();
						case "Spigot" -> CustomEmoji.SPIGOT.getAsMention();
						case "Velocity" -> CustomEmoji.VELOCITY.getAsMention();
						case "Bukkit" -> CustomEmoji.BUKKIT.getAsMention();
						case "Minecraft" -> CustomEmoji.MINECRAFT.getAsMention();
						case "Purpur" -> CustomEmoji.PURPUR.getAsMention();
						case "Waterfall" -> CustomEmoji.WATERFALL.getAsMention();
						case "Sponge" -> CustomEmoji.SPONGE.getAsMention();
						case "Rift" -> CustomEmoji.RIFT.getAsMention();
						case "Modloader" -> CustomEmoji.MODLOADER.getAsMention();
						case "Liteloader" -> CustomEmoji.LITELOADER.getAsMention();
						case "Folia" -> CustomEmoji.FOLIA.getAsMention();
						case "Bungeecord" -> CustomEmoji.BUNGEECORD.getAsMention();
						default -> CustomEmoji.UNKNOWN.getAsMention();
					} + " " + loader);

					embed.addField("Loaders", String.join("\n", loaders), true);
				}

				if (project.gameVersions() != null) {
					embed.addField("Versions", StringUtil.truncate(String.join("\n", project.gameVersions()), 256), true);
				}

				embed.setColor(CommonColors.BLURPLE);

				if (project.iconUrl() != null) {
					embed.setThumbnail(project.iconUrl());

					try {
						BufferedImage image = HttpHelper.getImage(project.iconUrl());
						embed.setColor(new Color(ImageHelper.getCommonColor(image)));
					} catch (IOException ignored) {

					}
				}

				hook.sendMessageEmbeds(embed.build()).queue();
			}).exceptionally(throwable -> {
				Ghost.logError("", throwable);
				return null;
			}));
		}
	}
}
