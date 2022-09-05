package io.github.fourlastor.settings

import kotlinx.serialization.Serializable

@Serializable
data class SettingsData(
    val dir: String,
    val jar: String,
    val devMode: Boolean,
    val angleGles20: Boolean,
)
