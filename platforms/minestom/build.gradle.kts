repositories {
    maven("https://jitpack.io") {
        name = "JitPack"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":tensai-common"))
    compileOnly("com.github.Minestom:Minestom:aebf72de90")
}

tasks {
    register("minestomFatJar", Jar::class.java) {
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
