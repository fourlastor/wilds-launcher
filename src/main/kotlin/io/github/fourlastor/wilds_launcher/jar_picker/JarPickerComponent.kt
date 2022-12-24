package io.github.fourlastor.wilds_launcher.jar_picker

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.github.fourlastor.wilds_launcher.navigation.Component
import io.github.fourlastor.wilds_launcher.ui.JarPicker

class JarPickerComponent @AssistedInject constructor(
    @Assisted context: ComponentContext,
) : Component, ComponentContext by context {

    @Composable
    override fun render() {
        JarPicker(
            downloadLatestRelease = {},
            findJar = {},
            pickJar = { null },
            saveWildsDir = { _, _ -> },
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(context: ComponentContext): JarPickerComponent
    }
}
