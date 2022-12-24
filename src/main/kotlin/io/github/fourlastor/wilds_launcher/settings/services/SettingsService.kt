package io.github.fourlastor.wilds_launcher.settings.services

import io.github.fourlastor.wilds_launcher.app.Dirs
import io.github.fourlastor.wilds_launcher.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsService @Inject constructor(
    private val dirs: Dirs,
) {
    private val settings = MutableStateFlow(Settings())

    private fun settingsFile(): File = File(dirs.config, "settings.json")

    fun load() {
        if (!settingsFile().exists()) {
            return
        }

        settings.update { Json.decodeFromString(settingsFile().readText()) }
    }

    private fun save() {
        if (!settingsFile().exists()) {
            settingsFile().parentFile.mkdirs()
        }

        settingsFile().writeText(Json.encodeToString(settings.value))
    }

    fun clear() {
        settings.update { Settings() }
        settingsFile().delete()
    }

    fun getJar(): String {
        return settings.value.jar
    }

    fun setJar(jar: String) {
        settings.update { it.copy(jar = jar) }
        save()
    }

    fun getDevMode(): Boolean {
        return settings.value.devMode
    }

    fun setDevMode(devMode: Boolean) {
        settings.update { it.copy(devMode = devMode) }
        save()
    }

    fun getLogsEnabled(): Boolean {
        return settings.value.logsEnabled
    }

    fun setLogsEnabled(logsEnabled: Boolean) {
        settings.update { it.copy(logsEnabled = logsEnabled) }
        save()
    }

    fun getAngleGles20(): Boolean {
        return settings.value.angleGles20
    }

    fun setAngleGles20(angleGles20: Boolean) {
        settings.update { it.copy(angleGles20 = angleGles20) }
        save()
    }
}
