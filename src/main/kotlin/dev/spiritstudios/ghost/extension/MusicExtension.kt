@file:OptIn(KordVoice::class)

package dev.spiritstudios.ghost.extension

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.tools.Units
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.kord.common.Color
import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.BaseVoiceChannelBehavior
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.channel.connect
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.event.Event
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.voice.AudioFrame
import dev.kord.voice.VoiceConnection
import dev.kordex.core.DiscordRelayedException
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.checks.memberFor
import dev.kordex.core.checks.types.CheckContext
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import dev.spiritstudios.ghost.util.commonColor
import dev.spiritstudios.ghost.i18n.Translations
import kotlinx.coroutines.launch
import javax.imageio.ImageIO
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MusicExtension : Extension() {
	override val name = "music"

	override suspend fun setup() {
		publicSlashCommand {
			name = Translations.Command.Music.name
			description = Translations.Command.Music.description

			check { anyGuild() }

			publicSubCommand(::PlayArguments) {
				name = Translations.Command.Music.Play.name
				description = Translations.Command.Music.Play.description

				check { callerInVoiceChannel() }

				action {
					val channel = member?.getVoiceState()?.getChannelOrNull()!! // Safe, already checked above
					val manager = MusicManager.getOrCreate(channel)

					val reference =
						if (arguments.song.startsWith("https://") || arguments.song.contains("search:")) {
							AudioReference(
								arguments.song,
								null
							)
						} else {
							AudioReference(
								"scsearch:" + arguments.song,
								null
							)
						}

					val tracks = manager.push(reference)

					if (tracks.size == 1) {
						respond {
							content = "Added ${tracks.first().info.title} to the queue."
						}
					} else {
						respond {
							content = "Added ${tracks.size} tracks to the queue."
						}
					}
				}
			}

			publicSubCommand {
				name = Translations.Command.Music.Stop.name
				description = Translations.Command.Music.Stop.description

				check { callerInVoiceChannel() }
				check { musicPlaying() }

				action {
					val channel = member?.getVoiceState()?.getChannelOrNull()!! // Safe, already checked above
					val manager = MusicManager.get(channel)!! // Safe, already checked above

					manager.shutdown()

					respond { content = "Music stopped." }
				}
			}

			publicSubCommand {
				name = Translations.Command.Music.Pause.name
				description = Translations.Command.Music.Pause.description

				check { callerInVoiceChannel() }
				check { musicPlaying() }

				action {
					val channel = member?.getVoiceState()?.getChannelOrNull()!! // Safe, already checked above
					val manager = MusicManager.get(channel)!! // Safe, already checked above

					manager.player.isPaused = true

					respond { content = "Music paused." }
				}
			}

			publicSubCommand {
				name = Translations.Command.Music.Unpause.name
				description = Translations.Command.Music.Unpause.description

				check { callerInVoiceChannel() }
				check { musicPlaying() }

				action {
					val channel = member?.getVoiceState()?.getChannelOrNull()!! // Safe, already checked above
					val manager = MusicManager.get(channel)!! // Safe, already checked above

					manager.player.isPaused = false

					respond { content = "Music resumed." }
				}
			}

			publicSubCommand {
				name = Translations.Command.Music.Skip.name
				description = Translations.Command.Music.Skip.description

				check { callerInVoiceChannel() }
				check { musicPlaying() }

				action {
					val channel = member?.getVoiceState()?.getChannelOrNull()!! // Safe, already checked above
					val manager = MusicManager.get(channel)!! // Safe, already checked above

					val currentTrack = manager.player.playingTrack
					manager.pop()

					respond { content = "Skipped track \"${currentTrack.info.title}\"." }
				}
			}

			publicSubCommand {
				name = Translations.Command.Music.Queue.name
				description = Translations.Command.Music.Queue.description

				check { callerInVoiceChannel() }
				check { musicPlaying() }

				action {
					val channel = member?.getVoiceState()?.getChannelOrNull()!! // Safe, already checked above
					val manager = MusicManager.get(channel)!! // Safe, already checked above

					editingPaginator {
						timeoutSeconds = 60

						for (track in manager.queue) {
							page { trackInfo(track, this@publicSlashCommand.kord) }
						}
					}.send()
				}
			}

			publicSubCommand {
				name = Translations.Command.Music.Shuffle.name
				description = Translations.Command.Music.Shuffle.description

				check { callerInVoiceChannel() }
				check { musicPlaying() }

				action {
					val channel = member?.getVoiceState()?.getChannelOrNull()!! // Safe, already checked above
					val manager = MusicManager.get(channel)!! // Safe, already checked above

					manager.queue.shuffle()

					respond { content = "Shuffled the queue." }
				}
			}
		}
	}

	class PlayArguments : Arguments() {
		val song by string {
			name = Translations.Command.Music.Play.Song.name
			description = Translations.Command.Music.Play.Song.description
		}
	}
}

