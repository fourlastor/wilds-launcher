package io.github.fourlastor.wilds_launcher

import io.github.fourlastor.wilds_launcher.releases.ReleaseService
import io.github.fourlastor.wilds_launcher.settings.SettingsService
import kotlinx.coroutines.CoroutineScope

data class Context(
    val coroutineScope: CoroutineScope,
    val settingsService: SettingsService,
    val releaseService: ReleaseService,
)