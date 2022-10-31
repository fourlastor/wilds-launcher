package io.github.fourlastor.wilds_launcher.settings.services

interface SettingsService {
    fun load()
    fun save()
    fun clear()

    fun getJar() : String
    fun setJar(jar: String)

    fun getDevMode() : Boolean
    fun setDevMode(devMode: Boolean)

    fun getLogsEnabled() : Boolean
    fun setLogsEnabled(logsEnabled: Boolean)

    fun getAngleGles20() : Boolean
    fun setAngleGles20(angleGles20: Boolean)
}