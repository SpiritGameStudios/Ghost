package dev.spiritstudios.ghost.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.audio.AudioSourceBase;

public class LavaplayerAudioSource extends AudioSourceBase {
    private final AudioPlayer player;
    private AudioFrame prevFrame;

    public LavaplayerAudioSource(DiscordApi api, AudioPlayer player) {
        super(api);
        this.player = player;
    }

    @Override
    public byte[] getNextFrame() {
        if (prevFrame == null) return null;
        return this.applyTransformers(prevFrame.getData());
    }

    @Override
    public boolean hasNextFrame() {
        prevFrame = player.provide();
        return prevFrame != null;
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
