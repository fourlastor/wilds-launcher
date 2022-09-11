package io.github.fourlastor.wilds_launcher

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.fourlastor.wilds_launcher.settings.SettingsState
import io.github.fourlastor.wilds_launcher.settings.SettingsViewModel
import io.github.fourlastor.wilds_launcher.state.ViewModelContainer
import io.github.fourlastor.wilds_launcher.ui.JarPicker
import io.github.fourlastor.wilds_launcher.ui.Launcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
                    onLogsEnabledChanged = {
                        viewModel.logsEnabled(it)
                    },
                    onAngleGles20Changed = {
                        viewModel.angleGles20(it)
                    },
                    runPokeWilds = {
                        viewModel.runPokeWilds(it)
                    },
                    clearData = {
                        viewModel.clearData()
                    }
                )

                is SettingsState.Missing -> JarPicker(getPokeWildsLocation, viewModel)
                is SettingsState.Loading -> {}
            }
        }
    }
}

class AppComponent : KoinComponent {
    val settingsViewModel by inject<SettingsViewModel>()
}
