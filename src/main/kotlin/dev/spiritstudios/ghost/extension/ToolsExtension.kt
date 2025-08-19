package dev.spiritstudios.ghost.extension

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.behavior.createEmoji
import dev.kord.core.entity.effectiveName
import dev.kord.rest.Image
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.DISCORD_RED
import dev.kordex.core.DISCORD_YELLOW
import dev.kordex.core.DiscordRelayedException
import dev.kordex.core.checks.anyGuild
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalUser
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.spiritstudios.ghost.i18n.Translations
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class ToolsExtension : Extension() {
	override val name = "tools"

	override suspend fun setup() {
		publicSlashCommand {
			name = Translations.Command.Ping.name
			description = Translations.Command.Ping.description

			action {
				// Should never be null if this command reaches the bot, but better to be safe
				val gatewayLatency = event.kord.gateway.averagePing
					?: throw DiscordRelayedException(Translations.Command.Ping.Error.failedToGetLatency)

				respond {
					embed {
						title = "Pong!"

						field {
							name = "Gateway Latency"
							value = gatewayLatency.toString(DurationUnit.MILLISECONDS)
							inline = true
						}

						color =
							if (gatewayLatency < 150.milliseconds) {
								DISCORD_GREEN
							} else if (gatewayLatency < 250.milliseconds) {
								DISCORD_YELLOW
							} else {
								DISCORD_RED
							}
					}
				}
			}
		}

		publicSlashCommand(::AvatarArguments) {
			name = Translations.Command.Avatar.name
			description = Translations.Command.Avatar.description

			action {
				val target = arguments.target ?: user.asUser()
				val avatar = target.avatar ?: throw DiscordRelayedException(Translations.Command.Avatar.Error.noAvatar);

				respond {
					embed {
						title = "Avatar of ${target.effectiveName}"
						image = avatar.cdnUrl.toUrl()
						url = avatar.cdnUrl.toUrl()
					}

					actionRow {
						linkButton(avatar.cdnUrl.toUrl { format = Image.Format.PNG }) { label = "Download as PNG" }
						linkButton(avatar.cdnUrl.toUrl { format = Image.Format.JPEG }) { label = "Download as JPEG" }
						linkButton(avatar.cdnUrl.toUrl { format = Image.Format.WEBP }) { label = "Download as WebP" }

						if (avatar.isAnimated) {
							linkButton(avatar.cdnUrl.toUrl { format = Image.Format.GIF }) {
								label = "Download as GIF"
							}
						}
					}
				}
			}
		}

		publicSlashCommand(::GrabEmojiArguments) {
			name = Translations.Command.GrabEmoji.name
			description = Translations.Command.GrabEmoji.description

			check { anyGuild() }
			check { requireBotPermissions(Permission.CreateGuildExpressions) }
			check { requirePermission(Permission.CreateGuildExpressions) }

			action {
				guild!!.createEmoji( // Safe assertion, we already do a check above
					arguments.name,
					Image.fromUrl(this@publicSlashCommand.kord.resources.httpClient, arguments.url)
				) {
					reason = "Created by ${user.asUser().username} using /grabemoji"
				}

				respond {
					embed {
						title = "Emoji Created"
						description = "Emoji `:${arguments.name}:` has been created."
						color = DISCORD_GREEN

						thumbnail { url = arguments.url }
					}
				}
			}
		}
	}

	class AvatarArguments : Arguments() {
		val target by optionalUser {
			name = Translations.Command.Avatar.Target.name
			description = Translations.Command.Avatar.Target.description
		}
	}

	class GrabEmojiArguments : Arguments() {
		val name by string {
			name = Translations.Command.GrabEmoji.Name.name
			description = Translations.Command.GrabEmoji.Name.description
		}

		val url by string {
			name = Translations.Command.GrabEmoji.Url.name
			description = Translations.Command.GrabEmoji.Url.description
		}
	}
}
