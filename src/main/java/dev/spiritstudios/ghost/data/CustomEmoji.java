package dev.spiritstudios.ghost.data;

import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.registry.Registries;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

public final class CustomEmoji {
	public static final RichCustomEmoji MODRINTH = register(1284078451745095690L);

	public static final RichCustomEmoji UNKNOWN = register(1304723233857212457L);
	public static final RichCustomEmoji LEXFORGE = register(1304723011999633482L);
	public static final RichCustomEmoji NEOFORGE = register(1304723186339942454L);
	public static final RichCustomEmoji FABRIC = register(1304723115988881461L);
	public static final RichCustomEmoji QUILT = register(1304726377408237622L);
	public static final RichCustomEmoji PAPER = register(1305037062726225933L);
	public static final RichCustomEmoji SPIGOT = register(1305037125669879838L);
	public static final RichCustomEmoji VELOCITY = register(1305037185954615317L);
	public static final RichCustomEmoji BUKKIT = register(1305037239595696190L);
	public static final RichCustomEmoji MINECRAFT = register(1305037307631632465L);
	public static final RichCustomEmoji PURPUR = register(1305037360504897608L);
	public static final RichCustomEmoji WATERFALL = register(1305037412077928528L);
	public static final RichCustomEmoji SPONGE = register(1305037468336390185L);
	public static final RichCustomEmoji RIFT = register(1305037558236839986L);
	public static final RichCustomEmoji MODLOADER = register(1305037631599546450L);
	public static final RichCustomEmoji LITELOADER = register(1305037691385286677L);
	public static final RichCustomEmoji FOLIA = register(1305037743746977846L);
	public static final RichCustomEmoji BUNGEECORD = register(1305037804627165195L);

	private static RichCustomEmoji register(long discordId) {
		RichCustomEmoji emoji = Ghost.getApi().getEmojiById(discordId);


		return Registries.CUSTOM_EMOJI.register(emoji.getName(), emoji);
	}

	public static void init() {
		// NO-OP
	}
}
