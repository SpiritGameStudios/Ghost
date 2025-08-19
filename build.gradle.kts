import dev.kordex.gradle.plugins.kordex.DataCollection

plugins {
	distribution

	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.kordex)
}

group = "dev.spiritstudios"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()

	maven("https://maven.spiritstudios.dev/snapshots")
	maven("https://repo.kord.dev/snapshots")
	maven("https://maven.lavalink.dev/releases")
    maven("https://maven.lukebemish.dev/releases")
}

dependencies {
    implementation(libs.log4j.core)
	implementation(libs.kotlin.reflect)
	implementation(libs.log4j.kotlin)
	implementation(libs.log4j.slf4j)

	implementation(libs.bundles.kord)

    implementation(libs.fastutil)
    implementation(libs.webp.imageio)

    implementation(libs.lavaplayer)
}

distributions {
	main {
		distributionBaseName = project.name

		contents {
			from("LICENSE")
			exclude("README.md")
		}
	}
}

kordEx {
	jvmTarget = 21
	kordExVersion = libs.versions.kordex.asProvider()

	bot {
		dataCollection(DataCollection.None)

		module("func-phishing")

		mainClass = "dev.spiritstudios.ghost.GhostKt"
	}

	i18n {
		classPackage = "dev.spiritstudios.ghost.i18n"
		translationBundle = "ghost.strings"
	}
}
