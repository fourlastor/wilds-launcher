package io.github.fourlastor.wilds_launcher.settings

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val jar: String = "",
    val devMode: Boolean = false,
    val logsEnabled: Boolean = false,
    val angleGles20: Boolean = false,
)
