package io.github.fourlastor.wilds_launcher.settings.services

import io.github.fourlastor.wilds_launcher.settings.Settings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class FileSettingsService(
    private val file: File,
) : SettingsService {
    private var settings: Settings = Settings()

    override fun load() {
        if (!file.exists()) {
            return
        }

        settings = Json.decodeFromString(file.readText())
    }

    override fun save() {
        if (!file.exists()) {
            file.parentFile.mkdirs()
        }

        file.writeText(Json.encodeToString(settings))
    }

    override fun clear() {
        settings = Settings()
        file.delete()
    }

    override fun getDir(): String {
        return settings.dir
    }

    override fun setDir(dir: String) {
        settings.dir = dir
        save()
    }

    override fun getJar(): String {
        return settings.jar
    }

    override fun setJar(jar: String) {
        settings.jar = jar
        save()
    }

    override fun getDevMode(): Boolean {
        return settings.devMode
    }

    override fun setDevMode(devMode: Boolean) {
        settings.devMode = devMode
        save()
    }

    override fun getLogsEnabled(): Boolean {
        return settings.logsEnabled
    }

    override fun setLogsEnabled(logsEnabled: Boolean) {
        settings.logsEnabled = logsEnabled
        save()
    }

    override fun getAngleGles20(): Boolean {
        return settings.angleGles20
    }

    override fun setAngleGles20(angleGles20: Boolean) {
        settings.angleGles20 = angleGles20
        save()
    }
}