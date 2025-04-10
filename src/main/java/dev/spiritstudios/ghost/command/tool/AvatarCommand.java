package dev.spiritstudios.ghost.command.tool;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

public class AvatarCommand implements Command {
	@Override
	public String getName() {
		return "avatar";
	}

	@Override
	public SlashCommandData createSlashCommand() {
		return Commands.slash(getName(), "Get the avatar of a user")
			.addOption(
				OptionType.USER,
				"user",
				"The user to get the avatar of",
				false
			);
	}

	@Override
	public void execute(CommandContext context) {
		User user = context.getUserOption("user")
			.orElse(context.user());

		String avatarUrl = user.getEffectiveAvatarUrl();
		avatarUrl = avatarUrl.replaceAll("\\?size=\\d+$", "");
		avatarUrl = avatarUrl.replaceAll("\\.(gif|png|jpg|jpeg|webp)$", "");

		boolean animated = user.getEffectiveAvatarUrl().contains(".gif");

		String pngUrl = avatarUrl + ".png?size=1024";
		String jpgUrl = avatarUrl + ".jpg?size=1024";
		String gifUrl = avatarUrl + ".gif?size=1024";
		String webpUrl = avatarUrl + ".webp?size=1024";

		EmbedBuilder embed = new EmbedBuilder()
			.setTitle("Avatar of %s".formatted(user.getEffectiveName()))
			.setImage(pngUrl)
			.setUrl(pngUrl);

		List<Button> buttons = new ArrayList<>();
		buttons.add(Button.link(pngUrl, "Download as PNG"));
		buttons.add(Button.link(jpgUrl, "Download as JPEG"));
		buttons.add(Button.link(webpUrl, "Download as webP"));
		if (animated) buttons.add(Button.link(gifUrl, "Download as GIF"));

		context
			.reply(embed)
			.addComponents(ActionRow.of(buttons))
			.queue();
	}
}
