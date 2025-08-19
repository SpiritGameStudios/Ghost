package dev.spiritstudios.ghost.extension

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.converters.impl.stringChoice
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import dev.spiritstudios.ghost.i18n.Translations
import okio.FileSystem
import okio.Path.Companion.toPath

val tags = buildMap {
	var tagsPath = "/tags/".toPath();

	val files = FileSystem.RESOURCES.list(tagsPath)

	check(!files.isEmpty()) { "Tags folder is empty" }

	val alias = mutableMapOf<String, String>()

	for (path in files) {
		val content = FileSystem.RESOURCES.read(path) { readUtf8() }
		val name = path.name

		if (name.endsWith(".md")) {
			this[name.replace(".md", "")] = content
			continue
		}

		if (name.endsWith(".alias")) alias[name.replace(".alias", "")] = content
	}

	alias.forEach { (alias, name) -> this[alias] = this[name]!! }
}

class TagsExtension : Extension() {
	override val name = "tags"

	override suspend fun setup() {
		publicSlashCommand(::TagArguments) {
			name = Translations.Command.Tag.name
			description = Translations.Command.Tag.description

			action {
				respond {
					content = tags[arguments.tag]!!
				}
			}
		}
	}
}

class TagArguments : Arguments() {
	val tag by stringChoice {
		name = Translations.Command.Tag.Tag.name
		description = Translations.Command.Tag.Tag.description

		validate {
			this.errorResponseKey = Translations.Command.Tag.Error.notFound
			tags.contains(this.value)
		}

		tags.forEach { (name, content) ->
			choice(name.toKey(), name)
		}
	}
}
