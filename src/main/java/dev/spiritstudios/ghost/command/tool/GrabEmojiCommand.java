package dev.spiritstudios.ghost.command.tool;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.command.CommandContext;
import dev.spiritstudios.ghost.data.CommonColors;
import dev.spiritstudios.ghost.util.EmbedUtil;
import dev.spiritstudios.ghost.util.HttpHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

public class GrabEmojiCommand implements Command {
	@Override
	public String getName() {
		return "grabemoji";
	}

	@Override
	public SlashCommandData createSlashCommand() {
		return Commands.slash(getName(), "Grab an emoji from a message")
			.addOption(
				OptionType.STRING,
				"name",
				"The name of the emoji",
				true
			)
			.addOption(
				OptionType.STRING,
				"url",
				"The URL of the emoji",
				true
			)
			.setContexts(InteractionContextType.GUILD)
			.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_GUILD_EXPRESSIONS));
	}

	@Override
	public void execute(CommandContext context) {
		if (!context.hasPermission(Permission.MANAGE_GUILD_EXPRESSIONS)) {
			context
				.reply(EmbedUtil.error("Ghost does not have permission to manage emojis"))
				.setEphemeral(true)
				.queue();

			return;
		}

		String name = context.getStringOption("name").orElseThrow();
		String url = context.getStringOption("url").orElseThrow();

		try {
			// We are doing this to check if the URL is valid.
			//noinspection ResultOfMethodCallIgnored
			URI.create(url).toURL();
		} catch (MalformedURLException | IllegalArgumentException e) {
			context
				.reply(EmbedUtil.error("Invalid URL"))
				.setEphemeral(true)
				.queue();

			return;
		}

		context.defer().queue(hook -> {
			try {
				Icon icon = HttpHelper.getIcon(url);

				context.guild().orElseThrow().createEmoji(name, icon)
					.queue(emoji -> hook.sendMessageEmbeds(
						new EmbedBuilder()
							.setTitle("Emoji Created")
							.setDescription(":%s: has been created".formatted(name))
							.setColor(CommonColors.GREEN)
							.setThumbnail(url)
							.build()
					).queue());
			} catch (IOException e) {
				context
					.reply(EmbedUtil.error("Failed to get emoji from URL"))
					.setEphemeral(true)
					.queue();
			}
		});
	}
}
