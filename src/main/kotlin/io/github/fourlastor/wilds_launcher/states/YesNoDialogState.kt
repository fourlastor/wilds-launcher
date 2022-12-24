package io.github.fourlastor.wilds_launcher.states

data class YesNoDialogState(
    val lines: List<String>,
    val onYes: () -> Unit,
    val onNo: () -> Unit
) : State
