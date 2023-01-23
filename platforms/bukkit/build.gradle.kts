dependencies {
    implementation(project(":tensai-common"))

    compileOnly("org.spigotmc:spigot-api:${property("minecraftVersion")}-R0.1-SNAPSHOT")
}
