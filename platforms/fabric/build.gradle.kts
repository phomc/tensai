import org.gradle.kotlin.dsl.support.unzipTo

plugins {
    id("fabric-loom")
}

loom {
    splitEnvironmentSourceSets()
}

sourceSets {
    test {
        compileClasspath += main.get().compileClasspath
        runtimeClasspath += main.get().runtimeClasspath

        compileClasspath += sourceSets["client"].compileClasspath
        runtimeClasspath += sourceSets["client"].runtimeClasspath
    }
}

loom {
    runtimeOnlyLog4j.set(true)

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

repositories {
    maven("https://server.bbkr.space/artifactory/libs-release") {
        name = "CottonMC"
    }
}

dependencies {
    implementation(project(":tensai-common"))

    implementation(platform("org.apache.logging.log4j:log4j-bom:2.19.0") {
        because("Mojang provides Log4J and somehow loom does not import it")
    })

    minecraft("com.mojang:minecraft:${property("minecraftVersion")}")
    mappings("net.fabricmc:yarn:${property("minecraftVersion")}+${property("yarnMappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loaderVersion")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabricApiVersion")}")
    modImplementation("io.github.cottonmc:LibGui:${property("libGuiVersion")}")
}

tasks {
    register("fabricFatJar", Jar::class.java) {
        dependsOn(remapJar)
        archiveClassifier.set("fat")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        delete(File("$buildDir/remapped"))
        remapJar.get().outputs.files
            .filter { it.name.endsWith("jar") && it.isFile && it.exists() }
            .map {
                unzipTo(File("$buildDir/remapped"), it)
            }
        from(File("$buildDir/remapped"))
        from(File(project(":tensai-common").buildDir, "classes/java/main"))
    }

    register("testJar", Jar::class.java) {
        archiveClassifier.set("test-sources")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(sourceSets.test.get().output)
        from(sourceSets.test.get().resources)
        getByName("prepareRemapTestJar").dependsOn("testJar")
    }

    register("remapTestJar", net.fabricmc.loom.task.RemapJarTask::class.java) {
        dependsOn("testJar")
        inputFile.set((getByName("testJar") as Jar).archiveFile.get())
        archiveClassifier.set("test")
        addNestedDependencies.set(false)
    }

    build.get().dependsOn(getByName("fabricFatJar"))
    build.get().dependsOn(getByName("remapTestJar"))
}
