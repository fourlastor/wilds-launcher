package io.github.fourlastor.wilds_launcher.states

data class YesNoDialogState(
    val lines: Array<String>,
    val onYes: () -> Unit,
    val onNo: () -> Unit
) : State