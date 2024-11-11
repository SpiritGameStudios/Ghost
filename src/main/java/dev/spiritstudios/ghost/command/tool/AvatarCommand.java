package dev.spiritstudios.ghost.command.tool;

import dev.spiritstudios.ghost.command.Command;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;

import java.util.ArrayList;
import java.util.List;

public class AvatarCommand implements Command {
	@Override
	public String getName() {
		return "avatar";
	}

	@Override
	public SlashCommandBuilder createSlashCommand() {
		return SlashCommand.with(getName(), "Get the avatar of a user")
			.addOption(SlashCommandOption.createUserOption(
				"user",
				"The user to get the avatar of",
				false
			));
	}

	@Override
	public void execute(SlashCommandInteraction interaction, DiscordApi api) {
		User user = interaction.getOptionByName("user")
			.flatMap(SlashCommandInteractionOption::getUserValue)
			.orElse(interaction.getUser());

		Icon avatar = user.getAvatar();

		String avatarUrl = avatar.getUrl().toString();
		avatarUrl = avatarUrl.replaceAll("\\?size=\\d+$", "");
		avatarUrl = avatarUrl.replaceAll("\\.(gif|png|jpg|jpeg|webp)$", "");

		String pngUrl = avatarUrl + ".png?size=1024";
		String jpgUrl = avatarUrl + ".jpg?size=1024";
		String gifUrl = avatarUrl + ".gif?size=1024";
		String webpUrl = avatarUrl + ".webp?size=1024";

		EmbedBuilder embed = new EmbedBuilder()
			.setTitle("Avatar of %s".formatted(user.getName()))
			.setImage(pngUrl)
			.setUrl(pngUrl);

		List<LowLevelComponent> buttons = new ArrayList<>();
		buttons.add(Button.link(pngUrl, "Download as PNG"));
		buttons.add(Button.link(jpgUrl, "Download as JPEG"));
		buttons.add(Button.link(webpUrl, "Download as webP"));
		if (avatar.isAnimated()) buttons.add(Button.link(gifUrl, "Download as GIF"));

		interaction.createImmediateResponder()
			.addEmbed(embed)
			.addComponents(ActionRow.of(buttons))
			.respond();
	}
}
