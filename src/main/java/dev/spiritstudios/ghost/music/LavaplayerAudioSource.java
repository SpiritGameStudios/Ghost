package dev.spiritstudios.ghost.music;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.audio.AudioSourceBase;

import java.nio.ByteBuffer;

public class LavaplayerAudioSource extends AudioSourceBase {
	private final AudioPlayer player;
	private final MutableAudioFrame frame;

	public LavaplayerAudioSource(DiscordApi api, AudioPlayer player) {
		super(api);
		this.player = player;
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
	}

	@Override
	public byte[] getNextFrame() {
		return this.applyTransformers(frame.getData());
	}

	@Override
	public boolean hasNextFrame() {
		return player.provide(frame);
	}

	@Override
	public AudioSource copy() {
		return new LavaplayerAudioSource(this.getApi(), player);
	}

	@Override
	public boolean hasFinished() {
		return false;
	}
}
