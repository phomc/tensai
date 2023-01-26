dependencies {
    implementation(project(":tensai-common"))
    implementation("org.slf4j:slf4j-api:1.7.25")
    compileOnly("org.spigotmc:spigot-api:${property("minecraftVersion")}-R0.1-SNAPSHOT")
}

tasks {
    register("bukkitFatJar", Jar::class.java) {
        mustRunAfter(build)
        dependsOn(jar)
        archiveClassifier.set("fat")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.compileClasspath.get()
            .filter {it.name.equals("main") }
            .map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)
    }
}
