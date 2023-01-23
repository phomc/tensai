pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") {
            name = "FabricMC"
        }
        maven("https://maven.minecraftforge.net/") {
            name = "MinecraftForge"
        }
    }

    plugins {
        id("fabric-loom") version "1.0-SNAPSHOT"
        id("com.diffplug.spotless") version "6.8.0"
    }
}

rootProject.name = "tensai"

include("tensai-common")
project(":tensai-common").projectDir = file("common")

listOf("bukkit", "fabric").forEach {
    include("tensai-$it")
    project(":tensai-$it").projectDir = file("platforms/$it")
}
