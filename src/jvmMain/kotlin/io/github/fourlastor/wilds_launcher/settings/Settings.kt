package io.github.fourlastor.wilds_launcher.settings

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    var jar: String = "",
    var devMode: Boolean = false,
    var logsEnabled: Boolean = false,
    var angleGles20: Boolean = false,
)
