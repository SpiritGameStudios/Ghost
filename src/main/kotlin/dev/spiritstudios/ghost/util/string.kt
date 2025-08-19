package dev.spiritstudios.ghost.util

fun String.truncate(length: Int): String {
	if (this.length > length) {
		return this.substring(0, length - 3) + "..."
	}
	return this
}
