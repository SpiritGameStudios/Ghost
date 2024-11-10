package dev.spiritstudios.ghost;

import dev.spiritstudios.ghost.data.CommonColors;
import dev.spiritstudios.ghost.registry.Registries;
import dev.spiritstudios.ghost.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Ghost {
    private static final Logger LOGGER = LogManager.getLogger(Ghost.class);
    private static DiscordApi api;

    public static void main(String[] args) {
        api = new DiscordApiBuilder()
                .setToken(GhostConfig.INSTANCE.token())
                .addIntents(Intent.GUILDS, Intent.GUILD_VOICE_STATES)
                .login()
                .join();

        if (GhostConfig.INSTANCE.debug()) {
            FallbackLoggerConfiguration.setDebug(true);
            api.updateActivity(ActivityType.WATCHING, "Echo struggle");
            LOGGER.debug("Debug mode enabled");
        }

        Registries.init();
        tryRun(TagManager::init, "An error occurred while initializing tags");

        Registries.freezeAll();

        LOGGER.info("Logged in as {}", api.getYourself().getDiscriminatedName());
    }

    public static <T extends Runnable> void tryRun(T runnable, String message) {
        try {
            runnable.run();
        } catch (Throwable t) {
            logError(message, t);
        }
    }

    public static void logError(String message, Throwable t) {
//        if (!GhostConfig.INSTANCE.debug() || GhostConfig.INSTANCE.channelId() <= 0) return;

        StringWriter stackTrace = new StringWriter();
        PrintWriter writer = new PrintWriter(stackTrace);
        t.printStackTrace(writer);

        api.getTextChannelById(GhostConfig.INSTANCE.channelId())
                .ifPresent(channel -> {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(message)
                            .setDescription(StringUtil.truncate(("```lisp\n%s```").formatted(stackTrace.toString()), 4096))
                            .addField("Full message", t.getMessage(), false)
                            .setColor(CommonColors.RED);

                    new MessageBuilder()
                            .setEmbed(embed)
                            .send(channel);
                });
    }

    public static DiscordApi getApi() {
        return api;
    }
}
