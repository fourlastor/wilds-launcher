package io.github.fourlastor.wilds_launcher.releases

import io.github.fourlastor.wilds_launcher.Context
import java.io.File

fun Context.getInstalledReleaseVersion(): String? {
    val file = File(this.settingsService.getDir())

    if (!file.exists()) {
        return null
    }

    val parent = file.parentFile
    val name = parent.name
    val parts = name.split('-')

    return parts[1]
}