package dev.spiritstudios.ghost.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.spiritstudios.ghost.command.CommandWithSubcommands;
import dev.spiritstudios.ghost.command.util.EmbedUtil;
import dev.spiritstudios.ghost.data.CommonColors;
import dev.spiritstudios.ghost.music.MusicManager;
import dev.spiritstudios.ghost.util.Constants;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MusicCommand implements CommandWithSubcommands {
    @Override
    public String getName() {
        return "music";
    }

    @Override
    public SlashCommandBuilder createSlashCommand() {
        return SlashCommand.with(getName(), "Play music from SoundCloud or Bandcamp in a voice channel")
                .addOption(SlashCommandOption.createSubcommand(
                        "play",
                        "Play a song",
                        List.of(SlashCommandOption.createStringOption("song", "The song to play, either a URL or a search term", true))
                ))
                .addOption(SlashCommandOption.createSubcommand(
                        "pause",
                        "Pause the current song"
                ))
                .addOption(SlashCommandOption.createSubcommand(
                        "unpause",
                        "Resume the current song"
                ))
                .addOption(SlashCommandOption.createSubcommand(
                        "stop",
                        "Stop playing music"
                ))
                .addOption(SlashCommandOption.createSubcommand(
                        "skip",
                        "Skip the current song"
                ));
    }

    @Override
    public Map<String, Subcommand> getSubcommands() {
        return Map.of(
                "play", MusicCommand::play,
                "pause", (interaction, options, api) -> setPaused(true, interaction),
                "unpause", (interaction, options, api) -> setPaused(false, interaction),
                "stop", MusicCommand::stop,
                "skip", MusicCommand::skip
        );
    }

    private static void skip(SlashCommandInteraction interaction, SlashCommandInteractionOptionsProvider options, DiscordApi api) {
        Optional<ServerVoiceChannel> channel = interaction.getUser().getConnectedVoiceChannel(interaction.getServer().orElseThrow());

        if (channel.isEmpty()) {
            interaction.createImmediateResponder()
                    .addEmbed(EmbedUtil.error("You are not currently in a voice channel"))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();

            return;
        }

        MusicManager.getExisting(channel.get()).ifPresentOrElse(
                scheduler -> {
                    scheduler.pop();
                    interaction.createImmediateResponder()
                            .addEmbed(EmbedUtil.titleOnly("Song Skipped", CommonColors.GREEN)).respond();
                },
                () -> interaction.createImmediateResponder()
                        .addEmbed(EmbedUtil.error("No music is currently playing")).setFlags(MessageFlag.EPHEMERAL).respond()
        );
    }

    private static void stop(SlashCommandInteraction interaction, SlashCommandInteractionOptionsProvider options, DiscordApi api) {
        Optional<ServerVoiceChannel> channel = interaction.getUser().getConnectedVoiceChannel(interaction.getServer().orElseThrow());

        if (channel.isEmpty()) {
            interaction.createImmediateResponder()
                    .addEmbed(EmbedUtil.error("You are not currently in a voice channel"))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();

            return;
        }

        MusicManager.getExisting(channel.get()).ifPresentOrElse(
                scheduler -> {
                    scheduler.destroy();
                    interaction.createImmediateResponder()
                            .addEmbed(EmbedUtil.titleOnly("Music Stopped", CommonColors.GREEN)).respond();
                },
                () -> interaction.createImmediateResponder()
                        .addEmbed(EmbedUtil.error("No music is currently playing")).setFlags(MessageFlag.EPHEMERAL).respond()
        );
    }

    private static void setPaused(boolean value, SlashCommandInteraction interaction) {
        Optional<ServerVoiceChannel> channel = interaction.getUser().getConnectedVoiceChannel(interaction.getServer().orElseThrow());

        if (channel.isEmpty()) {
            interaction.createImmediateResponder()
                    .addEmbed(EmbedUtil.error("You are not currently in a voice channel"))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();

            return;
        }

        MusicManager.getExisting(channel.get()).ifPresentOrElse(
                scheduler -> {
                    scheduler.setPaused(value);
                    interaction.createImmediateResponder()
                            .addEmbed(EmbedUtil.titleOnly(value ? "Music Paused" : "Music Resumed", CommonColors.BLURPLE)).respond();
                },
                () -> interaction.createImmediateResponder()
                        .addEmbed(EmbedUtil.error("No music is currently playing")).setFlags(MessageFlag.EPHEMERAL).respond()
        );
    }

    private static void play(SlashCommandInteraction interaction, SlashCommandInteractionOptionsProvider options, DiscordApi api) {
        String identifier = options.getOptionByName("song")
                .flatMap(SlashCommandInteractionOption::getStringValue)
                .orElseThrow();

        interaction.respondLater().thenAccept(updater -> {
            Optional<ServerVoiceChannel> channel = interaction.getUser().getConnectedVoiceChannel(interaction.getServer().orElseThrow());

            if (channel.isEmpty()) {
                updater
                        .addEmbed(EmbedUtil.error("You are not currently in a voice channel"))
                        .setFlags(MessageFlag.EPHEMERAL)
                        .update().thenAccept(ignored -> {
                        });

                return;
            }

            MusicManager scheduler = MusicManager.getOrCreate(channel.get());

            Constants.PLAYER_MANAGER.loadItem(identifier, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    scheduler.push(track);
                    updater.addEmbed(EmbedUtil.titleOnly("Song added to queue", CommonColors.GREEN)).update();
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) scheduler.push(track);
                    updater.addEmbed(EmbedUtil.titleOnly("Playlist added to queue", CommonColors.GREEN)).update();
                }

                @Override
                public void noMatches() {
                    updater
                            .addEmbed(EmbedUtil.error("No match found"))
                            .setFlags(MessageFlag.EPHEMERAL)
                            .update();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    updater
                            .addEmbed(EmbedUtil.error(exception.getMessage()))
                            .setFlags(MessageFlag.EPHEMERAL)
                            .update();
                }
            });
        });
    }
}

