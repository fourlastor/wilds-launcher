package io.github.fourlastor.wilds_launcher

import java.io.File

fun getInstalledReleaseVersion(file: File): String? {
    if (!file.exists()) {
        return null
    }

    val parent = file.parentFile
    val name = parent.name
    val parts = name.split('-')

    return parts[1]
}