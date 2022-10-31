package io.github.fourlastor.wilds_launcher.settings

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

fun loadSettings(file: File): Settings? {
    if (!file.exists()) {
        return null
    }

    return Json.decodeFromString<Settings>(file.readText())
}