package dev.spiritstudios.ghost.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.util.SharedConstants;
import dev.spiritstudios.ghost.util.HttpHelper;
import dev.spiritstudios.ghost.util.ImageHelper;
import dev.spiritstudios.ghost.util.StringUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MusicManager extends AudioEventAdapter {
	private static final Map<Long, MusicManager> MANAGERS = new Long2ObjectOpenHashMap<>();

	private final AudioPlayer player;
	private final Queue<AudioTrack> queue = new LinkedBlockingQueue<>();
	private final ServerVoiceChannel channel;
	private AudioConnection connection;

	public static MusicManager getOrCreate(ServerVoiceChannel channel) {
		return MANAGERS.computeIfAbsent(channel.getId(), key -> new MusicManager(channel));
	}

	public static Optional<MusicManager> get(ServerVoiceChannel channel) {
		return Optional.ofNullable(MANAGERS.get(channel.getId()));
	}

	private MusicManager(ServerVoiceChannel channel) {
		this.channel = channel;
		this.player = SharedConstants.PLAYER_MANAGER.createPlayer();
		player.addListener(this);

		AudioSource source = new LavaplayerAudioSource(Ghost.getApi(), player);

		channel.connect().thenAccept(connection -> {
			connection.setAudioSource(source);
			this.connection = connection;
		});

		MANAGERS.put(channel.getId(), this);
	}

	public void push(AudioTrack track) {
		boolean didStartTrack = player.startTrack(track, true);
		if (!didStartTrack) queue.offer(track);
		else nowPlaying(track);
	}

	public void pop() {
		AudioTrack track = queue.poll();
		player.startTrack(track, false);

		if (track == null) {
			destroy();
			return;
		}

		nowPlaying(track);
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public void destroy() {
		player.destroy();
		connection.close();
		MANAGERS.remove(channel.getId());
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) pop();
	}

	public void setPaused(boolean value) {
		player.setPaused(value);
	}

	private void nowPlaying(AudioTrack track) {
		AudioTrackInfo info = track.getInfo();

		EmbedBuilder nowPlaying = new EmbedBuilder()
			.setTitle(info.title)
			.setUrl(info.uri)
			.addInlineField("Length", StringUtil.formatDuration(Duration.ofMillis(info.length)))
			.setAuthor(info.author);

		if (track.getInfo().artworkUrl != null) {
			nowPlaying.setThumbnail(info.artworkUrl);
			HttpHelper.getImage(info.artworkUrl)
				.thenCompose(icon -> {
					nowPlaying.setColor(new Color(ImageHelper.getCommonColor(icon)));
					return channel.sendMessage(nowPlaying);
				}).whenComplete((ignored, throwable) -> {
					if (throwable == null) return;

					Ghost.logError("", throwable);
				});

			return;
		}

		channel.sendMessage(nowPlaying);
	}
}
