package dev.spiritstudios.ghost;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.ghost.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record GhostConfig(
        String token,
        long guildId,
        long channelId,
        String modrinthApiKey,
        boolean debug
) {
    public static final Codec<GhostConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("token").forGetter(GhostConfig::token),
            Codec.LONG.fieldOf("guild_id").forGetter(GhostConfig::guildId),
            Codec.LONG.optionalFieldOf("channel_id", 0L).forGetter(GhostConfig::channelId),
            Codec.STRING.fieldOf("modrinth_api_key").forGetter(GhostConfig::modrinthApiKey),
            Codec.BOOL.optionalFieldOf("debug", false).forGetter(GhostConfig::debug)
    ).apply(instance, GhostConfig::new));

    public static final GhostConfig INSTANCE = Util.make(() -> {
        Path path = Path.of("config.json");

        if (!Files.exists(path)) throw new IllegalArgumentException("Config file does not exist");

        List<String> lines;

        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonElement element = new Gson().fromJson(String.join("\n", lines), JsonElement.class);
        DataResult<GhostConfig> result = GhostConfig.CODEC.parse(JsonOps.INSTANCE, element);

        if (result.error().isPresent())
            throw new IllegalStateException(result.error().toString());

        return result.getOrThrow();
    });
}
