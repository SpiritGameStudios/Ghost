package dev.spiritstudios.ghost.registry;

import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.command.Commands;
import dev.spiritstudios.ghost.data.CustomEmoji;
import dev.spiritstudios.ghost.listener.Listeners;
import dev.spiritstudios.ghost.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.listener.GloballyAttachableListener;

public final class Registries {
    private static final Logger LOGGER = LogManager.getLogger(Registries.class);

    public static final CommandRegistry COMMAND = new CommandRegistry();

    public static final Registry<GloballyAttachableListener> LISTENER = new SimpleRegistry<>() {
        @Override
        public void freeze() {
            super.freeze();
            for (GloballyAttachableListener listener : this) Ghost.getApi().addListener(listener);
        }
    };

    public static final Registry<KnownCustomEmoji> CUSTOM_EMOJI = new SimpleRegistry<>();

    private Registries() {
        Util.utilError();
    }

    public static void init() {
        CustomEmoji.init();
        Commands.init();
        Listeners.init();
    }

    public static void freezeAll() {
        LOGGER.trace("Freezing all registries");

        CUSTOM_EMOJI.freeze();
        COMMAND.freeze();
        LISTENER.freeze();
    }
}
