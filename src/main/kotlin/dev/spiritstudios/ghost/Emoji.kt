package dev.spiritstudios.ghost

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.ApplicationEmoji

val MODRINTH = Snowflake(1407248335336767569)
val LEXFORGE = Snowflake(1407247022909689926)
val FABRIC = Snowflake(1407247014906826782)
val NEOFORGE = Snowflake(1407247003120959539)
val UNKNOWN = Snowflake(1407246991054077994)
val QUILT = Snowflake(1407246981604184124)
val PAPER = Snowflake(1407246970816430140)
val SPIGOT = Snowflake(1407246960762818571)
val VELOCITY = Snowflake(1407246950817988668)
val BUKKIT = Snowflake(1407246939761934386)
val MINECRAFT = Snowflake(1407246924385353758)
val PURPUR = Snowflake(1407246909344841788)
val WATERFALL = Snowflake(1407246899819581471)
val SPONGE = Snowflake(1407246887370756116)
val RIFT = Snowflake(1407246874108493884)
val RISUGAMIS_MODLOADER = Snowflake(1407246863572140032)
val LITELOADER = Snowflake(1407246855770869881)
val FOLIA = Snowflake(1407246846207852636)
val BUNGEECORD = Snowflake(1407246829808255048)

object Emoji {
	suspend fun init(kord: Kord) {
		modrinth = kord.getApplicationEmoji(MODRINTH)
		lexforge = kord.getApplicationEmoji(LEXFORGE)
		fabric = kord.getApplicationEmoji(FABRIC)
		neoforge = kord.getApplicationEmoji(NEOFORGE)
		unknown = kord.getApplicationEmoji(UNKNOWN)
		quilt = kord.getApplicationEmoji(QUILT)
		paper = kord.getApplicationEmoji(PAPER)
		spigot = kord.getApplicationEmoji(SPIGOT)
		velocity = kord.getApplicationEmoji(VELOCITY)
		bukkit = kord.getApplicationEmoji(BUKKIT)
		minecraft = kord.getApplicationEmoji(MINECRAFT)
		purpur = kord.getApplicationEmoji(PURPUR)
		waterfall = kord.getApplicationEmoji(WATERFALL)
		sponge = kord.getApplicationEmoji(SPONGE)
		rift = kord.getApplicationEmoji(RIFT)
		risugamisModLoader = kord.getApplicationEmoji(RISUGAMIS_MODLOADER)
		liteloader = kord.getApplicationEmoji(LITELOADER)
		folia = kord.getApplicationEmoji(FOLIA)
		bungeecord = kord.getApplicationEmoji(BUNGEECORD)
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
