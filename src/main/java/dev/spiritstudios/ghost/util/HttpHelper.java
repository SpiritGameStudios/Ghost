package dev.spiritstudios.ghost.util;

import net.dv8tion.jda.api.entities.Icon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class HttpHelper {
	private static final OkHttpClient client = new OkHttpClient();

	public static Response get(String url) throws IOException {
		Request request = new Request.Builder()
			.url(url)
			.header("User-Agent", "Ghost Discord Bot/CallMeEcho")
			.build();


		Response response = client.newCall(request).execute();
		if (response.body() == null)
			throw new NullPointerException("Body of HTTP response from %s empty".formatted(url));

		return response;
	}

	public static BufferedImage getImage(String url) throws IOException {
		try (Response response = get(url)) {
			return ImageIO.read(Objects.requireNonNull(response.body()).byteStream());
		}
	}

	public static Icon getIcon(String url) throws IOException {
		try (Response response = get(url)) {
			return Icon.from(Objects.requireNonNull(response.body()).byteStream());
		}
	}

	private HttpHelper() {
		Util.utilError();
	}
}
