package dev.spiritstudios.ghost.command;

import dev.spiritstudios.ghost.command.mod.ModrinthCommand;
import dev.spiritstudios.ghost.command.music.MusicCommand;
import dev.spiritstudios.ghost.command.tool.AvatarCommand;
import dev.spiritstudios.ghost.command.tool.GrabEmojiCommand;
import dev.spiritstudios.ghost.command.tool.PingCommand;
import dev.spiritstudios.ghost.command.tool.TagCommand;
import dev.spiritstudios.ghost.registry.Registries;

public final class GhostCommands {
	public static final Command PING = register(new PingCommand());
	public static final Command GRAB_EMOJI = register(new GrabEmojiCommand());
	public static final Command TAG = register(new TagCommand());
	public static final Command AVATAR = register(new AvatarCommand());
	public static final Command MODRINTH = register(new ModrinthCommand());

	public static final Command MUSIC = register(new MusicCommand());

	private static <T extends Command> T register(T entry) {
		Registries.COMMAND.register(entry.getName(), entry);
		return entry;
	}

	public static void init() {
		// NO-OP
	}
}
