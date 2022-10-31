package io.github.fourlastor.wilds_launcher.releases

import io.github.fourlastor.wilds_launcher.Context
import java.io.File

fun Context.getInstalledReleaseSizeInBytes(): Long? {
    val jar = this.settingsService.getJar()

    if (jar.isEmpty()) {
        return null
    }

    val jarFile = File(jar)

    if (!jarFile.exists()) {
        return null
    }

    val rootDirectory = jarFile.parentFile?.parentFile?.parentFile ?: return null
    val totalSizeInBytes = rootDirectory.walkTopDown().filter { it.isFile }.map { it.length() }.sum()

    return totalSizeInBytes
}