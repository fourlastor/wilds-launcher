import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


@Suppress(
    // known false positive: https://github.com/gradle/gradle/issues/22797
    "DSL_SCOPE_VIOLATION"
)
plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
}

val appVersion = requireNotNull(property("io.github.fourlastor.wilds_launcher.version") as? String)

group = "io.github.fourlastor.wilds-launcher"
version = appVersion

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "wilds-launcher"
            packageVersion = appVersion
        }
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.appDirs)
    implementation(libs.dagger)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    implementation(libs.decompose)
    implementation(libs.decomposeCompose)
    implementation(libs.immutableCollections)
    implementation(libs.kotlinCoroutines)
    implementation(libs.lwjgl)
    implementation(libs.lwjglNfd)
    implementation(libs.okio)
    implementation(libs.serializationJson)

    val natives = arrayOf("linux", "macos", "macos-arm64", "windows")
    for (distribution in natives) {
        runtimeOnly("${libs.lwjgl.get()}:natives-$distribution")
        runtimeOnly("${libs.lwjglNfd.get()}:natives-$distribution")
    }
}
