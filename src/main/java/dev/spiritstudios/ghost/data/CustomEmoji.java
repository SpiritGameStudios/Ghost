package dev.spiritstudios.ghost.data;

import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.registry.Registries;
import dev.spiritstudios.ghost.util.Util;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.listener.GloballyAttachableListener;

public final class CustomEmoji {
    public static final KnownCustomEmoji MODRINTH = register(1284078451745095690L);

    private static KnownCustomEmoji register(long discordId) {
        KnownCustomEmoji emoji = Ghost.getApi().getCustomEmojiById(discordId).orElseThrow();
        return Registries.CUSTOM_EMOJI.register(emoji.getName(), emoji);
    }

    public static void init() {
        // NO-OP
    }
}
