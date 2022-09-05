package io.github.fourlastor

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.fourlastor.settings.SettingsState
import io.github.fourlastor.settings.SettingsViewModel
import io.github.fourlastor.state.ViewModelContainer
import io.github.fourlastor.ui.JarPicker
import io.github.fourlastor.ui.Launcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

@Composable
@Preview
fun App(appComponent: AppComponent, getPokeWildsLocation: () -> Pair<String, String>?) {
    val viewModel = appComponent.settingsViewModel
    MaterialTheme {
        ViewModelContainer(viewModel) { settingsState ->
            when (settingsState) {
                is SettingsState.Loaded -> Launcher(
                    settingsState = settingsState,
                    onDevModeChanged = {
                        viewModel.devMode(it)
                    },
                    onAngleGles20Changed = {
                        viewModel.angleGles20(it)
                    },
                    runPokeWilds = {
                        runPokeWilds(it)
                    }
                )

                is SettingsState.Missing -> JarPicker(getPokeWildsLocation, viewModel)
            }
        }
    }
}

private fun runPokeWilds(state: SettingsState.Loaded) {
    val runArgs = mutableListOf("java", "-jar", state.jar).apply {
        if (state.angleGles20) {
            add("angle_gles20")
        }
        if (state.devMode) {
            add("dev")
        }
    }.toTypedArray()
    Runtime.getRuntime().exec(
        runArgs,
        null,
        File(state.dir)
    )
}

class AppComponent : KoinComponent {
    val settingsViewModel by inject<SettingsViewModel>()
}
