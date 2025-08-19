package dev.spiritstudios.ghost.extension

import dev.kord.common.Color
import dev.kord.rest.builder.message.embed
import dev.kord.rest.route.DiscordCdn.emoji
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.publicSubCommand
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.spiritstudios.ghost.Emoji
import dev.spiritstudios.ghost.i18n.Translations
import dev.spiritstudios.ghost.util.truncate
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ModrinthExtension : Extension() {
	override val name = "modrinth"

	private val client = HttpClient {
		install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
		install(UserAgent) { agent = "SpiritStudios/Ghost (@CallMeEcho on discord)" }

		defaultRequest {
			host = "api.modrinth.com/v2"
			url { protocol = URLProtocol.HTTPS }
		}
	}

	override suspend fun setup() {
		publicSlashCommand {
			name = Translations.Command.Modrinth.name
			description = Translations.Command.Modrinth.description

			publicSubCommand(::InfoArguments) {
				name = Translations.Command.Modrinth.Info.name
				description = Translations.Command.Modrinth.Info.description

				action {
					val project: Project = client.get { url { path("project/${arguments.slug}") } }.body()

					respond {
						embed {
							title = project.title
							url = "https://modrinth.com/${project.type.urlComponent}/${project.slug}"
							description = project.description

							field {
								name = "Categories"
								value = project.categories.joinToString("\n") {
									it.replaceFirstChar { char -> char.titlecase() }
								}

								inline = true
							}

							if (project.loaders.isNotEmpty()) {
								field {
									name = "Loaders"
									value = project.loaders.map { it.replaceFirstChar { char -> char.titlecase() } }
										.joinToString("\n") { loader ->
											"${
												when (loader) {
													"Fabric" -> Emoji.fabric.mention
													"Forge" -> Emoji.lexforge.mention
													"Neoforge" -> Emoji.neoforge.mention
													"Quilt" -> Emoji.quilt.mention
													"Paper" -> Emoji.paper.mention
													"Spigot" -> Emoji.spigot.mention
													"Velocity" -> Emoji.velocity.mention
													"Bukkit" -> Emoji.bukkit.mention
													"Minecraft" -> Emoji.minecraft.mention
													"Purpur" -> Emoji.purpur.mention
													"Waterfall" -> Emoji.waterfall.mention
													"Sponge" -> Emoji.sponge.mention
													"Rift" -> Emoji.rift.mention
													"Modloader" -> Emoji.risugamisModLoader.mention
													"Liteloader" -> Emoji.liteloader.mention
													"Folia" -> Emoji.folia.mention
													"Bungeecord" -> Emoji.bungeecord.mention
													else -> Emoji.unknown.mention
												}
											} $loader"
										}

									inline = true
								}
							}
							if (project.gameVersions.isNotEmpty()) {
								field {
									name = "Versions"
									value = project.gameVersions.joinToString("\n").truncate(256)
									inline = true
								}
							}

							if (project.iconUrl != null) {
								thumbnail { url = project.iconUrl }
							}

							color = project.color?.let { rgb -> Color(rgb) }

							timestamp = Clock.System.now()
							footer {
								text = "${project.type.humanName} on Modrinth"
								icon = emoji(Emoji.modrinth.id).toUrl()
							}
						}
					}
				}
			}
		}
	}

	class InfoArguments : Arguments() {
		val slug by string {
			name = Translations.Command.Modrinth.Info.Slug.name
			description = Translations.Command.Modrinth.Info.Slug.description
		}
	}

	@Serializable
	data class Project(
		val slug: String,
		val title: String,
		val description: String,
		val categories: List<String>,
		@SerialName("client_side") val clientSide: SideSupport,
		@SerialName("server_side") val serverSide: SideSupport,
		val body: String,
		val status: ProjectStatus,
		@SerialName("requested_status") val requestedStatus: ProjectStatus?,
		@SerialName("additional_categories") val additionalCategories: List<String>,
		@SerialName("issues_url") val issuesUrl: String?,
		@SerialName("source_url") val sourceUrl: String?,
		@SerialName("wiki_url") val wikiUrl: String?,
		@SerialName("discord_url") val discordUrl: String?,
		@SerialName("donation_urls") val donationUrls: List<DonationLink>,
		@SerialName("project_type") val type: ProjectType,
		val downloads: Int,
		@SerialName("icon_url") val iconUrl: String?,
		val color: Int?,
		val followers: Int,
		val license: License,
		@SerialName("game_versions") val gameVersions: List<String>,
		val loaders: List<String>
	);

	@Serializable
	enum class SideSupport {
		@SerialName("required")
		REQUIRED,

		@SerialName("optional")
		OPTIONAL,

		@SerialName("unsupported")
		UNSUPPORTED,

		@SerialName("unknown")
		UNKNOWN
	}

	@Serializable
	enum class ProjectStatus {
		@SerialName("approved")
		APPROVED,

		@SerialName("archived")
		ARCHIVED,

		@SerialName("rejected")
		REJECTED,

		@SerialName("draft")
		DRAFT,

		@SerialName("unlisted")
		UNLISTED,

		@SerialName("processing")
		PROCESSING,

		@SerialName("withheld")
		WITHHELD,

		@SerialName("scheduled")
		SCHEDULED,

		@SerialName("private")
		PRIVATE,

		@SerialName("unknown")
		UNKNOWN
	}

	@Serializable
	data class DonationLink(
		val id: String,
		val platform: String,
		val url: String
	)

	@Serializable
	data class License(
		val id: String,
		val name: String,
		val url: String?
	)

	@Serializable
	enum class ProjectType(val urlComponent: String, val humanName: String) {
		@SerialName("mod")
		MOD("mod", "Mod"),

		@SerialName("modpack")
		MOD_PACK("modpack", "Mod Pack"),

		@SerialName("resourcepack")
		RESOURCE_PACK("resourcepack", "Resource Pack"),

		@SerialName("shader")
		SHADER_PACK("shader", "Shader Pack")
	}
}
