import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.6.10"
}

group = "io.github.fourlastor.wilds-launcher"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
        withJava()
    }
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "wilds-launcher"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    commonMainImplementation("net.harawata:appdirs:1.2.1")
    commonMainImplementation("io.insert-koin:koin-core:3.2.0")

    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
}
