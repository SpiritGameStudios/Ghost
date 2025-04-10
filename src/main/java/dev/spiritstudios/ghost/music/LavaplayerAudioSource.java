package dev.spiritstudios.ghost.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class LavaplayerAudioSource implements AudioSendHandler {
	private final AudioPlayer player;
	private final ByteBuffer buffer;
	private final MutableAudioFrame frame;

	public LavaplayerAudioSource(AudioPlayer player) {
		this.player = player;
		this.buffer = ByteBuffer.allocate(1024);
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(buffer);
	}

	@Override
	public boolean canProvide() {
		return player.provide(frame);
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		return buffer.flip();
	}

	@Override
	public boolean isOpus() {
		return true;
	}
}
