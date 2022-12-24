package io.github.fourlastor.wilds_launcher.file

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import io.github.fourlastor.wilds_launcher.navigation.Component
import java.io.File

class LoadFileComponent(
    context: ComponentContext,
    private val onCloseRequest: (File?) -> Unit,
) : Component, ComponentContext by context {
    @Composable
    override fun render() {
        FileLoadDialog(onCloseRequest = onCloseRequest)
    }
}
