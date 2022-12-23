pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

}

dependencyResolutionManagement {
    versionCatalogs { create("libs") { from(files("versions.toml")) } }
}

rootProject.name = "wilds-launcher"
