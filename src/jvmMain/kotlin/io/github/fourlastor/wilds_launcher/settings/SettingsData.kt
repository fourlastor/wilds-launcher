package io.github.fourlastor.wilds_launcher.settings

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    val dir: String,
    val jar: String,
    val devMode: Boolean = false,
    val logsEnabled: Boolean = false,
    val angleGles20: Boolean = false,
    val java: String = "",
)
