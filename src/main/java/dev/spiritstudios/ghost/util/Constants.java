package dev.spiritstudios.ghost.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.spiritstudios.ghost.Ghost;
import masecla.modrinth4j.client.agent.UserAgent;
import masecla.modrinth4j.main.ModrinthAPI;

public final class Constants {
    public static final ModrinthAPI MODRINTH_API = ModrinthAPI.rateLimited(
            UserAgent.builder()
                    .authorUsername("Spirit Studios")
                    .projectName("Ghost")
                    .build(),
            Ghost.CONFIG.modrinthApiKey()
    );

    private Constants() {
        Util.utilError();
    }
}
