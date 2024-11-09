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
    mavenCentral()
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
    implementation(libs.modrinth4j)
    implementation(libs.webp.imageio)
}

application {
    mainClass.set("dev.spiritstudios.ghost.Ghost")
}