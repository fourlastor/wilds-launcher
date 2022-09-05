package io.github.fourlastor.settings

sealed class SettingsState {

    open fun wildsDir(dir: String, jar: String) = Loaded(dir, jar)

    object Loading: SettingsState()
    object Missing: SettingsState()
    data class Loaded(
        val dir: String,
        val jar: String,
    ): SettingsState() {
        override fun wildsDir(dir: String, jar: String): Loaded {
            return copy(
                dir = dir,
                jar = jar,
            )
        }
    }
}
