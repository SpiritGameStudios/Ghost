package dev.spiritstudios.ghost.data;

import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.registry.Registries;
import org.javacord.api.entity.emoji.KnownCustomEmoji;

public final class CustomEmoji {
	public static final KnownCustomEmoji MODRINTH = register(1284078451745095690L);

	public static final KnownCustomEmoji UNKNOWN = register(1304723233857212457L);
	public static final KnownCustomEmoji LEXFORGE = register(1304723011999633482L);
	public static final KnownCustomEmoji NEOFORGE = register(1304723186339942454L);
	public static final KnownCustomEmoji FABRIC = register(1304723115988881461L);
	public static final KnownCustomEmoji QUILT = register(1304726377408237622L);
	public static final KnownCustomEmoji PAPER = register(1305037062726225933L);
	public static final KnownCustomEmoji SPIGOT = register(1305037125669879838L);
	public static final KnownCustomEmoji VELOCITY = register(1305037185954615317L);
	public static final KnownCustomEmoji BUKKIT = register(1305037239595696190L);
	public static final KnownCustomEmoji MINECRAFT = register(1305037307631632465L);
	public static final KnownCustomEmoji PURPUR = register(1305037360504897608L);
	public static final KnownCustomEmoji WATERFALL = register(1305037412077928528L);
	public static final KnownCustomEmoji SPONGE = register(1305037468336390185L);
	public static final KnownCustomEmoji RIFT = register(1305037558236839986L);
	public static final KnownCustomEmoji MODLOADER = register(1305037631599546450L);
	public static final KnownCustomEmoji LITELOADER = register(1305037691385286677L);
	public static final KnownCustomEmoji FOLIA = register(1305037743746977846L);
	public static final KnownCustomEmoji BUNGEECORD = register(1305037804627165195L);

	private static KnownCustomEmoji register(long discordId) {
		KnownCustomEmoji emoji = Ghost.getApi().getCustomEmojiById(discordId).orElseThrow();
		return Registries.CUSTOM_EMOJI.register(emoji.getName(), emoji);
	}

	public static void init() {
		// NO-OP
	}
}
