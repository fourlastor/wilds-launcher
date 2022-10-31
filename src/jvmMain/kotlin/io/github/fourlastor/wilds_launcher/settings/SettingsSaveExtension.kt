package io.github.fourlastor.wilds_launcher.settings

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun Settings.save(file: File) {
    if (!file.exists()) {
        file.parentFile.mkdirs()
    }

    file.writeText(Json.encodeToString(this))
}