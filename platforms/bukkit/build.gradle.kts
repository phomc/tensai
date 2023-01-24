dependencies {
    implementation(project(":tensai-common"))
    implementation("org.slf4j:slf4j-api:1.7.25")
    compileOnly("org.spigotmc:spigot-api:${property("minecraftVersion")}-R0.1-SNAPSHOT")
}
