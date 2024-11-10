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

    private static KnownCustomEmoji register(long discordId) {
        KnownCustomEmoji emoji = Ghost.getApi().getCustomEmojiById(discordId).orElseThrow();
        return Registries.CUSTOM_EMOJI.register(emoji.getName(), emoji);
    }

    public static void init() {
        // NO-OP
    }
}
