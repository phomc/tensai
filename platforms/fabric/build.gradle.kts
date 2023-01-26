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

dependencies {
    implementation(project(":tensai-common"))

    implementation(platform("org.apache.logging.log4j:log4j-bom:2.19.0") {
        because("Mojang provides Log4J and somehow loom does not import it")
    })

    minecraft("com.mojang:minecraft:${property("minecraftVersion")}")
    mappings("net.fabricmc:yarn:${property("minecraftVersion")}+${property("yarnMappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loaderVersion")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabricApiVersion")}")
}

tasks {
    register("fabricFatJar", Jar::class.java) {
        dependsOn(remapJar.get())
        archiveClassifier.set("fat")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        delete(File("$buildDir/remapped"))
        remapJar.get().outputs.files
            .filter { it.name.endsWith("jar") }
            .map {
                unzipTo(File("$buildDir/remapped"), it)
            }
        from(File("$buildDir/remapped"))
        from(File(project(":tensai-common").buildDir, "classes/java/main"))
    }
}
