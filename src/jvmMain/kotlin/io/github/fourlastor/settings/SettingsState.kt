package io.github.fourlastor.settings

sealed class SettingsState {

    open fun wildsDir(dir: String, jar: String): SettingsState = Loaded(
        dir = dir,
        jar = jar,
        devMode = false,
        angleGles20 = false
    )

    open fun devMode(devMode: Boolean) = this
    open fun angleGles20(angleGles20: Boolean) = this

    object Loading: SettingsState()
    object Missing: SettingsState()
    data class Loaded(
        val dir: String,
        val jar: String,
        val devMode: Boolean,
        val angleGles20: Boolean,
    ): SettingsState() {
        override fun wildsDir(dir: String, jar: String) = copy(
            dir = dir,
            jar = jar,
        )

        override fun devMode(devMode: Boolean) = copy(
            devMode = devMode
        )

        override fun angleGles20(angleGles20: Boolean) = copy(
            angleGles20 = angleGles20
        )
    }
}
