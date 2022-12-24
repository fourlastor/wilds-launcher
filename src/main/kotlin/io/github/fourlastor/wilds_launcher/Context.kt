package io.github.fourlastor.wilds_launcher

import io.github.fourlastor.wilds_launcher.logs.Logger
import io.github.fourlastor.wilds_launcher.releases.services.GitHubReleaseService
import io.github.fourlastor.wilds_launcher.settings.services.SettingsService
import kotlinx.coroutines.CoroutineScope

data class Context(
    val coroutineScope: CoroutineScope,
    val settingsService: SettingsService,
    val releaseService: GitHubReleaseService,
    val logger: Logger,
    var process: Process? = null,
)
