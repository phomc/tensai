plugins {
    id("fabric-loom")
}

loom {
    runtimeOnlyLog4j.set(true)

    splitEnvironmentSourceSets()

    runs {
		create("testClient") {
			client()
			configName = "Test Minecraft Client"
			source(sourceSets.test.get())
		}

        create("testServer") {
			server()
			configName = "Test Minecraft Server"
            source(sourceSets.test.get())
		}
	}
}

dependencies {
    implementation(project(":tensai-common"))

    minecraft("com.mojang:minecraft:${property("minecraftVersion")}")
    mappings("net.fabricmc:yarn:${property("minecraftVersion")}+${property("yarnMappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loaderVersion")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabricApiVersion")}")
}
