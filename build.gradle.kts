plugins {
    idea
    eclipse
    checkstyle
    `java-library`
    `maven-publish`
    id("com.diffplug.spotless")
}

allprojects {
    apply(plugin = "checkstyle")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.diffplug.spotless")

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    java {
        withSourcesJar()
    }

    dependencies {
        implementation("org.jetbrains:annotations:23.0.0")
    }

	tasks.withType<ProcessResources>().configureEach {
		inputs.property("version", project.version)

		filesMatching(listOf("fabric.mod.json", "plugin.yml")) {
			expand("version" to project.version)
		}
	}

    spotless {
        java {
            licenseHeaderFile(rootProject.file("HEADER")).yearSeparator("-")
        }
    }

    checkstyle {
        toolVersion = "10.6.0"
        configFile = rootProject.file("checkstyle.xml")
    }
}
