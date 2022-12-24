package io.github.fourlastor.wilds_launcher.releases

import io.github.fourlastor.wilds_launcher.Context
import java.io.File

fun Context.getInstalledReleaseVersion(): String? {
    val jar = this.settingsService.getJar()

    if (jar.isEmpty()) {
        return null
    }

    val jarFile = File(jar)

    if (!jarFile.exists()) {
        return null
    }

    val parent = jarFile.parentFile.parentFile
    val name = parent.name
    val parts = name.split('-')

    return parts[1]
}