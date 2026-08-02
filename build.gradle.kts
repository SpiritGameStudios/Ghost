import dev.kordex.gradle.plugins.docker.file.*
import dev.kordex.gradle.plugins.kordex.DataCollection

plugins {
	distribution

	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.serialization)

	alias(libs.plugins.kordex)
	alias(libs.plugins.kordex.docker)
	alias(libs.plugins.kordex.i18n)
}

group = "dev.spiritstudios"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()

	maven("https://central.sonatype.com/repository/maven-snapshots/")
	maven("https://maven.lavalink.dev/releases")
}

dependencies {
	implementation(libs.bundles.kord)

	implementation(libs.webp.imageio)
	implementation(libs.lavaplayer)

	implementation(libs.bundles.logging)
	implementation(libs.bundles.log4j)
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

kotlin {
	compilerOptions {
		optIn.add("kotlin.time.ExperimentalTime")
	}
}

kordEx {
	jvmTarget = 25
	kordExVersion = libs.versions.kordex.asProvider()

	bot {
		dataCollection(DataCollection.None)

		module("func-phishing")

		mainClass = "dev.spiritstudios.ghost.GhostKt"
	}
}

i18n {
	bundle("ghost.strings", "dev.spiritstudios.ghost.i18n")
}

docker {
	file(rootProject.file("Dockerfile"))

	commands {
		from("eclipse-temurin:25")

		emptyLine()

		runShell("mkdir -p /bot/plugins")
		runShell("mkdir -p /bot/data")
		runShell("mkdir -p /dist/out")

		emptyLine()

		volume("/bot/data")
		volume("/bot/plugins")

		emptyLine()

		copy("build/distributions/$name-$version.tar", "/dist")

		emptyLine()

		runShell("tar -xf /dist/$name-$version.tar -C /dist/out")

		if (file("src/main/dist/plugins").isDirectory) {
			runShell("mv /dist/out/$name-$version/plugins/* /bot/plugins")
		}

		runShell("chmod +x /dist/out/$name-$version/bin/$name")

		emptyLine()

		runShell("rm /dist/$name-$version.tar")

		emptyLine()
		workdir("/bot")

		emptyLine()

		env {
			add("JAVA_OPTS", "-XX:+UseSerialGC -XX:+UseCompactObjectHeaders -XX:+UseStringDeduplication")
		}

		entryPointExec("/dist/out/$name-$version/bin/$name")
	}
}
