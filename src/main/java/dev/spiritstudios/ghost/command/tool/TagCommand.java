package dev.spiritstudios.ghost.command.tool;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.command.CommandContext;
import dev.spiritstudios.ghost.registry.Registries;
import dev.spiritstudios.ghost.util.EmbedUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Optional;

public class TagCommand implements Command {
	@Override
	public String getName() {
		return "tag";
	}

	@Override
	public SlashCommandData createSlashCommand() {
		return Commands.slash(getName(), "Send a tag message")
			.addOption(
				OptionType.STRING,
				"name",
				"The name of the tag",
				true,
				true
			);
	}

	@Override
	public void execute(CommandContext context) {
		String name = context.getStringOption("name").orElseThrow();

		Optional<String> tag = Registries.TAG.get(name);
		if (tag.isEmpty()) {
			context
				.reply(EmbedUtil.error("Tag not found"))
				.setEphemeral(true)
				.queue();

			return;
		}

		context.reply(tag.get()).queue();
	}

	@Override
	public void autoComplete(CommandAutoCompleteInteraction interaction) {
		// Since name is the only option here, we can be sure it's what we are trying to autocomplete
		AutoCompleteQuery option = interaction.getFocusedOption();
		String partial = option.getValue();

		interaction.replyChoiceStrings(Registries.TAG.keySet().parallelStream()
			.filter(choice -> choice.contains(partial)).toList()).queue();
	}
}
