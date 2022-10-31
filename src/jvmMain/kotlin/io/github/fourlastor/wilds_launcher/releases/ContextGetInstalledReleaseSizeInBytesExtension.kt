package io.github.fourlastor.wilds_launcher.releases

import io.github.fourlastor.wilds_launcher.Context
import java.io.File

fun Context.getInstalledReleaseSizeInBytes(): Long? {
    val file = File(this.settingsService.getDir())

    if (!file.exists()) {
        return null
    }

    val rootDirectory = file.parentFile?.parentFile ?: return null
    val totalSizeInBytes = rootDirectory.walkTopDown().filter { it.isFile }.map { it.length() }.sum()

    return totalSizeInBytes
}