private suspend fun CheckContext<Event>.callerInVoiceChannel() {
	failIf(Translations.Command.Music.Error.notInVc) {
		memberFor(event)?.getVoiceStateOrNull()?.getChannelOrNull() == null
	}
}

private suspend fun CheckContext<Event>.musicPlaying() {
	failIf(Translations.Command.Music.Error.noMusic) {
		MusicManager.get(memberFor(event)?.getVoiceStateOrNull()?.getChannelOrNull()!!) == null
	}
}

suspend fun EmbedBuilder.trackInfo(track: AudioTrack, kord: Kord) {
	title = track.info.title
	url = track.info.uri

	field {
		name = "Length"
		value = if (track.info.length == Units.DURATION_MS_UNKNOWN) "Unknown"
		else track.info.length.milliseconds.inWholeSeconds.seconds.toString()
	}

	author {
		name = track.info.author
	}

	if (track.info.artworkUrl != null) {
		thumbnail {
			url = track.info.artworkUrl
		}

		val image = ImageIO.read(
			Image.fromUrl(
				kord.resources.httpClient,
				track.info.artworkUrl
			).data.inputStream()
		)

		color = Color(image.commonColor())
	}
}

class MusicManager private constructor(
	val player: AudioPlayer,
	val channel: BaseVoiceChannelBehavior,
	val connection: VoiceConnection
) : AudioEventAdapter() {
	companion object {
		suspend fun getOrCreate(channel: BaseVoiceChannelBehavior): MusicManager {
			return managers.getOrPut(channel.id) {
				val player = playerManager.createPlayer()

				MusicManager(
					player,
					channel,
					channel.connect {
						audioProvider { AudioFrame.fromData(player.provide()?.data) }
						selfDeaf = true
					}
				)
			}
		}

		fun get(channel: Snowflake): MusicManager? {
			return managers[channel]
		}

		fun get(channel: BaseVoiceChannelBehavior): MusicManager? = get(channel.id)


		private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager().apply {
			registerSourceManagers(
				SoundCloudAudioSourceManager.createDefault(),
				BandcampAudioSourceManager(),
				HttpAudioSourceManager()
			)
		}

		private val managers: MutableMap<Snowflake, MusicManager> = mutableMapOf()
	}

	val queue = ArrayDeque<AudioTrack>()

	init {
		player.addListener(this)
	}

	suspend fun push(query: AudioReference): List<AudioTrack> {
		val tracks = suspendCoroutine {
			playerManager.loadItem(query, object : AudioLoadResultHandler {
				override fun trackLoaded(track: AudioTrack) {
					it.resume(listOf(track))
				}

				override fun playlistLoaded(playlist: AudioPlaylist) {
					it.resume(playlist.tracks)
				}

				override fun noMatches() {
					throw DiscordRelayedException(Translations.Command.Music.Error.songNotFound)
				}

				override fun loadFailed(exception: FriendlyException?) {
					throw DiscordRelayedException(
						exception?.message?.toKey() ?: Translations.Command.Music.Error.genericLoadFailure
					)
				}
			})
		}

		for (track in tracks) {
			val trackStarted = player.startTrack(track, true)

			if (!trackStarted) queue.add(track)
			else nowPlaying(track)
		}

		return tracks
	}

	suspend fun pop() {
		val track = queue.removeFirstOrNull()

		if (track == null) {
			shutdown()
			return
		}

		player.startTrack(track, false)

		nowPlaying(track)
	}

	private suspend fun nowPlaying(track: AudioTrack) {
		channel.asChannelOf<VoiceChannel>().createMessage {
			embed { trackInfo(track, channel.kord) }
		}
	}

	override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
		connection.scope.launch {
			if (endReason!!.mayStartNext) pop()
		}
	}

	suspend fun shutdown() {
		managers -= channel.id
		connection.shutdown()
		player.destroy()
	}
}


