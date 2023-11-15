pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    val kotlinVersion: String by settings
    val ktorVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion apply false
        id("io.ktor.plugin") version ktorVersion apply false
    }
}

rootProject.name = "claude-api-server"
