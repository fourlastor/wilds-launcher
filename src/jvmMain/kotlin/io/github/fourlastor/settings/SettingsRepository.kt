package io.github.fourlastor.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class SettingsRepository constructor(
    private val dirs: Dirs,
    private val dispatchers: Dispatchers,
) {

    suspend fun read(): SettingsData? = withContext(dispatchers.IO) {
        val file = configFile()
        if (file.exists()) {
            Json.decodeFromString<SettingsData>(file.readText())
        } else {
            null
        }
    }

    suspend fun save(state: SettingsData) = withContext(dispatchers.IO) {
        val configFile = configFile()
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
        }
        configFile
            .writeText(Json.encodeToString(state))
    }

    suspend fun clear() = withContext(dispatchers.IO) {
        configFile().delete()
    }

    private fun configFile(): File {
        return File(dirs.config, "config.json")
    }
}
