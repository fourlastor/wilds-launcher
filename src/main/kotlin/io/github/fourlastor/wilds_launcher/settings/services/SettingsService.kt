package io.github.fourlastor.wilds_launcher.settings.services

import io.github.fourlastor.wilds_launcher.app.Dirs
import io.github.fourlastor.wilds_launcher.settings.Settings
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
    private var settings: Settings = Settings()

    private fun settingsFile(): File = File(dirs.config)

    fun load() {
        if (!settingsFile().exists()) {
            return
        }

        settings = Json.decodeFromString(settingsFile().readText())
    }

    fun save() {
        if (!settingsFile().exists()) {
            settingsFile().parentFile.mkdirs()
        }

        settingsFile().writeText(Json.encodeToString(settings))
    }

    fun clear() {
        settings = Settings()
        settingsFile().delete()
    }

    fun getJar(): String {
        return settings.jar
    }

    fun setJar(jar: String) {
        settings = settings.copy(jar = jar)
        save()
    }

    fun getDevMode(): Boolean {
        return settings.devMode
    }

    fun setDevMode(devMode: Boolean) {
        settings = settings.copy(devMode = devMode)
        save()
    }

    fun getLogsEnabled(): Boolean {
        return settings.logsEnabled
    }

    fun setLogsEnabled(logsEnabled: Boolean) {
        settings = settings.copy(logsEnabled = logsEnabled)
        save()
    }

    fun getAngleGles20(): Boolean {
        return settings.angleGles20
    }

    fun setAngleGles20(angleGles20: Boolean) {
        settings = settings.copy(angleGles20 = angleGles20)
        save()
    }
}
