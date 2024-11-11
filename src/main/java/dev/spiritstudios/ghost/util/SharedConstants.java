package dev.spiritstudios.ghost.util;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import dev.callmeecho.maze.Labrinth;
import dev.callmeecho.maze.RateLimitedClient;
import dev.spiritstudios.ghost.GhostConfig;

public final class SharedConstants {
	public static final Labrinth MODRINTH_API = new Labrinth(RateLimitedClient.builder()
		.apiKey(GhostConfig.INSTANCE.modrinthApiKey())
		.baseUrl("https://api.modrinth.com/v2")
		.userAgent("SpiritGameStudios/Ghost (spiritstudios.dev)")
		.build());

	public static final AudioPlayerManager PLAYER_MANAGER = Util.make(
		new DefaultAudioPlayerManager(),
		manager -> {
			manager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
			manager.registerSourceManager(new BandcampAudioSourceManager());
		}
	);

	private SharedConstants() {
		Util.utilError();
	}
}
