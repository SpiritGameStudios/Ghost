package dev.spiritstudios.ghost.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.spiritstudios.ghost.command.CommandContext;
import dev.spiritstudios.ghost.command.CommandWithSubcommands;
import dev.spiritstudios.ghost.data.CommonColors;
import dev.spiritstudios.ghost.menu.Paginator;
import dev.spiritstudios.ghost.music.MusicManager;
import dev.spiritstudios.ghost.util.EmbedUtil;
import dev.spiritstudios.ghost.util.SharedConstants;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MusicCommand implements CommandWithSubcommands {
	@Override
	public String getName() {
		return "music";
	}

	@Override
	public SlashCommandData createSlashCommand() {
		return Commands.slash(getName(), "Play music from SoundCloud or Bandcamp in a voice channel")
			.addSubcommands(
				new SubcommandData(
					"play",
					"Play a song"
				).addOption(
					OptionType.STRING,
					"song",
					"The song to play, either a URL or a search term",
					true
				),
				new SubcommandData(
					"pause",
					"Pause the current song"
				),
				new SubcommandData(
					"unpause",
					"Resume the current song"
				),
				new SubcommandData(
					"stop",
					"Stop playing music"
				),
				new SubcommandData(
					"skip",
					"Skip the current song"
				),
				new SubcommandData(
					"queue",
					"View the contents of the queue"
				)
			);
	}

	@Override
	public Map<String, Subcommand> getSubcommands() {
		return Map.of(
			"play", MusicCommand::play,
			"pause", (context) -> setPaused(true, context),
			"unpause", (context) -> setPaused(false, context),
			"stop", MusicCommand::stop,
			"skip", MusicCommand::skip,
			"queue", MusicCommand::queue
		);
	}

	private static void queue(CommandContext context) {
		Optional<AudioChannel> channel = channel(context);

		if (channel.isEmpty()) {
			notInVcError(context);
			return;
		}

		MusicManager.get(channel.get()).ifPresentOrElse(
			scheduler -> {
				List<MessageEditData> pages = scheduler.viewQueue().stream()
					.map(MessageEditData::fromEmbeds)
					.toList();

				Paginator paginator = new Paginator(pages);
				paginator.send(context.interaction());
			},
			() -> noMusicError(context)
		);
	}

	private static void skip(CommandContext context) {
		Optional<AudioChannel> channel = channel(context);

		if (channel.isEmpty()) {
			notInVcError(context);
			return;
		}

		MusicManager.get(channel.get()).ifPresentOrElse(
			scheduler -> {
				scheduler.pop();
				context.reply(EmbedUtil.titleOnly("Song Skipped", CommonColors.GREEN)).queue();
			},
			() -> noMusicError(context)
		);
	}

	private static void stop(CommandContext context) {
		Optional<AudioChannel> channel = channel(context);

		if (channel.isEmpty()) {
			notInVcError(context);
			return;
		}

		MusicManager.get(channel.get()).ifPresentOrElse(
			scheduler -> {
				scheduler.destroy();
				context.reply(EmbedUtil.titleOnly("Music Stopped", CommonColors.GREEN)).queue();
			},
			() -> noMusicError(context)
		);
	}

	private static void setPaused(boolean value, CommandContext context) {
		Optional<AudioChannel> channel = channel(context);

		if (channel.isEmpty()) {
			notInVcError(context);
			return;
		}

		MusicManager.get(channel.get()).ifPresentOrElse(
			scheduler -> {
				scheduler.setPaused(value);
				context.reply(EmbedUtil.titleOnly(value ? "Music Paused" : "Music Resumed", CommonColors.BLURPLE)).queue();
			},
			() -> context.reply(EmbedUtil.error("No music is currently playing"))
				.setEphemeral(true)
				.queue()
		);
	}

	private static void play(CommandContext context) {
		String identifier = context.getStringOption("song").orElseThrow();

		Optional<AudioChannel> channel = channel(context);

		if (channel.isEmpty()) {
			notInVcError(context);
			return;
		}

		context.defer().queue(hook -> {
			MusicManager scheduler = MusicManager.getOrCreate(channel.get());

			AudioReference reference = identifier.startsWith("https://") || identifier.startsWith("bcsearch:") ?
				new AudioReference(identifier, null) :
				new AudioReference("scsearch:" + identifier, null);

			SharedConstants.PLAYER_MANAGER.loadItem(reference, new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack track) {
					scheduler.push(track);
					hook.sendMessageEmbeds(EmbedUtil.titleOnly("Song added to queue", CommonColors.GREEN)).queue();
				}

				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
					if (!(reference.identifier.contains("search:"))) {
						for (AudioTrack track : playlist.getTracks()) scheduler.push(track);
						hook.sendMessageEmbeds(EmbedUtil.titleOnly("Playlist added to queue", CommonColors.GREEN)).queue();
					} else {
						trackLoaded(playlist.getTracks().getFirst());
					}
				}

				@Override
				public void noMatches() {
					hook.sendMessageEmbeds(EmbedUtil.error("No match found")).queue();
				}

				@Override
				public void loadFailed(FriendlyException exception) {
					hook.sendMessageEmbeds(EmbedUtil.error(exception.getMessage())).queue();
				}
			});
		});
	}

	private static void notInVcError(CommandContext interaction) {
		interaction.reply(EmbedUtil.error("You are not currently in a voice channel"))
			.setEphemeral(true)
			.queue();
	}

	private static void noMusicError(CommandContext interaction) {
		interaction.reply(EmbedUtil.error("No music is currently playing"))
			.setEphemeral(true)
			.queue();
	}

	private static Optional<AudioChannel> channel(CommandContext interaction) {
		return interaction.member()
			.map(Member::getVoiceState)
			.map(GuildVoiceState::getChannel);
	}
}

