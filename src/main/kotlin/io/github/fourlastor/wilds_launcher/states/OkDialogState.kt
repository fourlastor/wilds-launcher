package io.github.fourlastor.wilds_launcher.states

data class OkDialogState(
    val lines: List<String>,
    val onOk: () -> Unit
) : State
