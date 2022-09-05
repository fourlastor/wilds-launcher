package io.github.fourlastor.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState

@Composable
fun <T> ViewModelContainer(
    viewModel: ViewModel<T>,
    content: @Composable (T) -> Unit
) {
    DisposableEffect(true) {
        viewModel.start()
        onDispose { viewModel.stop() }
    }

    val state = viewModel.state.collectAsState()

    content(state.value)
}
