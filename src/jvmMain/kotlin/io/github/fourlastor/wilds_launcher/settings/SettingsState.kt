package io.github.fourlastor.wilds_launcher.settings

sealed class SettingsState {

    open fun wildsDir(dir: String, jar: String): SettingsState = Loaded(
        dir = dir,
        jar = jar,
        devMode = false,
        logsEnabled = false,
        angleGles20 = false,
        logs = ""
    )

    open fun devMode(devMode: Boolean) = this
    open fun logsEnabled(logsEnabled: Boolean) = this
    open fun angleGles20(angleGles20: Boolean) = this
    open fun appendLog(log: String) = this

    object Downloading : SettingsState()
    object Loading : SettingsState()
    object Missing : SettingsState()
    data class Loaded(
        val dir: String,
        val jar: String,
        val devMode: Boolean,
        val logsEnabled: Boolean,
        val angleGles20: Boolean,
        val logs: String
    ) : SettingsState() {
        override fun wildsDir(dir: String, jar: String) = copy(
            dir = dir,
            jar = jar,
        )

        override fun devMode(devMode: Boolean) = copy(
            devMode = devMode
        )

        override fun logsEnabled(logsEnabled: Boolean) = copy(
            logsEnabled = logsEnabled
        )

        override fun angleGles20(angleGles20: Boolean) = copy(
            angleGles20 = angleGles20
        )

        override fun appendLog(log: String) = copy(
            logs = "$logs\n$log"
        )
    }
}
