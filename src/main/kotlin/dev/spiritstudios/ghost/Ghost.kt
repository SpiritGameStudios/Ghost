package dev.spiritstudios.ghost

import dev.kord.common.entity.Snowflake
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.utils.env
import dev.kordex.core.utils.envOrNull
import dev.kordex.data.api.DataCollection
import dev.kordex.modules.func.phishing.extPhishing
import dev.spiritstudios.ghost.extension.*

suspend fun main() {
	val bot = ExtensibleBot(env("TOKEN")) {
		extensions {
			if (this@ExtensibleBot.devMode) add(::DebugExtension)
			add(::ToolsExtension)
			add(::MusicExtension)
			add(::TagsExtension)
			add(::ModrinthExtension)

			extPhishing {
				logChannelName = "\uD83D\uDD12︱logs"
			}
		}

		applicationCommands {
			defaultGuild = envOrNull("DEBUG_GUILD")?.let { Snowflake(it) }
		}

		presence {
			if (this@ExtensibleBot.devMode) {
				watching("Echo struggle")
			}
		}

		dataCollectionMode = DataCollection.None
	}

	Emoji.init(bot.kordRef)
	bot.start()
}
