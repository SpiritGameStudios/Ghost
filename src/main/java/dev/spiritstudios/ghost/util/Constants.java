package dev.spiritstudios.ghost.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.data.modrinth.RateLimitedClient;

public final class Constants {
    public static final RateLimitedClient MODRINTH_API = new RateLimitedClient(
            "https://api.modrinth.com/v2",
            "Ghost Discord Bot/Spirit Studios",
            Ghost.CONFIG.modrinthApiKey(),
            0
    );

    private Constants() {
        Util.utilError();
    }
}
