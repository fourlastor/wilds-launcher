package io.github.fourlastor.wilds_launcher

import io.github.fourlastor.wilds_launcher.logs.Logger
import io.github.fourlastor.wilds_launcher.releases.services.ReleaseService
import io.github.fourlastor.wilds_launcher.settings.services.SettingsService
import kotlinx.coroutines.CoroutineScope

data class Context(
    val coroutineScope: CoroutineScope,
    val settingsService: SettingsService,
    val releaseService: ReleaseService,
    val logger: Logger
)