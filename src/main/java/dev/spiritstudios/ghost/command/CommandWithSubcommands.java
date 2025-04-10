package dev.spiritstudios.ghost.command;

import java.util.Map;

public interface CommandWithSubcommands extends Command {
	Map<String, Subcommand> getSubcommands();

	@Override
	default void execute(CommandContext context) {
		String subcommandName = context.fullCommandName().replaceFirst(getName() + " ", "");
		Subcommand subcommand = getSubcommands().get(subcommandName);

		if (subcommand == null) throw new IllegalArgumentException("Subcommand %s not found".formatted(subcommandName));

		subcommand.execute(context);
	}

	@FunctionalInterface
	interface Subcommand {
		void execute(CommandContext context);
	}
}
