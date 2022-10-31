package io.github.fourlastor.wilds_launcher.states

data class DownloaderState(
    val onError: () -> Unit
) : State