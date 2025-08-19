package dev.spiritstudios.ghost

import dev.kord.core.Kord
import dev.kord.core.entity.ApplicationEmoji
import kotlinx.coroutines.flow.first

object Emoji {
	suspend fun init(kord: Kord) {
		val emoji = kord.emojis

		modrinth = emoji.first { it.name == "modrinth" }
		lexforge = emoji.first { it.name == "lexforge" }
		fabric = emoji.first { it.name == "fabric" }
		neoforge = emoji.first { it.name == "neoforge" }
		unknown = emoji.first { it.name == "unknown" }
		quilt = emoji.first { it.name == "quilt" }
		paper = emoji.first { it.name == "paper" }
		spigot = emoji.first { it.name == "spigot" }
		velocity = emoji.first { it.name == "velocity" }
		bukkit = emoji.first { it.name == "bukkit" }
		minecraft = emoji.first { it.name == "minecraft" }
		purpur = emoji.first { it.name == "purpur" }
		waterfall = emoji.first { it.name == "waterfall" }
		sponge = emoji.first { it.name == "sponge" }
		rift = emoji.first { it.name == "rift" }
		risugamisModLoader = emoji.first { it.name == "risugamismodloader" }
		liteloader = emoji.first { it.name == "liteloader" }
		folia = emoji.first { it.name == "folia" }
		bungeecord = emoji.first { it.name == "bungeecord" }
	}

	lateinit var modrinth: ApplicationEmoji private set
	lateinit var lexforge: ApplicationEmoji private set
	lateinit var fabric: ApplicationEmoji private set
	lateinit var neoforge: ApplicationEmoji private set
	lateinit var unknown: ApplicationEmoji private set
	lateinit var quilt: ApplicationEmoji private set
	lateinit var paper: ApplicationEmoji private set
	lateinit var spigot: ApplicationEmoji private set
	lateinit var velocity: ApplicationEmoji private set
	lateinit var bukkit: ApplicationEmoji private set
	lateinit var minecraft: ApplicationEmoji private set
	lateinit var purpur: ApplicationEmoji private set
	lateinit var waterfall: ApplicationEmoji private set
	lateinit var sponge: ApplicationEmoji private set
	lateinit var rift: ApplicationEmoji private set
	lateinit var risugamisModLoader: ApplicationEmoji private set
	lateinit var liteloader: ApplicationEmoji private set
	lateinit var folia: ApplicationEmoji private set
	lateinit var bungeecord: ApplicationEmoji private set
}
