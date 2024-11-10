plugins {
    java
    application
}

group = "dev.spiritstudios"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    maven("https://maven.callmeecho.dev/snapshots")

    mavenCentral()
    maven("https://maven.lavalink.dev/releases")
    maven("https://libraries.minecraft.net")
    maven("https://maven.lukebemish.dev/releases")
}

dependencies {
    compileOnly(libs.annotations)

    implementation(libs.javacord)

    implementation(libs.log4j.core)
    implementation(libs.log4j.slf4j)

    implementation(libs.datafixerupper)
    implementation(libs.fastutil)
    implementation(libs.webp.imageio)
    implementation(libs.maze)

    implementation(libs.lavaplayer)
    implementation(libs.lavaplayer.youtube)
}

application {
    mainClass.set("dev.spiritstudios.ghost.Ghost")
}