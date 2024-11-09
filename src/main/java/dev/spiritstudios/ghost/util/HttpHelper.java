package dev.spiritstudios.ghost.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import okhttp3.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class HttpHelper {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .create();
    
    public static CompletableFuture<BufferedImage> getImage(String url) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                    .thenApply(response -> {
                        try {
                            return ImageIO.read(response.body());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    public static <T> DataResult<T> checkResponse(ResponseBody body, Codec<T> codec) throws IOException {
        if (body == null) throw new NullPointerException("Response body is null");
        if (body.contentLength() == 0) return null;

        String json = body.string();
        JsonElement element = GSON.fromJson(json, JsonElement.class);

        if (element.isJsonObject() && element.getAsJsonObject().has("error")) {
            String error = element.getAsJsonObject().get("error").getAsString();
            String description = element.getAsJsonObject().get("description").getAsString();


            throw switch (error) {
                case "invalid_input", "unauthorized" -> new IllegalArgumentException(description);
                default -> new RuntimeException("Error: %s, Description: %s".formatted(error, description
                ));
            };
        }

        return codec.parse(JsonOps.INSTANCE, element);
    }


    private HttpHelper() {
        Util.utilError();
    }
}
