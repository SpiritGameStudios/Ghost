package dev.spiritstudios.ghost.extension

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.spiritstudios.ghost.i18n.Translations

class DebugExtension : Extension() {
	override val name = "debug"

	override suspend fun setup() {
		ephemeralSlashCommand {
			name = Translations.Command.Error.name
			description = Translations.Command.Error.description

			action {
				throw RuntimeException("Intentional error triggered by ${this.user}, should be handled gracefully.")
			}
		}
	}
}
