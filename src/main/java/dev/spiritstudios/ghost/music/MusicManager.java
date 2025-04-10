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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class MusicManager extends AudioEventAdapter {
	private static final Map<Long, MusicManager> MANAGERS = new Long2ObjectOpenHashMap<>();

	private final AudioPlayer player;
	private final Queue<AudioTrack> queue = new LinkedBlockingQueue<>();
	private final AudioChannel channel;

	public static MusicManager getOrCreate(AudioChannel channel) {
		return MANAGERS.computeIfAbsent(channel.getIdLong(), key -> new MusicManager(channel));
	}

	public static Optional<MusicManager> get(AudioChannel channel) {
		return Optional.ofNullable(MANAGERS.get(channel.getIdLong()));
	}

	private MusicManager(AudioChannel channel) {
		this.channel = channel;
		this.player = SharedConstants.PLAYER_MANAGER.createPlayer();
		player.addListener(this);

		AudioSendHandler source = new LavaplayerAudioSource(player);
		AudioManager manager = channel.getGuild().getAudioManager();

		manager.openAudioConnection(channel);
		manager.setSendingHandler(source);

		MANAGERS.put(channel.getIdLong(), this);
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

		AudioManager manager = channel.getGuild().getAudioManager();
		manager.closeAudioConnection();

		MANAGERS.remove(channel.getIdLong());
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
			.addField("Length", StringUtil.formatDuration(Duration.ofMillis(info.length)), true)
			.setAuthor(info.author);

		if (track.getInfo().artworkUrl != null) {
			nowPlaying.setThumbnail(info.artworkUrl);
			try {
				BufferedImage image = HttpHelper.getImage(info.artworkUrl);
				nowPlaying.setColor(new Color(ImageHelper.getCommonColor(image)));
			} catch (IOException ignored) {

			}
		}

		if (channel instanceof VoiceChannel voiceChannel)
			voiceChannel.sendMessageEmbeds(nowPlaying.build()).queue();
	}

	public List<MessageEmbed> viewQueue() {
		List<MessageEmbed> embeds = new ArrayList<>();

		queue.forEach(track -> {
			AudioTrackInfo info = track.getInfo();

			EmbedBuilder embed = new EmbedBuilder()
				.setTitle(info.title)
				.setUrl(info.uri)
				.addField("Length", StringUtil.formatDuration(Duration.ofMillis(track.getDuration())), true)
				.setAuthor(info.author)
				.setFooter("%d/%d".formatted(embeds.size() + 1, queue.size()));

			if (info.artworkUrl != null) embed.setThumbnail(info.artworkUrl);

			embeds.add(embed.build());
		});

		return embeds;
	}
}
