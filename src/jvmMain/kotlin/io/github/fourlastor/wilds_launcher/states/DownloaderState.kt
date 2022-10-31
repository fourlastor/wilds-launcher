package io.github.fourlastor.wilds_launcher.states

data class DownloaderState(
    val onSuccess: () -> Unit,
    val onError: () -> Unit
) : State