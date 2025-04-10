package dev.spiritstudios.ghost.command.debug;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.command.CommandContext;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ErrorCommand implements Command {
	@Override
	public String getName() {
		return "error";
	}

	@Override
	public SlashCommandData createSlashCommand() {
		return Commands.slash(getName(), "Throws an error (Debug command)");
	}

	@Override
	public void execute(CommandContext context) {
		throw new RuntimeException("The error command was executed by %s".formatted(context.user().getName()));
	}
}
