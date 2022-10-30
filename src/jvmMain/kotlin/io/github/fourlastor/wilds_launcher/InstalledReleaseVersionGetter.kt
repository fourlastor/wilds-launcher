package io.github.fourlastor.wilds_launcher

import io.github.fourlastor.wilds_launcher.settings.SettingsState
import java.io.File

fun getInstalledReleaseVersion(state: SettingsState.Loaded): String? {
    val file = File(state.dir)

    if (!file.exists()) {
        return null
    }

    val parent = file.parentFile
    val name = parent.name
    val parts = name.split('-')

    return parts[1]
}