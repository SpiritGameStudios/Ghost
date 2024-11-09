package dev.spiritstudios.ghost.data.modrinth;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitedClient {
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final String baseURL;
    private final String userAgent;

    private final @Nullable String apiKey;

    private final OkHttpClient client;

    private final AtomicInteger rateLimitRemaining = new AtomicInteger(0);
    private final AtomicInteger rateLimitReset = new AtomicInteger(0);

    public RateLimitedClient(String baseURL, String userAgent, @Nullable String apiKey, long timeout) {
        this.baseURL = baseURL;
        this.userAgent = userAgent;
        this.apiKey = apiKey;
        this.client = new OkHttpClient.Builder().callTimeout(timeout, TimeUnit.MILLISECONDS).build();
    }

    public CompletableFuture<Void> next() {
        if (rateLimitRemaining.get() <= 0) return CompletableFuture.runAsync(
                () -> {
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(rateLimitReset.get() + 1));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        rateLimitRemaining.decrementAndGet();
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Request.Builder> createRequest(String route, Map<String, String> queryParams) {
        return next().thenApply(ignored -> {
            HttpUrl url;
            if (queryParams == null || queryParams.isEmpty()) url = HttpUrl.get(baseURL + route);
            else {
                HttpUrl.Builder builder = HttpUrl.get(baseURL + route).newBuilder();
                queryParams.forEach(builder::addQueryParameter);
                url = builder.build();
            }

            Request.Builder request = new Request.Builder().url(url);
            request.header("User-Agent", userAgent);
            if (apiKey != null) request.header("Authorization", apiKey);

            return request;
        });
    }

    public CompletableFuture<Response> execute(Request request) {
        return next().thenApply(ignored -> {
            try {
                Response response = client.newCall(request).execute();
                handleRateLimit(response);
                return response;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleRateLimit(Response response) {
        rateLimitRemaining.set(Integer.parseInt(Objects.requireNonNull(response.header("X-RateLimit-Remaining"))));
        rateLimitReset.set(Integer.parseInt(Objects.requireNonNull(response.header("X-RateLimit-Reset"))));
    }
}
