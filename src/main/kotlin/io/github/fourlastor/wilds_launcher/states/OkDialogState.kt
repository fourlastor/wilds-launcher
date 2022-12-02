package io.github.fourlastor.wilds_launcher.states

data class OkDialogState(
    val lines: Array<String>,
    val onOk: () -> Unit
) : State