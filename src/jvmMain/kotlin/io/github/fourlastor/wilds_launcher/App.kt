package io.github.fourlastor.wilds_launcher

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.fourlastor.wilds_launcher.settings.SettingsState
import io.github.fourlastor.wilds_launcher.settings.SettingsViewModel
import io.github.fourlastor.wilds_launcher.state.ViewModelContainer
import io.github.fourlastor.wilds_launcher.ui.*
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
                    viewModel,
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

                is SettingsState.Downloading -> Downloader(viewModel)

                is SettingsState.CheckingForUpdates -> UpdateChecker(viewModel)
                is SettingsState.NoUpdatesFound -> OkDialog(arrayOf("No available update found.")) { viewModel.manager.update { SettingsState.Missing } }

                is SettingsState.UpdateFound -> YesNoDialog(
                    arrayOf("There is an update available (${getLatestReleaseVersion()}).", "Do you want to download it?"),
                    { viewModel.manager.update { SettingsState.Downloading } },
                    { viewModel.manager.update { SettingsState.Missing } }
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
