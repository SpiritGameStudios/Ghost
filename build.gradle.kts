plugins {
    application
}

group = "dev.spiritstudios"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {

    mavenCentral()
    maven("https://maven.lavalink.dev/releases")
    maven("https://libraries.minecraft.net")
    maven("https://maven.lukebemish.dev/releases")
	maven("https://maven.spiritstudios.dev/snapshots")
}

dependencies {
    compileOnly(libs.annotations)

    implementation(libs.jda)

    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j)

    implementation(libs.datafixerupper)
    implementation(libs.fastutil)
    implementation(libs.webp.imageio)
    implementation(libs.maze)

    implementation(libs.lavaplayer)

	implementation(libs.udpqueue.linux.x86)
	implementation(libs.udpqueue.windows.x86)
}

application {
    mainClass.set("dev.spiritstudios.ghost.Ghost")
}

tasks.withType<Jar>() {
    manifest.attributes["Main-Class"] = "dev.spiritstudios.ghost.Ghost"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

